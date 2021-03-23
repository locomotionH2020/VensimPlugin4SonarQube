package es.uva.locomotion.utilities;

import es.uva.locomotion.model.*;
import es.uva.locomotion.model.Module;
import es.uva.locomotion.model.category.Category;
import org.antlr.v4.runtime.misc.Triple;

import javax.json.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public static final String KEY_INDEXES = "indexes";
    public static final String KEY_EXCEL = "excels";
    public static final String KEY_SHEET = "sheet";
    public static final String KEY_CELLRANGE = "cellrange";
    public static final String KEY_SERIES = "series";
    public static final String KEY_INFO = "info";
    public static final String KEY_FILENAME = "filename";

    public JsonSymbolTableBuilder() {
        fileBuilder = Json.createArrayBuilder();
    }

    public void addTables(String file, SymbolTable table, ViewTable viewTable) {
        JsonObjectBuilder tableBuilder = Json.createObjectBuilder();

        tableBuilder.add("file", file);
        tableBuilder.add("symbols", symbolTableToJson(table));
        tableBuilder.add("views", viewsToJson(viewTable.getViews()));
        tableBuilder.add("modules", modulesToJson(viewTable.getModules()));
        tableBuilder.add("categories", categoriesToJson(viewTable.getCategoriesAndSubcategories()));

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
        for (int line : symbol.getLines())
            lines.add(line);

        symbolBuilder.add(KEY_LINES, lines);

        JsonArrayBuilder dependenciesBuilder = Json.createArrayBuilder();
        for (Symbol dependency : symbol.getDependencies())
            dependenciesBuilder.add(dependency.getToken());
        symbolBuilder.add(KEY_DEPENDENCIES, dependenciesBuilder);

        symbolBuilder.add(KEY_PRIMARY_VIEW, symbol.getPrimary_module() == null ? "null" : symbol.getPrimary_module().getName());

        JsonArrayBuilder shadowBuilder = Json.createArrayBuilder();
        for (Module module : symbol.getShadow_module())
            shadowBuilder.add(module.getName());
        symbolBuilder.add(KEY_SHADOW_VIEWS, shadowBuilder);

        if (symbol.getCategory() != null)
            symbolBuilder.add(KEY_CATEGORY, symbol.getCategory()== null ? "null" :symbol.getCategory().getName());

        symbolBuilder.add(KEY_UNITS, symbol.getUnits());
        symbolBuilder.add(KEY_COMMENT, symbol.getComment());
        symbolBuilder.add(KEY_GROUP, symbol.getGroup() == null ? "null" : symbol.getGroup());

        if (!symbol.isValid()) {
            symbolBuilder.add(KEY_VALID, symbol.getInvalidReason());
        }
        symbolBuilder.add(KEY_FILTERED, symbol.isFiltered());

        if (!symbol.getIndexes().isEmpty()) {
            List<String> indexes = symbol.getIndexes().stream().reduce(new ArrayList<>(), (subtotal, element) -> Stream.concat(subtotal.stream(), element.stream()).collect(Collectors.toList())).stream().map(Symbol::getToken).collect(Collectors.toList());

            JsonArrayBuilder indexBuilder = Json.createArrayBuilder();

            for(String index : indexes){
                indexBuilder.add(index);
            }

            symbolBuilder.add(KEY_INDEXES, indexBuilder); //TODO salen duplicados.
        }

        if (!symbol.getExcel().isEmpty()) {
            JsonArrayBuilder excelBuilder = Json.createArrayBuilder();

            for (ExcelRef excel : symbol.getExcel()) {
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
            symbolBuilder.add(KEY_EXCEL, excelBuilder);
        }
        return symbolBuilder.build();

    }

    private JsonArray viewsToJson(List<View> table) {
        JsonArrayBuilder tableBuilder = Json.createArrayBuilder();
        for (View view : table) {

            JsonObjectBuilder viewBuilder = Json.createObjectBuilder();
            viewBuilder.add(KEY_MODULE, view.getModule().getName());
            if (view.getCategory() != null)
                viewBuilder.add(KEY_CATEGORY, view.getCategory().getName());
            if (view.getSubcategory() != null)
                viewBuilder.add(KEY_SUBCATEGORY, view.getSubcategory().getName());
            tableBuilder.add(viewBuilder);

        }

        return tableBuilder.build();
    }


    private JsonArray modulesToJson(Set<Module> table) {
        JsonArrayBuilder tableBuilder = Json.createArrayBuilder();
        for (Module module : table) {
            tableBuilder.add(module.getName());
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
