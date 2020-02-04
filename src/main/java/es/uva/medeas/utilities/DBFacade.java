package es.uva.medeas.utilities;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.utilities.exceptions.ConnectionFailedException;
import es.uva.medeas.utilities.exceptions.EmptyServiceException;
import es.uva.medeas.utilities.exceptions.InvalidServiceUrlException;
import es.uva.medeas.utilities.exceptions.ServiceResponseFormatNotValid;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;


import javax.json.*;

import java.io.StringReader;
import java.util.List;

public class DBFacade {

    protected static ServiceConnectionHandler handler = new ServiceConnectionHandler();

    protected static Logger LOG = Loggers.get(DBFacade.class.getSimpleName());

    /**
     * Searches for the symbols given as a parameter in the DB
     * @param symbols
     * @return A SymbolTable with the symbols that have been found in the DB (might not be all).
     * The symbols contain the information found in the DB (type, module, etc).
     *
     * If the response from the service contains a duplicated symbol, it takes the first one and logs a warning.
     *
     * @throws ServiceResponseFormatNotValid If the response of the service doesn't have a valid format.
     * @throws InvalidServiceUrlException If the url isn't valid (doesn't have a protocol or invalid format)
     * @throws ConnectionFailedException If the domain address can't be resolved or the page is inaccessible.
     * @throws EmptyServiceException If {@code serviceUrl} is empty if null
     * @throws IllegalArgumentException If {@code symbols} is null
     */
    public static SymbolTable getExistingSymbolsFromDB(String serviceUrl, List<String> symbols) {

        String serviceResponse = handler.sendRequestToDictionaryService(serviceUrl, symbols);

        JsonReader jsonReader = Json.createReader(new StringReader(serviceResponse));
        try {
            JsonArray symbolsFound = jsonReader.readArray();
            return createSymbolTableFromJson(symbolsFound);
        } catch (JsonException ex) {
            throw new ServiceResponseFormatNotValid("Expected a JSON array of symbols.\nActual response:" + serviceResponse);
        } finally {
            jsonReader.close();
        }
    }

    protected static SymbolTable createSymbolTableFromJson(JsonArray symbolsFound) {
        SymbolTable table = new SymbolTable();

        for(int i=0;i<symbolsFound.size();i++){
            try {
                JsonObject jsonSymbol = symbolsFound.getJsonObject(i);
                Symbol symbol = jsonObjectToSymbol(jsonSymbol);
                if(table.hasSymbol(symbol.getToken()))
                    LOG.warn("Received duplicated symbol from the dictionary service.");
                else
                    table.addSymbol(symbol);
            }catch (ClassCastException ex) {
                throw new ServiceResponseFormatNotValid("Expected an array of symbols.\nActual response: " + symbolsFound.toString());
            }catch (IllegalArgumentException ex){
                throw new ServiceResponseFormatNotValid(ex.getMessage());
            }

        }

        return table;
    }

    private static Symbol jsonObjectToSymbol(JsonObject jsonSymbol) {
        String token = jsonSymbol.getString("symbol",null);

        if(token==null)
            throw new IllegalArgumentException("Symbol: "+ jsonSymbol.toString() + "does't have a 'symbol' key");

        return new Symbol(token.trim());
    }

}
