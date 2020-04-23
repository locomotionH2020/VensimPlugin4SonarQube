package es.uva.locomotion.utilities;

import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.SymbolTable;

import javax.json.*;

public class JsonSymbolTableBuilder {



    private JsonArrayBuilder fileBuilder;

    public final static String KEY_COMMENT = "comment";
    public final static String KEY_UNITS = "units";
    public final static String KEY_DEPENDENCIES = "dependencies";
    public static final String KEY_LINES = "lines";
    public static final String KEY_TYPE = "type" ;

    public JsonSymbolTableBuilder(){
        fileBuilder = Json.createArrayBuilder();
    }

    public void addSymbolTable(String file,SymbolTable table){
        JsonObjectBuilder tableBuilder = Json.createObjectBuilder();

        tableBuilder.add("file",file);
        tableBuilder.add("symbols",symbolTableToJson(table));

        fileBuilder.add(tableBuilder);
    }


    private JsonObject symbolTableToJson(SymbolTable table){
        JsonObjectBuilder tableBuilder = Json.createObjectBuilder();
        for(Symbol symbol: table.getSymbols()){

            tableBuilder.add(symbol.getToken(),symbolToJson(symbol));
        }

        return tableBuilder.build();
    }

    private JsonObject symbolToJson(Symbol symbol){
        JsonObjectBuilder symbolBuilder = Json.createObjectBuilder();

        symbolBuilder.add(KEY_TYPE,symbol.getType().toString());
        JsonArrayBuilder lines = Json.createArrayBuilder();
        for(int line:symbol.getDefinitionLines())
            lines.add(line);

        symbolBuilder.add(KEY_LINES,lines);

        JsonArrayBuilder dependenciesBuilder = Json.createArrayBuilder();
        for(Symbol dependency:symbol.getDependencies())
            dependenciesBuilder.add(dependency.getToken());

        symbolBuilder.add(KEY_DEPENDENCIES,dependenciesBuilder);


        symbolBuilder.add(KEY_UNITS,symbol.getUnits());
        symbolBuilder.add(KEY_COMMENT,symbol.getComment());



        return symbolBuilder.build();

    }


    public JsonArray build(){
        return fileBuilder.build();
    }
}
