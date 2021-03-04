package es.uva.locomotion.utilities;

import es.uva.locomotion.model.*;

import javax.json.*;
import java.util.List;

public class JsonSymbolTableBuilder {


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
    public static final String KEY_VALID = "notValidBecause";
    public static final String KEY_FILTERED = "isFiltered";

    public JsonSymbolTableBuilder() {
        fileBuilder = Json.createArrayBuilder();
    }

    public void addTables(String file, SymbolTable table, ViewTable viewTable) {
        JsonObjectBuilder tableBuilder = Json.createObjectBuilder();

        tableBuilder.add("file", file);
        tableBuilder.add("symbols", symbolTableToJson(table));
        tableBuilder.add("views", viewsToJson(viewTable.getViews()));
        tableBuilder.add("modules", modulesToJson(viewTable.getModules()));
        tableBuilder.add("categories", categoriesToJson(viewTable.getCategories().getCategoriesAndSubcategories()));

        fileBuilder.add(tableBuilder);
    }


    private JsonObject symbolTableToJson(SymbolTable table) {
        JsonObjectBuilder tableBuilder = Json.createObjectBuilder();
        for (Symbol symbol : table.getSymbols()) {

            tableBuilder.add(symbol.getToken(), symbolToJson(symbol));
        }

        return tableBuilder.build();
    }

    private JsonObject symbolToJson(Symbol symbol) {
        JsonObjectBuilder symbolBuilder = Json.createObjectBuilder();

        symbolBuilder.add(KEY_TYPE, symbol.getType().toString());
        JsonArrayBuilder lines = Json.createArrayBuilder();
        for (int line : symbol.getDefinitionLines())
            lines.add(line);

        symbolBuilder.add(KEY_LINES, lines);

        JsonArrayBuilder dependenciesBuilder = Json.createArrayBuilder();
        for (Symbol dependency : symbol.getDependencies())
            dependenciesBuilder.add(dependency.getToken());
        symbolBuilder.add(KEY_DEPENDENCIES, dependenciesBuilder);

        symbolBuilder.add(KEY_PRIMARY_VIEW, symbol.getPrimary_module());

        JsonArrayBuilder shadowBuilder = Json.createArrayBuilder();
        for (String view : symbol.getShadow_module())
            shadowBuilder.add(view);
        symbolBuilder.add(KEY_SHADOW_VIEWS, shadowBuilder);

        if (symbol.getCategory() != null)
        symbolBuilder.add(KEY_CATEGORY, symbol.getCategory());

        symbolBuilder.add(KEY_UNITS, symbol.getUnits());
        symbolBuilder.add(KEY_COMMENT, symbol.getComment());
        symbolBuilder.add(KEY_GROUP, symbol.getGroup() == null ?  "null" : symbol.getGroup());

        if(!symbol.isValid()) {
            symbolBuilder.add(KEY_VALID, symbol.getReasonForInvalid());
        }
        symbolBuilder.add(KEY_FILTERED, symbol.isFiltered());

        return symbolBuilder.build();

    }

    private JsonArray viewsToJson(List<View> table) {
        JsonArrayBuilder tableBuilder = Json.createArrayBuilder();
        for (View view : table) {

            JsonObjectBuilder viewBuilder = Json.createObjectBuilder();
            viewBuilder.add(KEY_MODULE, view.getModule());
            if (view.getCategory() != null)
                viewBuilder.add(KEY_CATEGORY, view.getCategory());
            if (view.getSubcategory() != null)
                viewBuilder.add(KEY_SUBCATEGORY, view.getSubcategory());
            tableBuilder.add(viewBuilder);

        }

        return tableBuilder.build();
    }


    private JsonArray modulesToJson(List<String> table) {
        JsonArrayBuilder tableBuilder = Json.createArrayBuilder();
        for (String module : table) {
            tableBuilder.add(module);
        }
        return tableBuilder.build();
    }

    private JsonArray categoriesToJson(List<Category> table) {
        JsonArrayBuilder tableBuilder = Json.createArrayBuilder();
        for (Category category : table) {

            JsonObjectBuilder viewBuilder = Json.createObjectBuilder();

            viewBuilder.add(KEY_NAME, category.getName());
            if (category.getSuperCategory() != null) {
                viewBuilder.add(KEY_LEVEL, 2);
                viewBuilder.add(KEY_SUPER, category.getSuperCategory().getName());
            } else {
                viewBuilder.add(KEY_LEVEL, 1);
                viewBuilder.add(KEY_SUPER, JsonValue.NULL);
            }

            tableBuilder.add(viewBuilder);

        }

        return tableBuilder.build();
    }


    public JsonArray build() {
        return fileBuilder.build();
    }

}
