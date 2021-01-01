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

public class TestDbServiceHandlerAcronyms {

    @Test
    public void testGetAcronymsUrlWithoutSlash() throws IOException, InterruptedException {
        final String serviceUrl = "http://www.google.com";

        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("[]");
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        handler.client = mockClient;


        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        URI url = URI.create(serviceUrl + "/qaGetAcronyms");
        requestBuilder.uri(url);
        requestBuilder.setHeader("Authorization","Bearer "+ "token");
        HttpRequest request = requestBuilder.GET().build();

        doReturn(mockResponse).when(mockClient).send(any(), any());
        String actualValue = handler.sendAcronymsRequestToDictionaryService(serviceUrl,"token");


        verify(mockClient, times(1)).send(eq(request), any());
        assertEquals(actualValue, "[]");
    }


    @Test(expected = EmptyServiceException.class)
    public void testGetAcronymsEmptyServiceRaisesException() {
        new ServiceConnectionHandler().sendAcronymsRequestToDictionaryService("", "token");
    }

    @Test(expected = EmptyServiceException.class)
    public void testGetAcronymsNullServiceRaisesException() {
        new ServiceConnectionHandler().sendAcronymsRequestToDictionaryService(null, "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetAcronymsServiceWithAnotherProtocol() {
        new ServiceConnectionHandler().sendAcronymsRequestToDictionaryService("ftp://somedomain/folder/file.txt", "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetAcronymsServiceWithInvalidProtocol() {
        new ServiceConnectionHandler().sendAcronymsRequestToDictionaryService("\\some$randomtext", "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetAcronymsDomainWithoutProtocol() {
        new ServiceConnectionHandler().sendAcronymsRequestToDictionaryService("www.google.com", "token");
    }



    @Test
    public void testGetAcronymsDomainWithoutWWW() throws IOException, InterruptedException {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(), any());
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockResponse.body()).thenReturn("honk");

        handler.client = mockClient;

        String actualValue = handler.sendAcronymsRequestToDictionaryService("http://google.com", "token");

        assertEquals("honk", actualValue);
    }

}


