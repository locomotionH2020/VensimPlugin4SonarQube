package es.uva.medeas.utilities;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class DBFacade {


    /**
     * Searches for the symbols given as a parameter in the DB
     * @param symbols
     * @return A SymbolTable with the symbols that have been found in the DB (might not be all).
     */
    public SymbolTable getExistingSymbolsFromDB(String serviceUrl, List<String> symbols){
        JsonArray symbolsFound = sendRequestToService(serviceUrl,symbols);

        return createSymbolTableFromJson(symbolsFound);
    }


    protected SymbolTable createSymbolTableFromJson(JsonArray symbolsFound) {
        SymbolTable table = new SymbolTable();

        for(int i=0;i<symbolsFound.size();i++){
            JsonObject jsonSymbol = symbolsFound.getJsonObject(i);
            Symbol symbol = jsonObjectToSymbol(jsonSymbol); //TODO si hay dos simbolos con el mismo token, lanzar excepcion y loggearlo.
            table.addSymbol(symbol);
        }

        return table;
    }

    private Symbol jsonObjectToSymbol(JsonObject jsonSymbol) {
        String token = jsonSymbol.getString("symbol");
        //String type = jsonSymbol.getString("type"); //TODO transformar el tipo
        Symbol symbol = new Symbol(token);



        return symbol;
    }


    protected JsonArray sendRequestToService(String serviceUrl, List<String> symbols){
        HttpClient client = HttpClient.newBuilder().build();


        URI url = URI.create(serviceUrl);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url).POST(HttpRequest.BodyPublishers.ofString(Arrays.toString(symbols.toArray()))) //TODO Testear esto
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String content = response.body();
            JsonReader jsonReader = Json.createReader(new StringReader(content));
            JsonArray symbolsFound = jsonReader.readArray(); //TODO handle invalid data
            jsonReader.close();


            return symbolsFound;
        } catch (InterruptedException | IOException e) {
            e.printStackTrace(); //TODO
            return null;
        }
    }
}
