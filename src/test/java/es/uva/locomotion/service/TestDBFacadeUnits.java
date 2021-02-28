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

public class TestDBFacadeUnits {

    @After
    public void resetDbServiceHandler() {
        DBFacade.handler = new ServiceConnectionHandler(); // Makes the tests independent
    }
    
    @Test
    public void testGetUnitsParsedJson() {

        List<String> expected = new ArrayList<>();

        expected.add("AnotherUnit");
        expected.add("Unit1");
        expected.add("Unit2");

        String jsonDbList = "[{\"concepts\":\"Primary Energy\",\"id\":1,\"unit\":\"AnotherUnit\"},{\"concepts\":\"Electricity\",\"id\":2,\"unit\":\"Unit1\"},{\"concepts\":\"Population\",\"id\":3,\"unit\":\"Unit2\"}]";

        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbList);


        List<String> obtainedList = DBFacade.getExistingUnitsFromDB("","token");
        assertEquals(expected, obtainedList);
    }



    @Test
    public void testGetUnitsEmptyResponse() {
        String jsonDbList = "[]";

        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbList);

        List<String> obtainedList = DBFacade.getExistingUnitsFromDB("", "token");

        assertEquals(new ArrayList<>(), obtainedList);
    }


    @Test
    public void testGetUnitsUnexpectedObject() {
        String jsonDbList = "{\"foo\":\"bar\"}";

        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbList);
        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingUnitsFromDB("http://localhost", "token"));
        assertEquals("Expected an array.", ex.getMessage());
        assertEquals(jsonDbList, ex.getServiceResponse());
    }

    @Test
    public void testGetUnitsUnexpectedTextInArray() {
        String jsonDbList = "[\"unit\"]";

        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbList);
        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingUnitsFromDB("http://localhost", "token"));
        assertEquals("Recived '\"unit\"' that is not a unit json object.", ex.getMessage());
        assertEquals(jsonDbList, ex.getServiceResponse());
    }

    @Test
    public void testGetUnitsUnexpectedUnitMissing() {
        String jsonDbList = "[{\"concepts\":\"Primary Energy\",\"id\":1}]";

        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbList);
        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingUnitsFromDB("http://localhost", "token"));
        assertEquals("Missing 'unit' field from a unit.", ex.getMessage());
        assertEquals(jsonDbList, ex.getServiceResponse());
    }
    @Test
    public void testGetUnitsUnexpectedConpetsMissing() {
        String jsonDbList = "[{\"id\":1,\"unit\":\"EJ\"}]";

        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbList);
        ServiceResponseFormatNotValid ex = assertThrows(ServiceResponseFormatNotValid.class, () -> DBFacade.getExistingUnitsFromDB("http://localhost", "token"));
        assertEquals("Missing 'concepts' field in category 'EJ'.", ex.getMessage());
        assertEquals(jsonDbList, ex.getServiceResponse());
    }
    @Test
    public void testGetUnitsResponseJsonContainsDuplicatedUnits() {

        String jsonDbList = "[{\"concepts\":\"Primary Energy\",\"id\":1,\"unit\":\"EJ\"},{\"concepts\":\"Electricity\",\"id\":2,\"unit\":\"TWh\"},{\"concepts\":\"Primary Energy\",\"id\":1,\"unit\":\"EJ\"}]";
        VensimLogger logger = mock(VensimLogger.class);
        DBFacade.LOG = logger;
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbList);


        DBFacade.getExistingUnitsFromDB("http://localhost", "token");


        verify(logger).warn("Received duplicated unit 'EJ' from the dictionary service.");
    }

    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testGetUnitsResponseIsntAJson() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("some text");

        DBFacade.getExistingUnitsFromDB("http://localhost", "token");
    }


    @Test(expected = EmptyServiceException.class)
    public void testGetUnitsEmptyServiceRaisesException() {
        DBFacade.getExistingUnitsFromDB("", "token");
    }

    @Test(expected = EmptyServiceException.class)
    public void testGetUnitsNullServiceRaisesException() {
        DBFacade.getExistingUnitsFromDB(null, "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetUnitsServiceWithAnotherProtocol() {
        DBFacade.getExistingUnitsFromDB("ftp://somedomain/folder/file.txt", "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetUnitsServiceWithInvalidProtocol() {
        DBFacade.getExistingUnitsFromDB("\\some$randomtext", "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetUnitsDomainWithoutProtocol() {
        DBFacade.getExistingUnitsFromDB("www.google.com","token");
    }


    @Test
    public void testGetUnitsDomainWithoutWWW() throws IOException, InterruptedException {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(), any());
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockResponse.body()).thenReturn("[]");

        handler.client = mockClient;
        DBFacade.handler = handler;

        DBFacade.getExistingUnitsFromDB("http://google.com", "token");
    }
}
