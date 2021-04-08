package es.uva.locomotion.service;

import es.uva.locomotion.utilities.exceptions.EmptyServiceException;
import es.uva.locomotion.utilities.exceptions.InvalidServiceUrlException;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

public class TestDbServiceHandlerUnits {

    @Test
    public void testGetUnitsUrlWithoutSlash() throws IOException, InterruptedException {
        final String serviceUrl = "http://www.google.com";

        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("[]");
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        handler.client = mockClient;


        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        URI url = URI.create(serviceUrl + "/qaGetUnitSystem");
        requestBuilder.uri(url);
        requestBuilder.setHeader("Authorization","Bearer "+ "token");
        HttpRequest request = requestBuilder.GET().build();

        doReturn(mockResponse).when(mockClient).send(any(), any());
        String actualValue = handler.sendUnitsRequestToDictionaryService(serviceUrl,"token");


        verify(mockClient, times(1)).send(eq(request), any());
        assertEquals(actualValue, "[]");
    }


    @Test(expected = EmptyServiceException.class)
    public void testGetUnitsEmptyServiceRaisesException() {
        new ServiceConnectionHandler().sendUnitsRequestToDictionaryService("", "token");
    }

    @Test(expected = EmptyServiceException.class)
    public void testGetUnitsNullServiceRaisesException() {
        new ServiceConnectionHandler().sendUnitsRequestToDictionaryService(null, "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetUnitsServiceWithAnotherProtocol() {
        new ServiceConnectionHandler().sendUnitsRequestToDictionaryService("ftp://somedomain/folder/file.txt","token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetUnitsServiceWithInvalidProtocol() {
        new ServiceConnectionHandler().sendUnitsRequestToDictionaryService("\\some$randomtext", "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetUnitsDomainWithoutProtocol() {
        new ServiceConnectionHandler().sendUnitsRequestToDictionaryService("www.google.com", "token");
    }



    @Test
    public void testGetUnitsDomainWithoutWWW() throws IOException, InterruptedException {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(), any());
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockResponse.body()).thenReturn("honk");

        handler.client = mockClient;

        String actualValue = handler.sendUnitsRequestToDictionaryService("http://google.com", "token");

        assertEquals("honk", actualValue);
    }


}


