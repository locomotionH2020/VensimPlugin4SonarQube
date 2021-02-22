package es.uva.locomotion.rules;


import es.uva.locomotion.model.*;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.testutilities.GeneralTestUtilities;
import es.uva.locomotion.utilities.Constants;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static es.uva.locomotion.testutilities.RuleTestUtilities.*;
import static es.uva.locomotion.testutilities.GeneralTestUtilities.addSymbolInLines;
import static org.junit.Assert.*;

public class TestDictionaryTypeMismatchCheck {


    @Test
    public void testIssue() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());
        SymbolTable dbTable = dbData.getDataBaseSymbols();
        SymbolTable parsedTable = new SymbolTable();

        GeneralTestUtilities.addSymbolInLines(dbTable, "var", SymbolType.Subscript);
        Symbol parsedVar = addSymbolInLines(parsedTable, "var", SymbolType.Variable, 1, 2, 3);


        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);


        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertEquals(3, context.getIssues().size());
        assertHasIssueInLines(context, DictionaryTypeMismatchCheck.class, 1, 2, 3);


        assertFalse(parsedVar.isValid());
        for (Issue issue : context.getIssues())
            assertEquals("The symbol 'var' has type 'Variable' but the dictionary has 'Subscript'.", issue.getMessage());
    }

    @Test
    public void testIssueInDifferentSymbols() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());
        SymbolTable dbTable = dbData.getDataBaseSymbols();
        SymbolTable parsedTable = new SymbolTable();

        GeneralTestUtilities.addSymbolInLines(dbTable, "var", SymbolType.Variable);
        addSymbolInLines(parsedTable, "var", SymbolType.Reality_Check, 1);

        GeneralTestUtilities.addSymbolInLines(dbTable, "valid", SymbolType.Constant);
        Symbol valid = addSymbolInLines(parsedTable, "valid", SymbolType.Constant, 2);

        GeneralTestUtilities.addSymbolInLines(dbTable, "var2", SymbolType.Reality_Check);
        GeneralTestUtilities.addSymbolInLines(parsedTable, "var2", SymbolType.Subscript_Value, 3);

        GeneralTestUtilities.addSymbolInLines(dbTable, "valid2", SymbolType.Subscript_Value);
        GeneralTestUtilities.addSymbolInLines(parsedTable, "valid2", SymbolType.Subscript_Value, 4);

        GeneralTestUtilities.addSymbolInLines(dbTable, "var3", SymbolType.Lookup_Table);
        GeneralTestUtilities.addSymbolInLines(parsedTable, "var3", SymbolType.Subscript, 5);

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);

        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertHasIssueInLines(context, DictionaryTypeMismatchCheck.class, 1, 3, 5);
        assertDoesntHaveIssueInLines(context, DictionaryTypeMismatchCheck.class, 2, 4);

        assertTrue(valid.isValid());

    }

    @Test
    public void testIgnoresSymbolsThatArentInTheDictionary() {
        SymbolTable parsedTable = new SymbolTable();

        GeneralTestUtilities.addSymbolInLines(parsedTable, "var", SymbolType.Variable, 1, 2, 3);


        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, new DataBaseRepresentation());
        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryTypeMismatchCheck.class);

    }

    @Test
    public void testRuleIsIgnoredIfDbTableIsNull() {
        SymbolTable parsedTable = new SymbolTable();

        GeneralTestUtilities.addSymbolInLines(parsedTable, "var", SymbolType.Variable, 1, 2, 3);


        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, null);
        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryTypeMismatchCheck.class);


    }

    @Test
    public void testRuleIsIgnoredIfSymbolDoenstHaveDefinitionLine() {
        SymbolTable parsedTable = new SymbolTable();
        parsedTable.addSymbol(new Symbol("var", SymbolType.Subscript));

        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());
        SymbolTable dbTable = dbData.getDataBaseSymbols();
        dbTable.addSymbol(new Symbol("var", SymbolType.Reality_Check));

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);


        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryTypeMismatchCheck.class);

    }

    @Test
    public void testRuleIgnoresFunctions() {

        SymbolTable parsedTable = new SymbolTable();
        addSymbolInLines(parsedTable, "function", SymbolType.Function, 1);
        addSymbolInLines(parsedTable, "constant", SymbolType.Constant, 2);
        addSymbolInLines(parsedTable, "var", SymbolType.Variable, 3);
        addSymbolInLines(parsedTable, "subscript", SymbolType.Subscript, 4);
        addSymbolInLines(parsedTable, "subscriptValue", SymbolType.Subscript_Value, 5);
        addSymbolInLines(parsedTable, "lookup", SymbolType.Lookup_Table, 6);
        addSymbolInLines(parsedTable, "realityCheck", SymbolType.Reality_Check, 7);

        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());
        SymbolTable dbTable = dbData.getDataBaseSymbols();

        addSymbolInLines(dbTable, "function", SymbolType.Variable);
        addSymbolInLines(dbTable, "constant", SymbolType.Variable);
        addSymbolInLines(dbTable, "var", SymbolType.Constant);
        addSymbolInLines(dbTable, "subscript", SymbolType.Variable);
        addSymbolInLines(dbTable, "subscriptValue", SymbolType.Variable);
        addSymbolInLines(dbTable, "lookup", SymbolType.Variable);
        addSymbolInLines(dbTable, "realityCheck", SymbolType.Variable);

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);
        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertHasIssueInLines(context, DictionaryTypeMismatchCheck.class, 2, 3, 4, 5, 6, 7);
        assertDoesntHaveIssueInLines(context, DictionaryTypeMismatchCheck.class, 1);


    }

    @Test
    public void testRuleIgnoresDefaultSymbols() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());
        SymbolTable dbTable = dbData.getDataBaseSymbols();
        SymbolTable parsedTable = new SymbolTable();


        List<Symbol> parsedSymbols = Constants.DEFAULT_VENSIM_SYMBOLS.stream().map(Symbol::new).collect(Collectors.toList());
        parsedSymbols.forEach(symbol -> {
            symbol.addDefinitionLine(1);
            symbol.setType(SymbolType.Variable);
            parsedTable.addSymbol(symbol);
        });

        List<Symbol> dbSymbols = Constants.DEFAULT_VENSIM_SYMBOLS.stream().map(Symbol::new).collect(Collectors.toList());
        dbSymbols.forEach(symbol -> {
            symbol.setType(SymbolType.Constant);
            dbTable.addSymbol(symbol);
        });

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);
        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryTypeMismatchCheck.class);


    }

    @Test
    public void testSymbolInDbButNotInFile() {
        SymbolTable parsedTable = new SymbolTable();

        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());
        SymbolTable dbTable = dbData.getDataBaseSymbols();
        dbTable.addSymbol(new Symbol("var", SymbolType.Reality_Check));

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);


        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryTypeMismatchCheck.class);
    }

}
