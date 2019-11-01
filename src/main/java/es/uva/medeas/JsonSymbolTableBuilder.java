package es.uva.medeas;

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
            tableBuilder.add(symbol.getToken(),symbol.getType().toString());
        }

        return tableBuilder.build();
    }

    public JsonArray build(){
        return fileBuilder.build();
    }
}
