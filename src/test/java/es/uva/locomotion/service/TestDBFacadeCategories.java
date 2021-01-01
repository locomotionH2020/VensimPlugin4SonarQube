package es.uva.locomotion.service;

import es.uva.locomotion.model.Category;
import es.uva.locomotion.model.CategoryMap;
import es.uva.locomotion.testutilities.ServiceTestUtilities;
import es.uva.locomotion.utilities.exceptions.EmptyServiceException;
import es.uva.locomotion.utilities.exceptions.InvalidServiceUrlException;
import es.uva.locomotion.utilities.exceptions.ServiceResponseFormatNotValid;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

public class TestDBFacadeCategories {

    @After
    public void resetDbServiceHandler() {
        DBFacade.handler = new ServiceConnectionHandler(); // Makes the tests independent
    }

    @Test
    public void testGetCategoriesParsedJson() {
        CategoryMap dbTable = new CategoryMap();

        Category category1 = new Category("category1");
        Category category2 = new Category("category2");
        Category subcategory1 = new Category("subcategory1");
        Category subcategory2 = new Category("subcategory2");
        category2.addSubcategory(subcategory1);
        category2.addSubcategory(subcategory2);

        dbTable.add(category1);
        dbTable.add(category2);

        String jsonDbTable = "[{\"name\":\"category1\",\"level\":0,\"super_category\":null}," +
                "{\"name\":\"category2\",\"level\":0,\"super_category\":null}," +
                "{\"name\":\"subcategory1\",\"level\":1,\"super_category\":\"category2\"}," +
                "{\"name\":\"subcategory2\",\"level\":1,\"super_category\":\"category2\"}]";

        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        CategoryMap obtainedTable = DBFacade.getExistingCategoriesFromDB("", "token");
        assertEquals(dbTable, obtainedTable);
    }

    @Test
    public void testGetCategoriesEmptyResponse() {
        String jsonDbTable = "[]";


        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        CategoryMap obtainedTable = DBFacade.getExistingCategoriesFromDB("", "token");

        assertEquals(new CategoryMap(), obtainedTable);

    }

