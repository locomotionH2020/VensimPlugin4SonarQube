package es.uva.locomotion.service;

import es.uva.locomotion.model.AcronymsList;
import es.uva.locomotion.testutilities.ServiceTestUtilities;
import es.uva.locomotion.utilities.exceptions.EmptyServiceException;
import es.uva.locomotion.utilities.exceptions.InvalidServiceUrlException;
import es.uva.locomotion.utilities.exceptions.ServiceResponseFormatNotValid;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

public class TestDBFacadeAcronyms {

    @After
    public void resetDbServiceHandler() {
        DBFacade.handler = new ServiceConnectionHandler(); // Makes the tests independent
    }

    @Test
    public void testGetAcronymsEmptyResponse() {
        String jsonDbTable = "[]";


        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        AcronymsList obtainedList = DBFacade.getExistingAcronymsFromDB("", "token");

        assertEquals(new AcronymsList(), obtainedList);

    }

    @Test
    public void testGetAcronymsParsedJson() {
        AcronymsList list = new AcronymsList();
        list.addAcronym("RES");
        list.addAcronym("2GEN");

        String jsonDbTable = "[{\"id\": 1,\"name\": \"RES\", \"definition\": \"Renewable Energy Source\" }, {\"id\":2,\"name\": \"2GEN\",\"definition\": \"Second generation biofuels\"}]";

        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);

        AcronymsList obtainedList = DBFacade.getExistingAcronymsFromDB("", "token");
        assertEquals(list, obtainedList);
    }

    @Ignore //Definition is not used
    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testGetAcronymsDefinionMissing() {
        String jsonDbTable = "[{\"id\": 1,\"name\": \"RES\", \"definition\": \"Renewable Energy Source\" }, {\"id\":2,\"name\": \"2GEN\"}]";

        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);
        DBFacade.getExistingAcronymsFromDB("http://localhost", "token");

    }

    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testGetAcronymsNameMissing() {
        String jsonDbTable = "[{\"id\": 1,\"name\": \"RES\", \"definition\": \"Renewable Energy Source\" }, {\"id\":2,\"definition\": \"Second generation biofuels\"}]";

        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);
        DBFacade.getExistingAcronymsFromDB("http://localhost", "token");

    }

    @Ignore //Definition is not used
    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testGetAcronymsIdMissing() {
        String jsonDbTable = "[{\"id\": 1,\"name\": \"RES\", \"definition\": \"Renewable Energy Source\" }, {\"name\": \"2GEN\",\"definition\": \"Second generation biofuels\"}]";

        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns(jsonDbTable);
        DBFacade.getExistingAcronymsFromDB("http://localhost", "token");

    }

    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testGetAcronymsUnexpectedFormatArrayOfLiterals() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("[1,3,4]");

        DBFacade.getExistingAcronymsFromDB("http://localhost", "token");
    }


    @Test(expected = ServiceResponseFormatNotValid.class)
    public void testGetAcronymsResponseIsntAJson() {
        DBFacade.handler = ServiceTestUtilities.getMockDbServiceHandlerThatReturns("some text");

        DBFacade.getExistingAcronymsFromDB("http://localhost", "token");
    }

    @Test(expected = EmptyServiceException.class)
    public void testGetAcronymsEmptyServiceRaisesException() {
        DBFacade.getExistingAcronymsFromDB("", "token");
    }

    @Test(expected = EmptyServiceException.class)
    public void testGetAcronymsNullServiceRaisesException() {
        DBFacade.getExistingAcronymsFromDB(null, "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetAcronymsServiceWithAnotherProtocol() {
        DBFacade.getExistingAcronymsFromDB("ftp://somedomain/folder/file.txt", "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetAcronymsServiceWithInvalidProtocol() {
        DBFacade.getExistingAcronymsFromDB("\\some$randomtext", "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetAcronymsDomainWithoutProtocol() {
        DBFacade.getExistingAcronymsFromDB("www.google.com", "token");
    }


    @Test
    public void testGetAcronymsDomainWithoutWWW() throws IOException, InterruptedException {
        String jsonDbTable = "[{\"id\": 1,\"name\": \"RES\", \"definition\": \"Renewable Energy Source\" }, {\"id\":2,\"name\": \"2GEN\",\"definition\": \"Second generation biofuels\"}]";

        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(), any());
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockResponse.body()).thenReturn(jsonDbTable);

        handler.client = mockClient;
        DBFacade.handler = handler;

        DBFacade.getExistingAcronymsFromDB("http://google.com", "token");
    }


}
