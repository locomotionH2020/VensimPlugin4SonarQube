package es.uva.locomotion.service;

import es.uva.locomotion.utilities.exceptions.ConnectionFailedException;
import es.uva.locomotion.utilities.exceptions.EmptyServiceException;
import es.uva.locomotion.utilities.exceptions.InvalidServiceUrlException;

import javax.json.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ServiceConnectionHandler {


    protected HttpClient client;

    public ServiceConnectionHandler(){
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
    public String sendRequestToDictionaryService(String serviceUrl, List<String> symbols, String token){
        if(serviceUrl==null || "".equals(serviceUrl.trim()))
            throw new EmptyServiceException("Service Url is null or an empty string");
        if(symbols==null)
            throw new IllegalArgumentException("The list of symbols can't be null.");

        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for(String s: symbols)
            arrayBuilder.add(s);

        jsonBuilder.add("symbols",jsonBuilder);
        JsonObject jsonSymbols = jsonBuilder.build();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.setHeader("Authorization","Bearer "+ token);


        if(serviceUrl.charAt(serviceUrl.length()-1)!='/')
            serviceUrl = serviceUrl + "/";

        try{
            URI url = URI.create(serviceUrl);
            url = url.resolve("qaGetSymbolsDefinition");
            requestBuilder.uri(url);
        }catch (IllegalArgumentException ex){
            throw new InvalidServiceUrlException("The format of the serviceUrl is invalid or isn't http/https");
        }
        HttpRequest request  =requestBuilder.POST(HttpRequest.BodyPublishers.ofString(jsonSymbols.toString()))
                .header("Content-Type", "application/json").build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpURLConnection.HTTP_OK)
                return response.body();
            else
                return null;
        } catch (InterruptedException | IOException e) {
            throw new ConnectionFailedException(e);
        }
    }

    /**
     *
     * @param serviceUrl
     * @param symbols
     * @return
     * @throws InvalidServiceUrlException If the url isn't valid (doesn't have a protocol or invalid format)
     * @throws ConnectionFailedException If the domain address can't be resolved or the page is inaccessible.
     * @throws EmptyServiceException If {@code serviceUrl} is empty if null
     * @throws IllegalArgumentException If {@code symbols} is null or empty
     */
    public String injectSymbols(String serviceUrl, JsonObject symbols, String token) {
        if(serviceUrl == null || "".equals(serviceUrl.trim()))
            throw new EmptyServiceException("Service Url is null or an empty string");
        if(symbols == null || symbols.isEmpty())
            throw new IllegalArgumentException("The symbols can't be empty");


        if(serviceUrl.charAt(serviceUrl.length()-1)!='/')
            serviceUrl = serviceUrl + "/";

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.setHeader("Authorization","Bearer "+ token);

        try{
            URI url = URI.create(serviceUrl);
            url = url.resolve("qaAddSymbolsDefinition");
            requestBuilder.uri(url);
        }catch (IllegalArgumentException ex){
            throw new InvalidServiceUrlException("The format of the serviceUrl is invalid or isn't http/https");
        }
        HttpRequest request  =requestBuilder.POST(HttpRequest.BodyPublishers.ofString(symbols.toString()))
                .header("Content-Type", "application/json").build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (InterruptedException | IOException e) {
            throw new ConnectionFailedException(e);
        }

    }


    public String authenticateForInjection(String serviceUrl, String user, String password){
        if(serviceUrl == null || "".equals(serviceUrl.trim()))
            throw new EmptyServiceException("Service Url is null or an empty string");

        if(serviceUrl.charAt(serviceUrl.length()-1)!='/')
            serviceUrl = serviceUrl + "/";

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

        try{
            URI url = URI.create(serviceUrl);
            url = url.resolve("authenticate");
            requestBuilder.uri(url);
        }catch (IllegalArgumentException ex){
            throw new InvalidServiceUrlException("The format of the serviceUrl is invalid or isn't http/https");
        }

        HttpRequest request  =requestBuilder.POST(HttpRequest.BodyPublishers.ofString(
                "{\"username\":\"" + user + "\"," +
                        " \"password\":\""+ password + "\"}"))
                .header("Content-Type", "application/json").build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (InterruptedException | IOException e) {
            throw new ConnectionFailedException(e);
        }

    }
}
