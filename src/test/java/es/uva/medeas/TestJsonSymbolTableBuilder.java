package es.uva.medeas;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
            assertEquals("{\"type\":\"" + type.toString() + "\",\"line\":-1,\"dependencies\":[]}",
                    symbols.getJsonObject(type.toString() + " symbol").toString());
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

    @Test
    public void testDefinedLine(){
        int line = 3;
        JsonSymbolTableBuilder builder = new JsonSymbolTableBuilder();

        SymbolTable table = new SymbolTable();
        Symbol symbol = table.createSymbol("var");
        symbol.setDefinitionLine(line);

        builder.addSymbolTable("file",table);
        JsonArray output = builder.build();
        JsonObject file = output.getJsonObject(0);

        assertEquals(line, file.getJsonObject("symbols").getJsonObject("var").getInt("line"));
    }


    @Test
    public void testDependencies(){
        JsonSymbolTableBuilder builder = new JsonSymbolTableBuilder();

        SymbolTable table = new SymbolTable();
        Symbol var = table.createSymbol("var");
        Symbol constant = table.createSymbol("constant");
        Symbol notFoo = table.createSymbol("notFoo");
        Symbol anotherOne = table.createSymbol("anotherOne");

        var.addDependencies(Arrays.asList(constant,notFoo));
        notFoo.addDependency(anotherOne);

        builder.addSymbolTable("file",table);
        JsonArray output = builder.build();
        JsonObject file = output.getJsonObject(0);

        JsonArray varDependencies = file.getJsonObject("symbols").getJsonObject("var").getJsonArray("dependencies");
        JsonArray notFooDependencies = file.getJsonObject("symbols").getJsonObject("notFoo").getJsonArray("dependencies");

        JsonValue[] varDependenciesValues = varDependencies.toArray(new JsonValue[0]);
        List<JsonValue> varDependenciesList =  Arrays.asList(varDependenciesValues);
        Set<String> actual  = varDependenciesList.stream().map(JsonValue::toString).collect(Collectors.toSet());
        Set<String> expected = new HashSet<>(Arrays.asList("\"constant\"","\"notFoo\""));

        assertEquals(expected,actual );
        assertEquals("[\"anotherOne\"]",notFooDependencies.toString());




    }

    @Test
    public void testSymbolWithoutDependencies(){
        JsonSymbolTableBuilder builder = new JsonSymbolTableBuilder();

        SymbolTable table = new SymbolTable();
        table.createSymbol("constant");

        builder.addSymbolTable("file",table);
        JsonObject file = builder.build().getJsonObject(0);

        JsonArray dependencies = file.getJsonObject("symbols").getJsonObject("constant").getJsonArray("dependencies");

        assertTrue(dependencies.isEmpty());

    }

    @Test
    public void testSymbolWithoutDefinedLine(){
        JsonSymbolTableBuilder builder = new JsonSymbolTableBuilder();

        SymbolTable table = new SymbolTable();
        table.createSymbol("constant");

        builder.addSymbolTable("file",table);
        JsonObject file = builder.build().getJsonObject(0);

        int actualLine = file.getJsonObject("symbols").getJsonObject("constant").getInt("line");

        assertEquals(Symbol.LINE_NOT_DEFINED,actualLine);

    }
}
