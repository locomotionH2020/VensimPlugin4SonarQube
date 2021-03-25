package es.uva.locomotion.service;

import es.uva.locomotion.model.*;
import es.uva.locomotion.model.Module;
import es.uva.locomotion.model.category.Category;
import es.uva.locomotion.model.category.CategoryMap;
import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import es.uva.locomotion.utilities.UtilityFunctions;
import es.uva.locomotion.utilities.exceptions.ConnectionFailedException;
import es.uva.locomotion.utilities.exceptions.EmptyServiceException;
import es.uva.locomotion.utilities.exceptions.InvalidServiceUrlException;
import es.uva.locomotion.utilities.exceptions.ServiceResponseFormatNotValid;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.antlr.v4.runtime.misc.Triple;


import javax.json.*;

import java.io.StringReader;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DBFacade {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_INDEX_NAME = "indexName";
    public static final String FIELD_SYMBOLS = "symbols";
    public static final String FIELD_INDEXES = "indexes";
    public static final String FIELD_SYMBOL_COMMENT = "definition";
    public static final String FIELD_SYMBOL_CATEGORY = "category";
    public static final String FIELD_SYMBOL_TYPE_SEND = "programmingsymboltype";
    public static final String FIELD_SYMBOL_TYPE_RECIEVE = "programmingSymbolType";
    public static final String FIELD_SYMBOL_INDEXES = "indexes";
    public static final String FIELD_SYMBOL_MODULES = "modules";
    public static final String FIELD_INDEX_VALUES = "values";
    public static final String FIELD_INJECTION_IS_INDEXED = "isIndexed";
    public static final String FIELD_SYMBOL_UNITS = "unit";
    public static final String FIELD_UNITS_CONCEPTS = "concepts";

    public static final List<String> REQUIRED_FIELDS_IN_SYMBOL = List.of(FIELD_NAME, FIELD_SYMBOL_TYPE_RECIEVE, FIELD_SYMBOL_COMMENT, FIELD_SYMBOL_CATEGORY, FIELD_SYMBOL_INDEXES, FIELD_SYMBOL_MODULES, FIELD_SYMBOL_UNITS);
    public static final List<String> REQUIRED_FIELDS_IN_INDEXES = List.of(FIELD_INDEX_NAME, FIELD_INDEX_VALUES, FIELD_SYMBOL_COMMENT);
    private static final String FIELD_SYMBOL_MODULES_MAIN = "main";
    private static final String FIELD_SYMBOL_MODULES_SECONDARY = "secondary";
    private static final String FIELD_CATEGORY_LEVEL = "level";
    private static final String FIELD_CATEGORY_SUPER_CATEGORY = "super_category";
    private static final String FIELD_MODULE = "module";

    public static final String KEY_INDEXES = "indexes";
    public static final String KEY_EXCEL = "excels";
    public static final String KEY_SHEET = "sheet";
    public static final String KEY_CELLRANGE = "cellrange";
    public static final String KEY_SERIES = "series";
    public static final String KEY_INFO = "info";
    public static final String KEY_FILENAME = "filename";

    protected static ServiceConnectionHandler handler = new ServiceConnectionHandler();

    protected static VensimLogger LOG = VensimLogger.getInstance();


    public static String getAuthenticationToken(String serviceUrl, String user, String password) {
        return handler.authenticate(serviceUrl, user, password);
    }

    public static SymbolTable getExistingSymbolsAndIndexesFromDB(String serviceUrl, List<String> rawSymbols, String token) {
        JsonObject jsonSymbols = getJsonFromSymbolList(rawSymbols);
        JsonObject symbols = makeExistingSymbolsCall(serviceUrl, jsonSymbols, token);
        JsonArray indexes = makeExistingIndexesCall(serviceUrl, token);
        return createSymbolsAndIndexTableFromJson(symbols, indexes);
    }

    protected static SymbolTable createSymbolsAndIndexTableFromJson(JsonObject symbolsFound, JsonArray indexesFound) {
        SymbolTable table = new SymbolTable();


        if (!symbolsFound.containsKey(FIELD_SYMBOLS)) {
            throw new ServiceResponseFormatNotValid("Missing '" + FIELD_SYMBOLS + "' field.");
        }

        JsonArray symbols = symbolsFound.getJsonArray(FIELD_SYMBOLS);
        try {
            loadSymbols(table, symbols);
        } catch (ServiceResponseFormatNotValid ex) {
            ex.setServiceResponse(symbolsFound.toString());
            throw ex;
        }
        try {
            loadIndexes(table, indexesFound);
        } catch (ServiceResponseFormatNotValid ex) {
            ex.setServiceResponse(indexesFound.toString());
            throw ex;
        }
        return table;
    }

    public static void injectSymbolsAndIndexes(String serviceUrl, List<Symbol> rawSymbols, List<Symbol> rawIndexes, String module, String token) {
        injectSymbols(serviceUrl, rawSymbols, module, token);
        injectIndexes(serviceUrl, rawIndexes, token);
    }

    /**
     * Searches for the symbols given as a parameter in the DB
     *
     * @param symbols list of given symbols
     * @return A SymbolTable with the symbols that have been found in the DB (might not be all).
     * The symbols contain the information found in the DB (type, module, etc).
     * <p>
     * If the response from the service contains a duplicated symbol, it takes the first one and logs a warning.
     * @throws ServiceResponseFormatNotValid If the response of the service doesn't have a valid format.
     * @throws InvalidServiceUrlException    If the url isn't valid (doesn't have a protocol or invalid format)
     * @throws ConnectionFailedException     If the domain address can't be resolved or the page is inaccessible.
     * @throws EmptyServiceException         If {@code serviceUrl} is empty if null
     * @throws IllegalArgumentException      If {@code symbols} is null
     */
    public static SymbolTable getExistingSymbolsFromDB(String serviceUrl, List<String> symbols, String token) {
        JsonObject jsonSymbols = getJsonFromSymbolList(symbols);

        return createSymbolTableFromJson(makeExistingSymbolsCall(serviceUrl, jsonSymbols, token));
    }

    private static JsonObject makeExistingSymbolsCall(String serviceUrl, JsonObject jsonSymbols, String token) {
        String serviceResponse = handler.sendSymbolTableRequestToDictionaryService(serviceUrl, jsonSymbols, token);
        if (serviceResponse == null)
            return null;

        try (JsonReader jsonReader = Json.createReader(new StringReader(serviceResponse))) {
            return jsonReader.readObject();
        } catch (JsonException ex) {
            throw new ServiceResponseFormatNotValid("Expected an object.", serviceResponse);
        } catch (ServiceResponseFormatNotValid ex) {
            ex.setServiceResponse(serviceResponse);
            throw ex;
        }
    }

    private static JsonObject getJsonFromSymbolList(List<String> symbols) {
        if (symbols == null)
            throw new IllegalArgumentException("The list of symbols can't be null.");

        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (String s : symbols)
            arrayBuilder.add(s);

        jsonBuilder.add("symbols", arrayBuilder);
        return jsonBuilder.build();
    }

    protected static SymbolTable createSymbolTableFromJson(JsonObject symbolsFound) {
        SymbolTable table = new SymbolTable();

        if (!symbolsFound.containsKey(FIELD_SYMBOLS)) {
            throw new ServiceResponseFormatNotValid("Missing '" + FIELD_SYMBOLS + "' field.", symbolsFound.toString());
        }

        JsonArray symbols = symbolsFound.getJsonArray(FIELD_SYMBOLS);

        try {
            loadSymbols(table, symbols);
        } catch (ServiceResponseFormatNotValid ex) {
            ex.setServiceResponse(symbolsFound.toString());
            throw ex;
        }
        return table;
    }

    private static void validateJsonSymbol(JsonObject jsonSymbol) {
        if (!jsonSymbol.containsKey(FIELD_NAME)) {
            throw new ServiceResponseFormatNotValid("Missing '" + FIELD_NAME + "' field from a symbol.");
        }

        String name = jsonSymbol.getString(FIELD_NAME);
        for (String field : REQUIRED_FIELDS_IN_SYMBOL) {

            if (!jsonSymbol.containsKey(field)) {
                throw new ServiceResponseFormatNotValid("Missing '" + field + "' field in symbol '" + name + "'.");
            }
        }
    }

    private static void loadSymbols(SymbolTable table, JsonArray symbols) {
        for (int s = 0; s < symbols.size(); s++) {

            JsonObject jsonSymbol = symbols.getJsonObject(s);

            validateJsonSymbol(jsonSymbol);

            String name = jsonSymbol.getString(FIELD_NAME);

            if (table.hasSymbol(name)) {
                LOG.info("Received duplicated symbol '" + name + "' from the dictionary service.");
                continue;
            }

            Symbol symbol = new Symbol(name);

            String comment = jsonSymbol.getString(FIELD_SYMBOL_COMMENT);
            symbol.setComment(comment);

            String units = jsonSymbol.getString(FIELD_SYMBOL_UNITS);
            symbol.setUnits(units);
            String category = jsonSymbol.getString(FIELD_SYMBOL_CATEGORY);
            symbol.setCategory(Category.create(category));

            String type = jsonSymbol.getString(FIELD_SYMBOL_TYPE_RECIEVE);
            try {
                symbol.setType(SymbolType.valueOf(type));
            } catch (IllegalArgumentException ex) {
                throw new ServiceResponseFormatNotValid("The symbol '" + name + "' has an unknown programming type: '" + type + "'");
            }

            JsonArray jsonIndexes = jsonSymbol.getJsonArray(FIELD_SYMBOL_INDEXES);
            List<Symbol> indexes = new ArrayList<>();
            for (int i = 0; i < jsonIndexes.size(); i++) {
                String index = jsonIndexes.getString(i);
                indexes.add(UtilityFunctions.getSymbolOrCreate(table, index));
            }

            symbol.addIndexLine(indexes);

            JsonObject modules = jsonSymbol.getJsonObject(FIELD_SYMBOL_MODULES);
            symbol.setPrimary_module(new Module(modules.getString(FIELD_SYMBOL_MODULES_MAIN)));

            JsonArray secondaryModules = modules.getJsonArray(FIELD_SYMBOL_MODULES_SECONDARY);
            for (int i = 0; i < secondaryModules.size(); i++) {
                String module = secondaryModules.getString(i);
                symbol.addShadow_module(new Module(module));
            }

            JsonArray excelJsonList = jsonSymbol.getJsonArray(FIELD_SYMBOL_MODULES);
            if (excelJsonList != null) {
                List<ExcelRef> excelList = new ArrayList<>();
                for (int i = 0; i < excelJsonList.size(); i++) {
                    JsonObject excelJson = excelJsonList.getJsonObject(i);

                    String sheet = excelJson.getString(KEY_SHEET);
                    String filename = excelJson.getString(KEY_SHEET);
                    ExcelRef excel = new ExcelRef(sheet, filename);

                    JsonArray infoJsonList = excelJson.getJsonArray(KEY_INFO);
                    for (int j = 0; j < infoJsonList.size(); j++) {
                        JsonObject infoJson = infoJsonList.getJsonObject(j);

                        JsonArray indexJson = infoJson.getJsonArray(KEY_INDEXES);
                        List<String> indexList = null;
                        if (indexJson != null) {
                            indexList = new ArrayList<>();
                            for (int k = 0; k < indexJson.size(); k++) {
                                indexList.add(indexJson.getString(k));
                            }
                        }
                        String cellRange = infoJson.getString(KEY_CELLRANGE);
                        String series = infoJson.getString(KEY_SERIES);

                        if (series == null) {
                            excel.addCellRangeInformation(indexList, cellRange);
                        } else {
                            excel.addCellRangeInformation(indexList, cellRange, series);
                        }
                    }
                    excelList.add(excel);
                }
                symbol.setExcel(excelList);
            }

            table.addSymbol(symbol);

        }
    }


    public static void injectSymbols(String serviceUrl, List<Symbol> symbols, String module, String token) { //TODO .filter(symbol -> symbol.getCategory() != null)
        List<Symbol> rawSymbols = symbols.stream().filter(symbol -> !List.of(SymbolType.Subscript_Value, SymbolType.Subscript,
                SymbolType.UNDETERMINED, SymbolType.UNDETERMINED_FUNCTION, SymbolType.Function).contains(symbol.getType())).collect(Collectors.toList());

        rawSymbols = rawSymbols.stream().filter(Symbol::isValid).filter(Predicate.not(Symbol::isFiltered)).collect(Collectors.toList());
        rawSymbols.sort(Comparator.comparing(Symbol::getToken));

        JsonArray jsonSymbols = getInjectSymbolsJson(rawSymbols);

        JsonObjectBuilder requestBuilder = Json.createObjectBuilder();

        requestBuilder.add(FIELD_SYMBOLS, jsonSymbols);
        requestBuilder.add(FIELD_MODULE, module);
        handler.injectSymbols(serviceUrl, requestBuilder.build(), token);
    }

    private static JsonArray getInjectSymbolsJson(List<Symbol> symbols) {
        // No incluye los índices, para eso está getInjectIndexesJson
        JsonArrayBuilder jsonSymbols = Json.createArrayBuilder();

        for (Symbol s : symbols) {
            JsonObjectBuilder jsonSymbol = Json.createObjectBuilder();

            jsonSymbol.add(FIELD_NAME, s.getToken().trim());
            jsonSymbol.add(FIELD_SYMBOL_UNITS, s.getUnits().trim());
            jsonSymbol.add(FIELD_SYMBOL_COMMENT, s.getComment().trim());
            jsonSymbol.add(FIELD_INJECTION_IS_INDEXED, String.valueOf(!s.getIndexes().isEmpty()).toLowerCase());
            jsonSymbol.add(FIELD_SYMBOL_CATEGORY, s.getCategory().getName().trim());
            jsonSymbol.add(FIELD_SYMBOL_TYPE_SEND, s.getType().toString());
            if (!s.getExcel().isEmpty()) {
                JsonArrayBuilder excelBuilder = Json.createArrayBuilder();

                for (ExcelRef excel : s.getExcel()) {
                    JsonObjectBuilder fileBuilder = Json.createObjectBuilder();
                    fileBuilder.add(KEY_SHEET, excel.getSheet());
                    fileBuilder.add(KEY_FILENAME, excel.getFilename());
                    JsonArrayBuilder infoListBuilder = Json.createArrayBuilder();

                    for (Triple<List<String>, String, String> info : excel.getCellRangeInformation()) {
                        JsonObjectBuilder infoBuilder = Json.createObjectBuilder();

                        if (!info.a.isEmpty()) {
                            JsonArrayBuilder indexBuilder = Json.createArrayBuilder();
                            for (String indexName : info.a) {
                                indexBuilder.add(indexName);
                            }
                            infoBuilder.add(KEY_INDEXES, indexBuilder);
                        }
                        infoBuilder.add(KEY_CELLRANGE, info.b);
                        if (info.c != null)
                            infoBuilder.add(KEY_SERIES, info.c);

                        infoListBuilder.add(infoBuilder);
                    }
                    fileBuilder.add(KEY_INFO, infoListBuilder);


                    excelBuilder.add(fileBuilder);
                }
                jsonSymbol.add(KEY_EXCEL, excelBuilder);
            }
            jsonSymbols.add(jsonSymbol);
        }

        return jsonSymbols.build();
    }


    public static AcronymsList getExistingAcronymsFromDB(String serviceUrl, String token) {

        String serviceResponse = handler.sendAcronymsRequestToDictionaryService(serviceUrl, token);

        if (serviceResponse == null)
            return null;

        try (JsonReader jsonReader = Json.createReader(new StringReader(serviceResponse))) {

            JsonArray acronymsFound = jsonReader.readArray();
            return createAcronymListFromJson(acronymsFound);
        } catch (JsonException ex) {
            throw new ServiceResponseFormatNotValid("Expected an array.", serviceResponse);
        } catch (ServiceResponseFormatNotValid ex) {
            ex.setServiceResponse(serviceResponse);
            throw ex;
        }

    }

    private static AcronymsList createAcronymListFromJson(JsonArray acronymsFound) {
        AcronymsList list = new AcronymsList();
        JsonObject acronym;
        for (JsonValue jsonValue : acronymsFound) {
            try {
                acronym = (JsonObject) jsonValue;
                list.addAcronym(acronym.getString(FIELD_NAME));
            } catch (ClassCastException e) {
                throw new ServiceResponseFormatNotValid("Expected object inside array.");
            } catch (NullPointerException e) {
                throw new ServiceResponseFormatNotValid("Missing '" + FIELD_NAME + "' field.");
            }

        }
        return list;
    }

    public static Set<String> getExistingModulesFromDB(String serviceUrl, String token) {
        String serviceResponse = handler.sendModuleRequestToDictionaryService(serviceUrl, token);

        if (serviceResponse == null)
            return null;

        try (JsonReader jsonReader = Json.createReader(new StringReader(serviceResponse))) {

            JsonArray modulesFound = jsonReader.readObject().getJsonArray(FIELD_SYMBOL_MODULES);

            if (modulesFound == null) {
                throw new ServiceResponseFormatNotValid("\"" + FIELD_SYMBOL_MODULES + "\" key not found in object.", serviceResponse);
            }
            return createModulesListFromJson(modulesFound);
        } catch (JsonException ex) {
            throw new ServiceResponseFormatNotValid("Expected an object.", serviceResponse);
        } catch (ServiceResponseFormatNotValid ex) {
            ex.setServiceResponse(serviceResponse);
            throw ex;
        }

    }

    private static Set<String> createModulesListFromJson(JsonArray modulesFound) {
        Set<String> list = new HashSet<>();
        JsonObject acronym;
        for (JsonValue jsonValue : modulesFound) {
            if (jsonValue.getValueType() != JsonValue.ValueType.STRING) {
                throw new ServiceResponseFormatNotValid("Format error '" + jsonValue + "' is not a module name.");
            }
            String module = jsonValue.toString().replaceAll("\"", "");

            if (list.contains(module)) {
                LOG.warn("Received duplicated module '" + module + "' from the dictionary service.");
            }
            list.add(module);

        }
        return list;
    }

    public static void injectModules(String serviceUrl, List<String> newModules, String token) {

        List<String> alreadyAdded = new ArrayList<>();
        newModules.sort(Comparator.comparing(String::toString));
        JsonObjectBuilder jsonTosend = Json.createObjectBuilder();
        JsonArrayBuilder jsonModulesBuilder = Json.createArrayBuilder();

        for (String st : newModules) {
            if (!alreadyAdded.contains(st)) {
                jsonModulesBuilder.add(st);
                alreadyAdded.add(st);
            } else {
                LOG.warn("Received duplicated module '" + st + "' to inject to the dictionary service.");
            }
        }

        jsonTosend.add("modules", jsonModulesBuilder);
        if (alreadyAdded.size() > 0) {
            handler.injectModules(serviceUrl, jsonTosend.build(), token);
        } else {
            LOG.warn("Module list to inject is empty.");
        }
    }

    public static CategoryMap getExistingCategoriesFromDB(String serviceUrl, String token) {

        String serviceResponse = handler.sendCategoriesRequestToDictionaryService(serviceUrl, token);
        if (serviceResponse == null)
            return null;

        try (JsonReader jsonReader = Json.createReader(new StringReader(serviceResponse))) {
            JsonArray categories = jsonReader.readArray();

            return createCategoryListFromJson(categories);
        } catch (JsonException ex) {
            throw new ServiceResponseFormatNotValid("Expected an array.", serviceResponse);
        } catch (ServiceResponseFormatNotValid ex) {
            ex.setServiceResponse(serviceResponse);
            throw ex;
        }
    }

    protected static CategoryMap createCategoryListFromJson(JsonArray symbolsFound) {
        CategoryMap categoryMap = new CategoryMap();

        for (int s = 0; s < symbolsFound.size(); s++) {
            if (symbolsFound.get(s).getValueType() != JsonValue.ValueType.OBJECT) {
                throw new ServiceResponseFormatNotValid("Recived '" + symbolsFound.get(s) + "' that is not a category json object.");
            }
            JsonObject jsonCategory = symbolsFound.getJsonObject(s);
            validateJsonCategories(jsonCategory);
            String name;
            int level;

            name = jsonCategory.getString(FIELD_NAME);
            if (categoryMap.contains(name)) {
                LOG.info("Received duplicated category '" + name + "' from the dictionary service.");
                continue;
            }
            level = jsonCategory.getInt(FIELD_CATEGORY_LEVEL);


            if (level == 2) { //Subcategory
                String super_categoryName;
                super_categoryName = jsonCategory.getString(FIELD_CATEGORY_SUPER_CATEGORY);

                if (!categoryMap.contains(super_categoryName)) {
                    throw new ServiceResponseFormatNotValid("'" + name + "' father's '" + super_categoryName + "' does not exists or it is a subcategory.");
                }
                Category super_category = categoryMap.createOrSelectCategory(super_categoryName);
                Category category = categoryMap.addSubcategoryTo(super_category, name);

            } else { //Category
                Category category = categoryMap.createOrSelectCategory(name);
            }


        }
        return categoryMap;
    }

    private static void validateJsonCategories(JsonObject jsonSymbol) {
        if (!jsonSymbol.containsKey(FIELD_NAME) || jsonSymbol.get(FIELD_NAME) == JsonValue.NULL) {
            throw new ServiceResponseFormatNotValid("Missing '" + FIELD_NAME + "' field from a category.");
        }

        String name = jsonSymbol.getString(FIELD_NAME);
        if (!jsonSymbol.containsKey(FIELD_CATEGORY_LEVEL) || jsonSymbol.get(FIELD_CATEGORY_LEVEL) == JsonValue.NULL) {
            throw new ServiceResponseFormatNotValid("Missing '" + FIELD_CATEGORY_LEVEL + "' field in category '" + name + "'.");
        }
        int level = jsonSymbol.getInt(FIELD_CATEGORY_LEVEL);

        if (level == 2) {
            if (!jsonSymbol.containsKey(FIELD_CATEGORY_SUPER_CATEGORY) || jsonSymbol.get(FIELD_CATEGORY_SUPER_CATEGORY).toString().equals("\"null\"")) {
                throw new ServiceResponseFormatNotValid("Missing '" + FIELD_CATEGORY_SUPER_CATEGORY + "' field in subcategory '" + name + "'.");
            }
        } else {
            if (jsonSymbol.containsKey(FIELD_CATEGORY_SUPER_CATEGORY) && !jsonSymbol.get(FIELD_CATEGORY_SUPER_CATEGORY).toString().equals("\"null\"")) {
                throw new ServiceResponseFormatNotValid("'" + name + "' can not have a super category.");
            }
        }
    }


    public static void injectCategories(String serviceUrl, List<Category> newCategories, String token) {

        JsonArrayBuilder jsonCategoriesBuilder = Json.createArrayBuilder();

        for (Category category : newCategories) {
            JsonObjectBuilder jsonCategory = Json.createObjectBuilder();
            jsonCategory.add(FIELD_NAME, category.getName());
            if (category.getSuperCategory() == null) { //Category
                jsonCategory.add(FIELD_CATEGORY_LEVEL, 1);
                jsonCategory.add(FIELD_CATEGORY_SUPER_CATEGORY, "null");
                jsonCategoriesBuilder.add(jsonCategory);

            }
        }
        for (Category category : newCategories) {
            JsonObjectBuilder jsonCategory = Json.createObjectBuilder();
            jsonCategory.add(FIELD_NAME, category.getName());
            if (category.getSuperCategory() != null) { //Category
                jsonCategory.add(FIELD_NAME, category.getName());
                jsonCategory.add(FIELD_CATEGORY_LEVEL, 2);
                jsonCategory.add(FIELD_CATEGORY_SUPER_CATEGORY, category.getSuperCategory().getName());
                jsonCategoriesBuilder.add(jsonCategory);
            }
        }
        handler.injectCategories(serviceUrl, jsonCategoriesBuilder.build(), token);
    }

    public static Set<String> getExistingUnitsFromDB(String serviceUrl, String token) {
        String serviceResponse = handler.sendUnitsRequestToDictionaryService(serviceUrl, token);

        if (serviceResponse == null)
            return null;

        try (JsonReader jsonReader = Json.createReader(new StringReader(serviceResponse))) {

            JsonArray unitsFound = jsonReader.readArray();

            if (unitsFound == null) {
                throw new ServiceResponseFormatNotValid("\"" + FIELD_SYMBOL_MODULES + "\" key not found in object.", serviceResponse);
            }
            return createUnitsListFromJson(unitsFound);
        } catch (JsonException ex) {
            throw new ServiceResponseFormatNotValid("Expected an array.", serviceResponse);
        } catch (ServiceResponseFormatNotValid ex) {
            ex.setServiceResponse(serviceResponse);
            throw ex;
        }

    }

    private static Set<String> createUnitsListFromJson(JsonArray unitsFound) {

        Set<String> list = new HashSet<>();
        JsonObject acronym;
        for (int s = 0; s < unitsFound.size(); s++) {
            if (unitsFound.get(s).getValueType() != JsonValue.ValueType.OBJECT) {
                throw new ServiceResponseFormatNotValid("Recived '" + unitsFound.get(s) + "' that is not a unit json object.");
            }
            JsonObject jsonUnit = unitsFound.getJsonObject(s);
            validateJsonUnits(jsonUnit);
            String unit;

            unit = jsonUnit.getString(FIELD_SYMBOL_UNITS);
            if (list.contains(unit)) {
                LOG.warn("Received duplicated unit '" + unit + "' from the dictionary service."); //TODO quitar, no son únicas.
                continue;
            }
            list.add(unit);

        }
        return list;
    }

    private static void validateJsonUnits(JsonObject jsonSymbol) {
        if (!jsonSymbol.containsKey(FIELD_SYMBOL_UNITS) || jsonSymbol.get(FIELD_SYMBOL_UNITS) == JsonValue.NULL) {
            throw new ServiceResponseFormatNotValid("Missing '" + FIELD_SYMBOL_UNITS + "' field from a unit.");
        }

        String name = jsonSymbol.getString(FIELD_SYMBOL_UNITS);
        if (!jsonSymbol.containsKey(FIELD_UNITS_CONCEPTS) || jsonSymbol.get(FIELD_UNITS_CONCEPTS) == JsonValue.NULL) {
            throw new ServiceResponseFormatNotValid("Missing '" + FIELD_UNITS_CONCEPTS + "' field in category '" + name + "'.");
        }
    }

    public static SymbolTable getExistingIndexesFromDB(String serviceUrl, String token) {

        String serviceResponse = handler.sendIndexesRequestToDictionaryService(serviceUrl, token);
        return createIndexTableFromJson(makeExistingIndexesCall(serviceUrl, token));
    }

    private static JsonArray makeExistingIndexesCall(String serviceUrl, String token) {
        String serviceResponse = handler.sendIndexesRequestToDictionaryService(serviceUrl, token);
        if (serviceResponse == null)
            return null;

        try (JsonReader jsonReader = Json.createReader(new StringReader(serviceResponse))) {
            return jsonReader.readArray();

        } catch (JsonException ex) {
            throw new ServiceResponseFormatNotValid("Expected an array.", serviceResponse);
        } catch (ServiceResponseFormatNotValid ex) {
            ex.setServiceResponse(serviceResponse);
            throw ex;
        }
    }

    protected static SymbolTable createIndexTableFromJson(JsonArray symbolsFound) {
        SymbolTable table = new SymbolTable();


        try {
            loadIndexes(table, symbolsFound);
        } catch (ServiceResponseFormatNotValid ex) {
            ex.setServiceResponse(symbolsFound.toString());
            throw ex;
        }
        return table;
    }

    private static void loadIndexes(SymbolTable table, JsonArray indexes) {
        for (int i = 0; i < indexes.size(); i++) {

            JsonObject jsonIndex = indexes.getJsonObject(i);
            validateJsonIndex(jsonIndex);

            String name = jsonIndex.getString(FIELD_INDEX_NAME);
            Symbol index = UtilityFunctions.getSymbolOrCreate(table, name);

            String comment = jsonIndex.getString(FIELD_SYMBOL_COMMENT);
            index.setComment(comment);
            index.setType(SymbolType.Subscript);


            JsonArray jsonValues = jsonIndex.getJsonArray(FIELD_INDEX_VALUES);
            for (int v = 0; v < jsonValues.size(); v++) {
                String indexValue = jsonValues.getString(v);
                Symbol valueSymbol = UtilityFunctions.getSymbolOrCreate(table, indexValue);
                valueSymbol.setType(SymbolType.Subscript_Value);
                index.addDependency(valueSymbol);
            }

        }
    }

    private static void validateJsonIndex(JsonObject jsonIndex) {
        if (!jsonIndex.containsKey(FIELD_INDEX_NAME)) {
            throw new ServiceResponseFormatNotValid("Missing '" + FIELD_INDEX_NAME + "' field from an index.");
        }

        String name = jsonIndex.getString(FIELD_INDEX_NAME);
        for (String field : REQUIRED_FIELDS_IN_INDEXES) {
            if (!jsonIndex.containsKey(field)) {
                throw new ServiceResponseFormatNotValid("Missing '" + field + "' field in the index '" + name + "'.");
            }
        }
    }


    public static void injectIndexes(String serviceUrl, List<Symbol> rawIndexes, String token) {

        List<Symbol> indexes = rawIndexes.stream().filter(symbol -> symbol.getType() == SymbolType.Subscript).sorted(Comparator.comparing(Symbol::getToken)).collect(Collectors.toList());

        JsonArray jsonIndexes = getInjectIndexesJson(indexes);

        JsonObjectBuilder jsonToSend = Json.createObjectBuilder();
        jsonToSend.add("indexes", jsonIndexes);
        handler.injectIndexes(serviceUrl, jsonToSend.build(), token);
    }


    private static JsonArray getInjectIndexesJson(List<Symbol> indexes) {
        JsonArrayBuilder jsonIndexes = Json.createArrayBuilder();

        for (Symbol index : indexes) {
            JsonObjectBuilder jsonSymbol = Json.createObjectBuilder();

            jsonSymbol.add(FIELD_INDEX_NAME, index.getToken().trim());

            JsonArrayBuilder jsonValues = Json.createArrayBuilder();
            List<Symbol> dependencies = new ArrayList<>(index.getDependencies());
            dependencies.sort(Comparator.comparing(Symbol::getToken));
            for (Symbol value : dependencies) {
                jsonValues.add(value.getToken().trim());
            }
            jsonSymbol.add(FIELD_INDEX_VALUES, jsonValues.build());
            jsonSymbol.add(FIELD_SYMBOL_COMMENT, index.getComment());

            jsonIndexes.add(jsonSymbol);
        }

        return jsonIndexes.build();
    }
}