    @Test
    public void testGetCategoriesNotAnArray() {

        String jsonDbTable = "{\"name\":\"category2\",\"level\":0,\"super_category\":null}";
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingCategoriesFromDB("", "token"));
        assertEquals("Expected an array.", ex.getMessage());
        assertEquals(jsonDbTable, ex.getServiceResponse());
    }

    @Test
    public void testGetCategoriesArrayFormatError() {

        String jsonDbTable = "[\"category\"]";
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingCategoriesFromDB("", "token"));
        assertEquals("Recived '\"category\"' that is not a category json object.", ex.getMessage());
        assertEquals(jsonDbTable, ex.getServiceResponse());
    }

    @Test
    public void testGetCategoriesNameMissing() {

        String jsonDbTable = "[{\"level\":0,\"super_category\":null}]";
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingCategoriesFromDB("", "token"));
        assertEquals("Missing 'name' field from a category.", ex.getMessage());
        assertEquals(jsonDbTable, ex.getServiceResponse());
    }

    @Test
    public void testGetCategoriesLevelMissing() {

        String jsonDbTable = "[{\"name\":\"category\",\"super_category\":null}]";
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingCategoriesFromDB("", "token"));
        assertEquals("Missing 'level' field in category 'category'.", ex.getMessage());
        assertEquals(jsonDbTable, ex.getServiceResponse());
    }

    @Test
    public void testGetCategoriesSubcategorySuperCategoryMissing() {

        String jsonDbTable = "[{\"name\":\"category\",\"level\":1}]";
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingCategoriesFromDB("", "token"));
        assertEquals("Missing 'super_category' field in subcategory 'category'.", ex.getMessage());
        assertEquals(jsonDbTable, ex.getServiceResponse());
    }

    @Test
    public void testGetCategoriesNameNull() {

        String jsonDbTable = "[{\"name\":null,\"level\":0,\"super_category\":null}]";
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingCategoriesFromDB("", "token"));
        assertEquals("Missing 'name' field from a category.", ex.getMessage());
        assertEquals(jsonDbTable, ex.getServiceResponse());
    }

    @Test
    public void testGetCategoriesLevelNull() {

        String jsonDbTable = "[{\"name\":\"category\",\"level\":null,\"super_category\":null}]";
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingCategoriesFromDB("", "token"));
        assertEquals("Missing 'level' field in category 'category'.", ex.getMessage());
        assertEquals(jsonDbTable, ex.getServiceResponse());
    }

    @Test
    public void testGetCategoriesSuperCategoryWithFather() {

        String jsonDbTable = "[{\"name\":\"category\",\"level\":0,\"super_category\":null}," +
                "{\"name\":\"category2\",\"level\":0,\"super_category\":\"category\"}]";
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingCategoriesFromDB("", "token"));
        assertEquals("'category2' can not have a super category.", ex.getMessage());
        assertEquals(jsonDbTable, ex.getServiceResponse());
    }

    @Test
    public void testGetCategoriesSubCategoryWithoutFather() {

        String jsonDbTable = "[{\"name\":\"subcategory\",\"level\":1,\"super_category\":null}]";
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingCategoriesFromDB("", "token"));
        assertEquals("Missing 'super_category' field in subcategory 'subcategory'.", ex.getMessage());
        assertEquals(jsonDbTable, ex.getServiceResponse());
    }

    @Test
    public void testGetCategoriesSubCategoryWithMissingFather() {

        String jsonDbTable = "[{\"name\":\"subcategory\",\"level\":1,\"super_category\":\"random_category\"}]";
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingCategoriesFromDB("", "token"));
        assertEquals("'subcategory' father's 'random_category' does not exists or it is a subcategory.", ex.getMessage());
        assertEquals(jsonDbTable, ex.getServiceResponse());
    }

    @Test
    public void testGetCategoriesSubCategoryWithSubcategoryAsSuper() {

        String jsonDbTable = "[{\"name\":\"category\",\"level\":0,\"super_category\":null}," +
                "{\"name\":\"subcategory\",\"level\":1,\"super_category\":\"category\"}," +
                "{\"name\":\"subsubcategory\",\"level\":1,\"super_category\":\"subcategory\"}]";
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingCategoriesFromDB("", "token"));
        assertEquals("'subsubcategory' father's 'subcategory' does not exists or it is a subcategory.", ex.getMessage());
        assertEquals(jsonDbTable, ex.getServiceResponse());
    }

    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testGetCategoriesResponseIsntAJson() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("some text");

        DBFacade.getExistingCategoriesFromDB("http://localhost", "token");
    }


    @Test(expected = EmptyServiceException.class)
    public void testGetCategoriesEmptyServiceRaisesException() {
        DBFacade.getExistingCategoriesFromDB("", "token");
    }

    @Test(expected = EmptyServiceException.class)
    public void testGetCategoriesNullServiceRaisesException() {
        DBFacade.getExistingCategoriesFromDB(null, "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetCategoriesServiceWithAnotherProtocol() {
        DBFacade.getExistingCategoriesFromDB("ftp://somedomain/folder/file.txt", "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetCategoriesServiceWithInvalidProtocol() {
        DBFacade.getExistingCategoriesFromDB("\\some$randomtext", "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetCategoriesDomainWithoutProtocol() {
        DBFacade.getExistingCategoriesFromDB("www.google.com", "token");
    }


    @Test
    public void testGetCategoriesDomainWithoutWWW() throws IOException, InterruptedException {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(), any());
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockResponse.body()).thenReturn("[]");

        handler.client = mockClient;
        DBFacade.handler = handler;

        DBFacade.getExistingCategoriesFromDB("http://google.com", "token");
    }

    @Test
    public void testInjectCategory() {
        ServiceConnectionHandler handler = Mockito.mock(ServiceConnectionHandler.class);
        DBFacade.handler = handler;

        CategoryMap catmap = new CategoryMap();

        Category category = new Category("category1");
        catmap.add(category);
        JsonReader jsonReader = Json.createReader(new StringReader("[{\"name\":\"category1\",\"level\":0,\"super_category\":null}]"));
        JsonArray object = jsonReader.readArray();
        jsonReader.close();

        DBFacade.injectCategories("http://www.gaagle.com", catmap.getCategoriesAndSubcategories(), "token");
        verify(handler).injectCategories("http://www.gaagle.com", object, "token");
    }

    @Test
    public void testInjectSubCategory() {
        ServiceConnectionHandler handler = Mockito.mock(ServiceConnectionHandler.class);
        DBFacade.handler = handler;

        CategoryMap catmap = new CategoryMap();

        Category category = new Category("category1");
        Category subcategory = new Category("subcategory1");
        category.addSubcategory(subcategory);
        catmap.add(category);
        JsonReader jsonReader = Json.createReader(new StringReader("[{\"name\":\"category1\",\"level\":0,\"super_category\":null}," +
                                        "{\"name\":\"subcategory1\",\"level\":1,\"super_category\":\"category1\"}]"));
        JsonArray object = jsonReader.readArray();
        jsonReader.close();
        DBFacade.injectCategories("http://www.gaagle.com", catmap.getCategoriesAndSubcategories(), "token");
        verify(handler).injectCategories("http://www.gaagle.com", object, "token");
    }
}
