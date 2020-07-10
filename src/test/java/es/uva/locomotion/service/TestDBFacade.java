package es.uva.locomotion.service;

import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.SymbolTable;
import es.uva.locomotion.parser.SymbolType;
import es.uva.locomotion.utilities.exceptions.EmptyServiceException;
import es.uva.locomotion.utilities.exceptions.InvalidServiceUrlException;
import es.uva.locomotion.utilities.exceptions.ServiceResponseFormatNotValid;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.utils.log.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertThrows;

public class TestDBFacade {

    @After
    public void resetDbServiceHandler(){
        DBFacade.handler = new ServiceConnectionHandler(); // Makes the tests independent
    }




    private ServiceConnectionHandler mockSymbolServiceHandlerToReturnSymbols(String jsonDbTable) {
        ServiceConnectionHandler handler = Mockito.mock(ServiceConnectionHandler.class);

        when(handler.sendRequestToDictionaryService(any(),any(),any())).thenReturn(jsonDbTable);

        return handler;
    }



    @Test
    public void testGetSymbolsParsedJson(){
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
                "{\"name\":\"foo\", \"category\":\"foo category\", \"modules\":{\"main\":\"module 1\", \"secondary\":[\"module 2\"]}," +
                "\"programming symbol type\": \"Constant\",\"indexes\":[], \"definition\": \"foo comment\", \"unit\":\"foo unit\"}," +
                "{\"name\":\"var\",\"programming symbol type\":\"Variable\",\"unit\":\"var unit\", \"definition\":\"var comment\", \"modules\":{\"main\":\"module 3\", \"secondary\":[\"module 4\"]}, \"category\":\"var category\",\"indexes\":[\"index1\",\"index2\"]}" +
                "]," +
                "\"indexes\":[{\"name\":\"index1\", \"values\":[],\"definition\":\"index1 comment\"}," +
                "{\"name\":\"index2\", \"values\":[],\"definition\":\"\"}]," +
                "\"modules\":{\"main\":\"module 1\", \"secondary\":[\"module 2\",\"module 3\", \"module 4\"]}," +
                "\"categories\":[\"var category\", \"foo category\"]}";

        DBFacade.handler = mockSymbolServiceHandlerToReturnSymbols(jsonDbTable);

        SymbolTable obtainedTable = DBFacade.getExistingSymbolsFromDB("", List.of("foo","var"),"token");
        assertEquals(dbTable,obtainedTable);
    }

    @Test
    public void testGetSymbolsLoadIndexValues(){
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


        DBFacade.handler = mockSymbolServiceHandlerToReturnSymbols(jsonDbTable);

        SymbolTable obtainedTable = DBFacade.getExistingSymbolsFromDB("", List.of("foo","var"),"token");

        assertEquals(dbTable,obtainedTable);
    }



    @Test
    public void testGetSymbolsOnlySymbols(){
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
                "{\"name\":\"foo\", \"category\":\"foo category\", \"modules\":{\"main\":\"module 1\", \"secondary\":[ \"module 2\"]}," +
                "\"programming symbol type\": \"Constant\",\"indexes\":[], \"definition\": \"foo comment\", \"unit\":\"foo unit\"}]," +
                "\"indexes\":[]}";

        DBFacade.handler = mockSymbolServiceHandlerToReturnSymbols(jsonDbTable);

        SymbolTable obtainedTable = DBFacade.getExistingSymbolsFromDB("", List.of("foo","var"),"token");

        assertEquals(dbTable,obtainedTable);
    }


    @Test
    public void testGetSymbolsEmptyResponse(){
        String jsonDbTable =  "{\"symbols\":[],\"indexes\":[],\"categories\":[],\"modules\":[]}";


        DBFacade.handler = mockSymbolServiceHandlerToReturnSymbols(jsonDbTable);

        SymbolTable obtainedTable = DBFacade.getExistingSymbolsFromDB("", List.of("foo","var"),"token");

        assertEquals(new SymbolTable(),obtainedTable);

    }


