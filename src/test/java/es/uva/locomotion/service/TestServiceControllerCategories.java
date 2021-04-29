package es.uva.locomotion.service;

import es.uva.locomotion.model.AcronymsList;
import es.uva.locomotion.model.category.Category;
import es.uva.locomotion.model.category.CategoryMap;
import es.uva.locomotion.testutilities.ServiceTestUtilities;
import es.uva.locomotion.utilities.exceptions.ConnectionFailedException;
import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.mockito.Mockito.*;


public class TestServiceControllerCategories {

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
    public void testGetCategoriesControllerMakesCorrectCall() {
        ServiceConnectionHandler mockHandler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[]");
        DBFacade.handler = mockHandler;

        ServiceController controller = getAuthenticatedServiceController("http://localhost");

        controller.getCategoriesFromDb();

        verify(mockHandler, Mockito.times(1)).sendCategoriesRequestToDictionaryService(any(), any());

    }

    @Test
    public void testGetCategoriesDictionaryInvalidServiceUrlMissingProtocol() {
        ServiceController controller = getAuthenticatedServiceController("www.falseservice.com");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        CategoryMap actualValue = controller.getCategoriesFromDb();

        Assert.assertNull(actualValue);
        verify(logger).unique("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n" +
                "Injection of new categories can't be done without the categories from the dictionary.", LoggingLevel.ERROR);
    }

    @Test
    public void testGetCategoriesDictionaryInvalidServiceUrlInvalidFormat() {
        ServiceController controller = getAuthenticatedServiceController("http://\\$*^");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        CategoryMap actualValue = controller.getCategoriesFromDb();


        Assert.assertNull(actualValue);
        verify(logger).unique("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n" +
                "Injection of new categories can't be done without the categories from the dictionary.", LoggingLevel.ERROR);
    }

    @Test
    public void testGetCategoriesDictionaryInvalidServiceUrlInvalidProtocol() {
        ServiceController controller = getAuthenticatedServiceController("smtp://address:password@coolmail.com");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        CategoryMap actualValue = controller.getCategoriesFromDb();

        Assert.assertNull(actualValue);
        verify(logger).unique("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n" +
                "Injection of new categories can't be done without the categories from the dictionary.", LoggingLevel.ERROR);
    }

    @Test
    public void testGetCategoriesDictionaryMissingServiceEmptyUrl() {
        ServiceController controller = getAuthenticatedServiceController("");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        CategoryMap actualValue = controller.getCategoriesFromDb();

        Assert.assertNull(actualValue);
        verify(logger).unique("Missing dictionary service parameter.\n" +
                "Injection of new categories can't be done without the categories from the dictionary.", LoggingLevel.INFO);
    }

    @Test
    public void testGetCategoriesDictionaryMissingServiceNullUrl() {
        ServiceController controller = getAuthenticatedServiceController(null);
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        CategoryMap actualValue = controller.getCategoriesFromDb();

        Assert.assertNull(actualValue);
        verify(logger).unique("Missing dictionary service parameter.\n" +
                "Injection of new categories can't be done without the categories from the dictionary.", LoggingLevel.INFO);
    }

    @Test
    public void testGetCategoriesDictionaryConnectionFailed() {
        ServiceConnectionHandler handler = mock(ServiceConnectionHandler.class);
        when(handler.sendCategoriesRequestToDictionaryService(any(), any())).thenThrow(new ConnectionFailedException(null));
        DBFacade.handler = handler;

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        CategoryMap actualValue = controller.getCategoriesFromDb();

        Assert.assertNull(actualValue);
        verify(logger).unique("The dictionary service was unreachable.\n" +
                "Injection of new categories can't be done without the categories from the dictionary.", LoggingLevel.ERROR);

    }

