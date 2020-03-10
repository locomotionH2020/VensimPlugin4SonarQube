package es.uva.medeas.utilities;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import es.uva.medeas.utilities.exceptions.ConnectionFailedException;
import es.uva.medeas.utilities.exceptions.EmptyServiceException;
import es.uva.medeas.utilities.exceptions.InvalidServiceUrlException;
import es.uva.medeas.utilities.exceptions.ServiceResponseFormatNotValid;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;


import javax.json.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class DBFacade {

    public static final String FIELD_SYMBOL_NAME = "name";
    public static final String FIELD_SYMBOLS = "symbols";
    public static final String FIELD_INDEXES = "indexes";
    public static final String FIELD_SYMBOL_COMMENT = "definition";
    public static final String FIELD_SYMBOL_CATEGORY = "category";
    public static final String FIELD_SYMBOL_TYPE = "programming symbol type";
    public static final String FIELD_SYMBOL_INDEXES = "indexes";
    public static final String FIELD_SYMBOL_MODULES = "modules";
    public static final String FIELD_INDEX_VALUES = "values";
    public static  final  String    FIELD_SyMBOL_UNITS = "unit";
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
            JsonObject symbolsFound = jsonReader.readObject();

            return createSymbolTableFromJson(symbolsFound);
        } catch (JsonException ex) {
            throw new ServiceResponseFormatNotValid("Expected an object,\nActual response:" + serviceResponse);
        } finally {
            jsonReader.close();
        }
    }

    protected static SymbolTable createSymbolTableFromJson(JsonObject symbolsFound) {
        SymbolTable table = new SymbolTable();

        JsonArray symbols = symbolsFound.getJsonArray(FIELD_SYMBOLS);
        JsonArray indexes = symbolsFound.getJsonArray(FIELD_INDEXES);

        loadSymbols(table,symbols);
        loadIndexes(table,indexes);

        return table;
    }

    private static void loadIndexes(SymbolTable table, JsonArray indexes) {
        for(int i=0;i<indexes.size();i++) {

            JsonObject jsonIndex = indexes.getJsonObject(i);

            String name = jsonIndex.getString(FIELD_SYMBOL_NAME);
            Symbol index = UtilityFunctions.getSymbolOrCreate(table,name);

            String comment = jsonIndex.getString(FIELD_SYMBOL_COMMENT);
            index.setComment(comment);


            JsonArray jsonValues= jsonIndex.getJsonArray(FIELD_INDEX_VALUES);
            for(int v=0;v<jsonValues.size();v++) {
                String indexValue = jsonValues.getString(i);
                index.addDependency(UtilityFunctions.getSymbolOrCreate(table,indexValue));
            }

        }
    }

    private static void loadSymbols(SymbolTable table, JsonArray symbols) {
        for(int s=0;s<symbols.size();s++) {

            JsonObject jsonSymbol = symbols.getJsonObject(s);
            String name = jsonSymbol.getString(FIELD_SYMBOL_NAME);
            Symbol symbol = UtilityFunctions.getSymbolOrCreate(table,name);

            String comment = jsonSymbol.getString(FIELD_SYMBOL_COMMENT);
            symbol.setComment(comment);

            String units = jsonSymbol.getString(FIELD_SyMBOL_UNITS);
            symbol.setUnits(units);

            String category = jsonSymbol.getString(FIELD_SYMBOL_CATEGORY);
            symbol.setCategory(category);

            String type = jsonSymbol.getString(FIELD_SYMBOL_TYPE);
            symbol.setType(SymbolType.valueOf(type));
            
            JsonArray jsonIndexes = jsonSymbol.getJsonArray(FIELD_SYMBOL_INDEXES);
            List<Symbol> indexes = new ArrayList<>();
            for(int i=0;i<jsonIndexes.size();i++) {
               String index = jsonIndexes.getString(i);
                indexes.add(UtilityFunctions.getSymbolOrCreate(table,index));
            }

            symbol.addIndexLine(indexes);

            JsonArray modules = jsonSymbol.getJsonArray(FIELD_SYMBOL_MODULES);
            for(int i=0;i<modules.size();i++) {
                String module = modules.getString(i);
                symbol.addModule(module);
            }

        }
    }



}
