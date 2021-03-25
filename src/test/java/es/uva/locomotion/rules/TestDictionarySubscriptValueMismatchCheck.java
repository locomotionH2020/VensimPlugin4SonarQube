package es.uva.locomotion.rules;

import es.uva.locomotion.model.*;
import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import org.junit.Test;

import static es.uva.locomotion.testutilities.RuleTestUtilities.assertHasIssueInLines;
import static org.junit.Assert.*;

public class TestDictionarySubscriptValueMismatchCheck {
    @Test
    public void testOneUnexpectedValue(){
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());
        SymbolTable dbTable = dbData.getDataBaseSymbols();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Subscript);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.Subscript_Value));
        parsedSubscript.addLine(1);
        parsedSubscript.addLine(2);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbVar = new Symbol("subscript",SymbolType.Subscript);
        dbVar.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        dbVar.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        dbTable.addSymbol(dbVar);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertEquals(2,context.getIssues().size());
        assertHasIssueInLines(context,DictionarySubscriptValueMismatchCheck.class,1,2);
        assertTrue(parsedSubscript.isValid());


        for(Issue issue:context.getIssues())
            assertEquals("The subscript 'subscript' has values that aren't defined in the database. Unexpected values: '[unexpected_value]'.",issue.getMessage());
    }

    @Test
    public void testTwoUnexpectedValues(){
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());

        SymbolTable dbTable = dbData.getDataBaseSymbols();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Subscript);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        parsedSubscript.addDependency(new Symbol("unexpected_value1",SymbolType.Subscript_Value));
        parsedSubscript.addDependency(new Symbol("unexpected_value2",SymbolType.Subscript_Value));
        parsedSubscript.addLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.Subscript);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        dbTable.addSymbol(dbSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertEquals(1,context.getIssues().size());
        assertHasIssueInLines(context,DictionarySubscriptValueMismatchCheck.class,1);


        for(Issue issue:context.getIssues())
            assertEquals("The subscript 'subscript' has values that aren't defined in the database. Unexpected values: '[unexpected_value1, unexpected_value2]'.",issue.getMessage());

    }

    @Test
    public void testIssueDoesntDestroyTheDependencies(){
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());

        SymbolTable dbTable = dbData.getDataBaseSymbols();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Subscript);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.Subscript_Value));
        parsedSubscript.addLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.Subscript);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        dbTable.addSymbol(dbSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertEquals(2,parsedSubscript.getDependencies().size());
        assertEquals(2,dbSubscript.getDependencies().size());
    }

    @Test
    public void testSubsetsAreAllowed(){
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());

        SymbolTable dbTable = dbData.getDataBaseSymbols();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Subscript);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        parsedSubscript.addLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.Subscript);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        dbTable.addSymbol(dbSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
        assertTrue(parsedSubscript.isValid());
    }

    @Test
    public void testSameValuesExactly(){
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());

        SymbolTable dbTable = dbData.getDataBaseSymbols();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Subscript);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        parsedSubscript.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        parsedSubscript.addLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.Subscript);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        dbTable.addSymbol(dbSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testParsedSymbolIsNotASubscript(){
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());

        SymbolTable dbTable = dbData.getDataBaseSymbols();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Variable);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.Subscript_Value));
        parsedSubscript.addLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.Subscript);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        dbTable.addSymbol(dbSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testDictionarySymbolIsNotASubscript(){
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());

        SymbolTable dbTable = dbData.getDataBaseSymbols();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Subscript);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.Subscript_Value));
        parsedSubscript.addLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.Constant);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        dbTable.addSymbol(dbSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testFoundSubscriptDoesntHaveValues(){
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());

        SymbolTable dbTable = dbData.getDataBaseSymbols();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Subscript);
        parsedSubscript.addLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.Constant);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        dbTable.addSymbol(dbSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testDictionarySubscriptDoesntHaveValues(){
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());

        SymbolTable dbTable = dbData.getDataBaseSymbols();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Subscript);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.Subscript_Value));
        parsedSubscript.addLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.Constant);
        dbTable.addSymbol(dbSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        for(Issue issue:context.getIssues())
            assertEquals("The subscript 'subscript' has values that aren't defined in the database. Unexpected values: '[first_value, unexpected_value]'.",issue.getMessage());
    }

    @Test
    public void testDictonaryDoesntContainSubscript(){
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        SymbolTable dbTable = dbData.getDataBaseSymbols();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Subscript);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.Subscript_Value));
        parsedSubscript.addLine(1);
        parsedTable.addSymbol(parsedSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testParsedTableDoesntContainSubscript(){
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());

        SymbolTable dbTable = dbData.getDataBaseSymbols();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbSubscript = new Symbol("subscript",SymbolType.Subscript);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        dbTable.addSymbol(dbSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testDbTableIsNull(){
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Subscript);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.Subscript_Value));
        parsedSubscript.addLine(1);
        parsedTable.addSymbol(parsedSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, null);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }
}
