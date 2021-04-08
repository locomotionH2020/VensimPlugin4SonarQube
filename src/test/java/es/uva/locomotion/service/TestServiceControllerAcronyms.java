package es.uva.locomotion.service;

import es.uva.locomotion.model.AcronymsList;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.testutilities.ServiceTestUtilities;
import es.uva.locomotion.utilities.exceptions.ConnectionFailedException;
import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;


public class TestServiceControllerAcronyms {

    @After
    public void resetDbFacade() {
        DBFacade.handler = new ServiceConnectionHandler();
    }

    public ServiceController getAuthenticatedServiceController(String dictionaryService) {
        ServiceController controller = spy(new ServiceController(dictionaryService));
        doReturn(true).when(controller).isAuthenticated();
        doNothing().when(controller).authenticate(anyString(), anyString());
        return controller;

    }

    @Test
    public void testGetAcronymsController() {
        ServiceConnectionHandler mockHandler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[]");
        DBFacade.handler = mockHandler;

        ServiceController controller = getAuthenticatedServiceController("https://localhost");

        controller.getAcronymsFromDb();

        verify(mockHandler, Mockito.times(1)).sendAcronymsRequestToDictionaryService(any(), any());
    }


    @Test
    public void testGetAcronymsDictionaryInvalidServiceUrlMissingProtocol() {
        ServiceController controller = getAuthenticatedServiceController("www.myextremelyepicservice.com");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        AcronymsList actualValue = controller.getAcronymsFromDb();

        Assert.assertEquals(new AcronymsList(), actualValue);
        verify(logger).unique("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n" +
                "Variable name check may cause false positives without acronyms.", LoggingLevel.ERROR);
    }

    @Test
    public void testGetAcronymsDictionaryInvalidServiceUrlInvalidFormat() {
        ServiceController controller = getAuthenticatedServiceController("http://\\$*^");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        AcronymsList actualValue = controller.getAcronymsFromDb();


        Assert.assertEquals(new AcronymsList(), actualValue);
        verify(logger).unique("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n" +
                "Variable name check may cause false positives without acronyms.", LoggingLevel.ERROR);
    }

    @Test
    public void testGetAcronymsDictionaryInvalidServiceUrlInvalidProtocol() {
        ServiceController controller = getAuthenticatedServiceController("smtp://address:password@coolmail.com");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        AcronymsList actualValue = controller.getAcronymsFromDb();

        Assert.assertEquals(new AcronymsList(), actualValue);
        verify(logger).unique("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n" +
                "Variable name check may cause false positives without acronyms.", LoggingLevel.ERROR);
    }

    @Test
    public void testGetAcronymsDictionaryMissingServiceEmptyUrl() {
        ServiceController controller = getAuthenticatedServiceController("");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        AcronymsList actualValue = controller.getAcronymsFromDb();

        Assert.assertEquals(new AcronymsList(), actualValue);
        verify(logger).unique("Missing dictionary service parameter.\n" +
                "Variable name check may cause false positives without acronyms.", LoggingLevel.INFO);
    }

    @Test
    public void testGetAcronymsDictionaryMissingServiceNullUrl() {
        ServiceController controller = getAuthenticatedServiceController(null);
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        AcronymsList actualValue = controller.getAcronymsFromDb();

        Assert.assertEquals(new AcronymsList(), actualValue);
        verify(logger).unique("Missing dictionary service parameter.\n" +
                "Variable name check may cause false positives without acronyms.", LoggingLevel.INFO);
    }

    @Test
    public void testGetAcronymsDictionaryConnectionFailed() {
        ServiceConnectionHandler handler = mock(ServiceConnectionHandler.class);
        when(handler.sendAcronymsRequestToDictionaryService(any(), any())).thenThrow(new ConnectionFailedException(null));
        DBFacade.handler = handler;

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        AcronymsList actualValue = controller.getAcronymsFromDb();

        Assert.assertEquals(new AcronymsList(), actualValue);
        verify(logger).unique("The dictionary service was unreachable.\n" +
                "Variable name check may cause false positives without acronyms.", LoggingLevel.ERROR);

    }

    @Test
    public void testGetAcronymsDictionaryInvalidFormatLiteralList() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[1,2,3]");
        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        AcronymsList actualValue = controller.getAcronymsFromDb();
        Assert.assertEquals(new AcronymsList(), actualValue);
        verify(logger).error("The response of the dictionary service wasn't valid. Expected object inside array.\n" +
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \nVariable name check may cause false positives without acronyms.");

    }


    @Test
    public void testGetAcronymsDictionaryInvalidFormatNotAnArray() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{\"symbol\":\"foo\"}");

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        AcronymsList actualValue = controller.getAcronymsFromDb();

        Assert.assertEquals(new AcronymsList(), actualValue);
        verify(logger).error("The response of the dictionary service wasn't valid. Expected an array.\n" +
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                "Variable name check may cause false positives without acronyms.");
    }


    @Test
    public void testGetAcronymsDictionaryInvalidFormatMissingName() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[{\"id\":\"1\", \"definition\": \"this is a definition\"}]");

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        AcronymsList actualValue = controller.getAcronymsFromDb();

        Assert.assertEquals(new AcronymsList(), actualValue);
        verify(logger).error("The response of the dictionary service wasn't valid. Missing 'name' field.\n" +
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                "Variable name check may cause false positives without acronyms.");
    }


    @Test
    public void testGetAcronymsConsecutiveDifferentErrorsAreLogged() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{\"randomKey\":\"foo\"}");

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        controller.getAcronymsFromDb();
        controller.getAcronymsFromDb();
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[{\"id\":\"1\", \"definition\": \"this is a definition\"}]");
        controller.getAcronymsFromDb();
        controller.getAcronymsFromDb();


        verify(logger, atLeastOnce()).error("The response of the dictionary service wasn't valid. Missing 'name' field.\n" +
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                "Variable name check may cause false positives without acronyms.");
        verify(logger, atLeastOnce()).error("The response of the dictionary service wasn't valid. Expected an array.\n" +
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                "Variable name check may cause false positives without acronyms.");
    }


}

