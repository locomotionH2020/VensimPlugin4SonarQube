package es.uva.locomotion.service;

import es.uva.locomotion.VensimPlugin;
import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.SymbolTable;
import es.uva.locomotion.parser.SymbolType;
import es.uva.locomotion.testutilities.TestUtilities;
import es.uva.locomotion.utilities.Constants;
import es.uva.locomotion.utilities.exceptions.ConnectionFailedException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.TestCase.fail;
import static  org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.sonar.api.utils.log.Logger;

import java.util.*;
import java.util.stream.Collectors;


public class TestServiceController {

    @After
    public void  resetDbFacade(){
        ServiceTestUtilities.setDbFacadeHandler(new ServiceConnectionHandler());
    }

    @Test
    public void testGetSymbolsControllerIgnoresFunctions(){
        ServiceConnectionHandler mockHandler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[]");
        ServiceTestUtilities.setDbFacadeHandler(mockHandler);

        ServiceController controller = new ServiceController("https://localhost","user","password");


        List<Symbol> symbols = Arrays.asList(new Symbol("func1", SymbolType.Function),
                new Symbol("var", SymbolType.Variable),
                new Symbol("func2", SymbolType.Function),
                new Symbol("const", SymbolType.Constant),
                new Symbol("lookup", SymbolType.Lookup_Table),
                new Symbol("subscript", SymbolType.Subscript),
                new Symbol("sValue", SymbolType.Subscript_Value),
                new Symbol("realityCheck", SymbolType.Reality_Check),
                new Symbol("func3", SymbolType.Function)
        );

        for(Symbol s:symbols)
            s.addDefinitionLine(1);

        controller.getSymbolsFromDb(symbols);

        verify(mockHandler,Mockito.times(1)).sendRequestToDictionaryService(any(), argThat(arg->{
            Set<String> actualSet = new HashSet<>(arg);
            Set<String> expectedSet =  new HashSet<>(Arrays.asList("var", "const", "lookup","subscript","sValue","realityCheck"));
            return actualSet.equals(expectedSet);

        }),any());
    }

    @Test
    public void testGetSymbolsControllerIgnoresDefaultSymbols(){
        ServiceConnectionHandler mockHandler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[]");
        ServiceTestUtilities.setDbFacadeHandler(mockHandler);

        ServiceController controller = new ServiceController("http://localhost","user", "token");

        List<Symbol> symbols = Arrays.asList("FINAL TIME", "INITIAL TIME", "SAVEPER", "TIME STEP").stream().map(Symbol::new).collect(Collectors.toList());


        controller.getSymbolsFromDb(symbols);

        verify(mockHandler,Mockito.times(1)).sendRequestToDictionaryService(any(), argThat(List::isEmpty), anyString());

    }

