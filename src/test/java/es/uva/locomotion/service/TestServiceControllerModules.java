package es.uva.locomotion.service;

import es.uva.locomotion.model.Module;
import es.uva.locomotion.model.category.CategoryMap;
import es.uva.locomotion.testutilities.ServiceTestUtilities;
import es.uva.locomotion.utilities.exceptions.ConnectionFailedException;
import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;


public class TestServiceControllerModules {

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
    public void testGetModulesControllerMakesCorrectCall() {
        ServiceConnectionHandler mockHandler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[]");
        DBFacade.handler = mockHandler;

        ServiceController controller = getAuthenticatedServiceController("http://localhost");

        controller.getModulesFromDb();

        verify(mockHandler, Mockito.times(1)).sendModuleRequestToDictionaryService(any(), any());

    }

    @Test
    public void testGetModulesDictionaryInvalidServiceUrlMissingProtocol() {
        ServiceController controller = getAuthenticatedServiceController("www.falseservice.com");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        Set<Module> actualValue = controller.getModulesFromDb();

        Assert.assertEquals(Collections.emptySet(), actualValue);
        verify(logger).unique("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n" +
                "Injection of new modules can't be done without the modules from the dictionary.", LoggingLevel.ERROR);
    }

    @Test
    public void testGetModulesDictionaryInvalidServiceUrlInvalidFormat() {
        ServiceController controller = getAuthenticatedServiceController("http://\\$*^");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        Set<Module> actualValue = controller.getModulesFromDb();


        Assert.assertEquals(Collections.emptySet(), actualValue);
        verify(logger).unique("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n" +
                "Injection of new modules can't be done without the modules from the dictionary.", LoggingLevel.ERROR);
    }

    @Test
    public void testGetModulesDictionaryInvalidServiceUrlInvalidProtocol() {
        ServiceController controller = getAuthenticatedServiceController("smtp://address:password@coolmail.com");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        Set<Module> actualValue = controller.getModulesFromDb();

        Assert.assertEquals(Collections.emptySet(), actualValue);
        verify(logger).unique("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n" +
                "Injection of new modules can't be done without the modules from the dictionary.", LoggingLevel.ERROR);
    }

    @Test
    public void testGetModulesDictionaryMissingServiceEmptyUrl() {
        ServiceController controller = getAuthenticatedServiceController("");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

         Set<Module> actualValue = controller.getModulesFromDb();

        Assert.assertEquals(Collections.emptySet(), actualValue);
        verify(logger).unique("Missing dictionary service parameter.\n" +
                "Injection of new modules can't be done without the modules from the dictionary.", LoggingLevel.INFO);
    }

    @Test
    public void testGetModulesDictionaryMissingServiceNullUrl() {
        ServiceController controller = getAuthenticatedServiceController(null);
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

         Set<Module> actualValue = controller.getModulesFromDb();

        Assert.assertEquals(Collections.emptySet(), actualValue);
        verify(logger).unique("Missing dictionary service parameter.\n" +
                "Injection of new modules can't be done without the modules from the dictionary.", LoggingLevel.INFO);
    }

    @Test
    public void testGetModulesDictionaryConnectionFailed() {
        ServiceConnectionHandler handler = mock(ServiceConnectionHandler.class);
        when(handler.sendModuleRequestToDictionaryService(any(), any())).thenThrow(new ConnectionFailedException(null));
        DBFacade.handler = handler;

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

         Set<Module> actualValue = controller.getModulesFromDb();

        Assert.assertEquals(Collections.emptySet(), actualValue);
        verify(logger).unique("The dictionary service was unreachable.\n" +
                "Injection of new modules can't be done without the modules from the dictionary.", LoggingLevel.ERROR);

    }

    @Test
    public void testGetModulesDictionaryInvalidFormatNotAnObject() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[{\"name\":\"Juan\"}]");
        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

         Set<Module> actualValue = controller.getModulesFromDb();
        Assert.assertEquals(Collections.emptySet(), actualValue);
        verify(logger).error("The response of the dictionary service wasn't valid. Expected an object. Dictionary response: [{\"name\":\"Juan\"}].\n" +
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                "Injection of new modules can't be done without the modules from the dictionary.");

    }


    @Test
    public void testGetModulesDictionaryInvalidFormatMissingModuleKey() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{\"notTheKey\":\"foo\"}");

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

         Set<Module> actualValue = controller.getModulesFromDb();