    @Test
    public void testGetCategoriesDictionaryInvalidFormatNotAList() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{'name':'Juan'}");
        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        CategoryMap actualValue = controller.getCategoriesFromDb();
        Assert.assertNull(actualValue);
        verify(logger).error("The response of the dictionary service wasn't valid. Expected an array. Dictionary response: {'name':'Juan'}.\n" +
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                "Injection of new categories can't be done without the categories from the dictionary.");

    }


    @Test
    public void testGetCategoriesDictionaryInvalidFormatNotAnCorrectList() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[{\"category\":\"foo\"}]");

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        CategoryMap actualValue = controller.getCategoriesFromDb();

        Assert.assertNull(actualValue);
        verify(logger).error("The response of the dictionary service wasn't valid. Missing 'name' field from a category. Dictionary response: [{\"category\":\"foo\"}].\n" +
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                "Injection of new categories can't be done without the categories from the dictionary.");
    }


    @Test
    public void testGetCategoriesConsecutiveDifferentErrorsAreLogged() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{'name':'Juan'}");

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        controller.getCategoriesFromDb();
        controller.getCategoriesFromDb();
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[{\"category\":\"foo\"}]");
        controller.getCategoriesFromDb();
        controller.getCategoriesFromDb();


        verify(logger, atLeastOnce()).error("The response of the dictionary service wasn't valid. Expected an array. Dictionary response: {'name':'Juan'}.\n" +
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                "Injection of new categories can't be done without the categories from the dictionary.");

        verify(logger, atLeastOnce()).error("The response of the dictionary service wasn't valid. Missing 'name' field from a category. Dictionary response: [{\"category\":\"foo\"}].\n" +
                "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                "Injection of new categories can't be done without the categories from the dictionary.");
    }


    @Test
    public void testInjectNewCategoriesNullDbCategoryList() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{}");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;


        CategoryMap list = new CategoryMap();

        ServiceController controller = getAuthenticatedServiceController("https://something");
        controller.injectNewCategories(list.getCategoriesAndSubcategories(), null);


        verify(logger, never()).info(anyString());
        verify(DBFacade.handler, never()).injectCategories(any(), any(), any());
    }


    @Test
    public void testInjectNewCategoriesOnlyIncludesNewAndValidCategories() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{}");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        CategoryMap foundList = new CategoryMap();

        Category c1 = foundList.createOrSelectCategory("Category_1");
        Category sc1 = foundList.addSubcategoryTo(c1, "Subcategory_1");
        Category sc2 = foundList.addSubcategoryTo(c1, "Subcategory_with_bad_name_2$");
        sc2.setAsInvalid("Bad name");
        Category c2 = foundList.createOrSelectCategory("Category_empty_2");

        Category c3 = foundList.createOrSelectCategory("Category_with_bad_name 3");
        Category sc3 = foundList.addSubcategoryTo(c3, "Subcategory_3_super_with_bad_name");
        c3.setAsInvalid("Bad name");
        sc3.setAsInvalid("Bad name");
        Category c4 = foundList.createOrSelectCategory("Category_4");
        Category sc4 = foundList.addSubcategoryTo(c4, "Subcategory_4");

        Category c5 = foundList.createOrSelectCategory("Category_already_in_db_5");
        Category sc5 = foundList.addSubcategoryTo(c5, "Subcategory_already_in_db_5");
        Category sc6 = foundList.addSubcategoryTo(c5, "Subcategory_6");

        CategoryMap dbList = new CategoryMap();
        Category dbc5 = dbList.createOrSelectCategory("Category_already_in_db_5");
        Category dbsc5 = dbList.addSubcategoryTo(dbc5, "Subcategory_already_in_db_5");

        ServiceController controller = getAuthenticatedServiceController("https://something");
        controller.injectNewCategories(foundList.getCategoriesAndSubcategories(), dbList.getCategoriesAndSubcategories());


        verify(logger, times(1)).info("Injected categories: [Category_1, Category_1.Subcategory_1, Category_4, Category_4.Subcategory_4, Category_already_in_db_5.Subcategory_6, Category_empty_2]");
    }

    @Test
    public void testInjectNewCategoriesNoneInjected() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("{}");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        CategoryMap foundList = new CategoryMap();

        Category c1 = foundList.createOrSelectCategory("Category_1");
        Category sc1 = foundList.addSubcategoryTo(c1, "Subcategory_1");
        Category sc2 = foundList.addSubcategoryTo(c1, "Subcategory_with_bad_name_2$");
        sc2.setAsInvalid("Bad name");
        CategoryMap dbList = new CategoryMap();

        Category dbc1 = dbList.createOrSelectCategory("Category_1");
        Category dbsc1 = dbList.addSubcategoryTo(dbc1, "Subcategory_1");

        ServiceController controller = getAuthenticatedServiceController("https://localhost");
        controller.injectNewCategories(foundList.getCategoriesAndSubcategories(), dbList.getCategoriesAndSubcategories());

        verify(logger, times(1)).info("No new categories to inject");
    }

    @Test
    public void testInjectNewCategoriesEmptyService() {
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        ServiceController controller = getAuthenticatedServiceController("");

        CategoryMap foundList = new CategoryMap();
        Category c1 = foundList.createOrSelectCategory("Category_1");

        controller.injectNewCategories(foundList.getCategoriesAndSubcategories(), new ArrayList<>());


        verify(logger, times(1)).unique("Missing dictionary service parameter.\nInjection of new categories can't be done without the categories from the dictionary."
                , LoggingLevel.INFO);
    }

    @Test
    public void testInjectNewCategoriesNullService() {
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        ServiceController controller = getAuthenticatedServiceController(null);

        CategoryMap foundList = new CategoryMap();
        Category c1 = foundList.createOrSelectCategory("Category_1");
        controller.injectNewCategories(foundList.getCategoriesAndSubcategories(), new ArrayList<>());


        verify(logger, times(1)).unique("Missing dictionary service parameter.\nInjection of new categories can't be done without the categories from the dictionary."
                , LoggingLevel.INFO);
    }

    @Test
    public void testInjectNewCategoriesConnectionFailed() {
        ServiceConnectionHandler handler = mock(ServiceConnectionHandler.class);
        when(handler.injectCategories(any(), any(), any())).thenThrow(new ConnectionFailedException(null));
        DBFacade.handler = handler;

        ServiceController controller = getAuthenticatedServiceController("http://localhost");
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        CategoryMap foundList = new CategoryMap();
        Category c1 = foundList.createOrSelectCategory("Category_1");

        controller.injectNewCategories(foundList.getCategoriesAndSubcategories(), new ArrayList<>());


        verify(logger, times(1)).unique("The dictionary service was unreachable.\nInjection of new categories can't be done without the categories from the dictionary.", LoggingLevel.ERROR);
    }


    @Test
    public void testInjectNewCategoriesMissingServiceProtocol() {
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        ServiceController.logger = logger;

        ServiceController controller = getAuthenticatedServiceController("www.google.com");

        CategoryMap foundList = new CategoryMap();
        Category c1 = foundList.createOrSelectCategory("Category_1");

        controller.injectNewCategories(foundList.getCategoriesAndSubcategories(), new ArrayList<>());


        verify(logger, times(1))
                .unique("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\nInjection of new categories can't be done without the categories from the dictionary.", LoggingLevel.ERROR);
    }


}