    @Test
    public void testGetSymbolsDictionaryInvalidServiceUrlMissingProtocol(){
        ServiceController controller = new ServiceController("www.myextremelyepicservice.com", "user","password");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).error("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }

    @Test
    public void testGetSymbolsDictionaryInvalidServiceUrlInvalidFormat(){
        ServiceController controller = new ServiceController("http://\\$*^", "user","name");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());


        Assert.assertNull(actualValue);
        verify(logger).error("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }

    @Test
    public void testGetSymbolsDictionaryInvalidServiceUrlInvalidProtocol(){
        ServiceController controller = new ServiceController("smtp://address:password@coolmail.com", "user", "password");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).error("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }

    @Test
    public void testGetSymbolsDictionaryMissingServiceEmptyUrl(){
        ServiceController controller = new ServiceController("", "user", "password");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).info("Missing dictionary service parameter.\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }

    @Test
    public void testGetSymbolsDictionaryMissingServiceNullUrl(){
        ServiceController controller = new ServiceController(null, "user", "password");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).info("Missing dictionary service parameter.\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }

    @Test
    public void testGetSymbolsDictionaryConnectionFailed(){
        ServiceConnectionHandler handler = mock(ServiceConnectionHandler.class);
        when(handler.sendRequestToDictionaryService(any(),anyList(), any())).thenThrow(new ConnectionFailedException(null));
        ServiceTestUtilities.setDbFacadeHandler(handler);

        ServiceController controller = new ServiceController("http://localhost", "user", "password");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).error( "The dictionary service was unreachable.\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");

    }

    @Test
    public void testGetSymbolsDictionaryInvalidFormatLiteralList(){
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[1,2,3]"));

        ServiceController controller = new ServiceController("http://localhost", "user", "password");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).error(  "The response of the dictionary service wasn't valid. Expected an object.\n"+
        "Actual response: [1,2,3]\nThe rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");

    }


    @Test
    public void testGetSymbolsDictionaryInvalidFormatNotAnObject(){
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[{\"symbol\":\"foo\"}]"));

        ServiceController controller = new ServiceController("http://localhost", "user", "password");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).error("The response of the dictionary service wasn't valid. Expected an object.\n" +
                "Actual response: [{\"symbol\":\"foo\"}]\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }


    @Test
    public void testGetSymbolsDictionaryInvalidFormatMissingKey(){
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{\"randomKey\":\"foo\"}"));

        ServiceController controller = new ServiceController("http://localhost", "user", "password");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).error("The response of the dictionary service wasn't valid. Missing 'symbols' field.\n"+
                "Actual response: {\"randomKey\":\"foo\"}\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }


    @Test
    public void testGetSymbolsConsecutiveSameErrorsAreOnlyLoggedOnce(){
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{\"randomKey\":\"foo\"}"));

        ServiceController controller = new ServiceController("http://localhost", "user", "password");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        controller.getSymbolsFromDb(new ArrayList<>());
        controller.getSymbolsFromDb(new ArrayList<>());
        controller.getSymbolsFromDb(new ArrayList<>());
        controller.getSymbolsFromDb(new ArrayList<>());

        verify(logger,times(1)).error("The response of the dictionary service wasn't valid. Missing 'symbols' field.\n"+
                "Actual response: {\"randomKey\":\"foo\"}\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }

    @Test
    public void testGetSymbolsConsecutiveDifferentErrorsAreLogged(){
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{\"randomKey\":\"foo\"}"));

        ServiceController controller = new ServiceController("http://localhost", "user", "password");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        controller.getSymbolsFromDb(new ArrayList<>());
        controller.getSymbolsFromDb(new ArrayList<>());
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{\"symbol\":\"foo\"}"));
        controller.getSymbolsFromDb(new ArrayList<>());
        controller.getSymbolsFromDb(new ArrayList<>());


        verify(logger).error("The response of the dictionary service wasn't valid. Missing 'symbols' field.\n"+
                "Actual response: {\"randomKey\":\"foo\"}\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");


        verify(logger).error("The response of the dictionary service wasn't valid. Missing 'symbols' field.\n" +
                "Actual response: {\"symbol\":\"foo\"}\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }

    @Test
    public void testInjectNewSymbolsRemovesFunctionsAndUndetermined(){
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{}"));
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;


        SymbolTable table = new SymbolTable();
        TestUtilities.addSymbolInLines(table,"function", SymbolType.Function,1);
        TestUtilities.addSymbolInLines(table,"undetermined function", SymbolType.UNDETERMINED_FUNCTION,2);
        TestUtilities.addSymbolInLines(table,"undetermined", SymbolType.UNDETERMINED,3);


        ServiceController controller = new ServiceController("https://something", "user", "password");
        controller.injectNewSymbols("module",new ArrayList<>(table.getSymbols()),new SymbolTable());


        verify(logger,never()).info(anyString());
        verify(DBFacade.handler,never()).injectSymbols(any(), any(), any());
    }

    @Test
    public void testInjectNewSymbolsNullDbSymbolTable(){
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{}"));
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;


        SymbolTable table = new SymbolTable();
        TestUtilities.addSymbolInLines(table,"  constant  ",SymbolType.Constant, 1);

        ServiceController controller = new ServiceController("https://something", "user", "password");
        controller.injectNewSymbols("module",new ArrayList<>(table.getSymbols()),null);


        verify(logger,never()).info(anyString());
        verify(DBFacade.handler,never()).injectSymbols(any(), any(), any());
    }


    @Test
    public void testInjectNewSymbolsRemovesDefaultSymbols(){
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{}"));
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable table = new SymbolTable();
        for(String symbol: Constants.DEFAULT_VENSIM_SYMBOLS){
            Symbol s = new Symbol(symbol, SymbolType.Constant);
            s.addDefinitionLine(1);
            table.addSymbol(s);
        }

        ServiceController controller = new ServiceController("https://something", "user", "password");
        controller.injectNewSymbols("module",new ArrayList<>(table.getSymbols()),new SymbolTable());



        verify(logger,never()).info(anyString());
        verify(DBFacade.handler,never()).injectSymbols(any(), any(), any());

    }


    @Test
    public void testInjectNewSymbolsOnlyIncludesNewAndValidSymbols(){
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{}"));
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable foundTable = new SymbolTable();
        TestUtilities.addSymbolInLines(foundTable,"  constant  ",SymbolType.Constant, 1);
        TestUtilities.addSymbolInLines(foundTable, "variable", SymbolType.Variable,2);
        TestUtilities.addSymbolInLines(foundTable, "swtich", SymbolType.Switch,3);
        TestUtilities.addSymbolInLines(foundTable, "subscript",SymbolType.Subscript,4);
        TestUtilities.addSymbolInLines(foundTable,"subscript value", SymbolType.Subscript_Value,5);
        TestUtilities.addSymbolInLines(foundTable, "reality check", SymbolType.Reality_Check,6);
        TestUtilities.addSymbolInLines(foundTable, "lookup table", SymbolType.Lookup_Table, 7);
        TestUtilities.addSymbolInLines(foundTable, "Symbol found in db", SymbolType.Constant, 8);

        Symbol notValid = TestUtilities.addSymbolInLines(foundTable, "invalid symbol", SymbolType.Constant, 9);
        notValid.setAsInvalid();


        SymbolTable dbTable = new SymbolTable();
        dbTable.addSymbol(new Symbol("            Symbol found in db     ",SymbolType.Constant));

        ServiceController controller = new ServiceController("https://something", "user", "password");
        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), dbTable);


        verify(logger,times(1)).info("Injected  symbols:[constant, lookup table, reality check, subscript, subscript value, swtich, variable]");
    }

    @Test
    public void testInjectNewSymbolsEmptyService(){
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        ServiceController controller = new ServiceController("", "user", "password");

        SymbolTable foundTable = new SymbolTable();
        TestUtilities.addSymbolInLines(foundTable,"constant",SymbolType.Constant,1);

        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), new SymbolTable());


        verify(logger,times(1)).info("Missing dictionary service parameter.\nNew symbols won't be injected to the service. [vensim]");
    }

    @Test
    public void testInjectNewSymbolsNullService(){
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        ServiceController controller = new ServiceController(null, "user", "password");

        SymbolTable foundTable = new SymbolTable();
        TestUtilities.addSymbolInLines(foundTable,"constant",SymbolType.Constant,1);

        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), new SymbolTable());


