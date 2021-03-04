package es.uva.locomotion.service;

import es.uva.locomotion.testutilities.ServiceTestUtilities;
import es.uva.locomotion.utilities.exceptions.EmptyServiceException;
import es.uva.locomotion.utilities.exceptions.InvalidServiceUrlException;
import es.uva.locomotion.utilities.exceptions.ServiceResponseFormatNotValid;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

public class TestDBFacadeModules {

    @After
    public void resetDbServiceHandler() {
        DBFacade.handler = new ServiceConnectionHandler(); // Makes the tests independent
    }
    
    @Test
    public void testGetModulesParsedJson() {

        List<String> expected = new ArrayList<>();

        expected.add("AnotherModule");
        expected.add("Module1");
        expected.add("Module2");

        String jsonDbList = "{ \"modules\" :[\"Module1\",\"AnotherModule\",\"Module2\"]}";

        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbList);


        List<String> obtainedList = DBFacade.getExistingModulesFromDB("","token");
        assertEquals(expected, obtainedList);
    }



    @Test
    public void testGetModulesEmptyResponse() {
        String jsonDbList = "{\"modules\" : []}";

        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbList);

        List<String> obtainedList = DBFacade.getExistingModulesFromDB("", "token");

        assertEquals(new ArrayList<>(), obtainedList);
    }


    @Test
    public void testGetModulesUnexpectedList() {
        String jsonDbList = "[{\"foo\":\"bar\"}]";

        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbList);
        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingModulesFromDB("http://localhost", "token"));
        assertEquals("Expected an object.", ex.getMessage());
        assertEquals(jsonDbList, ex.getServiceResponse());
    }
    @Test
    public void testGetModulesUnexpectedKeyMissing() {
        String jsonDbList = "{\"foo\":\"bar\"}";

        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbList);
        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingModulesFromDB("http://localhost", "token"));
        assertEquals("\"modules\" key not found in object.", ex.getMessage());
        assertEquals(jsonDbList, ex.getServiceResponse());
    }
    @Test
    public void testGetModulesUnexpectedObjectInArray() {
        String jsonDbList = "{\"modules\" :[{\"foo\":\"bar\"}]}";

        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbList);
        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingModulesFromDB("http://localhost", "token"));
        assertEquals("Format error '{\"foo\":\"bar\"}' is not a module name.", ex.getMessage());
        assertEquals(jsonDbList, ex.getServiceResponse());
    }


    @Test
    public void testGetModulesResponseJsonContainsDuplicatedModules() {

        String jsonDbList = "{\"modules\":[\"Module\",\"Another\",\"Module\"]}";
        VensimLogger logger = mock(VensimLogger.class);
        DBFacade.LOG = logger;
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbList);


        DBFacade.getExistingModulesFromDB("http://localhost", "token");


        verify(logger).warn("Received duplicated module 'Module' from the dictionary service.");
    }

    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testGetModulesResponseIsntAJson() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("some text");

        DBFacade.getExistingModulesFromDB("http://localhost", "token");
    }


    @Test(expected = EmptyServiceException.class)
    public void testGetModulesEmptyServiceRaisesException() {
        DBFacade.getExistingModulesFromDB("", "token");
    }

    @Test(expected = EmptyServiceException.class)
    public void testGetModulesNullServiceRaisesException() {
        DBFacade.getExistingModulesFromDB(null, "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetModulesServiceWithAnotherProtocol() {
        DBFacade.getExistingModulesFromDB("ftp://somedomain/folder/file.txt", "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetModulesServiceWithInvalidProtocol() {
        DBFacade.getExistingModulesFromDB("\\some$randomtext", "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetModulesDomainWithoutProtocol() {
        DBFacade.getExistingModulesFromDB("www.google.com","token");
    }


    @Test
    public void testGetModulesDomainWithoutWWW() throws IOException, InterruptedException {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(), any());
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockResponse.body()).thenReturn("{\"modules\": []}");

        handler.client = mockClient;
        DBFacade.handler = handler;

        DBFacade.getExistingModulesFromDB("http://google.com", "token");
    }

    @Test
    public void testInjectModule() {
        ServiceConnectionHandler handler = Mockito.mock(ServiceConnectionHandler.class);
        DBFacade.handler = handler;

        List<String> modules = new ArrayList<>();


        modules.add("Module1");
        modules.add("AnotherModule");

        JsonReader jsonReader = Json.createReader(new StringReader("{\"modules\":[\"AnotherModule\",\"Module1\"]}"));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        DBFacade.injectModules("http://www.gaagle.com", modules, "token");
        verify(handler).injectModules("http://www.gaagle.com", object, "token");
    }

    @Test
    public void testInjectModuleDuplicated() {
        ServiceConnectionHandler handler = Mockito.mock(ServiceConnectionHandler.class);
        DBFacade.handler = handler;
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        DBFacade.LOG = logger;

        List<String> modules = new ArrayList<>();
        modules.add("Module1");
        modules.add("Module1");

        JsonReader jsonReader = Json.createReader(new StringReader("{\"modules\":[\"Module1\"]}"));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        DBFacade.injectModules("http://www.gaagle.com", modules, "token");
        verify(handler).injectModules("http://www.gaagle.com", object, "token");

        verify(logger, times(1)).warn("Received duplicated module 'Module1' to inject to the dictionary service.");
    }
    @Test
    public void testInjectModuleEmpty() {
        ServiceConnectionHandler handler = Mockito.mock(ServiceConnectionHandler.class);
        DBFacade.handler = handler;
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        DBFacade.LOG = logger;

        List<String> modules = new ArrayList<>();

        DBFacade.injectModules("http://www.gaagle.com", modules, "token");
        verify(handler, never()).injectModules(eq("http://www.gaagle.com"), any(), eq("token"));

        verify(logger, times(1)).warn("Module list to inject is empty.");
    }
}
