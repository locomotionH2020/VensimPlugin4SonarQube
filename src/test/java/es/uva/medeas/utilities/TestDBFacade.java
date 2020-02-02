package es.uva.medeas.utilities;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.rules.MagicNumberCheck;
import es.uva.medeas.utilities.exceptions.ConnectionFailedException;
import es.uva.medeas.utilities.exceptions.EmptyServiceException;
import es.uva.medeas.utilities.exceptions.InvalidServiceUrlException;
import es.uva.medeas.utilities.exceptions.ServiceResponseFormatNotValid;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.utils.log.Logger;

import javax.json.*;

import java.io.IOException;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import java.util.List;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TestDBFacade {

    @Before
    public void resetDbServiceHandler(){
        DBFacade.handler = new DbServiceHandler(); // Makes the tests independent
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

    private DbServiceHandler mockSymbolServiceHandlerToReturn(String jsonDbTable) {
        DbServiceHandler handler = Mockito.mock(DbServiceHandler.class);

        when(handler.sendRequestToService(any(),any())).thenReturn(jsonDbTable);

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
        obtainedTable.print();
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

    @Test
    public void testSendNoSymbols() throws IOException, InterruptedException {
        DbServiceHandler handler = new DbServiceHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("[]");
        handler.client = mockClient;


        HttpRequest.BodyPublisher expectedBody = HttpRequest.BodyPublishers.ofString("[]");

        doAnswer(invocationOnMock ->{
            HttpRequest mRequest = invocationOnMock.getArgument(0);
            HttpRequest.BodyPublisher body = mRequest.bodyPublisher().get();
            assertEquals("The body of the request send should be empty ([]), but it isn't.",expectedBody.contentLength(),body.contentLength());


            return mockResponse;}).when(mockClient).send(any(),any());
        handler.sendRequestToService("https://randomUrl", new ArrayList<>());


        verify(mockClient,times(1)).send(any(),any());



    }

    @Test(expected = IllegalArgumentException.class)
    public void testSymbolListIsNull(){
        DBFacade.getExistingSymbolsFromDB("https://localhost",null);
    }


    @Test
    public void testSymbolsSentCorrectly() throws IOException, InterruptedException {
        DbServiceHandler handler = new DbServiceHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("[]");
        handler.client = mockClient;


        HttpRequest.BodyPublisher expectedBody = HttpRequest.BodyPublishers.ofString("[\"var\",\"foo\",\"duck\"]");

        doAnswer(invocationOnMock -> {
            HttpRequest mRequest = invocationOnMock.getArgument(0);
            HttpRequest.BodyPublisher body = mRequest.bodyPublisher().get();
            assertEquals("The body of the request sent doesn't match the expected body",expectedBody.contentLength(), body.contentLength());


            return mockResponse;
        }).when(mockClient).send(any(), any());
        handler.sendRequestToService("https://randomUrl", List.of("var", "foo", "duck"));


        verify(mockClient, times(1)).send(any(), any());

    }

    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testUnexpectedFormatArrayOfLiterals() throws IOException, InterruptedException {
        DbServiceHandler handler = new DbServiceHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(),any());
        when(mockResponse.body()).thenReturn("[1,3,4]");

        handler.client = mockClient;
        DBFacade.handler = handler;

        DBFacade.getExistingSymbolsFromDB("http://localhost",List.of("foo","var"));
    }

    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testUnexpectedFormatObjectsDontHaveSymbolKey() throws IOException, InterruptedException {
        DbServiceHandler handler = new DbServiceHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(),any());
        when(mockResponse.body()).thenReturn("[{\"symbol\":\"foo\"},{\"randomKey\":\"nope\"}]");

        handler.client = mockClient;
        DBFacade.handler = handler;

        DBFacade.getExistingSymbolsFromDB("http://localhost",List.of("foo","var"));
    }

    @Test
    public void testResponseJsonContainsDuplicatedSymbols() throws IOException, InterruptedException {
        DbServiceHandler handler = new DbServiceHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(),any());
        when(mockResponse.body()).thenReturn("[{\"symbol\":\"var\"},{\"symbol\":\"var\"}]");

        Logger logger = mock(Logger.class); //Extract to method
        DBFacade.LOG = logger;


        handler.client = mockClient;
        DBFacade.handler = handler;

        DBFacade.getExistingSymbolsFromDB("http://localhost",List.of("foo","var"));



        verify(logger).warn("Received duplicated symbol from the dictionary service.");
    }

    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testResponseIsntAJson() throws IOException, InterruptedException {
        DbServiceHandler handler = new DbServiceHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(),any());
        when(mockResponse.body()).thenReturn("some text");

        handler.client = mockClient;
        DBFacade.handler = handler;

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
        DbServiceHandler handler = new DbServiceHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(),any());
        when(mockResponse.body()).thenReturn("[]");

        handler.client = mockClient;
        DBFacade.handler = handler;

        DBFacade.getExistingSymbolsFromDB("http://google.com",List.of("foo","var"));
    }


}
