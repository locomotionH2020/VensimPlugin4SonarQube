package es.uva.medeas.utilities;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import es.uva.medeas.utilities.exceptions.ConnectionFailedException;
import es.uva.medeas.utilities.exceptions.EmptyServiceException;
import es.uva.medeas.utilities.exceptions.InvalidServiceUrlException;
import es.uva.medeas.utilities.exceptions.ServiceResponseFormatNotValid;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.utils.log.Logger;

import javax.json.*;

import java.io.IOException;
import java.io.StringReader;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertThrows;

public class TestDBFacade {

    @After
    public void resetDbServiceHandler(){
        DBFacade.handler = new ServiceConnectionHandler(); // Makes the tests independent
    }




    private ServiceConnectionHandler mockSymbolServiceHandlerToReturn(String jsonDbTable) {
        ServiceConnectionHandler handler = Mockito.mock(ServiceConnectionHandler.class);

        when(handler.sendRequestToDictionaryService(any(),any())).thenReturn(jsonDbTable);

        return handler;
    }



    @Test
    public void testParsedJson(){
        SymbolTable dbTable = new SymbolTable();

        Symbol index1 = new Symbol("index1");
        index1.setType(SymbolType.Subscript);
        index1.setComment("index1 comment");
        dbTable.addSymbol(index1);
        Symbol index2 = new Symbol("index2");
        index2.setType(SymbolType.Subscript);
        dbTable.addSymbol(index2);

        Symbol foo = new Symbol("foo");
        foo.setUnits("foo unit");
        foo.setComment("foo comment");
        foo.addModule("module 1");
        foo.setCategory("foo category");
        foo.addModule("module 2");
        foo.setType(SymbolType.Constant);
        dbTable.addSymbol(foo);

        Symbol var = new Symbol("var");
        var.setUnits("var unit");
        var.setComment("var comment");
        var.addModule("module 3");
        var.setCategory("var category");
        var.addModule("module 4");
        var.addIndexLine(List.of(index1,index2));
        var.setType(SymbolType.Variable);
        dbTable.addSymbol(var);

        String jsonDbTable = "{\"symbols\":[" +
                "{\"name\":\"foo\", \"category\":\"foo category\", \"modules\":[\"module 1\", \"module 2\"]," +
                "\"programming symbol type\": \"Constant\",\"indexes\":[], \"definition\": \"foo comment\", \"unit\":\"foo unit\"}," +
                "{\"name\":\"var\",\"programming symbol type\":\"Variable\",\"unit\":\"var unit\", \"definition\":\"var comment\", \"modules\":[\"module 3\",\"module 4\"], \"category\":\"var category\",\"indexes\":[\"index1\",\"index2\"]}" +
                "]," +
                "\"indexes\":[{\"name\":\"index1\", \"values\":[],\"definition\":\"index1 comment\"}," +
                "{\"name\":\"index2\", \"values\":[],\"definition\":\"\"}]," +
                "\"modules\":[\"module 1\",\"module 2\",\"module 3\",\"module 4\"]," +
                "\"categories\":[\"var category\", \"foo category\"]}";


        DBFacade.handler = mockSymbolServiceHandlerToReturn(jsonDbTable);

        SymbolTable obtainedTable = DBFacade.getExistingSymbolsFromDB("", List.of("foo","var"));
        assertEquals(dbTable,obtainedTable);
    }

    @Test
    public void testLoadIndexValues(){
        SymbolTable dbTable = new SymbolTable();

        Symbol index = new Symbol("index",SymbolType.Subscript);
        Symbol first_value = new Symbol("first value",SymbolType.Subscript_Value);
        Symbol second_value = new Symbol("second value", SymbolType.Subscript_Value);
        index.addDependency(first_value);
        index.addDependency(second_value);

        dbTable.addSymbol(index);
        dbTable.addSymbol(first_value);
        dbTable.addSymbol(second_value);

        String jsonDbTable = "{\"symbols\":[],"+
                "\"indexes\":[{\"name\":\"index\", \"definition\":\"\",\"values\":[\"first value\", \"second value\"]}]}";


        DBFacade.handler = mockSymbolServiceHandlerToReturn(jsonDbTable);

        SymbolTable obtainedTable = DBFacade.getExistingSymbolsFromDB("", List.of("foo","var"));

        assertEquals(dbTable,obtainedTable);
    }



