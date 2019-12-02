package es.uva.medeas.utilities;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;

import javax.json.*;

public class JsonSymbolTableBuilder {

    private JsonArrayBuilder fileBuilder;

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

        symbolBuilder.add("type",symbol.getType().toString());
        JsonArrayBuilder lines = Json.createArrayBuilder();
        for(int line:symbol.getDefinitionLines())
            lines.add(line);

        symbolBuilder.add("lines",lines);

        JsonArrayBuilder dependenciesBuilder = Json.createArrayBuilder();
        for(Symbol dependency:symbol.getDependencies())
            dependenciesBuilder.add(dependency.getToken());

        symbolBuilder.add("dependencies",dependenciesBuilder);

        return symbolBuilder.build();

    }


    public JsonArray build(){
        return fileBuilder.build();
    }
}
