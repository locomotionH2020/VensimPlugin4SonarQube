package es.uva.medeas.utilities;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.utilities.exceptions.ConnectionFailedException;
import es.uva.medeas.utilities.exceptions.EmptyServiceException;
import es.uva.medeas.utilities.exceptions.InvalidServiceUrlException;
import es.uva.medeas.utilities.exceptions.ServiceResponseFormatNotValid;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.utils.log.Logger;

import javax.json.*;

import java.io.IOException;
import java.io.StringReader;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import java.util.List;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TestDBFacade {

    @After
    public void resetDbServiceHandler(){
        DBFacade.handler = new ServiceConnectionHandler(); // Makes the tests independent
    }

    private String generateJsonFromSymbolTable(SymbolTable table){
        JsonArrayBuilder builder = Json.createArrayBuilder();
        
        for(Symbol s: table.getSymbols()){
            JsonObject jsonSymbol = generateJsonFromSymbol(s);
            builder.add(jsonSymbol);
        }

        return  builder.build().toString();
            
    }

    private JsonObject generateJsonFromSymbol(Symbol s) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("symbol",s.getToken());

        return builder.build();
    }

    private ServiceConnectionHandler mockSymbolServiceHandlerToReturn(String jsonDbTable) {
        ServiceConnectionHandler handler = Mockito.mock(ServiceConnectionHandler.class);

        when(handler.sendRequestToDictionaryService(any(),any())).thenReturn(jsonDbTable);

        return handler;
    }



    @Test
    public void testParsedJson(){
        SymbolTable dbTable = new SymbolTable();
        dbTable.addSymbol(new Symbol("foo"));
        dbTable.addSymbol(new Symbol("var"));

        String jsonDbTable = generateJsonFromSymbolTable(dbTable);
        DBFacade.handler = mockSymbolServiceHandlerToReturn(jsonDbTable);

        SymbolTable obtainedTable = DBFacade.getExistingSymbolsFromDB("", List.of("foo","var"));
        assertEquals(dbTable,obtainedTable);
    }


    @Test
    public void testEmptyResponse(){
        JsonReader jsonReader = Json.createReader(new StringReader("[]"));
        JsonArray array = jsonReader.readArray();
        jsonReader.close();

        SymbolTable table = DBFacade.createSymbolTableFromJson(array);

        assertTrue(table.getSymbols().isEmpty());



    }


    @Test(expected = IllegalArgumentException.class)
    public void testSymbolListIsNull(){
        DBFacade.getExistingSymbolsFromDB("https://localhost",null);
    }



    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testUnexpectedFormatArrayOfLiterals() {
        DBFacade.handler =  Utilities.getMockDbServiceHandlerThatReturns("[1,3,4]");

        DBFacade.getExistingSymbolsFromDB("http://localhost",List.of("foo","var"));
    }

    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testUnexpectedFormatNotAList(){
       DBFacade.handler = Utilities.getMockDbServiceHandlerThatReturns("{\"symbol\":\"foo\"}");

       DBFacade.getExistingSymbolsFromDB("http://localhost",List.of("foo"));

    }

    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testUnexpectedFormatObjectsDontHaveSymbolKey(){
        DBFacade.handler =  Utilities.getMockDbServiceHandlerThatReturns("[{\"symbol\":\"foo\"},{\"randomKey\":\"nope\"}]");

        DBFacade.getExistingSymbolsFromDB("http://localhost",List.of("foo","var"));
    }

    @Test
    public void testResponseJsonContainsDuplicatedSymbols(){

        Logger logger = mock(Logger.class); //Extract to method
        DBFacade.LOG = logger;
        DBFacade.handler =  Utilities.getMockDbServiceHandlerThatReturns("[{\"symbol\":\"var\"},{\"symbol\":\"var\"}]");


        DBFacade.getExistingSymbolsFromDB("http://localhost",List.of("var"));


        verify(logger).warn("Received duplicated symbol from the dictionary service.");
    }

    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testResponseIsntAJson() {
        DBFacade.handler = Utilities.getMockDbServiceHandlerThatReturns("some text");

        DBFacade.getExistingSymbolsFromDB("http://localhost",List.of("foo","var"));
    }


    @Test(expected = EmptyServiceException.class)
    public void testEmptyServiceRaisesException(){
        DBFacade.getExistingSymbolsFromDB("",List.of("foo","var"));
    }

    @Test(expected = EmptyServiceException.class)
    public void testNullServiceRaisesException(){
        DBFacade.getExistingSymbolsFromDB(null,List.of("foo","var"));
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testServiceWithAnotherProtocol(){
       DBFacade.getExistingSymbolsFromDB("ftp://somedomain/folder/file.txt",List.of("foo","var"));
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testServiceWithInvalidProtocol(){
        DBFacade.getExistingSymbolsFromDB("\\some$randomtext",List.of("foo","var"));
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testDomainWithoutProtocol(){
        DBFacade.getExistingSymbolsFromDB("www.google.com",List.of("foo","var"));
    }

    @Test(expected = ConnectionFailedException.class)
    public void testDomainNotFound(){
        DBFacade.getExistingSymbolsFromDB("https://adsmfmgekrhadbsfsfaf.com",List.of("foo","var"));
    }



    @Test
    public void testDomainWithoutWWW() throws IOException, InterruptedException {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(),any());
        when(mockResponse.body()).thenReturn("[]");

        handler.client = mockClient;
        DBFacade.handler = handler;

        DBFacade.getExistingSymbolsFromDB("http://google.com",List.of("foo","var"));
    }


}
