package es.uva.locomotion.rules;

import es.uva.locomotion.model.DataBaseRepresentation;
import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import es.uva.locomotion.model.VensimVisitorContext;
import es.uva.locomotion.plugin.Issue;
import org.junit.Test;

import static es.uva.locomotion.testutilities.RuleTestUtilities.assertHasIssueInLines;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestDictionarySubscriptValueMismatchCheck {
    @Test
    public void testOneUnexpectedValue(){
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        SymbolTable dbTable = new SymbolTable();
        dbData.setDataBaseSymbols(dbTable);

        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.SUBSCRIPT);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.SUBSCRIPT_VALUE));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.SUBSCRIPT_VALUE));
        parsedSubscript.addLine(1);
        parsedSubscript.addLine(2);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbVar = new Symbol("subscript",SymbolType.SUBSCRIPT);
        dbVar.addDependency(new Symbol("first_value",SymbolType.SUBSCRIPT_VALUE));
        dbVar.addDependency(new Symbol("second_value",SymbolType.SUBSCRIPT_VALUE));
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
        SymbolTable dbTable = new SymbolTable();
        dbData.setDataBaseSymbols(dbTable);
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.SUBSCRIPT);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.SUBSCRIPT_VALUE));
        parsedSubscript.addDependency(new Symbol("unexpected_value1",SymbolType.SUBSCRIPT_VALUE));
        parsedSubscript.addDependency(new Symbol("unexpected_value2",SymbolType.SUBSCRIPT_VALUE));
        parsedSubscript.addLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.SUBSCRIPT);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.SUBSCRIPT_VALUE));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.SUBSCRIPT_VALUE));
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

        SymbolTable dbTable = dbData.getDataBaseSymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.SUBSCRIPT);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.SUBSCRIPT_VALUE));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.SUBSCRIPT_VALUE));
        parsedSubscript.addLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.SUBSCRIPT);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.SUBSCRIPT_VALUE));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.SUBSCRIPT_VALUE));
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

        SymbolTable dbTable = dbData.getDataBaseSymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.SUBSCRIPT);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.SUBSCRIPT_VALUE));
        parsedSubscript.addLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.SUBSCRIPT);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.SUBSCRIPT_VALUE));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.SUBSCRIPT_VALUE));
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

        SymbolTable dbTable = dbData.getDataBaseSymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.SUBSCRIPT);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.SUBSCRIPT_VALUE));
        parsedSubscript.addDependency(new Symbol("second_value",SymbolType.SUBSCRIPT_VALUE));
        parsedSubscript.addLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.SUBSCRIPT);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.SUBSCRIPT_VALUE));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.SUBSCRIPT_VALUE));
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

        SymbolTable dbTable = dbData.getDataBaseSymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.VARIABLE);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.SUBSCRIPT_VALUE));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.SUBSCRIPT_VALUE));
        parsedSubscript.addLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.SUBSCRIPT);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.SUBSCRIPT_VALUE));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.SUBSCRIPT_VALUE));
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

        SymbolTable dbTable = dbData.getDataBaseSymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.SUBSCRIPT);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.SUBSCRIPT_VALUE));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.SUBSCRIPT_VALUE));
        parsedSubscript.addLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.CONSTANT);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.SUBSCRIPT_VALUE));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.SUBSCRIPT_VALUE));
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

        SymbolTable dbTable = dbData.getDataBaseSymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.SUBSCRIPT);
        parsedSubscript.addLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.CONSTANT);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.SUBSCRIPT_VALUE));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.SUBSCRIPT_VALUE));
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

        SymbolTable dbTable = dbData.getDataBaseSymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.SUBSCRIPT);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.SUBSCRIPT_VALUE));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.SUBSCRIPT_VALUE));
        parsedSubscript.addLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.CONSTANT);
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
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.SUBSCRIPT);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.SUBSCRIPT_VALUE));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.SUBSCRIPT_VALUE));
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

        SymbolTable dbTable = dbData.getDataBaseSymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbSubscript = new Symbol("subscript",SymbolType.SUBSCRIPT);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.SUBSCRIPT_VALUE));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.SUBSCRIPT_VALUE));
        dbTable.addSymbol(dbSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testDbTableIsNull(){
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.SUBSCRIPT);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.SUBSCRIPT_VALUE));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.SUBSCRIPT_VALUE));
        parsedSubscript.addLine(1);
        parsedTable.addSymbol(parsedSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, null);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }
}
