package es.uva.locomotion.service;

import es.uva.locomotion.utilities.logs.VensimLogger;
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

public class ServiceConnectionHandler {

    protected final VensimLogger LOG = VensimLogger.getInstance();
    protected HttpClient client;

    public ServiceConnectionHandler(){
        client =  HttpClient.newBuilder().build();
    }

    /**
     *
     * @param serviceUrl
     * @param jsonSymbols
     * @return
     * @throws InvalidServiceUrlException If the url isn't valid (doesn't have a protocol or invalid format)
     * @throws ConnectionFailedException If the domain address can't be resolved or the page is inaccessible.
     * @throws EmptyServiceException If {@code serviceUrl} is empty if null
     * @throws IllegalArgumentException If {@code symbols} is null
     */
    public String sendSymbolTableRequestToDictionaryService(String serviceUrl, JsonObject jsonSymbols, String token){
        if(serviceUrl==null || "".equals(serviceUrl.trim()))
            throw new EmptyServiceException("Service Url is null or an empty string");
        if(jsonSymbols==null)
            throw new IllegalArgumentException("The list of symbols can't be null.");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.setHeader("Authorization","Bearer "+ token);


        if(serviceUrl.charAt(serviceUrl.length()-1)!='/')
            serviceUrl = serviceUrl + "/";

        URI url;
        try{
            url = URI.create(serviceUrl);
            url = url.resolve("qaGetSymbolsDefinition");
            LOG.server("Sending POST request to: " + url.toString() + " with data: \n" + jsonSymbols);
            requestBuilder.uri(url);
        }catch (IllegalArgumentException ex){
            throw new InvalidServiceUrlException("The format of the serviceUrl is invalid or isn't http/https");
        }
        HttpRequest request  =requestBuilder.POST(HttpRequest.BodyPublishers.ofString(jsonSymbols.toString()))
                .header("Content-Type", "application/json").build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            LOG.server("The response of the server to the request to " + url.toString() + " was HTTP" + +response.statusCode() +": \n" + responseBody);
            if (response.statusCode() == HttpURLConnection.HTTP_OK)
                return responseBody;
            else
                throw new ConnectionFailedException(new IllegalArgumentException("The status code of the response to qaGetSymbolsDefinition was: " + response.statusCode()));
            
        } catch (InterruptedException | IOException e) {
            LOG.server("The connection failed: " + e.getMessage());
            throw new ConnectionFailedException(e);
        }
    }

    public String sendAcronymsRequestToDictionaryService(String serviceUrl, String token){
        if(serviceUrl==null || "".equals(serviceUrl.trim()))
            throw new EmptyServiceException("Service Url is null or an empty string");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.setHeader("Authorization","Bearer "+ token);

        if(serviceUrl.charAt(serviceUrl.length()-1)!='/')
            serviceUrl = serviceUrl + "/";

        URI url;
        try{
            url = URI.create(serviceUrl);
            url = url.resolve("qaGetAcronyms");
            LOG.server("Sending POST request to: " + url.toString());
            requestBuilder.uri(url);
        }catch (IllegalArgumentException ex){
            throw new InvalidServiceUrlException("The format of the serviceUrl is invalid or isn't http/https");
        }
        HttpRequest request  =requestBuilder.GET().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            LOG.server("The response of the server to the request to " + url.toString() + " was HTTP" + +response.statusCode() +": \n" + responseBody);
            if (response.statusCode() == HttpURLConnection.HTTP_OK)
                return responseBody;
            else
                throw new ConnectionFailedException(new IllegalArgumentException("The status code of the response to qaGetAcronyms was: " + response.statusCode()));

        } catch (InterruptedException | IOException e) {
            LOG.server("The connection failed: " + e.getMessage());
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


        URI url;
        try{
            url = URI.create(serviceUrl);
            url = url.resolve("qaAddSymbolsDefinition");
            LOG.server("Sending POST request to: " + url.toString() + "with data: \n" + symbols.toString());
            requestBuilder.uri(url);
        }catch (IllegalArgumentException ex){
            throw new InvalidServiceUrlException("The format of the serviceUrl is invalid or isn't http/https");
        }
        HttpRequest request  =requestBuilder.POST(HttpRequest.BodyPublishers.ofString(symbols.toString()))
                .header("Content-Type", "application/json").build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            LOG.server("The response of the server to the request to " + url.toString() + " was HTTP " +response.statusCode() +": \n" + responseBody);
            return responseBody;
        } catch (InterruptedException | IOException e) {
            LOG.server("The connection failed: " + e.getMessage());
            throw new ConnectionFailedException(e);
        }

    }


    public String authenticate(String serviceUrl, String user, String password){
        if(serviceUrl == null || "".equals(serviceUrl.trim()))
            throw new EmptyServiceException("Service Url is null or an empty string");

        if(serviceUrl.charAt(serviceUrl.length()-1)!='/')
            serviceUrl = serviceUrl + "/";

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

        String requestBody= "{\"username\":\"" + user + "\"," +
                " \"password\":\""+ password + "\"}";

        URI url;
        try{
            url = URI.create(serviceUrl);
            url = url.resolve("authenticate");
            LOG.server("Sending POST request to: " + url.toString() + " with data: \n" + requestBody);

            requestBuilder.uri(url);
        }catch (IllegalArgumentException ex){
            throw new InvalidServiceUrlException("The format of the serviceUrl is invalid or isn't http/https");
        }

        HttpRequest request  =requestBuilder.POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json").build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            LOG.server("The response of the server to the request to " + url.toString() + " was HTTP " +response.statusCode() +": \n" + responseBody);
            return responseBody;
        } catch (InterruptedException | IOException e) {
            LOG.server("The connection failed: " + e.getMessage());
            throw new ConnectionFailedException(e);
        }

    }

    public String sendModuleRequestToDictionaryService(String serviceUrl, String token) {
        if(serviceUrl==null || "".equals(serviceUrl.trim()))
            throw new EmptyServiceException("Service Url is null or an empty string");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.setHeader("Authorization","Bearer "+ token);

        if(serviceUrl.charAt(serviceUrl.length()-1)!='/')
            serviceUrl = serviceUrl + "/";

        URI url;
        try{
            url = URI.create(serviceUrl);
            url = url.resolve("qaGetModules");
            LOG.server("Sending POST request to: " + url.toString());
            requestBuilder.uri(url);
        }catch (IllegalArgumentException ex){
            throw new InvalidServiceUrlException("The format of the serviceUrl is invalid or isn't http/https");
        }
        HttpRequest request  =requestBuilder.GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            LOG.server("The response of the server to the request to " + url.toString() + " was HTTP" + +response.statusCode() +": \n" + responseBody);
            if (response.statusCode() == HttpURLConnection.HTTP_OK)
                return responseBody;
            else
                throw new ConnectionFailedException(new IllegalArgumentException("The status code of the response to qaGetAcronyms was: " + response.statusCode()));

        } catch (InterruptedException | IOException e) {
            LOG.server("The connection failed: " + e.getMessage());
            throw new ConnectionFailedException(e);
        }
    }

    public String sendCategoriesRequestToDictionaryService(String serviceUrl, String token) {
        if (serviceUrl == null || "".equals(serviceUrl.trim()))
            throw new EmptyServiceException("Service Url is null or an empty string");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.setHeader("Authorization", "Bearer " + token);

        if (serviceUrl.charAt(serviceUrl.length() - 1) != '/')
            serviceUrl = serviceUrl + "/";

        URI url;
        try {
            url = URI.create(serviceUrl);
            url = url.resolve("qaGetCategories");
            LOG.server("Sending POST request to: " + url.toString());
            requestBuilder.uri(url);
        } catch (IllegalArgumentException ex) {
            throw new InvalidServiceUrlException("The format of the serviceUrl is invalid or isn't http/https");
        }
        HttpRequest request = requestBuilder.GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            LOG.server("The response of the server to the request to " + url.toString() + " was HTTP" + +response.statusCode() + ": \n" + responseBody);
            if (response.statusCode() == HttpURLConnection.HTTP_OK)
                return responseBody;
            else
                throw new ConnectionFailedException(new IllegalArgumentException("The status code of the response to qaGetAcronyms was: " + response.statusCode()));

        } catch (InterruptedException | IOException e) {
            LOG.server("The connection failed: " + e.getMessage());
            throw new ConnectionFailedException(e);
        }
    }

    public String injectModules(String serviceUrl, JsonArray modules, String token) {
        if(serviceUrl == null || "".equals(serviceUrl.trim()))
            throw new EmptyServiceException("Service Url is null or an empty string");
        if(modules == null || modules.isEmpty())
            throw new IllegalArgumentException("The modules can't be empty");


        if(serviceUrl.charAt(serviceUrl.length()-1)!='/')
            serviceUrl = serviceUrl + "/";

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.setHeader("Authorization","Bearer "+ token);


        URI url;
        try{
            url = URI.create(serviceUrl);
            url = url.resolve("qaAddModules");
            LOG.server("Sending POST request to: " + url.toString() + "with data: \n" + modules.toString());
            requestBuilder.uri(url);
        }catch (IllegalArgumentException ex){
            throw new InvalidServiceUrlException("The format of the serviceUrl is invalid or isn't http/https");
        }
        HttpRequest request  =requestBuilder.POST(HttpRequest.BodyPublishers.ofString(modules.toString()))
                .header("Content-Type", "application/json").build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            LOG.server("The response of the server to the request to " + url.toString() + " was HTTP " +response.statusCode() +": \n" + responseBody);
            return responseBody;
        } catch (InterruptedException | IOException e) {
            LOG.server("The connection failed: " + e.getMessage());
            throw new ConnectionFailedException(e);
        }

    }

    public String injectCategories(String serviceUrl, JsonArray categories, String token) {
        if(serviceUrl == null || "".equals(serviceUrl.trim()))
            throw new EmptyServiceException("Service Url is null or an empty string");
        if(categories == null || categories.isEmpty())
            throw new IllegalArgumentException("The categories can't be empty");


        if(serviceUrl.charAt(serviceUrl.length()-1)!='/')
            serviceUrl = serviceUrl + "/";

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        requestBuilder.setHeader("Authorization","Bearer "+ token);


        URI url;
        try{
            url = URI.create(serviceUrl);
            url = url.resolve("qaAddCategories");
            LOG.server("Sending POST request to: " + url.toString() + "with data: \n" + categories.toString());
            requestBuilder.uri(url);
        }catch (IllegalArgumentException ex){
            throw new InvalidServiceUrlException("The format of the serviceUrl is invalid or isn't http/https");
        }
        HttpRequest request  =requestBuilder.POST(HttpRequest.BodyPublishers.ofString(categories.toString()))
                .header("Content-Type", "application/json").build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            LOG.server("The response of the server to the request to " + url.toString() + " was HTTP " +response.statusCode() +": \n" + responseBody);
            return responseBody;
        } catch (InterruptedException | IOException e) {
            LOG.server("The connection failed: " + e.getMessage());
            throw new ConnectionFailedException(e);
        }

    }
}
