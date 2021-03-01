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

public class TestDbServiceHandlerCategories {

    @Test
    public void testGetCategoriesUrlWithoutSlash() throws IOException, InterruptedException {
        final String serviceUrl = "http://www.google.com";

        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("[]");
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        handler.client = mockClient;


        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        URI url = URI.create(serviceUrl + "/qaGetCategories");
        requestBuilder.uri(url);
        requestBuilder.setHeader("Authorization","Bearer "+ "token");
        HttpRequest request = requestBuilder.GET().build();

        doReturn(mockResponse).when(mockClient).send(any(), any());
        String actualValue = handler.sendCategoriesRequestToDictionaryService(serviceUrl,"token");


        verify(mockClient, times(1)).send(eq(request), any());
        assertEquals(actualValue, "[]");
    }


    @Test(expected = EmptyServiceException.class)
    public void testGetCategoriesEmptyServiceRaisesException() {
        new ServiceConnectionHandler().sendCategoriesRequestToDictionaryService("", "token");
    }

    @Test(expected = EmptyServiceException.class)
    public void testGetCategoriesNullServiceRaisesException() {
        new ServiceConnectionHandler().sendCategoriesRequestToDictionaryService(null, "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetCategoriesServiceWithAnotherProtocol() {
        new ServiceConnectionHandler().sendCategoriesRequestToDictionaryService("ftp://somedomain/folder/file.txt","token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetCategoriesServiceWithInvalidProtocol() {
        new ServiceConnectionHandler().sendCategoriesRequestToDictionaryService("\\some$randomtext", "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testGetCategoriesDomainWithoutProtocol() {
        new ServiceConnectionHandler().sendCategoriesRequestToDictionaryService("www.google.com", "token");
    }



    @Test
    public void testGetCategoriesDomainWithoutWWW() throws IOException, InterruptedException {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(), any());
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockResponse.body()).thenReturn("honk");

        handler.client = mockClient;

        String actualValue = handler.sendCategoriesRequestToDictionaryService("http://google.com", "token");

        assertEquals("honk", actualValue);
    }


    @Test
    public void testInjectCategoriesCorrect() throws IOException, InterruptedException {
        final String serviceUrl = "http://www.google.com/";

        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("[]");
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);

        handler.client = mockClient;

        String foundCategories = "[\"category1\"]";
        JsonReader jsonReader = Json.createReader(new StringReader(foundCategories));
        JsonArray array = jsonReader.readArray();
        jsonReader.close();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        URI url = URI.create(serviceUrl + "qaAddCategories");
        requestBuilder.uri(url);
        requestBuilder.setHeader("Authorization","Bearer "+ "token");
        HttpRequest request = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(foundCategories))
                .header("Content-Type", "application/json").build();

        HttpRequest.BodyPublisher expectedBody = HttpRequest.BodyPublishers.ofString(foundCategories);

        doAnswer(invocationOnMock -> {
            HttpRequest mRequest = invocationOnMock.getArgument(0);
            HttpRequest.BodyPublisher body = mRequest.bodyPublisher().get();
            assertEquals("The body of the request sent doesn't match the expected body", expectedBody.contentLength(), body.contentLength());


            return mockResponse;
        }).when(mockClient).send(any(), any());
        String actualValue = handler.injectCategories(serviceUrl, array,"token");

        verify(mockClient, times(1)).send(eq(request), any());
        assertEquals(actualValue, "[]");
    }

    @Test
    public void testInjectCategoriesUriWithoutSlash() throws IOException, InterruptedException {
        final String serviceUrl = "http://www.google.com";

        String foundCategories = "[\"category1\"]";
        JsonReader jsonReader = Json.createReader(new StringReader(foundCategories));
        JsonArray array = jsonReader.readArray();
        jsonReader.close();


        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.setHeader("Authorization","Bearer token");
        URI url = URI.create(serviceUrl + "/qaAddCategories");
        requestBuilder.uri(url);
        HttpRequest request = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(foundCategories))
                .header("Content-Type", "application/json").build();

        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("[]");
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        handler.client = mockClient;

        doReturn(mockResponse).when(mockClient).send(any(), any());


        handler.injectCategories(serviceUrl, array, "token");
        verify(mockClient, times(1)).send(eq(request), any());

    }

    public void testInjectCategoriesEmptyRequest() throws IOException, InterruptedException {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(), any());
        when(mockResponse.body()).thenReturn("[]");
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);

        handler.client = mockClient;

        handler.injectCategories("https://randomUrl", Json.createArrayBuilder().build(), "token");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInjectCategoriesNullCategories() {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        handler.injectCategories("https://randomUrl", null, "token");
    }

    @Test(expected = EmptyServiceException.class)
    public void testInjectCategoriesEmptyUrl() {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        handler.injectCategories("", mock(JsonArray.class), "token");
    }

    @Test(expected = EmptyServiceException.class)
    public void testInjectCategoriesNullUrl() {
        new ServiceConnectionHandler().injectCategories(null, mock(JsonArray.class), "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testInjectCategoriesServiceWithAnotherProtocol() {
        new ServiceConnectionHandler().injectCategories("ftp://somedomain/folder/file.txt", mock(JsonArray.class), "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testInjectCategoriesServiceWithInvalidProtocol() {
        new ServiceConnectionHandler().injectCategories("\\some$randomtext", mock(JsonArray.class), "token");
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testInjectCategoriesDomainWithoutProtocol() {
        new ServiceConnectionHandler().injectCategories("www.google.com", mock(JsonArray.class), "token");
    }


    @Test
    public void testInjectCategoriesDomainWithoutWWW() throws IOException, InterruptedException {
        ServiceConnectionHandler handler = new ServiceConnectionHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(), any());
        when(mockResponse.body()).thenReturn("honk");
        when(mockResponse.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);

        handler.client = mockClient;

        String actualValue = handler.injectCategories("http://google.com", mock(JsonArray.class), "token");

        assertEquals("honk", actualValue);

    }

}


