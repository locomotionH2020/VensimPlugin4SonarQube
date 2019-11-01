package es.uva.medeas;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;

import static org.junit.Assert.*;

public class TestJsonSymbolTableBuilder {

    @Test
    public void testEmptyBuilder(){
        JsonSymbolTableBuilder builder = new JsonSymbolTableBuilder();

        JsonArray array = builder.build();
        assertTrue(array.isEmpty());
    }

    @Test
    public void testEmptySymbolTable(){
        JsonSymbolTableBuilder builder = new JsonSymbolTableBuilder();

        builder.addSymbolTable("file",new SymbolTable());

        JsonArray array = builder.build();
        JsonObject emptyJson = array.getJsonObject(0);
        assertTrue(emptyJson.getJsonObject("symbols").isEmpty());

    }

    @Test
    public void testBuildTwice(){
        JsonSymbolTableBuilder builder = new JsonSymbolTableBuilder();

        JsonArray array = builder.build();
        assertTrue(array.isEmpty());

        builder.addSymbolTable("file",new SymbolTable());
        array = builder.build();

        JsonObject emptyJson = array.getJsonObject(0);
        assertTrue(emptyJson.getJsonObject("symbols").isEmpty());

    }

    @Test
    public void testCompleteSymbolTable(){
        SymbolTable table = new SymbolTable();

        for(SymbolType type: SymbolType.values()){
            Symbol symbol = table.createSymbol(type.toString() + " symbol");
            symbol.setType(type);
        }

        JsonSymbolTableBuilder builder = new JsonSymbolTableBuilder();
        builder.addSymbolTable("file",table);

        JsonArray jsonTable = builder.build();

        JsonObject symbols = jsonTable.getJsonObject(0).getJsonObject("symbols");

        for(SymbolType type: SymbolType.values()){
            assertEquals(type.toString(),symbols.getString(type.toString() + " symbol"));
        }

    }

    @Test
    public void testMultipleSymbolTables(){
        JsonSymbolTableBuilder builder = new JsonSymbolTableBuilder();

        SymbolTable firstSymbolTable = new SymbolTable();
        firstSymbolTable.createSymbol("var");

        builder.addSymbolTable("firstFile",firstSymbolTable);
        builder.addSymbolTable("secondFile",new SymbolTable());
        builder.addSymbolTable("thirdFile",new SymbolTable());

        JsonArray array = builder.build();

       JsonObject firstJson =  array.getJsonObject(0);
       JsonObject secondJson = array.getJsonObject(1);
       JsonObject thirdJson = array.getJsonObject(2);

       assertEquals("firstFile",firstJson.getString("file"));
        assertEquals("secondFile",secondJson.getString("file"));
        assertEquals("thirdFile",thirdJson.getString("file"));

        assertFalse(firstJson.getJsonObject("symbols").isEmpty());
        assertTrue(secondJson.getJsonObject("symbols").isEmpty());
        assertTrue(thirdJson.getJsonObject("symbols").isEmpty());
    }
}
