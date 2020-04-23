package es.uva.locomotion.rules;

import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.SymbolTable;
import es.uva.locomotion.parser.SymbolType;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.plugin.VensimVisitorContext;
import org.junit.Test;

import static es.uva.locomotion.testutilities.RuleTestUtilities.assertHasIssueInLines;
import static org.junit.Assert.*;

public class TestDictionarySubscriptValueMismatchCheck {
    @Test
    public void testOneUnexpectedValue(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Subscript);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.Subscript_Value));
        parsedSubscript.addDefinitionLine(1);
        parsedSubscript.addDefinitionLine(2);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbVar = new Symbol("subscript",SymbolType.Subscript);
        dbVar.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        dbVar.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        dbTable.addSymbol(dbVar);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertEquals(2,context.getIssues().size());
        assertHasIssueInLines(context,DictionarySubscriptValueMismatchCheck.class,1,2);
        assertFalse(parsedSubscript.isValid());


        for(Issue issue:context.getIssues())
            assertEquals("The subscript 'subscript' has values that aren't defined in the database. Unexpected values: '[unexpected_value]'.",issue.getMessage());
    }

    @Test
    public void testTwoUnexpectedValues(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Subscript);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        parsedSubscript.addDependency(new Symbol("unexpected_value1",SymbolType.Subscript_Value));
        parsedSubscript.addDependency(new Symbol("unexpected_value2",SymbolType.Subscript_Value));
        parsedSubscript.addDefinitionLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.Subscript);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        dbTable.addSymbol(dbSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertEquals(1,context.getIssues().size());
        assertHasIssueInLines(context,DictionarySubscriptValueMismatchCheck.class,1);


        for(Issue issue:context.getIssues())
            assertEquals("The subscript 'subscript' has values that aren't defined in the database. Unexpected values: '[unexpected_value1, unexpected_value2]'.",issue.getMessage());

    }

    @Test
    public void testIssueDoesntDestroyTheDependencies(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Subscript);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.Subscript_Value));
        parsedSubscript.addDefinitionLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.Subscript);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        dbTable.addSymbol(dbSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertEquals(2,parsedSubscript.getDependencies().size());
        assertEquals(2,dbSubscript.getDependencies().size());
    }

    @Test
    public void testSubsetsAreAllowed(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Subscript);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        parsedSubscript.addDefinitionLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.Subscript);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        dbTable.addSymbol(dbSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
        assertTrue(parsedSubscript.isValid());
    }

    @Test
    public void testSameValuesExactly(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Subscript);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        parsedSubscript.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        parsedSubscript.addDefinitionLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.Subscript);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        dbTable.addSymbol(dbSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testParsedSymbolIsNotASubscript(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Variable);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.Subscript_Value));
        parsedSubscript.addDefinitionLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.Subscript);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        dbTable.addSymbol(dbSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testDictionarySymbolIsNotASubscript(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Subscript);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.Subscript_Value));
        parsedSubscript.addDefinitionLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.Constant);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        dbTable.addSymbol(dbSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testFoundSubscriptDoesntHaveValues(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Subscript);
        parsedSubscript.addDefinitionLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.Constant);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        dbTable.addSymbol(dbSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testDictionarySubscriptDoesntHaveValues(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Subscript);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.Subscript_Value));
        parsedSubscript.addDefinitionLine(1);
        parsedTable.addSymbol(parsedSubscript);

        Symbol dbSubscript = new Symbol("subscript",SymbolType.Constant);
        dbTable.addSymbol(dbSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        for(Issue issue:context.getIssues())
            assertEquals("The subscript 'subscript' has values that aren't defined in the database. Unexpected values: '[first_value, unexpected_value]'.",issue.getMessage());
    }

    @Test
    public void testDictonaryDoesntContainSubscript(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedSubscript = new Symbol("subscript", SymbolType.Subscript);
        parsedSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        parsedSubscript.addDependency(new Symbol("unexpected_value",SymbolType.Subscript_Value));
        parsedSubscript.addDefinitionLine(1);
        parsedTable.addSymbol(parsedSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testParsedTableDoesntContainSubscript(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbSubscript = new Symbol("subscript",SymbolType.Subscript);
        dbSubscript.addDependency(new Symbol("first_value",SymbolType.Subscript_Value));
        dbSubscript.addDependency(new Symbol("second_value",SymbolType.Subscript_Value));
        dbTable.addSymbol(dbSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);


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
        parsedSubscript.addDefinitionLine(1);
        parsedTable.addSymbol(parsedSubscript);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,null);


        DictionarySubscriptValueMismatchCheck check = new DictionarySubscriptValueMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }
}