    @Test
    public void testOnlySymbols(){
        SymbolTable dbTable = new SymbolTable();

        Symbol foo = new Symbol("foo");
        foo.setUnits("foo unit");
        foo.setComment("foo comment");
        foo.addModule("module 1");
        foo.setCategory("foo category");
        foo.addModule("module 2");
        foo.setType(SymbolType.Constant);
        dbTable.addSymbol(foo);


        String jsonDbTable = "{\"symbols\":[" +
                "{\"name\":\"foo\", \"category\":\"foo category\", \"modules\":[\"module 1\", \"module 2\"]," +
                "\"programming symbol type\": \"Constant\",\"indexes\":[], \"definition\": \"foo comment\", \"unit\":\"foo unit\"}]," +
                "\"indexes\":[]}";


        DBFacade.handler = mockSymbolServiceHandlerToReturn(jsonDbTable);

        SymbolTable obtainedTable = DBFacade.getExistingSymbolsFromDB("", List.of("foo","var"));

        assertEquals(dbTable,obtainedTable);
    }


    @Test
    public void testEmptyResponse(){
        String jsonDbTable =  "{\"symbols\":[],\"indexes\":[],\"categories\":[],\"modules\":[]}";


        DBFacade.handler = mockSymbolServiceHandlerToReturn(jsonDbTable);

        SymbolTable obtainedTable = DBFacade.getExistingSymbolsFromDB("", List.of("foo","var"));

        assertEquals(new SymbolTable(),obtainedTable);

    }


    @Test(expected = IllegalArgumentException.class)
    public void testSymbolListIsNull(){
        DBFacade.getExistingSymbolsFromDB("https://localhost",null);
    }