        Assert.assertEquals(Collections.emptySet(), actualValue);
        verify(logger).error("The response of the dictionary service wasn't valid. \"modules\" key not found in object. Dictionary response: {\"notTheKey\":\"foo\"}.\n" +
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                "Injection of new modules can't be done without the modules from the dictionary.");
    }


    @Test
    public void testGetModulesConsecutiveDifferentErrorsAreLogged() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{\"name\":\"Juan\"}");

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        controller.getModulesFromDb();
        controller.getModulesFromDb();
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[{\"notTheKey\":\"foo\"}]");
        controller.getModulesFromDb();
        controller.getModulesFromDb();


        verify(logger,atLeastOnce()).error("The response of the dictionary service wasn't valid. \"modules\" key not found in object. Dictionary response: {\"name\":\"Juan\"}.\n" +
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                "Injection of new modules can't be done without the modules from the dictionary.");
        verify(logger,atLeastOnce()).error("The response of the dictionary service wasn't valid. Expected an object. Dictionary response: [{\"notTheKey\":\"foo\"}].\n" +
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                "Injection of new modules can't be done without the modules from the dictionary.");

    }
    

    @Test
    public void testInjectNewModulesNullDbModulesList() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{}");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;


        Set<Module> list = new HashSet<>();

        ServiceController controller = getAuthenticatedServiceController("https://something");
        controller.injectNewModules(list, null);


        verify(logger, never()).info(anyString());
        verify(DBFacade.handler, never()).injectModules(any(), any(), any());
    }


    @Test
    public void testInjectNewModulesOnlyIncludesNewAndValidModules() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{}");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

         Set<Module> foundList = new HashSet<>();
        foundList.add(new Module("module_correct_1"));
        Module m = new Module("module_not_correct 2");
        m.setAsInvalid("Invalid name");
        foundList.add(m);
        Module m2 = new Module("module_not_correct_3$");
        m2.setAsInvalid("Invalid name");
        foundList.add(m2);
        foundList.add(new Module("module4"));
        foundList.add(new Module("module5_in_db"));
        Module m3 = new Module("module__not_correct_6");
        m3.setAsInvalid("Invalid name");
        foundList.add(m3);


         Set<Module> dbList = new HashSet<>();
        dbList.add(new Module("module5_in_db"));

        ServiceController controller = getAuthenticatedServiceController("https://something");
        controller.injectNewModules(new HashSet<>(foundList), dbList);


        verify(logger, times(1)).info("Injected modules: [module4, module_correct_1]");
    }
    @Test
    public void testInjectNewModulesNoneInjected() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{}");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        Set<Module> foundList = new HashSet<>();
        Module m = new Module("module_not_correct 2");
        m.setAsInvalid("Invalid name");
        foundList.add(m);
        Module m2 = new Module("module_not_correct_3$");
        m2.setAsInvalid("Invalid name");
        foundList.add(m2);
        foundList.add(new Module("module5_in_db"));
        Module m3 = new Module("module__not_correct_6");
        m3.setAsInvalid("Invalid name");
        foundList.add(m3);

         Set<Module> dbList = new HashSet<>();
        dbList.add(new Module("module5_in_db"));

        ServiceController controller = getAuthenticatedServiceController("https://something");
        controller.injectNewModules(new HashSet<>(foundList), dbList);


        verify(logger, never()).info(any());
    }
    @Test
    public void testInjectNewModulesEmptyService() {
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        ServiceController controller = getAuthenticatedServiceController("");

         Set<Module> foundList = new HashSet<>();
        foundList.add(new Module("module_correct_1"));

        controller.injectNewModules(new HashSet<>(foundList), new HashSet<>());


        verify(logger, times(1)).unique("Missing dictionary service parameter.\nInjection was not succesful."
                , LoggingLevel.INFO);
    }

    @Test
    public void testInjectNewModulesNullService() {
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        ServiceController controller = getAuthenticatedServiceController(null);

         Set<Module> foundList = new HashSet<>();
        foundList.add(new Module("module_correct_1"));

        controller.injectNewModules(new HashSet<>(foundList), new HashSet<>());


        verify(logger, times(1)).unique("Missing dictionary service parameter.\nInjection was not succesful."
                , LoggingLevel.INFO);
    }

    @Test
    public void testInjectNewModulesConnectionFailed() {
        ServiceConnectionHandler handler = mock(ServiceConnectionHandler.class);
        when(handler.injectModules(any(), any(), any())).thenThrow(new ConnectionFailedException(null));
        DBFacade.handler = handler;

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

         Set<Module> foundList = new HashSet<>();
        foundList.add(new Module("module_correct_1"));

        controller.injectNewModules(new HashSet<>(foundList), new HashSet<>());


        verify(logger, times(1)).unique("The dictionary service was unreachable.\nInjection was not succesful.", LoggingLevel.ERROR);
    }


    @Test
    public void testInjectNewModulesMissingServiceProtocol() {
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        ServiceController controller = getAuthenticatedServiceController("www.google.com");

         Set<Module> foundList = new HashSet<>();
        foundList.add(new Module("module_correct_1"));

        controller.injectNewModules(new HashSet<>(foundList), new HashSet<>());


        verify(logger, times(1))
                .unique("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\nInjection was not succesful.", LoggingLevel.ERROR);
    }


}

