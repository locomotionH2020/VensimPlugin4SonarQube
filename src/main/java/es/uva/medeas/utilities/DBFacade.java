package es.uva.medeas.utilities;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.utilities.exceptions.ServiceResponseFormatNotValid;


import javax.json.*;
import javax.json.stream.JsonParsingException;

import java.io.StringReader;
import java.util.List;

public class DBFacade {

    protected static DbServiceHandler handler = new DbServiceHandler();

    /**
     * Searches for the symbols given as a parameter in the DB
     * @param symbols
     * @return A SymbolTable with the symbols that have been found in the DB (might not be all).
     */
    public static SymbolTable getExistingSymbolsFromDB(String serviceUrl, List<String> symbols) {
        String serviceResponse = handler.sendRequestToService(serviceUrl, symbols);


        JsonReader jsonReader = Json.createReader(new StringReader(serviceResponse));
        try {
            JsonArray symbolsFound = jsonReader.readArray(); //TODO handle invalid data and invalid url/no connection
            return createSymbolTableFromJson(symbolsFound);
        } catch (JsonParsingException ex) {
            throw new ServiceResponseFormatNotValid();
        } finally {
            jsonReader.close();
        }





    }


    protected static SymbolTable createSymbolTableFromJson(JsonArray symbolsFound) {
        SymbolTable table = new SymbolTable();

        for(int i=0;i<symbolsFound.size();i++){
            try {
                JsonObject jsonSymbol = symbolsFound.getJsonObject(i);
                Symbol symbol = jsonObjectToSymbol(jsonSymbol); //TODO si hay dos simbolos con el mismo token, lanzar excepcion y loggearlo.
                table.addSymbol(symbol);
            }catch (ClassCastException ex) {
                throw new ServiceResponseFormatNotValid();
            }

        }

        return table;
    }

    private static Symbol jsonObjectToSymbol(JsonObject jsonSymbol) {
        String token = jsonSymbol.getString("symbol",null);
        //String type = jsonSymbol.getString("type"); //TODO transformar el tipo
        if(token==null)
            throw new ServiceResponseFormatNotValid();

        return new Symbol(token.trim());
    }

}
