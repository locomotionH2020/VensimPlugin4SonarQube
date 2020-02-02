package es.uva.medeas.utilities;

import es.uva.medeas.utilities.exceptions.ConnectionFailedException;
import es.uva.medeas.utilities.exceptions.EmptyServiceException;
import es.uva.medeas.utilities.exceptions.InvalidServiceUrlException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class DbServiceHandler {


    protected HttpClient client;

    public DbServiceHandler(){
        client =  HttpClient.newBuilder().build();
    }

    /**
     *
     * @param serviceUrl
     * @param symbols
     * @return
     * @throws InvalidServiceUrlException If the url isn't valid (doesn't have a protocol or invalid format)
     * @throws ConnectionFailedException If the domain address can't be resolved or the page is inaccessible.
     * @throws EmptyServiceException If {@code serviceUrl} is empty if null
     * @throws IllegalArgumentException If {@code symbols} is null
     */
    public String sendRequestToService(String serviceUrl, List<String> symbols){
        if(serviceUrl==null || "".equals(serviceUrl.trim()))
            throw new EmptyServiceException("Service Url is null or empty string");
        if(symbols==null)
            throw new IllegalArgumentException("The list of symbols can't be null.");


        JsonArrayBuilder jsonBuilder = Json.createArrayBuilder();
        for(String s: symbols)
            jsonBuilder.add(s);

        JsonArray jsonSymbols = jsonBuilder.build();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

        try{
            URI url = URI.create(serviceUrl);
            requestBuilder.uri(url);
        }catch (IllegalArgumentException ex){
            throw new InvalidServiceUrlException("The format of the serviceUrl is invalid or isn't http/https");
        }
        HttpRequest request  =requestBuilder.POST(HttpRequest.BodyPublishers.ofString(jsonSymbols.toString()))
                .header("Content-Type", "application/json").build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (InterruptedException | IOException e) {
            throw new ConnectionFailedException(e);
        }
    }
}
