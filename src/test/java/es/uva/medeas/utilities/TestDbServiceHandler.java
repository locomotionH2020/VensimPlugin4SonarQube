package es.uva.medeas.utilities;

import es.uva.medeas.utilities.exceptions.ConnectionFailedException;
import es.uva.medeas.utilities.exceptions.EmptyServiceException;
import es.uva.medeas.utilities.exceptions.InvalidServiceUrlException;
import org.junit.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TestDbServiceHandler {


    @Test
    public void testSymbolsSentCorrectly() throws IOException, InterruptedException {
        DbServiceHandler handler = new DbServiceHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("[]");
        handler.client = mockClient;


        HttpRequest.BodyPublisher expectedBody = HttpRequest.BodyPublishers.ofString("[\"var\",\"foo\",\"duck\"]");

        doAnswer(invocationOnMock -> {
            HttpRequest mRequest = invocationOnMock.getArgument(0);
            HttpRequest.BodyPublisher body = mRequest.bodyPublisher().get();
            assertEquals("The body of the request sent doesn't match the expected body",expectedBody.contentLength(), body.contentLength());


            return mockResponse;
        }).when(mockClient).send(any(), any());
        String actualValue = handler.sendRequestToService("https://randomUrl", List.of("var", "foo", "duck"));


        verify(mockClient, times(1)).send(any(), any());
        assertEquals(actualValue,"[]");

    }


    @Test
    public void testSendNoSymbols() throws IOException, InterruptedException {
        DbServiceHandler handler = new DbServiceHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.body()).thenReturn("[]");
        handler.client = mockClient;


        HttpRequest.BodyPublisher expectedBody = HttpRequest.BodyPublishers.ofString("[]");

        doAnswer(invocationOnMock ->{
            HttpRequest mRequest = invocationOnMock.getArgument(0);
            HttpRequest.BodyPublisher body = mRequest.bodyPublisher().get();
            assertEquals("The body of the request send should be empty ([]), but it isn't.",expectedBody.contentLength(),body.contentLength());


            return mockResponse;}).when(mockClient).send(any(),any());
        handler.sendRequestToService("https://randomUrl", new ArrayList<>());


        verify(mockClient,times(1)).send(any(),any());

    }

    @Test(expected = EmptyServiceException.class)
    public void testEmptyServiceRaisesException(){
        new DbServiceHandler().sendRequestToService("",List.of("foo","var"));
    }

    @Test(expected = EmptyServiceException.class)
    public void testNullServiceRaisesException(){
        new DbServiceHandler().sendRequestToService(null,List.of("foo","var"));
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testServiceWithAnotherProtocol(){
        new DbServiceHandler().sendRequestToService("ftp://somedomain/folder/file.txt",List.of("foo","var"));
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testServiceWithInvalidProtocol(){
        new DbServiceHandler().sendRequestToService("\\some$randomtext",List.of("foo","var"));
    }

    @Test(expected = InvalidServiceUrlException.class)
    public void testDomainWithoutProtocol(){
        new DbServiceHandler().sendRequestToService("www.google.com",List.of("foo","var"));
    }

    @Test(expected = ConnectionFailedException.class)
    public void testDomainNotFound(){
        new DbServiceHandler().sendRequestToService("https://adsmfmgekrhadbsfsfaf.com",List.of("foo","var"));
    }



    @Test
    public void testDomainWithoutWWW() throws IOException, InterruptedException {
        DbServiceHandler handler = new DbServiceHandler();
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        doReturn(mockResponse).when(mockClient).send(any(),any());
        when(mockResponse.body()).thenReturn("honk");

        handler.client = mockClient;

        String actualValue = handler.sendRequestToService("http://google.com", List.of("foo", "var"));

        assertEquals("honk",actualValue);
    }
}