    @Test(expected = IllegalArgumentException.class)
    public void testGetSymbolsSymbolListIsNull(){
        DBFacade.getExistingSymbolsFromDB("https://localhost",null,"token");
    }

    @Test
    public void testGetSymbolsUnexpectedFormatNoSymbolField(){
        String jsonDbTable =  "{\"indexes\":[],\"categories\":[],\"modules\":[]}";

        DBFacade.handler =  ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);
        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var"),"token"));
        assertEquals("Missing 'symbols' field.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());
    }

    @Test
    public void testGetSymbolsUnexpectedFormatNoIndexField(){
        String jsonDbTable =  "{\"symbols\":[],\"categories\":[],\"modules\":[]}";

        DBFacade.handler =  ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);
        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var"),"token"));
        assertEquals("Missing 'indexes' field.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());
    }


    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testGetSymbolsUnexpectedFormatArrayOfLiterals() {
        DBFacade.handler =  ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[1,3,4]");

        DBFacade.getExistingSymbolsFromDB("http://localhost",List.of("foo","var"),"token");
    }

    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testGetSymbolsUnexpectedFormatNotAnObject(){
       DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[{\"symbol\":\"foo\"}]");

       DBFacade.getExistingSymbolsFromDB("http://localhost",List.of("foo"),"token");

    }

    @Test
    public void testGetSymbolsUnexpectedFormatSymbolDoesntHaveNameKey(){
        String jsonDbTable = "{\"symbols\":[" +
                "{\"definition\":\"var comment\",\"unit\":\"\",  \"category\":\"\",\"modules\":[], \"programming symbol type\":\"Constant\",\"indexes\":[]}], " +
                "\"indexes\":[]}";

        DBFacade.handler =  ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);
        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var"),"token"));
        assertEquals("Missing 'name' field from a symbol.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testGetSymbolsUnexpectedFormatSymbolDoesntHaveDefinitionKey(){
        String jsonDbTable = "{\"symbols\":[" +
                "{\"name\":\"var\",\"unit\":\"\",  \"category\":\"\",\"modules\":[], \"programming symbol type\":\"Constant\",\"indexes\":[]}], " +
                "\"indexes\":[]}";
        DBFacade.handler =  ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var"),"token"));
        assertEquals("Missing 'definition' field in symbol 'var'.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testGetSymbolsUnexpectedFormatDoesntHaveUnitKey(){
        String jsonDbTable = "{\"symbols\":[" +
                "{\"name\":\"var\", \"definition\":\"var comment\",  \"category\":\"\",\"modules\":[], \"programming symbol type\":\"Constant\",\"indexes\":[]}], " +

                "\"indexes\":[]}";
        DBFacade.handler =  ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var"),"token"));
        assertEquals("Missing 'unit' field in symbol 'var'.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testGetSymbolsUnexpectedFormatDoesntHaveCategoryKey(){
        String jsonDbTable = "{\"symbols\":[" +
                "{\"name\":\"var\",\"unit\":\"\", \"definition\":\"first comment\",\"modules\":[], \"programming symbol type\":\"Constant\",\"indexes\":[]}], " +
                "\"indexes\":[]}";
        DBFacade.handler =  ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var"),"token"));
        assertEquals("Missing 'category' field in symbol 'var'.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testGetSymbolsUnexpectedFormatDoesntHaveModulesKey(){
        String jsonDbTable = "{\"symbols\":[" +
                "{\"name\":\"var\",\"unit\":\"\", \"definition\":\"first comment\", \"category\":\"\", \"programming symbol type\":\"Constant\",\"indexes\":[]}], " +
                "\"indexes\":[]}";
        DBFacade.handler =  ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var"),"token"));
        assertEquals("Missing 'modules' field in symbol 'var'.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testGetSymbolsUnexpectedFormatDoesntHaveProgrammingSymbolTypeKey(){
        String jsonDbTable = "{\"symbols\":[" +
                "{\"name\":\"var\",\"unit\":\"\", \"definition\":\"first comment\", \"category\":\"\", \"modules\":[],\"indexes\":[]}], " +
                "\"indexes\":[]}";
        DBFacade.handler =  ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var"),"token"));
        assertEquals("Missing 'programming symbol type' field in symbol 'var'.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testGetSymbolsUnexpectedFormatDoesntHaveSymbolIndexesKey(){
        String jsonDbTable = "{\"symbols\":[" +
                "{\"name\":\"var\",\"unit\":\"\", \"definition\":\"first comment\", \"category\":\"\", \"modules\":[], \"programming symbol type\":\"Constant\"}], " +
                "\"indexes\":[]}";
        DBFacade.handler =  ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var"),"token"));
        assertEquals("Missing 'indexes' field in symbol 'var'.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testGetSymbolsUnexpectedFormatUnknownProgrammingType(){
        String jsonDbTable = "{\"symbols\":[" +
                "{\"name\":\"var\",\"unit\":\"\", \"definition\":\"first comment\", \"category\":\"\", \"modules\":[], \"programming symbol type\":\"Unexpected random type\",\"indexes\":[]}], " +
                "\"indexes\":[]}";
        DBFacade.handler =  ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var"),"token"));
        assertEquals("The symbol 'var' has an unknown programming type: 'Unexpected random type'",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testGetSymbolsResponseJsonContainsDuplicatedSymbols(){

        String jsonDbTable = "{\"symbols\":[" +
                "{\"name\":\"var\",\"unit\":\"\", \"definition\":\"first comment\", \"category\":\"\",\"modules\":{\"main\":\"foo\", \"secondary\":[]}, \"programming symbol type\":\"Constant\",\"indexes\":[]}, " +
                "{\"name\":\"var\",\"unit\":\"other units\", \"definition\":\"other comment\", \"category\":\"\",\"modules\":[], \"programming symbol type\":\"Constant\",\"indexes\":[]}], " +
                "\"indexes\":[]}";
        Logger logger = mock(Logger.class);
        DBFacade.LOG = logger;
        DBFacade.handler =  ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);


        DBFacade.getExistingSymbolsFromDB("http://localhost",List.of("var"),"token");


        verify(logger).warn("Received duplicated symbol 'var' from the dictionary service.");
    }

    @Test
    public void testGetSymbolsUnexpectedFormatIndexDoesntHaveNameKey(){
        String jsonDbTable = "{\"symbols\":[],"+
                "\"indexes\":[{\"definition\":\"\",\"values\":[\"first value\", \"second value\"]}]}";
        DBFacade.handler =  ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var"),"token"));
        assertEquals("Missing 'name' field from an index.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testGetSymbolsUnexpectedFormatIndexDoesntHaveDefinitionKey(){
        String jsonDbTable = "{\"symbols\":[],"+
                "\"indexes\":[{\"name\":\"index\",\"values\":[\"first value\", \"second value\"]}]}";
        DBFacade.handler =  ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var"),"token"));
        assertEquals("Missing 'definition' field in the index 'index'.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }

    @Test
    public void testGetSymbolsUnexpectedFormatIndexDoesntHaveValuesKey(){
        String jsonDbTable = "{\"symbols\":[],"+
                "\"indexes\":[{\"name\":\"index\", \"definition\":\"index comment\"}]}";
        DBFacade.handler =  ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingSymbolsFromDB("http://localhost", List.of("foo", "var"),"token"));
        assertEquals("Missing 'values' field in the index 'index'.",ex.getMessage());
        assertEquals(jsonDbTable,ex.getServiceResponse());

    }




    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testGetSymbolsResponseIsntAJson() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("some text");

        DBFacade.getExistingSymbolsFromDB("http://localhost",List.of("foo","var"),"token");
    }


    @Test(expected = EmptyServiceException.class)
    public void testGetSymbolsEmptyServiceRaisesException(){
        DBFacade.getExistingSymbolsFromDB("",List.of("foo","var"),"token");
    }

    @Test(expected = EmptyServiceException.class)
    public void testGetSymbolsNullServiceRaisesException(){
        DBFacade.getExistingSymbolsFromDB(null,List.of("foo","var"), "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetSymbolsServiceWithAnotherProtocol(){
       DBFacade.getExistingSymbolsFromDB("ftp://somedomain/folder/file.txt",List.of("foo","var"), "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetSymbolsServiceWithInvalidProtocol(){
        DBFacade.getExistingSymbolsFromDB("\\some$randomtext",List.of("foo","var"), "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetSymbolsDomainWithoutProtocol(){
        DBFacade.getExistingSymbolsFromDB("www.google.com",List.of("foo","var"), "token");
    }



    @Test
    public void testGetSymbolsDomainWithoutWWW() throws IOException, InterruptedException {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(),any());
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockResponse.body()).thenReturn("{\"symbols\":[],\"indexes\":[],\"categories\":[],\"modules\":[]}");

        handler.client = mockClient;
        DBFacade.handler = handler;

        DBFacade.getExistingSymbolsFromDB("http://google.com",List.of("foo","var"), "token");
    }

    @Test
    public void testInjectSymbol(){
        ServiceConnectionHandler handler = Mockito.mock(ServiceConnectionHandler.class);
        DBFacade.handler = handler;

        List<Symbol> symbols = new ArrayList<>();

        Symbol constant = new Symbol("    constant     ",SymbolType.Constant);
        constant.setUnits("          constant units           ");
        constant.setComment("            constant comment         ");
        constant.setCategory("           constant category      ");
        constant.addIndexLine(List.of(mock(Symbol.class)));

        symbols.add(constant);

        JsonReader jsonReader = Json.createReader(new StringReader("{\"symbols\": [{\"name\":\"constant\",\"unit\":\"constant units\",\"definition\":\"constant comment\",\"is_indexed\":\"true\",\"category\":\"constant category\",\"programming symbol type\":\"Constant\"}], \"indexes\": [], \"module\": \"module\"}"));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        DBFacade.injectSymbols("http://www.gaagle.com", "module", symbols, "token");
        verify(handler).injectSymbols("http://www.gaagle.com",object, "token");
    }

    @Test
    public void testInjectSubscript(){
        ServiceConnectionHandler handler = Mockito.mock(ServiceConnectionHandler.class);
        DBFacade.handler = handler;

        List<Symbol> symbols = new ArrayList<>();

        Symbol subscript = new Symbol("    subscript     ",SymbolType.Subscript);
        subscript.addDependency(new Symbol("           value1         ",SymbolType.Subscript_Value));
        subscript.addDependency(new Symbol("           value2          ", SymbolType.Subscript_Value));

        symbols.add(subscript);

        JsonReader jsonReader = Json.createReader(new StringReader("{\"symbols\": [], \"indexes\": [{\"name\":\"subscript\",\"values\":[\"value1\",\"value2\"]}], \"module\": \"module\"}"));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        DBFacade.injectSymbols("http://www.gaagle.com", "module", symbols,"token");
        verify(handler).injectSymbols("http://www.gaagle.com", object, "token");
    }

    @Test
    public void testInjectSubscriptValue(){
        ServiceConnectionHandler handler = Mockito.mock(ServiceConnectionHandler.class);
        DBFacade.handler = handler;

        List<Symbol> symbols = new ArrayList<>();
        symbols.add(new Symbol("value",SymbolType.Subscript_Value));

        JsonReader jsonReader = Json.createReader(new StringReader("{\"symbols\": [], \"indexes\": [], \"module\": \"module\"}"));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        DBFacade.injectSymbols("http://www.gaagle.com", "module", symbols, "token");
        verify(handler).injectSymbols("http://www.gaagle.com",object, "token");
    }

    @Test
    public void testInjectVariable(){
        ServiceConnectionHandler handler = Mockito.mock(ServiceConnectionHandler.class);
        DBFacade.handler = handler;

        List<Symbol> symbols = new ArrayList<>();
        symbols.add(new Symbol("variable",SymbolType.Variable));

        JsonReader jsonReader = Json.createReader(new StringReader("{\"symbols\": [{\"name\":\"variable\",\"unit\":\"\",\"definition\":\"\",\"is_indexed\":\"false\",\"category\":\"\",\"programming symbol type\":\"Variable\"}], \"indexes\": [], \"module\": \"module\"}"));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        DBFacade.injectSymbols("http://www.gaagle.com", "module", symbols, "token");
        verify(handler).injectSymbols("http://www.gaagle.com", object, "token");
    }

    @Test
    public void testInjectFunction(){
        ServiceConnectionHandler handler = Mockito.mock(ServiceConnectionHandler.class);
        DBFacade.handler = handler;

        List<Symbol> symbols = new ArrayList<>();
        symbols.add(new Symbol("function",SymbolType.Function));

        JsonReader jsonReader = Json.createReader(new StringReader("{\"symbols\": [], \"indexes\": [], \"module\": \"module\"}"));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        DBFacade.injectSymbols("http://www.gaagle.com", "module", symbols, "token");
        verify(handler).injectSymbols("http://www.gaagle.com",object, "token");
    }

    @Test
    public void testInjectRealityCheck(){
        ServiceConnectionHandler handler = Mockito.mock(ServiceConnectionHandler.class);
        DBFacade.handler = handler;

        List<Symbol> symbols = new ArrayList<>();
        symbols.add(new Symbol("reality check",SymbolType.Reality_Check));

        JsonReader jsonReader = Json.createReader(new StringReader("{\"symbols\": [{\"name\":\"reality check\",\"unit\":\"\",\"definition\":\"\",\"is_indexed\":\"false\",\"category\":\"\",\"programming symbol type\":\"Reality_Check\"}], \"indexes\": [], \"module\": \"module\"}"));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        DBFacade.injectSymbols("http://www.gaagle.com", "module", symbols, "token");
        verify(handler).injectSymbols("http://www.gaagle.com",object, "token");
    }

    @Test
    public void testInjectLookupTable(){
        ServiceConnectionHandler handler = Mockito.mock(ServiceConnectionHandler.class);
        DBFacade.handler = handler;

        List<Symbol> symbols = new ArrayList<>();
        symbols.add(new Symbol("lookup table",SymbolType.Lookup_Table));

        JsonReader jsonReader = Json.createReader(new StringReader("{\"symbols\": [{\"name\":\"lookup table\",\"unit\":\"\",\"definition\":\"\",\"is_indexed\":\"false\",\"category\":\"\",\"programming symbol type\":\"Lookup_Table\"}], \"indexes\": [], \"module\": \"module\"}"));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        DBFacade.injectSymbols("http://www.gaagle.com", "module", symbols, "token");
        verify(handler).injectSymbols("http://www.gaagle.com",object, "token");
    }


    @Test
    public void testInjectSwitch(){
        ServiceConnectionHandler handler = Mockito.mock(ServiceConnectionHandler.class);
        DBFacade.handler = handler;

        List<Symbol> symbols = new ArrayList<>();
        symbols.add(new Symbol("switch",SymbolType.Switch));

        JsonReader jsonReader = Json.createReader(new StringReader("{\"symbols\": [{\"name\":\"switch\",\"unit\":\"\",\"definition\":\"\",\"is_indexed\":\"false\",\"category\":\"\",\"programming symbol type\":\"Switch\"}], \"indexes\": [], \"module\": \"module\"}"));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        DBFacade.injectSymbols("http://www.gaagle.com", "module", symbols, "token");
        verify(handler).injectSymbols("http://www.gaagle.com",object, "token");
    }



}
