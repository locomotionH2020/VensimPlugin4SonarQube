package es.uva.locomotion.utilities;

import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import org.junit.Test;

import javax.json.JsonArray;
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

        builder.addTables("file",new SymbolTable(), new ViewTable());

        JsonArray array = builder.build();
        JsonObject emptyJson = array.getJsonObject(0);
        assertTrue(emptyJson.getJsonObject("symbols").isEmpty());

    }

    @Test
    public void testBuildTwice(){
        JsonSymbolTableBuilder builder = new JsonSymbolTableBuilder();

        JsonArray array = builder.build();
        assertTrue(array.isEmpty());

        builder.addTables("file",new SymbolTable(), new ViewTable());
        array = builder.build();

        JsonObject emptyJson = array.getJsonObject(0);
        assertTrue(emptyJson.getJsonObject("symbols").isEmpty());

    }


    @Test
    public void testMultipleSymbolTables(){
        JsonSymbolTableBuilder builder = new JsonSymbolTableBuilder();

        SymbolTable firstSymbolTable = new SymbolTable();
        firstSymbolTable.addSymbol(new Symbol("var"));

        builder.addTables("firstFile",firstSymbolTable, new ViewTable());
        builder.addTables("secondFile",new SymbolTable(), new ViewTable());
        builder.addTables("thirdFile",new SymbolTable(), new ViewTable());

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
        Symbol symbol = table.addSymbol(new Symbol("var"));
        symbol.addLine(line);

        builder.addTables("file",table, new ViewTable());
        JsonArray output = builder.build();
        JsonObject file = output.getJsonObject(0);

        assertEquals(line, file.getJsonObject("symbols").getJsonObject("var").getJsonArray(JsonSymbolTableBuilder.KEY_LINES).getInt(0));
    }

    @Test
    public void testType(){
        JsonSymbolTableBuilder builder = new JsonSymbolTableBuilder();

        SymbolTable table = new SymbolTable();
        Symbol symbol = table.addSymbol(new Symbol("scenario1",SymbolType.SUBSCRIPT_VALUE));

        builder.addTables("file",table, new ViewTable());
        JsonObject file = builder.build().getJsonObject(0);

        String type = file.getJsonObject("symbols").getJsonObject("scenario1").getString(JsonSymbolTableBuilder.KEY_TYPE);

        assertEquals("SUBSCRIPT_VALUE",type);

    }

    @Test
    public void testDependencies(){
        JsonSymbolTableBuilder builder = new JsonSymbolTableBuilder();

        SymbolTable table = new SymbolTable();
        Symbol var = table.addSymbol(new Symbol("var"));
        Symbol constant = table.addSymbol(new Symbol("constant"));
        Symbol notFoo = table.addSymbol(new Symbol("notFoo"));
        Symbol anotherOne = table.addSymbol(new Symbol("anotherOne"));

        var.addDependencies(Arrays.asList(constant,notFoo));
        notFoo.addDependency(anotherOne);

        builder.addTables("file",table, new ViewTable());
        JsonArray output = builder.build();
        JsonObject file = output.getJsonObject(0);

        JsonArray varDependencies = file.getJsonObject("symbols").getJsonObject("var").getJsonArray(JsonSymbolTableBuilder.KEY_DEPENDENCIES);
        JsonArray notFooDependencies = file.getJsonObject("symbols").getJsonObject("notFoo").getJsonArray(JsonSymbolTableBuilder.KEY_DEPENDENCIES);

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
        table.addSymbol(new Symbol("constant"));

        builder.addTables("file",table, new ViewTable());
        JsonObject file = builder.build().getJsonObject(0);

        JsonArray dependencies = file.getJsonObject("symbols").getJsonObject("constant").getJsonArray(JsonSymbolTableBuilder.KEY_DEPENDENCIES);

        assertTrue(dependencies.isEmpty());

    }

    @Test
    public void testSymbolWithoutDefinedLine(){
        JsonSymbolTableBuilder builder = new JsonSymbolTableBuilder();

        SymbolTable table = new SymbolTable();
        table.addSymbol(new Symbol("constant"));

        builder.addTables("file",table, new ViewTable());
        JsonObject file = builder.build().getJsonObject(0);

        JsonArray actualLines = file.getJsonObject("symbols").getJsonObject("constant").getJsonArray(JsonSymbolTableBuilder.KEY_LINES);

        assertTrue(actualLines.isEmpty());

    }

    @Test
    public void testSymbolWithoutComment(){
        JsonSymbolTableBuilder builder = new JsonSymbolTableBuilder();

        SymbolTable table = new SymbolTable();
        table.addSymbol(new Symbol("constant"));

        builder.addTables("file",table, new ViewTable());
        JsonObject file = builder.build().getJsonObject(0);

        String comment = file.getJsonObject("symbols").getJsonObject("constant").getString(JsonSymbolTableBuilder.KEY_COMMENT);

        assertTrue(comment.isBlank());
    }

    @Test
    public void testSymbolWithComment(){
        JsonSymbolTableBuilder builder = new JsonSymbolTableBuilder();

        SymbolTable table = new SymbolTable();
        Symbol constant = new Symbol("constant");
        constant.setComment("extremely important comment");
        table.addSymbol(constant);

        builder.addTables("file",table, new ViewTable());
        JsonObject file = builder.build().getJsonObject(0);

        String comment = file.getJsonObject("symbols").getJsonObject("constant").getString(JsonSymbolTableBuilder.KEY_COMMENT);

        assertEquals("extremely important comment",comment);
    }

    @Test
    public void testSymbolWithoutUnits(){
        JsonSymbolTableBuilder builder = new JsonSymbolTableBuilder();

        SymbolTable table = new SymbolTable();
        table.addSymbol(new Symbol("constant"));

        builder.addTables("file",table, new ViewTable());
        JsonObject file = builder.build().getJsonObject(0);

        String units = file.getJsonObject("symbols").getJsonObject("constant").getString(JsonSymbolTableBuilder.KEY_UNITS);

        assertTrue(units.isBlank());
    }

    @Test
    public void testSymbolWithUnits(){
        JsonSymbolTableBuilder builder = new JsonSymbolTableBuilder();

        SymbolTable table = new SymbolTable();
        Symbol constant = new Symbol("constant");
        constant.setUnits("kg");
        table.addSymbol(constant);

        builder.addTables("file",table, new ViewTable());
        JsonObject file = builder.build().getJsonObject(0);

        String units = file.getJsonObject("symbols").getJsonObject("constant").getString(JsonSymbolTableBuilder.KEY_UNITS);

        assertEquals("kg",units);
    }



}
