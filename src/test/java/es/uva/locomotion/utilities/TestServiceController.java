package es.uva.locomotion.utilities;

import es.uva.locomotion.VensimPlugin;
import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.SymbolTable;
import es.uva.locomotion.parser.SymbolType;
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
        Utilities.setDbFacadeHandler(new ServiceConnectionHandler());
    }

    @Test
    public void testGetSymbolsControllerIgnoresFunctions(){
        ServiceController controller = new ServiceController("https://localhost");

        ServiceConnectionHandler mockHandler = Utilities.getMockDbServiceHandlerThatReturns("[]");

        Utilities.setDbFacadeHandler(mockHandler);

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
        controller.getSymbolsFromDb(symbols);

        verify(mockHandler,Mockito.times(1)).sendRequestToDictionaryService(any(), argThat(arg->{
            Set<String> actualSet = new HashSet<>(arg);
            Set<String> expectedSet =  new HashSet<>(Arrays.asList("var", "const", "lookup","subscript","sValue","realityCheck"));
            return actualSet.equals(expectedSet);

        }));
    }

    @Test
    public void testGetSymbolsControllerIgnoresDefaultSymbols(){
        ServiceController controller = new ServiceController("http://localhost");

        List<Symbol> symbols = Arrays.asList("FINAL TIME", "INITIAL TIME", "SAVEPER", "TIME STEP").stream().map(Symbol::new).collect(Collectors.toList());
        ServiceConnectionHandler mockHandler = Utilities.getMockDbServiceHandlerThatReturns("[]");
        Utilities.setDbFacadeHandler(mockHandler);

        controller.getSymbolsFromDb(symbols);

        verify(mockHandler,Mockito.times(1)).sendRequestToDictionaryService(any(), argThat(List::isEmpty));

    }

    @Test
    public void testGetSymbolsDictionaryInvalidServiceUrlMissingProtocol(){
        ServiceController controller = new ServiceController("www.myextremelyepicservice.com");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).error("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }

    @Test
    public void testGetSymbolsDictionaryInvalidServiceUrlInvalidFormat(){
        ServiceController controller = new ServiceController("http://\\$*^");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());


        Assert.assertNull(actualValue);
        verify(logger).error("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }

    @Test
    public void testGetSymbolsDictionaryInvalidServiceUrlInvalidProtocol(){
        ServiceController controller = new ServiceController("smtp://address:password@coolmail.com");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).error("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }

    @Test
    public void testGetSymbolsDictionaryMissingServiceEmptyUrl(){
        ServiceController controller = new ServiceController("");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).info("Missing dictionary service parameter.\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }

    @Test
    public void testGetSymbolsDictionaryMissingServiceNullUrl(){
        ServiceController controller = new ServiceController(null);
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
        when(handler.sendRequestToDictionaryService(any(),anyList())).thenThrow(new ConnectionFailedException(null));
        Utilities.setDbFacadeHandler(handler);

        ServiceController controller = new ServiceController("http://localhost");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).error( "The dictionary service was unreachable.\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");

    }

    @Test
    public void testGetSymbolsDictionaryInvalidFormatLiteralList(){
        Utilities.setDbFacadeHandler(Utilities.getMockDbServiceHandlerThatReturns("[1,2,3]"));

        ServiceController controller = new ServiceController("http://localhost");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).error(  "The response of the dictionary service wasn't valid. Expected an object.\n"+
        "Actual response: [1,2,3]\nThe rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");

    }


    @Test
    public void testGetSymbolsDictionaryInvalidFormatNotAnObject(){
        Utilities.setDbFacadeHandler(Utilities.getMockDbServiceHandlerThatReturns("[{\"symbol\":\"foo\"}]"));

        ServiceController controller = new ServiceController("http://localhost");
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
        Utilities.setDbFacadeHandler(Utilities.getMockDbServiceHandlerThatReturns("{\"randomKey\":\"foo\"}"));

        ServiceController controller = new ServiceController("http://localhost");
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
        Utilities.setDbFacadeHandler(Utilities.getMockDbServiceHandlerThatReturns("{\"randomKey\":\"foo\"}"));

        ServiceController controller = new ServiceController("http://localhost");
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
        Utilities.setDbFacadeHandler(Utilities.getMockDbServiceHandlerThatReturns("{\"randomKey\":\"foo\"}"));

        ServiceController controller = new ServiceController("http://localhost");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        controller.getSymbolsFromDb(new ArrayList<>());
        controller.getSymbolsFromDb(new ArrayList<>());
        Utilities.setDbFacadeHandler(Utilities.getMockDbServiceHandlerThatReturns("{\"symbol\":\"foo\"}"));
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
        Utilities.setDbFacadeHandler(Utilities.getMockDbServiceHandlerThatReturns("{}"));
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;


        SymbolTable table = new SymbolTable();
        table.addSymbol(new Symbol("function",SymbolType.Function));
        table.addSymbol(new Symbol("undetermined function", SymbolType.UNDETERMINED_FUNCTION));
        table.addSymbol(new Symbol("undetermined", SymbolType.UNDETERMINED));

        ServiceController controller = new ServiceController("https://something");
        controller.injectNewSymbols("module",new ArrayList<>(table.getSymbols()),new SymbolTable());


        verify(logger,never()).info(anyString());
        verify(DBFacade.handler,never()).injectSymbols(any(), any());
    }

    @Test
    public void testInjectNewSymbolsNullDbSymbolTable(){
        Utilities.setDbFacadeHandler(Utilities.getMockDbServiceHandlerThatReturns("{}"));
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;


        SymbolTable table = new SymbolTable();
        table.addSymbol(new Symbol("function",SymbolType.Function));
        table.addSymbol(new Symbol("undetermined function", SymbolType.UNDETERMINED_FUNCTION));
        table.addSymbol(new Symbol("undetermined", SymbolType.UNDETERMINED));

        ServiceController controller = new ServiceController("https://something");
        controller.injectNewSymbols("module",new ArrayList<>(table.getSymbols()),null);


        verify(logger,never()).info(anyString());
        verify(DBFacade.handler,never()).injectSymbols(any(), any());
    }


    @Test
    public void testInjectNewSymbolsRemovesDefaultSymbols(){
        Utilities.setDbFacadeHandler(Utilities.getMockDbServiceHandlerThatReturns("{}"));
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable table = new SymbolTable();
        for(String symbol: Constants.DEFAULT_VENSIM_SYMBOLS){
            Symbol s = new Symbol(symbol, SymbolType.Constant);
            table.addSymbol(s);
        }

        ServiceController controller = new ServiceController("https://something");
        controller.injectNewSymbols("module",new ArrayList<>(table.getSymbols()),new SymbolTable());



        verify(logger,never()).info(anyString());
        verify(DBFacade.handler,never()).injectSymbols(any(), any());

    }


    @Test
    public void testInjectNewSymbolsOnlyIncludesNewAndValidSymbols(){
        Utilities.setDbFacadeHandler(Utilities.getMockDbServiceHandlerThatReturns("{}"));
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable foundTable = new SymbolTable();
        foundTable.addSymbol(new Symbol("constant",SymbolType.Constant));
        foundTable.addSymbol(new Symbol("variable", SymbolType.Variable));
        foundTable.addSymbol(new Symbol("switch", SymbolType.Switch));
        foundTable.addSymbol(new Symbol("subscript", SymbolType.Subscript));
        foundTable.addSymbol(new Symbol("subscript value", SymbolType.Subscript_Value));
        foundTable.addSymbol(new Symbol("reality check", SymbolType.Reality_Check));
        foundTable.addSymbol(new Symbol("Lookup table", SymbolType.Lookup_Table));
        foundTable.addSymbol(new Symbol("  Symbol found in db  ", SymbolType.Constant));
        Symbol notValid = new Symbol("invalid symbol",SymbolType.Constant);
        notValid.setAsInvalid();
        foundTable.addSymbol(notValid);

        SymbolTable dbTable = new SymbolTable();
        dbTable.addSymbol(new Symbol("            Symbol found in db     ",SymbolType.Constant));

        ServiceController controller = new ServiceController("https://something");
        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), dbTable);


        verify(logger,times(1)).info("Injected  symbols:[Lookup table, constant, reality check, subscript, subscript value, switch, variable]");
    }

    @Test
    public void testInjectNewSymbolsEmptyService(){
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        ServiceController controller = new ServiceController("");

        SymbolTable foundTable = new SymbolTable();
        foundTable.addSymbol(new Symbol("constant",SymbolType.Constant));

        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), new SymbolTable());


        verify(logger,times(1)).info("Missing dictionary service parameter.\nNew symbols won't be injected to the service. [vensim]");
    }

    @Test
    public void testInjectNewSymbolsNullService(){
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        ServiceController controller = new ServiceController(null);

        SymbolTable foundTable = new SymbolTable();
        foundTable.addSymbol(new Symbol("constant",SymbolType.Constant));

        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), new SymbolTable());


        verify(logger,times(1)).info("Missing dictionary service parameter.\nNew symbols won't be injected to the service. [vensim]");
    }

    @Test
    public void testInjectNewSymbolsConnectionFailed(){
        ServiceConnectionHandler handler = mock(ServiceConnectionHandler.class);
        when(handler.injectSymbols(any(),any())).thenThrow(new ConnectionFailedException(null));
        Utilities.setDbFacadeHandler(handler);

        ServiceController controller = new ServiceController("http://localhost");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable foundTable = new SymbolTable();
        foundTable.addSymbol(new Symbol("constant",SymbolType.Constant));

        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), new SymbolTable());


        verify(logger,times(1)).error("The dictionary service was unreachable.\nNew symbols won't be injected to the service. [vensim]");
    }

    @Test
    public void testInjectNewSymbolsDoesntRepeatLogMessages(){
        ServiceConnectionHandler handler = mock(ServiceConnectionHandler.class);
        when(handler.injectSymbols(any(),any())).thenThrow(new ConnectionFailedException(null));
        Utilities.setDbFacadeHandler(handler);

        ServiceController controller = new ServiceController("http://localhost");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable foundTable = new SymbolTable();
        foundTable.addSymbol(new Symbol("constant",SymbolType.Constant));

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

        ServiceController controller = new ServiceController("www.google.com");

        SymbolTable foundTable = new SymbolTable();
        foundTable.addSymbol(new Symbol("constant",SymbolType.Constant));

        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), new SymbolTable());


        verify(logger,times(1)).error("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\nNew symbols won't be injected to the service. [vensim]");
    }


}

