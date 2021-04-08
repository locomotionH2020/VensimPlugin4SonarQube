package es.uva.locomotion.service;

import es.uva.locomotion.utilities.exceptions.ConnectionFailedException;
import es.uva.locomotion.utilities.exceptions.EmptyServiceException;
import es.uva.locomotion.utilities.exceptions.InvalidServiceUrlException;
import es.uva.locomotion.utilities.logs.VensimLogger;

import javax.json.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServiceConnectionHandler { //TODO eso podría ser solo dos funciones casi

    protected final VensimLogger logger = VensimLogger.getInstance();
    protected HttpClient client;

    public ServiceConnectionHandler() {
        client = HttpClient.newBuilder().build();
    }


    private String sendPOSTRequest(String serviceUrl, String service, JsonValue data, String token) {
        if (serviceUrl == null || "".equals(serviceUrl.trim()))
            throw new EmptyServiceException("Service Url is null or an empty string");
        if (data == null)
            throw new IllegalArgumentException("Data is null.");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        if (token != null)
            requestBuilder.setHeader("Authorization", "Bearer " + token);


        if (serviceUrl.charAt(serviceUrl.length() - 1) != '/')
            serviceUrl = serviceUrl + "/";

        URI url;
        try {
            url = URI.create(serviceUrl);
            url = url.resolve(service);
            logger.serverInfo("Sending POST request to: " + url.toString() + " with data: \n" + data);
            requestBuilder.uri(url);
        } catch (IllegalArgumentException ex) {
            throw new InvalidServiceUrlException("The format of the serviceUrl is invalid or isn't http/https");
        }
        HttpRequest request = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(data.toString()))
                .header("Content-Type", "application/json").build();

        return sendRequest(url, request);
    }

    private String sendGETRequest(String serviceUrl, String service, String token) {
        if (serviceUrl == null || "".equals(serviceUrl.trim()))
            throw new EmptyServiceException("Service Url is null or an empty string");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        if (token != null)
            requestBuilder.setHeader("Authorization", "Bearer " + token);

        if (serviceUrl.charAt(serviceUrl.length() - 1) != '/')
            serviceUrl = serviceUrl + "/";

        URI url;
        try {
            url = URI.create(serviceUrl);
            url = url.resolve(service);
            logger.serverInfo("Sending GET request to: " + url.toString());
            requestBuilder.uri(url);
        } catch (IllegalArgumentException ex) {
            throw new InvalidServiceUrlException("The format of the serviceUrl is invalid or isn't http/https");
        }
        HttpRequest request = requestBuilder.GET().build();

        return sendRequest(url, request);
    }

    private String sendRequest(URI url, HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            logger.serverInfo("The response of the server to the request to " + url.toString() + " was HTTP" + +response.statusCode() + ": \n" + responseBody);
            if (response.statusCode() == HttpURLConnection.HTTP_OK)
                return responseBody;
            else {
                logger.serverError("The response of the server to the request to " + url.toString() + " was HTTP" + +response.statusCode() + ": \n" + responseBody);
                throw new ConnectionFailedException(new IllegalArgumentException("The status code of the response to " + url.toString() + " was: " + response.statusCode()));
            }
        } catch (IOException e) {
            logger.serverInfo("The connection failed: " + e.getMessage());
            throw new ConnectionFailedException(e);
        } catch (InterruptedException e) {
            logger.serverInfo("Interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
            throw new ConnectionFailedException(e);
        }


    }

    public String authenticate(String serviceUrl, String user, String password) {
        JsonObjectBuilder credentials = Json.createObjectBuilder();
        credentials.add("username", user);
        credentials.add("password", password);

        return sendPOSTRequest(serviceUrl, "authenticate", credentials.build(), null);
    }


    public String sendSymbolTableRequestToDictionaryService(String serviceUrl, JsonObject jsonSymbols, String token) {
        return sendPOSTRequest(serviceUrl, "qaGetSymbolsDefinition", jsonSymbols, token);
    }

    public String sendAcronymsRequestToDictionaryService(String serviceUrl, String token) {
        return sendGETRequest(serviceUrl, "qaGetAcronyms", token);
    }

    public String injectSymbols(String serviceUrl, JsonObject symbols, String token) {
        return sendPOSTRequest(serviceUrl, "qaAddSymbolsDefinition", symbols, token);
    }

    public String sendModuleRequestToDictionaryService(String serviceUrl, String token) {
        return sendGETRequest(serviceUrl, "qaGetModules", token);
    }

    public String sendCategoriesRequestToDictionaryService(String serviceUrl, String token) {
        return sendGETRequest(serviceUrl, "qaGetCategories", token);
    }

    public String injectModules(String serviceUrl, JsonObject modules, String token) {
        return sendPOSTRequest(serviceUrl, "qaAddModules", modules, token);
    }

    public String injectCategories(String serviceUrl, JsonArray categories, String token) {
        return sendPOSTRequest(serviceUrl, "qaAddCategories", categories, token);
    }

    public String sendUnitsRequestToDictionaryService(String serviceUrl, String token) {
        return sendGETRequest(serviceUrl, "qaGetUnitSystem", token);
    }

    public String sendIndexesRequestToDictionaryService(String serviceUrl, String token) {
        return sendGETRequest(serviceUrl, "qaGetIndexesDefinition", token);
    }

    public String injectIndexes(String serviceUrl, JsonObject indexes, String token) {
        return sendPOSTRequest(serviceUrl, "qaAddIndexesDefinition", indexes, token);
    }

}
