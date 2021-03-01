package es.uva.locomotion.service;

import es.uva.locomotion.utilities.exceptions.EmptyServiceException;
import es.uva.locomotion.utilities.exceptions.InvalidServiceUrlException;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

public class TestDbServiceHandlerModules {

    @Test
    public void testGetModulesUrlWithoutSlash() throws IOException, InterruptedException {
        final String serviceUrl = "http://www.google.com";

        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("[]");
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        handler.client = mockClient;


        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        URI url = URI.create(serviceUrl + "/qaGetModules");
        requestBuilder.uri(url);
        requestBuilder.setHeader("Authorization","Bearer "+ "token");
        HttpRequest request = requestBuilder.GET().build();

        doReturn(mockResponse).when(mockClient).send(any(), any());
        String actualValue = handler.sendModuleRequestToDictionaryService(serviceUrl,"token");


        verify(mockClient, times(1)).send(eq(request), any());
        assertEquals(actualValue, "[]");
    }


    @Test(expected = EmptyServiceException.class)
    public void testGetModulesEmptyServiceRaisesException() {
        new ServiceConnectionHandler().sendModuleRequestToDictionaryService("", "token");
    }

    @Test(expected = EmptyServiceException.class)
    public void testGetModulesNullServiceRaisesException() {
        new ServiceConnectionHandler().sendModuleRequestToDictionaryService(null, "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetModulesServiceWithAnotherProtocol() {
        new ServiceConnectionHandler().sendModuleRequestToDictionaryService("ftp://somedomain/folder/file.txt","token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetModulesServiceWithInvalidProtocol() {
        new ServiceConnectionHandler().sendModuleRequestToDictionaryService("\\some$randomtext", "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetModulesDomainWithoutProtocol() {
        new ServiceConnectionHandler().sendModuleRequestToDictionaryService("www.google.com", "token");
    }



    @Test
    public void testGetModulesDomainWithoutWWW() throws IOException, InterruptedException {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(), any());
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockResponse.body()).thenReturn("honk");

        handler.client = mockClient;

        String actualValue = handler.sendModuleRequestToDictionaryService("http://google.com", "token");

        assertEquals("honk", actualValue);
    }


    @Test
    public void testInjectModulesCorrect() throws IOException, InterruptedException {
        final String serviceUrl = "http://www.google.com/";

        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("[]");
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);

        handler.client = mockClient;

        String foundModules = "[\"module1\"]";
        JsonReader jsonReader = Json.createReader(new StringReader(foundModules));
        JsonArray array = jsonReader.readArray();
        jsonReader.close();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        URI url = URI.create(serviceUrl + "qaAddModules");
        requestBuilder.uri(url);
        requestBuilder.setHeader("Authorization","Bearer "+ "token");
        HttpRequest request = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(foundModules))
                .header("Content-Type", "application/json").build();

        HttpRequest.BodyPublisher expectedBody = HttpRequest.BodyPublishers.ofString(foundModules);

        doAnswer(invocationOnMock -> {
            HttpRequest mRequest = invocationOnMock.getArgument(0);
            HttpRequest.BodyPublisher body = mRequest.bodyPublisher().get();
            assertEquals("The body of the request sent doesn't match the expected body", expectedBody.contentLength(), body.contentLength());


            return mockResponse;
        }).when(mockClient).send(any(), any());
        String actualValue = handler.injectModules(serviceUrl, array,"token");

        verify(mockClient, times(1)).send(eq(request), any());
        assertEquals(actualValue, "[]");
    }

    @Test
    public void testInjectModulesUriWithoutSlash() throws IOException, InterruptedException {
        final String serviceUrl = "http://www.google.com";

        String foundModules = "[\"module1\"]";
        JsonReader jsonReader = Json.createReader(new StringReader(foundModules));
        JsonArray array = jsonReader.readArray();
        jsonReader.close();


        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.setHeader("Authorization","Bearer token");
        URI url = URI.create(serviceUrl + "/qaAddModules");
        requestBuilder.uri(url);
        HttpRequest request = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(foundModules))
                .header("Content-Type", "application/json").build();

        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("[]");
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);

        handler.client = mockClient;

        doReturn(mockResponse).when(mockClient).send(any(), any());


        handler.injectModules(serviceUrl, array, "token");
        verify(mockClient, times(1)).send(eq(request), any());

    }

    public void testInjectModulesEmptyRequest() throws IOException, InterruptedException {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(), any());
        when(mockResponse.body()).thenReturn("[]");
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);

        handler.client = mockClient;

        handler.injectModules("https://randomUrl", Json.createArrayBuilder().build(), "token");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInjectModulesNullModules() {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        handler.injectModules("https://randomUrl", null, "token");
    }

    @Test(expected = EmptyServiceException.class)
    public void testInjectModulesEmptyUrl() {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        handler.injectModules("", mock(JsonArray.class), "token");
    }

    @Test(expected = EmptyServiceException.class)
    public void testInjectModulesNullUrl() {
        new ServiceConnectionHandler().injectModules(null, mock(JsonArray.class), "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testInjectModulesServiceWithAnotherProtocol() {
        new ServiceConnectionHandler().injectModules("ftp://somedomain/folder/file.txt", mock(JsonArray.class), "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testInjectModulesServiceWithInvalidProtocol() {
        new ServiceConnectionHandler().injectModules("\\some$randomtext", mock(JsonArray.class), "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testInjectModulesDomainWithoutProtocol() {
        new ServiceConnectionHandler().injectModules("www.google.com", mock(JsonArray.class), "token");
    }


    @Test
    public void testInjectModulesDomainWithoutWWW() throws IOException, InterruptedException {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(), any());
        when(mockResponse.body()).thenReturn("honk");
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);

        handler.client = mockClient;

        String actualValue = handler.injectModules("http://google.com", mock(JsonArray.class), "token");

        assertEquals("honk", actualValue);

    }

}