        verify(logger,times(1)).info("Missing dictionary service parameter.\nNew symbols won't be injected to the service. [vensim]");
    }

    @Test
    public void testInjectNewSymbolsConnectionFailed(){
        ServiceConnectionHandler handler = mock(ServiceConnectionHandler.class);
        when(handler.injectSymbols(any(),any(),any())).thenThrow(new ConnectionFailedException(null));
        ServiceTestUtilities.setDbFacadeHandler(handler);

        ServiceController controller = new ServiceController("http://localhost", "user", "password");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable foundTable = new SymbolTable();
        TestUtilities.addSymbolInLines(foundTable,"constant",SymbolType.Constant,1);

        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), new SymbolTable());


        verify(logger,times(1)).error("The dictionary service was unreachable.\nNew symbols won't be injected to the service. [vensim]");
    }

    @Test
    public void testInjectNewSymbolsDoesntRepeatLogMessages(){
        ServiceConnectionHandler handler = mock(ServiceConnectionHandler.class);
        when(handler.injectSymbols(any(),any(),any())).thenThrow(new ConnectionFailedException(null));
        ServiceTestUtilities.setDbFacadeHandler(handler);

        ServiceController controller = new ServiceController("http://localhost", "user", "password");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable foundTable = new SymbolTable();
        TestUtilities.addSymbolInLines(foundTable,"constant",SymbolType.Constant,1);

        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), new SymbolTable());
        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), new SymbolTable());
        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), new SymbolTable());
        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), new SymbolTable());
        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), new SymbolTable());


        verify(logger,times(1)).error("The dictionary service was unreachable.\nNew symbols won't be injected to the service. [vensim]");
    }


    @Test
    public void testInjectNewSymbolsMissingServiceProtocol(){
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        ServiceController controller = new ServiceController("www.google.com", "user", "password");

        SymbolTable foundTable = new SymbolTable();
        TestUtilities.addSymbolInLines(foundTable,"constant",SymbolType.Constant,1);

        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), new SymbolTable());


        verify(logger,times(1)).error("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\nNew symbols won't be injected to the service. [vensim]");
    }


    @Test
    public void testInjectNewSymbolsNotDefinedInAnyLine(){
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{}"));

        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        ServiceController controller = new ServiceController("https://www.google.com", "user", "password");

        SymbolTable foundTable = new SymbolTable();
        foundTable.addSymbol(new Symbol("constant",SymbolType.Constant));

        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), new SymbolTable());


        verify(logger,never()).info(anyString());
        verify(DBFacade.handler,never()).injectSymbols(any(), any(), any());
    }

}