    @Test
    public void testUnexpectedFormatNoSymbolField(){
        String jsonDbTable =  "{\"indexes\":[],\"categories\":[],\"modules\":[]}";

        DBFacade.handler =  Utilities.getMockDbServiceHandlerThatReturns(jsonDbTable);
        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var")));
        assertEquals("Missing 'symbols' field.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());
    }

    @Test
    public void testUnexpectedFormatNoIndexField(){
        String jsonDbTable =  "{\"symbols\":[],\"categories\":[],\"modules\":[]}";

        DBFacade.handler =  Utilities.getMockDbServiceHandlerThatReturns(jsonDbTable);
        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var")));
        assertEquals("Missing 'indexes' field.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());
    }


    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testUnexpectedFormatArrayOfLiterals() {
        DBFacade.handler =  Utilities.getMockDbServiceHandlerThatReturns("[1,3,4]");

        DBFacade.getExistingSymbolsFromDB("http://localhost",List.of("foo","var"));
    }

    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testUnexpectedFormatNotAnObject(){
       DBFacade.handler = Utilities.getMockDbServiceHandlerThatReturns("[{\"symbol\":\"foo\"}]");

       DBFacade.getExistingSymbolsFromDB("http://localhost",List.of("foo"));

    }

    @Test
    public void testUnexpectedFormatSymbolDoesntHaveNameKey(){
        String jsonDbTable = "{\"symbols\":[" +
                "{\"definition\":\"var comment\",\"unit\":\"\",  \"category\":\"\",\"modules\":[], \"programming symbol type\":\"Constant\",\"indexes\":[]}], " +
                "\"indexes\":[]}";

        DBFacade.handler =  Utilities.getMockDbServiceHandlerThatReturns(jsonDbTable);
        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var")));
        assertEquals("Missing 'name' field from a symbol.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testUnexpectedFormatSymbolDoesntHaveDefinitionKey(){
        String jsonDbTable = "{\"symbols\":[" +
                "{\"name\":\"var\",\"unit\":\"\",  \"category\":\"\",\"modules\":[], \"programming symbol type\":\"Constant\",\"indexes\":[]}], " +
                "\"indexes\":[]}";
        DBFacade.handler =  Utilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var")));
        assertEquals("Missing 'definition' field in symbol 'var'.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testUnexpectedFormatDoesntHaveUnitKey(){
        String jsonDbTable = "{\"symbols\":[" +
                "{\"name\":\"var\", \"definition\":\"var comment\",  \"category\":\"\",\"modules\":[], \"programming symbol type\":\"Constant\",\"indexes\":[]}], " +

                "\"indexes\":[]}";
        DBFacade.handler =  Utilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var")));
        assertEquals("Missing 'unit' field in symbol 'var'.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testUnexpectedFormatDoesntHaveCategoryKey(){
        String jsonDbTable = "{\"symbols\":[" +
                "{\"name\":\"var\",\"unit\":\"\", \"definition\":\"first comment\",\"modules\":[], \"programming symbol type\":\"Constant\",\"indexes\":[]}], " +
                "\"indexes\":[]}";
        DBFacade.handler =  Utilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var")));
        assertEquals("Missing 'category' field in symbol 'var'.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testUnexpectedFormatDoesntHaveModulesKey(){
        String jsonDbTable = "{\"symbols\":[" +
                "{\"name\":\"var\",\"unit\":\"\", \"definition\":\"first comment\", \"category\":\"\", \"programming symbol type\":\"Constant\",\"indexes\":[]}], " +
                "\"indexes\":[]}";
        DBFacade.handler =  Utilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var")));
        assertEquals("Missing 'modules' field in symbol 'var'.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testUnexpectedFormatDoesntHaveProgrammingSymbolTypeKey(){
        String jsonDbTable = "{\"symbols\":[" +
                "{\"name\":\"var\",\"unit\":\"\", \"definition\":\"first comment\", \"category\":\"\", \"modules\":[],\"indexes\":[]}], " +
                "\"indexes\":[]}";
        DBFacade.handler =  Utilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var")));
        assertEquals("Missing 'programming symbol type' field in symbol 'var'.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testUnexpectedFormatDoesntHaveSymbolIndexesKey(){
        String jsonDbTable = "{\"symbols\":[" +
                "{\"name\":\"var\",\"unit\":\"\", \"definition\":\"first comment\", \"category\":\"\", \"modules\":[], \"programming symbol type\":\"Constant\"}], " +
                "\"indexes\":[]}";
        DBFacade.handler =  Utilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var")));
        assertEquals("Missing 'indexes' field in symbol 'var'.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testUnexpectedFormatUnknownProgrammingType(){
        String jsonDbTable = "{\"symbols\":[" +
                "{\"name\":\"var\",\"unit\":\"\", \"definition\":\"first comment\", \"category\":\"\", \"modules\":[], \"programming symbol type\":\"Unexpected random type\",\"indexes\":[]}], " +
                "\"indexes\":[]}";
        DBFacade.handler =  Utilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var")));
        assertEquals("The symbol 'var' has an unknown programming type: 'Unexpected random type'",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testResponseJsonContainsDuplicatedSymbols(){

        String jsonDbTable = "{\"symbols\":[" +
                "{\"name\":\"var\",\"unit\":\"\", \"definition\":\"first comment\", \"category\":\"\",\"modules\":[], \"programming symbol type\":\"Constant\",\"indexes\":[]}, " +
                "{\"name\":\"var\",\"unit\":\"other units\", \"definition\":\"other comment\", \"category\":\"\",\"modules\":[], \"programming symbol type\":\"Constant\",\"indexes\":[]}], " +
                "\"indexes\":[]}";
        Logger logger = mock(Logger.class);
        DBFacade.LOG = logger;
        DBFacade.handler =  Utilities.getMockDbServiceHandlerThatReturns(jsonDbTable);


        DBFacade.getExistingSymbolsFromDB("http://localhost",List.of("var"));


        verify(logger).warn("Received duplicated symbol 'var' from the dictionary service.");
    }

    @Test
    public void testUnexpectedFormatIndexDoesntHaveNameKey(){
        String jsonDbTable = "{\"symbols\":[],"+
                "\"indexes\":[{\"definition\":\"\",\"values\":[\"first value\", \"second value\"]}]}";
        DBFacade.handler =  Utilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var")));
        assertEquals("Missing 'name' field from an index.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testUnexpectedFormatIndexDoesntHaveDefinitionKey(){
        String jsonDbTable = "{\"symbols\":[],"+
                "\"indexes\":[{\"name\":\"index\",\"values\":[\"first value\", \"second value\"]}]}";
        DBFacade.handler =  Utilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var")));
        assertEquals("Missing 'definition' field in the index 'index'.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testUnexpectedFormatIndexDoesntHaveValuesKey(){
        String jsonDbTable = "{\"symbols\":[],"+
                "\"indexes\":[{\"name\":\"index\", \"definition\":\"index comment\"}]}";
        DBFacade.handler =  Utilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var")));
        assertEquals("Missing 'values' field in the index 'index'.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

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
        //TODO Cambiar/Quitar/Repensar
        DBFacade.getExistingSymbolsFromDB("https://adsmfmgekrhadbsfsfaf.com",List.of("foo","var"));
    }



    @Test
    public void testDomainWithoutWWW() throws IOException, InterruptedException {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(),any());
        when(mockResponse.body()).thenReturn("{\"symbols\":[],\"indexes\":[],\"categories\":[],\"modules\":[]}");

        handler.client = mockClient;
        DBFacade.handler = handler;

        DBFacade.getExistingSymbolsFromDB("http://google.com",List.of("foo","var"));
    }


}
