package es.uva.medeas;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import es.uva.medeas.utilities.DbServiceHandler;
import es.uva.medeas.utilities.Utilities;
import es.uva.medeas.utilities.exceptions.ConnectionFailedException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import static  org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.sonar.api.utils.log.Logger;

import java.util.*;
import java.util.stream.Collectors;


public class TestServiceController {

    @After
    public void  resetDbFacade(){
        Utilities.setDbFacadeHandler(new DbServiceHandler());
    }

    @Test
    public void testControllerIgnoresFunctions(){
        ServiceController controller = new ServiceController("https://localhost");

        DbServiceHandler mockHandler = Utilities.getMockDbServiceHandlerThatReturns("[]");

        Utilities.setDbFacadeHandler(mockHandler);

        List<Symbol> symbols = Arrays.asList(new Symbol("func1", SymbolType.FUNCTION),
                new Symbol("var", SymbolType.VARIABLE),
                new Symbol("func2", SymbolType.FUNCTION),
                new Symbol("const", SymbolType.CONSTANT),
                new Symbol("lookup", SymbolType.LOOKUP),
                new Symbol("subscript", SymbolType.SUBSCRIPT_NAME),
                new Symbol("sValue", SymbolType.SUBSCRIPT_VALUE),
                new Symbol("realityCheck", SymbolType.REALITY_CHECK),
                new Symbol("func3", SymbolType.FUNCTION)
        );
        controller.getSymbolsFromDb(symbols);

        verify(mockHandler,Mockito.times(1)).sendRequestToService(any(), argThat(arg->{
            Set<String> actualSet = new HashSet<>(arg);
            Set<String> expectedSet =  new HashSet<>(Arrays.asList("var", "const", "lookup","subscript","sValue","realityCheck"));
            return actualSet.equals(expectedSet);

        }));
    }

    @Test
    public void testControllerIgnoresDefaultSymbols(){
        ServiceController controller = new ServiceController("http://localhost");

        List<Symbol> symbols = Arrays.asList("FINAL TIME", "INITIAL TIME", "SAVEPER", "TIME STEP").stream().map(Symbol::new).collect(Collectors.toList());
        DbServiceHandler mockHandler = Utilities.getMockDbServiceHandlerThatReturns("[]");
        Utilities.setDbFacadeHandler(mockHandler);

        controller.getSymbolsFromDb(symbols);

        verify(mockHandler,Mockito.times(1)).sendRequestToService(any(), argThat(List::isEmpty));

    }

    @Test
    public void testDictionaryInvalidServiceUrlMissingProtocol(){
        ServiceController controller = new ServiceController("www.myextremelyepicservice.com");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).error("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }

    @Test
    public void testDictionaryInvalidServiceUrlInvalidFormat(){
        ServiceController controller = new ServiceController("http://\\$*^");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).error("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }

    @Test
    public void testDictionaryInvalidServiceUrlInvalidProtocol(){
        ServiceController controller = new ServiceController("smtp://address:password@coolmail.com");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).error("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }

    @Test
    public void testDictionaryMissingServiceEmptyUrl(){
        ServiceController controller = new ServiceController("");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).info("Missing dictionary service parameter.\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }

    @Test
    public void testDictionaryMissingServiceNullUrl(){
        ServiceController controller = new ServiceController(null);
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).info("Missing dictionary service parameter.\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }

    @Test
    public void testDictionaryConnectionFailed(){
        DbServiceHandler handler = mock(DbServiceHandler.class);
        when(handler.sendRequestToService(any(),anyList())).thenThrow(new ConnectionFailedException(null));
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
    public void testDictionaryInvalidFormatLiteralList(){
        Utilities.setDbFacadeHandler(Utilities.getMockDbServiceHandlerThatReturns("[1,2,3]"));

        ServiceController controller = new ServiceController("http://localhost");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).error(  "The response of the dictionary service wasn't valid. Expected an array of symbols.\n"+
        "Actual response: [1,2,3]\nThe rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");

    }


    @Test
    public void testDictionaryInvalidFormatNotAList(){
        Utilities.setDbFacadeHandler(Utilities.getMockDbServiceHandlerThatReturns("{\"symbol\":\"foo\"}"));

        ServiceController controller = new ServiceController("http://localhost");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).error("The response of the dictionary service wasn't valid. Expected a JSON array of symbols.\n" +
                "Actual response:{\"symbol\":\"foo\"}\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }


    @Test
    public void testDictionaryInvalidFormatMissingKey(){
        Utilities.setDbFacadeHandler(Utilities.getMockDbServiceHandlerThatReturns("{\"randomKey\":\"foo\"}"));

        ServiceController controller = new ServiceController("http://localhost");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        SymbolTable actualValue = controller.getSymbolsFromDb(new ArrayList<>());

        Assert.assertNull(actualValue);
        verify(logger).error("The response of the dictionary service wasn't valid. Expected a JSON array of symbols.\n"+
                "Actual response:{\"randomKey\":\"foo\"}\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }


    @Test
    public void testConsecutiveSameErrorsAreOnlyLoggedOnce(){
        Utilities.setDbFacadeHandler(Utilities.getMockDbServiceHandlerThatReturns("{\"randomKey\":\"foo\"}"));

        ServiceController controller = new ServiceController("http://localhost");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        controller.getSymbolsFromDb(new ArrayList<>());
        controller.getSymbolsFromDb(new ArrayList<>());
        controller.getSymbolsFromDb(new ArrayList<>());
        controller.getSymbolsFromDb(new ArrayList<>());


        verify(logger,times(1)).error("The response of the dictionary service wasn't valid. Expected a JSON array of symbols.\n"+
                "Actual response:{\"randomKey\":\"foo\"}\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }

    @Test
    public void testConsecutiveDifferentErrorsAreLogged(){
        Utilities.setDbFacadeHandler(Utilities.getMockDbServiceHandlerThatReturns("{\"randomKey\":\"foo\"}"));

        ServiceController controller = new ServiceController("http://localhost");
        Logger logger = Mockito.mock(Logger.class);
        ServiceController.LOG = logger;

        controller.getSymbolsFromDb(new ArrayList<>());
        controller.getSymbolsFromDb(new ArrayList<>());
        Utilities.setDbFacadeHandler(Utilities.getMockDbServiceHandlerThatReturns("{\"symbol\":\"foo\"}"));
        controller.getSymbolsFromDb(new ArrayList<>());
        controller.getSymbolsFromDb(new ArrayList<>());


        verify(logger).error("The response of the dictionary service wasn't valid. Expected a JSON array of symbols.\n"+
                "Actual response:{\"randomKey\":\"foo\"}\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");


        verify(logger).error("The response of the dictionary service wasn't valid. Expected a JSON array of symbols.\n" +
                "Actual response:{\"symbol\":\"foo\"}\n"+
                "The rules that require the data from the dictionary service will be skipped. "+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }




}
