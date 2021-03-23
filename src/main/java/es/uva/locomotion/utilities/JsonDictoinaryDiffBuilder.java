package es.uva.locomotion.utilities;

import es.uva.locomotion.model.*;
import es.uva.locomotion.model.Module;
import es.uva.locomotion.model.category.Category;

import javax.json.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonDictoinaryDiffBuilder {


    private final JsonArrayBuilder fileBuilder;

    public final static String KEY_COMMENT = "comment";
    public final static String KEY_UNITS = "units";
    public final static String KEY_DEPENDENCIES = "dependencies";
    public static final String KEY_LINES = "lines";
    public static final String KEY_TYPE = "type";
    public static final String KEY_PRIMARY_VIEW = "primary";
    public static final String KEY_SHADOW_VIEWS = "shadows";
    public static final String KEY_MODULE = "module";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_SUBCATEGORY = "subcategory";
    public static final String KEY_MODULES = "modules";
    public static final String KEY_CATEGORIES = "categories";
    public static final String KEY_NAME = "name";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_SUPER = "super";
    public static final String KEY_GROUP = "group";
    public static final String KEY_LOCAL = "Local";
    public static final String KEY_DICTIONARY = "Dictionary";

    public JsonDictoinaryDiffBuilder() {
        fileBuilder = Json.createArrayBuilder();
    }

    public void addTables(String file, SymbolTable table, ViewTable viewTable, DataBaseRepresentation dbData) {
        JsonObjectBuilder tableBuilder = Json.createObjectBuilder();

        tableBuilder.add("file", file);
        tableBuilder.add("symbols", symbolTableDiffToJson(table, dbData.getDataBaseSymbols()));
        tableBuilder.add("modules", modulesDiffToJson(viewTable.getModules(), dbData.getModules()));
        tableBuilder.add("categories", categoriesDiffToJson(viewTable.getCategoriesAndSubcategories(), dbData.getCategories().getCategoriesAndSubcategories()));

        fileBuilder.add(tableBuilder);
    }

    //TODO hacer tres grupos, diferencias, solo en local y solo en remoto.
    private JsonObject symbolTableDiffToJson(SymbolTable symbolTable, SymbolTable dbTable) {
        JsonObjectBuilder tableBuilder = Json.createObjectBuilder();
        JsonObjectBuilder missmatchBuilder = Json.createObjectBuilder();
        JsonArrayBuilder missingLocalBuilder = Json.createArrayBuilder();
        JsonArrayBuilder missingDBBuilder = Json.createArrayBuilder();
        int counter = 1;
        List<SymbolType> ignore = Arrays.asList(SymbolType.Function,SymbolType.Subscript_Value);
        List<Symbol> localSymbols = symbolTable.getSymbols().stream().filter(symbol -> !ignore.contains(symbol.getType())).sorted(Comparator.comparing(Symbol::getToken)).collect(Collectors.toList());



        for (Symbol symbol : localSymbols) {

            if (dbTable.hasSymbol(symbol.getToken())) {
                Symbol dbSymbol = dbTable.getSymbol(symbol.getToken());
                if (!symbol.dbEquals(dbSymbol)) {
                    missmatchBuilder.add(symbol.getToken(), symbolDiffToJson(symbol, dbSymbol));
                }
                dbTable.removeSymbol(symbol.getToken());
            } else {
                missingDBBuilder.add(symbol.getToken());
            }
        }
//TODO separar índices.

        List<Symbol> DBSymbols = dbTable.getSymbols().stream().sorted(Comparator.comparing(Symbol::getToken)).collect(Collectors.toList());

        for (Symbol dbsymbol : DBSymbols) {
            missingLocalBuilder.add(dbsymbol.getToken());
        }
        tableBuilder.add("missmatches", missmatchBuilder);
        tableBuilder.add("not_found_in_DB", missingDBBuilder);
        tableBuilder.add("not_found_in_local", missingLocalBuilder);
        return tableBuilder.build();
    }

    private JsonObject symbolDiffToJson(Symbol symbol, Symbol dbSymbol) {
        JsonObjectBuilder symbolBuilder = Json.createObjectBuilder();

        if (!symbol.getPrimary_module().equals(dbSymbol.getPrimary_module())) {
            JsonObjectBuilder primaryBuilder = Json.createObjectBuilder();
            primaryBuilder.add(KEY_LOCAL, symbol.getPrimary_module().getName());
            primaryBuilder.add(KEY_DICTIONARY, dbSymbol.getPrimary_module().getName());
            symbolBuilder.add(KEY_PRIMARY_VIEW, primaryBuilder);
        }

        if (!symbol.getShadow_module().equals(dbSymbol.getShadow_module())) {

            JsonObjectBuilder shadowBuilder = Json.createObjectBuilder();

            JsonArrayBuilder shadowLocalBuilder = Json.createArrayBuilder();
            for (Module view : symbol.getShadow_module())
                shadowLocalBuilder.add(view.getName());
            shadowBuilder.add(KEY_LOCAL, shadowLocalBuilder);

            JsonArrayBuilder shadowDBBuilder = Json.createArrayBuilder();
            for (Module view : symbol.getShadow_module())
                shadowDBBuilder.add(view.getName());
            shadowBuilder.add(KEY_DICTIONARY, shadowDBBuilder);

            symbolBuilder.add(KEY_SHADOW_VIEWS, shadowBuilder);
        }
        if (!symbol.getShadow_module().equals(dbSymbol.getShadow_module())) {

            JsonObjectBuilder categoryBuilder = Json.createObjectBuilder();
            categoryBuilder.add(KEY_LOCAL, symbol.getCategory().getName());
            categoryBuilder.add(KEY_DICTIONARY, dbSymbol.getCategory().getName());
            symbolBuilder.add(KEY_CATEGORY, categoryBuilder);
        }
        if (!symbol.getShadow_module().equals(dbSymbol.getShadow_module())) {

            JsonObjectBuilder unitsBuilder = Json.createObjectBuilder();
            unitsBuilder.add(KEY_LOCAL, symbol.getUnits());
            unitsBuilder.add(KEY_DICTIONARY, dbSymbol.getUnits());
            symbolBuilder.add(KEY_UNITS, unitsBuilder);
        }
        if (!symbol.getShadow_module().equals(dbSymbol.getShadow_module())) {

            JsonObjectBuilder commentBuilder = Json.createObjectBuilder();
            commentBuilder.add(KEY_LOCAL, symbol.getComment());
            commentBuilder.add(KEY_DICTIONARY, dbSymbol.getComment());
            symbolBuilder.add(KEY_COMMENT, commentBuilder);
        }
        return symbolBuilder.build();

    }

    private JsonObject modulesDiffToJson(Set<Module> modules, Set<Module> dbModules) {
        JsonArrayBuilder missingLocalBuilder = Json.createArrayBuilder();
        JsonArrayBuilder missingDBBuilder = Json.createArrayBuilder();

        List<Module> localModules = modules.stream().sorted().collect(Collectors.toList());
        JsonObjectBuilder tableBuilder = Json.createObjectBuilder();
        for (Module module : localModules) {
            if (dbModules.contains(module))
                dbModules.remove(module);
            else
                missingDBBuilder.add(module.getName());
        }
        List<Module> DBModules = dbModules.stream().sorted().collect(Collectors.toList());

        for (Module dbModule : DBModules) {
            missingLocalBuilder.add(dbModule.getName());
        }

        tableBuilder.add("not_found_in_DB", missingDBBuilder);
        tableBuilder.add("not_found_in_local", missingLocalBuilder);
        return tableBuilder.build();
    }

    private JsonObject categoriesDiffToJson(List<Category> categories, List<Category> dbCategories) {
        JsonObjectBuilder tableBuilder = Json.createObjectBuilder();
        JsonObjectBuilder missmatchBuilder = Json.createObjectBuilder();
        JsonArrayBuilder missingLocalBuilder = Json.createArrayBuilder();
        JsonArrayBuilder missingDBBuilder = Json.createArrayBuilder();

        List<Category> localCategories = categories.stream().sorted(Comparator.comparing(Category::getName)).collect(Collectors.toList());

        for (Category category : localCategories) {

            List<Category> dbCategoryCopy = List.copyOf(dbCategories);
            boolean categoryInDb = false;
            for (Category dbCategory : dbCategoryCopy) {
                if (dbCategory.getName().equals(category.getName())) {
                    if (!dbCategory.equals(category)) {
                        missingDBBuilder.add(categoryDiffToJson(category, dbCategory));
                    }
                    dbCategories.remove(category);
                    categoryInDb = true;
                }
            }
            if (!categoryInDb) {
                missingDBBuilder.add(category.getWholeName());

            }


        }
        List<Category> DBCategories = dbCategories.stream().sorted(Comparator.comparing(Category::getWholeName)).collect(Collectors.toList());


        for (Category dbCategory : DBCategories) {
            missingLocalBuilder.add(dbCategory.getWholeName());
        }

        tableBuilder.add("missmatches", missmatchBuilder);
        tableBuilder.add("not_found_in_DB", missingDBBuilder);
        tableBuilder.add("not_found_in_local", missingLocalBuilder);

        return tableBuilder.build();
    }

    private JsonObject categoryDiffToJson(Category category, Category dbCategory) {
        JsonObjectBuilder categoryBuilder = Json.createObjectBuilder();

        if (!category.getSuperCategory().getName().equals(dbCategory.getSuperCategory().getName())) {
            JsonObjectBuilder superBuilder = Json.createObjectBuilder();
            superBuilder.add(KEY_LOCAL, category.getSuperCategory().getName());
            superBuilder.add(KEY_DICTIONARY, dbCategory.getSuperCategory().getName());
            categoryBuilder.add(KEY_SUPER, superBuilder);
        }

        JsonObjectBuilder subcategoryBuilder = Json.createObjectBuilder();

        if (!category.getSubcategories().equals(dbCategory.getSubcategories())) {
            JsonObjectBuilder superBuilder = Json.createObjectBuilder();

            JsonArrayBuilder subcategoriesLocalBuilder = Json.createArrayBuilder();
            for (Category subcategory : category.getSubcategories())
                subcategoriesLocalBuilder.add(subcategory.getName());
            subcategoryBuilder.add(KEY_LOCAL, subcategoriesLocalBuilder);

            JsonArrayBuilder subcategoriesDBBuilder = Json.createArrayBuilder();
            for (Category dbSubcategory : dbCategory.getSubcategories())
                subcategoriesDBBuilder.add(dbSubcategory.getName());
            subcategoryBuilder.add(KEY_DICTIONARY, subcategoriesDBBuilder);

            categoryBuilder.add(KEY_SUBCATEGORY, subcategoryBuilder);

        }
        return categoryBuilder.build();

    }

    public JsonArray build() {
        return fileBuilder.build();
    }

}
