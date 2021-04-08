package es.uva.locomotion.service;

import es.uva.locomotion.testutilities.ServiceTestUtilities;
import es.uva.locomotion.utilities.exceptions.ConnectionFailedException;
import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.mockito.Mockito.*;


public class TestServiceControllerUnit {

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
    public void testGetUnitsControllerMakesCorrectCall() {
        ServiceConnectionHandler mockHandler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[]");
        DBFacade.handler = mockHandler;

        ServiceController controller = getAuthenticatedServiceController("http://localhost");

        controller.getUnitsFromDb();

        verify(mockHandler, Mockito.times(1)).sendUnitsRequestToDictionaryService(any(), any());

    }

    @Test
    public void testGetUnitsDictionaryInvalidServiceUrlMissingProtocol() {
        ServiceController controller = getAuthenticatedServiceController("www.falseservice.com");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

         Set<String> actualValue = controller.getUnitsFromDb();

        Assert.assertNull(actualValue);
        verify(logger).unique("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n" +
                "Units check can't be done without the units from the dictionary.", LoggingLevel.ERROR);
    }

    @Test
    public void testGetUnitsDictionaryInvalidServiceUrlInvalidFormat() {
        ServiceController controller = getAuthenticatedServiceController("http://\\$*^");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

         Set<String> actualValue = controller.getUnitsFromDb();


        Assert.assertNull(actualValue);
        verify(logger).unique("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n" +
                "Units check can't be done without the units from the dictionary.", LoggingLevel.ERROR);
    }

    @Test
    public void testGetUnitsDictionaryInvalidServiceUrlInvalidProtocol() {
        ServiceController controller = getAuthenticatedServiceController("smtp://address:password@coolmail.com");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

         Set<String> actualValue = controller.getUnitsFromDb();

        Assert.assertNull(actualValue);
        verify(logger).unique("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n" +
                "Units check can't be done without the units from the dictionary.", LoggingLevel.ERROR);
    }

    @Test
    public void testGetUnitsDictionaryMissingServiceEmptyUrl() {
        ServiceController controller = getAuthenticatedServiceController("");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

         Set<String> actualValue = controller.getUnitsFromDb();

        Assert.assertNull(actualValue);
        verify(logger).unique("Missing dictionary service parameter.\n" +
                "Units check can't be done without the units from the dictionary.", LoggingLevel.INFO);
    }

    @Test
    public void testGetUnitsDictionaryMissingServiceNullUrl() {
        ServiceController controller = getAuthenticatedServiceController(null);
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

         Set<String> actualValue = controller.getUnitsFromDb();

        Assert.assertNull(actualValue);
        verify(logger).unique("Missing dictionary service parameter.\n" +
                "Units check can't be done without the units from the dictionary.", LoggingLevel.INFO);
    }

    @Test
    public void testGetUnitsDictionaryConnectionFailed() {
        ServiceConnectionHandler handler = mock(ServiceConnectionHandler.class);
        when(handler.sendUnitsRequestToDictionaryService(any(), any())).thenThrow(new ConnectionFailedException(null));
        DBFacade.handler = handler;

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

         Set<String> actualValue = controller.getUnitsFromDb();

        Assert.assertNull(actualValue);
        verify(logger).unique("The dictionary service was unreachable.\n" +
                "Units check can't be done without the units from the dictionary.", LoggingLevel.ERROR);

    }

    @Test
    public void testGetUnitsDictionaryInvalidFormatNotAnArray() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{\"name\":\"Juan\"}");
        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

         Set<String> actualValue = controller.getUnitsFromDb();
        Assert.assertNull(actualValue);
        verify(logger).error("The response of the dictionary service wasn't valid. Expected an array. Dictionary response: {\"name\":\"Juan\"}.\n" +
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                "Units check can't be done without the units from the dictionary.");

    }


    @Test
    public void testGetUnitsDictionaryInvalidFormatMissingUnitKey() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[{\"notTheKey\":\"foo\"}]");

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

         Set<String> actualValue = controller.getUnitsFromDb();

        Assert.assertNull(actualValue);
        verify(logger).error("The response of the dictionary service wasn't valid. Missing 'unit' field from a unit. Dictionary response: [{\"notTheKey\":\"foo\"}].\n" +
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                "Units check can't be done without the units from the dictionary.");
    }


    @Test
    public void testGetUnitsConsecutiveDifferentErrorsAreLogged() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{\"name\":\"Juan\"}");

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.LOG = logger;

        controller.getUnitsFromDb();
        controller.getUnitsFromDb();
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[{\"notTheKey\":\"foo\"}]");
        controller.getUnitsFromDb();
        controller.getUnitsFromDb();


        verify(logger,atLeastOnce()).error("The response of the dictionary service wasn't valid. Expected an array. Dictionary response: {\"name\":\"Juan\"}.\n" +
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                "Units check can't be done without the units from the dictionary.");
        verify(logger,atLeastOnce()).error("The response of the dictionary service wasn't valid. Missing 'unit' field from a unit. Dictionary response: [{\"notTheKey\":\"foo\"}].\n" +
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                "Units check can't be done without the units from the dictionary.");

    }
}

