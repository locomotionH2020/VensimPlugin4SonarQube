package es.uva.locomotion.service;

import es.uva.locomotion.VensimPlugin;
import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.SymbolTable;
import es.uva.locomotion.parser.SymbolType;
import es.uva.locomotion.testutilities.TestUtilities;
import es.uva.locomotion.utilities.Constants;
import es.uva.locomotion.utilities.exceptions.ConnectionFailedException;
import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;
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

    public ServiceController getAuthenticatedServiceController(String dictionaryService){
        ServiceController controller = spy(new ServiceController(dictionaryService));
        doReturn(true).when(controller).isAuthenticated();
        doNothing().when(controller).authenticate(anyString(),anyString());
        return controller;

    }
    @Test
    public void testGetSymbolsControllerIgnoresFunctions(){
        ServiceConnectionHandler mockHandler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[]");
        ServiceTestUtilities.setDbFacadeHandler(mockHandler);

        ServiceController controller = getAuthenticatedServiceController("https://localhost");


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

        ServiceController controller = getAuthenticatedServiceController("http://localhost");

        List<Symbol> symbols = Arrays.asList("FINAL TIME", "INITIAL TIME", "SAVEPER", "TIME STEP").stream().map(Symbol::new).collect(Collectors.toList());


        controller.getSymbolsFromDb(symbols);

        verify(mockHandler,Mockito.times(1)).sendRequestToDictionaryService(any(), argThat(List::isEmpty), any());

    }

    @Test
    public void testGetSymbolsDictionaryInvalidServiceUrlMissingProtocol(){
        ServiceController controller = getAuthenticatedServiceController("www.myextremelyepicservice.com");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).unique("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n"+
                "The rules that require the data from the dictionary service will be skipped.", LoggingLevel.ERROR);
    }

    @Test
    public void testGetSymbolsDictionaryInvalidServiceUrlInvalidFormat(){
        ServiceController controller = getAuthenticatedServiceController("http://\\$*^");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());


        Assert.assertNull(actualValue);
        verify(logger).unique("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n"+
                "The rules that require the data from the dictionary service will be skipped.", LoggingLevel.ERROR);
    }

    @Test
    public void testGetSymbolsDictionaryInvalidServiceUrlInvalidProtocol(){
        ServiceController controller = getAuthenticatedServiceController("smtp://address:password@coolmail.com");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).unique("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n"+
                "The rules that require the data from the dictionary service will be skipped." , LoggingLevel.ERROR);
    }

    @Test
    public void testGetSymbolsDictionaryMissingServiceEmptyUrl(){
        ServiceController controller = getAuthenticatedServiceController("");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).unique("Missing dictionary service parameter.\n"+
                "The rules that require the data from the dictionary service will be skipped.", LoggingLevel.INFO);
    }

    @Test
    public void testGetSymbolsDictionaryMissingServiceNullUrl(){
        ServiceController controller = getAuthenticatedServiceController(null);
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).unique("Missing dictionary service parameter.\n"+
                "The rules that require the data from the dictionary service will be skipped.", LoggingLevel.INFO);
    }

    @Test
    public void testGetSymbolsDictionaryConnectionFailed(){
        ServiceConnectionHandler handler = mock(ServiceConnectionHandler.class);
        when(handler.sendRequestToDictionaryService(any(),anyList(), any())).thenThrow(new ConnectionFailedException(null));
        ServiceTestUtilities.setDbFacadeHandler(handler);

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).unique( "The dictionary service was unreachable.\n"+
                "The rules that require the data from the dictionary service will be skipped.", LoggingLevel.ERROR);

    }

    @Test
    public void testGetSymbolsDictionaryInvalidFormatLiteralList(){
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[1,2,3]"));
        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());
        Assert.assertNull(actualValue);
        verify(logger).error(  "The response of the dictionary service wasn't valid. Expected an object.\n"+
        "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \nThe rules that require the data from the dictionary service will be skipped.");

    }


    @Test
    public void testGetSymbolsDictionaryInvalidFormatNotAnObject(){
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[{\"symbol\":\"foo\"}]"));

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).error("The response of the dictionary service wasn't valid. Expected an object.\n" +
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n"+
                "The rules that require the data from the dictionary service will be skipped.");
    }


    @Test
    public void testGetSymbolsDictionaryInvalidFormatMissingKey(){
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{\"randomKey\":\"foo\"}"));

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).error("The response of the dictionary service wasn't valid. Missing 'symbols' field.\n"+
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n"+
                "The rules that require the data from the dictionary service will be skipped.");
    }


    @Test
    public void testGetSymbolsConsecutiveDifferentErrorsAreLogged(){
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{\"randomKey\":\"foo\"}"));

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

        controller.getSymbolsFromDb(new ArrayList<>());
        controller.getSymbolsFromDb(new ArrayList<>());
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{\"symbol\":\"foo\"}"));
        controller.getSymbolsFromDb(new ArrayList<>());
        controller.getSymbolsFromDb(new ArrayList<>());


        verify(logger, atLeastOnce()).error("The response of the dictionary service wasn't valid. Missing 'symbols' field.\n"+
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n"+
                "The rules that require the data from the dictionary service will be skipped.");
    }

    @Test
    public void testInjectNewSymbolsRemovesFunctionsAndUndetermined(){
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{}"));
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;


        SymbolTable table = new SymbolTable();
        TestUtilities.addSymbolInLines(table,"function", SymbolType.Function,1);
        TestUtilities.addSymbolInLines(table,"undetermined function", SymbolType.UNDETERMINED_FUNCTION,2);
        TestUtilities.addSymbolInLines(table,"undetermined", SymbolType.UNDETERMINED,3);


        ServiceController controller = getAuthenticatedServiceController("https://something");
        controller.injectNewSymbols("module",new ArrayList<>(table.getSymbols()),new SymbolTable());


        verify(logger,never()).info(anyString());
        verify(DBFacade.handler,never()).injectSymbols(any(), any(), any());
    }

    @Test
    public void testInjectNewSymbolsNullDbSymbolTable(){
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{}"));
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;


        SymbolTable table = new SymbolTable();
        TestUtilities.addSymbolInLines(table,"  constant  ",SymbolType.Constant, 1);

        ServiceController controller = getAuthenticatedServiceController("https://something");
        controller.injectNewSymbols("module",new ArrayList<>(table.getSymbols()),null);


        verify(logger,never()).info(anyString());
        verify(DBFacade.handler,never()).injectSymbols(any(), any(), any());
    }


    @Test
    public void testInjectNewSymbolsRemovesDefaultSymbols(){
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{}"));
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

        SymbolTable table = new SymbolTable();
        for(String symbol: Constants.DEFAULT_VENSIM_SYMBOLS){
            Symbol s = new Symbol(symbol, SymbolType.Constant);
            s.addDefinitionLine(1);
            table.addSymbol(s);
        }

        ServiceController controller = getAuthenticatedServiceController("https://something");
        controller.injectNewSymbols("module",new ArrayList<>(table.getSymbols()),new SymbolTable());



        verify(logger,never()).info(anyString());
        verify(DBFacade.handler,never()).injectSymbols(any(), any(), any());

    }


    @Test
    public void testInjectNewSymbolsOnlyIncludesNewAndValidSymbols(){
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{}"));
        VensimLogger logger = Mockito.mock(VensimLogger.class);
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

        ServiceController controller = getAuthenticatedServiceController("https://something");
        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), dbTable);


        verify(logger,times(1)).info("Injected symbols in module 'module': [constant, lookup table, reality check, subscript, subscript value, swtich, variable]");
    }

    @Test
    public void testInjectNewSymbolsEmptyService(){
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

        ServiceController controller = getAuthenticatedServiceController("");

        SymbolTable foundTable = new SymbolTable();
        TestUtilities.addSymbolInLines(foundTable,"constant",SymbolType.Constant,1);

        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), new SymbolTable());


        verify(logger,times(1)).unique("Missing dictionary service parameter.\nThe rules that require the data from the dictionary service will be skipped."
                , LoggingLevel.INFO);
    }

    @Test
    public void testInjectNewSymbolsNullService(){
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

        ServiceController controller = getAuthenticatedServiceController(null);

        SymbolTable foundTable = new SymbolTable();
        TestUtilities.addSymbolInLines(foundTable,"constant",SymbolType.Constant,1);

        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), new SymbolTable());


        verify(logger,times(1)).unique("Missing dictionary service parameter.\nThe rules that require the data from the dictionary service will be skipped."
                ,LoggingLevel.INFO);
    }

    @Test
    public void testInjectNewSymbolsConnectionFailed(){
        ServiceConnectionHandler handler = mock(ServiceConnectionHandler.class);
        when(handler.injectSymbols(any(),any(),any())).thenThrow(new ConnectionFailedException(null));
        ServiceTestUtilities.setDbFacadeHandler(handler);

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

        SymbolTable foundTable = new SymbolTable();
        TestUtilities.addSymbolInLines(foundTable,"constant",SymbolType.Constant,1);

        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), new SymbolTable());


        verify(logger,times(1)).unique("The dictionary service was unreachable.\nThe rules that require the data from the dictionary service will be skipped.", LoggingLevel.ERROR);
    }



    @Test
    public void testInjectNewSymbolsMissingServiceProtocol(){
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

        ServiceController controller = getAuthenticatedServiceController("www.google.com");

        SymbolTable foundTable = new SymbolTable();
        TestUtilities.addSymbolInLines(foundTable,"constant",SymbolType.Constant,1);

        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), new SymbolTable());


        verify(logger,times(1))
                .unique("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\nThe rules that require the data from the dictionary service will be skipped.",LoggingLevel.ERROR);
    }


    @Test
    public void testInjectNewSymbolsNotDefinedInAnyLine(){
        ServiceTestUtilities.setDbFacadeHandler(ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{}"));

        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

        ServiceController controller = getAuthenticatedServiceController("https://www.google.com");

        SymbolTable foundTable = new SymbolTable();
        foundTable.addSymbol(new Symbol("constant",SymbolType.Constant));

        controller.injectNewSymbols("module",new ArrayList<>(foundTable.getSymbols()), new SymbolTable());


        verify(logger,never()).info(anyString());
        verify(DBFacade.handler,never()).injectSymbols(any(), any(), any());
    }

}

