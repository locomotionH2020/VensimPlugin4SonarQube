package es.uva.locomotion.service;

import es.uva.locomotion.utilities.exceptions.EmptyServiceException;
import es.uva.locomotion.utilities.exceptions.InvalidServiceUrlException;
import org.junit.Ignore;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


import static es.uva.locomotion.testutilities.GeneralTestUtilities.getJsonObjectFromList;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TestDbServiceHandler {

    @Ignore
    @Test
    public void testGetSymbolsSymbolsSentCorrectly() throws IOException, InterruptedException {
        final String serviceUrl = "http://www.google.com/";

        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("[]");
        handler.client = mockClient;


        HttpRequest.BodyPublisher expectedBody = HttpRequest.BodyPublishers.ofString("[\"var\",\"foo\",\"duck\"]");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        URI url = URI.create(serviceUrl + "qaGetSymbolsDefinition");
        requestBuilder.uri(url);
        HttpRequest request = requestBuilder.POST(expectedBody)
                .header("Content-Type", "application/json").build();

        doAnswer(invocationOnMock -> {
            HttpRequest mRequest = invocationOnMock.getArgument(0);
            HttpRequest.BodyPublisher body = mRequest.bodyPublisher().get();
            assertEquals("The body of the request sent doesn't match the expected body", expectedBody.contentLength(), body.contentLength());


            return mockResponse;
        }).when(mockClient).send(any(), any());
        String actualValue = handler.sendSymbolTableRequestToDictionaryService(serviceUrl, getJsonObjectFromList("var", "foo", "duck"),"token");


        verify(mockClient, times(1)).send(eq(request), any());
        assertEquals(actualValue, "[]");

    }

    @Test
    public void testGetSymbolsUrlWithoutSlash() throws IOException, InterruptedException {
        final String serviceUrl = "http://www.google.com";

        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("[]");
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        handler.client = mockClient;


        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        URI url = URI.create(serviceUrl + "/qaGetSymbolsDefinition");
        requestBuilder.uri(url);
        requestBuilder.setHeader("Authorization","Bearer "+ "token");
        HttpRequest request = requestBuilder.POST(HttpRequest.BodyPublishers.ofString("[\"var\",\"foo\",\"duck\"]"))
                .header("Content-Type", "application/json").build();

        doReturn(mockResponse).when(mockClient).send(any(), any());
        String actualValue = handler.sendSymbolTableRequestToDictionaryService(serviceUrl, getJsonObjectFromList("var", "foo", "duck"),"token");


        verify(mockClient, times(1)).send(eq(request), any());
        assertEquals(actualValue, "[]");
    }


    @Ignore
    @Test
    public void testGetSymbolsSendNoSymbols() throws IOException, InterruptedException {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("[]");
        handler.client = mockClient;


        HttpRequest.BodyPublisher expectedBody = HttpRequest.BodyPublishers.ofString("[]");

        doAnswer(invocationOnMock -> {
            HttpRequest mRequest = invocationOnMock.getArgument(0);
            HttpRequest.BodyPublisher body = mRequest.bodyPublisher().get();
            assertEquals("The body of the request send should be empty ([]), but it isn't.", expectedBody.contentLength(), body.contentLength());


            return mockResponse;
        }).when(mockClient).send(any(), any());
        handler.sendSymbolTableRequestToDictionaryService("https://randomUrl", mock(JsonObject.class), "token");


        verify(mockClient, times(1)).send(any(), any());

    }

    @Test(expected = EmptyServiceException.class)
    public void testGetSymbolsEmptyServiceRaisesException() {
        new ServiceConnectionHandler().sendSymbolTableRequestToDictionaryService("", getJsonObjectFromList("foo", "var"), "token");
    }

    @Test(expected = EmptyServiceException.class)
    public void testGetSymbolsNullServiceRaisesException() {
        new ServiceConnectionHandler().sendSymbolTableRequestToDictionaryService(null, getJsonObjectFromList("foo", "var"), "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetSymbolsServiceWithAnotherProtocol() {
        new ServiceConnectionHandler().sendSymbolTableRequestToDictionaryService("ftp://somedomain/folder/file.txt", getJsonObjectFromList("foo", "var"), "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetSymbolsServiceWithInvalidProtocol() {
        new ServiceConnectionHandler().sendSymbolTableRequestToDictionaryService("\\some$randomtext", getJsonObjectFromList("foo", "var"), "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetSymbolsDomainWithoutProtocol() {
        new ServiceConnectionHandler().sendSymbolTableRequestToDictionaryService("www.google.com", getJsonObjectFromList("foo", "var"), "token");
    }



    @Test
    public void testGetSymbolsDomainWithoutWWW() throws IOException, InterruptedException {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(), any());
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockResponse.body()).thenReturn("honk");

        handler.client = mockClient;

        String actualValue = handler.sendSymbolTableRequestToDictionaryService("http://google.com", getJsonObjectFromList("foo", "var"), "token");

        assertEquals("honk", actualValue);
    }


    @Test
    public void testInjectSymbolsCorrect() throws IOException, InterruptedException {
        final String serviceUrl = "http://www.google.com/";

        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("[]");
        handler.client = mockClient;

        String foundSymbols = "{\"symbols\":[],\"indexes\":[],\"module\":\"some module\"}";
        JsonReader jsonReader = Json.createReader(new StringReader(foundSymbols));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        URI url = URI.create(serviceUrl + "qaAddSymbolsDefinition");
        requestBuilder.uri(url);
        requestBuilder.setHeader("Authorization","Bearer "+ "token");
        HttpRequest request = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(foundSymbols))
                .header("Content-Type", "application/json").build();

        HttpRequest.BodyPublisher expectedBody = HttpRequest.BodyPublishers.ofString(foundSymbols);

        doAnswer(invocationOnMock -> {
            HttpRequest mRequest = invocationOnMock.getArgument(0);
            HttpRequest.BodyPublisher body = mRequest.bodyPublisher().get();
            assertEquals("The body of the request sent doesn't match the expected body", expectedBody.contentLength(), body.contentLength());


            return mockResponse;
        }).when(mockClient).send(any(), any());
        String actualValue = handler.injectSymbols(serviceUrl, object,"token");

        verify(mockClient, times(1)).send(eq(request), any());
        assertEquals(actualValue, "[]");
    }

    @Test
    public void testInjectSymbolsUriWithoutSlash() throws IOException, InterruptedException {
        final String serviceUrl = "http://www.google.com";

        String foundSymbols = "{\"symbols\":[],\"indexes\":[],\"module\":\"some module\"}";
        JsonReader jsonReader = Json.createReader(new StringReader(foundSymbols));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();


        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.setHeader("Authorization","Bearer token");
        URI url = URI.create(serviceUrl + "/qaAddSymbolsDefinition");
        requestBuilder.uri(url);
        HttpRequest request = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(foundSymbols))
                .header("Content-Type", "application/json").build();

        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("[]");
        handler.client = mockClient;

        doReturn(mockResponse).when(mockClient).send(any(), any());


        handler.injectSymbols(serviceUrl, object, "token");
        verify(mockClient, times(1)).send(eq(request), any());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInjectSymbolsEmptyRequest() throws IOException, InterruptedException {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("[]");
        handler.client = mockClient;

        handler.injectSymbols("https://randomUrl", Json.createObjectBuilder().build(), "token");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInjectSymbolsNullSymbols() throws IOException, InterruptedException {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        handler.injectSymbols("https://randomUrl", null, "token");
    }

    @Test(expected = EmptyServiceException.class)
    public void testInjectSymbolsEmptyUrl() {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        handler.injectSymbols("", mock(JsonObject.class), "token");
    }

    @Test(expected = EmptyServiceException.class)
    public void testInjectSymbolsNullUrl() {
        new ServiceConnectionHandler().injectSymbols(null, mock(JsonObject.class), "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testInjectSymbolsServiceWithAnotherProtocol() {
        new ServiceConnectionHandler().injectSymbols("ftp://somedomain/folder/file.txt", mock(JsonObject.class), "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testInjectSymbolsServiceWithInvalidProtocol() {
        new ServiceConnectionHandler().injectSymbols("\\some$randomtext", mock(JsonObject.class), "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testInjectSymbolsDomainWithoutProtocol() {
        new ServiceConnectionHandler().injectSymbols("www.google.com", mock(JsonObject.class), "token");
    }


    @Test
    public void testInjectSymbolsDomainWithoutWWW() throws IOException, InterruptedException {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(), any());
        when(mockResponse.body()).thenReturn("honk");

        handler.client = mockClient;

        String actualValue = handler.injectSymbols("http://google.com", mock(JsonObject.class), "token");

        assertEquals("honk", actualValue);

    }


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


