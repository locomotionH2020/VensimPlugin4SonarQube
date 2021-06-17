package es.uva.locomotion.rules;


import es.uva.locomotion.model.DataBaseRepresentation;
import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import es.uva.locomotion.model.VensimVisitorContext;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.testutilities.GeneralTestUtilities;
import es.uva.locomotion.utilities.Constants;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static es.uva.locomotion.testutilities.GeneralTestUtilities.addSymbolInLines;
import static es.uva.locomotion.testutilities.RuleTestUtilities.*;
import static org.junit.Assert.*;

public class TestDictionaryTypeMismatchCheck {


    @Test
    public void testIssue() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        SymbolTable dbTable = new SymbolTable();
        dbData.setDataBaseSymbols(dbTable);
        SymbolTable parsedTable = new SymbolTable();

        GeneralTestUtilities.addSymbolInLines(dbTable, "var", SymbolType.SUBSCRIPT);
        Symbol parsedVar = addSymbolInLines(parsedTable, "var", SymbolType.VARIABLE, 1, 2, 3);


        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);


        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertEquals(3, context.getIssues().size());
        assertHasIssueInLines(context, DictionaryTypeMismatchCheck.class, 1, 2, 3);


        assertFalse(parsedVar.isValid());
        for (Issue issue : context.getIssues())
            assertEquals("The symbol 'var' has type 'VARIABLE' but the dictionary has 'SUBSCRIPT'.", issue.getMessage());
    }

    @Test
    public void testIssueInDifferentSymbols() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        SymbolTable dbTable = new SymbolTable();
        dbData.setDataBaseSymbols(dbTable);
        SymbolTable parsedTable = new SymbolTable();

        GeneralTestUtilities.addSymbolInLines(dbTable, "var", SymbolType.VARIABLE);
        addSymbolInLines(parsedTable, "var", SymbolType.REALITY_CHECK, 1);

        GeneralTestUtilities.addSymbolInLines(dbTable, "valid", SymbolType.CONSTANT);
        Symbol valid = addSymbolInLines(parsedTable, "valid", SymbolType.CONSTANT, 2);

        GeneralTestUtilities.addSymbolInLines(dbTable, "var2", SymbolType.REALITY_CHECK);
        GeneralTestUtilities.addSymbolInLines(parsedTable, "var2", SymbolType.SUBSCRIPT_VALUE, 3);

        GeneralTestUtilities.addSymbolInLines(dbTable, "valid2", SymbolType.SUBSCRIPT_VALUE);
        GeneralTestUtilities.addSymbolInLines(parsedTable, "valid2", SymbolType.SUBSCRIPT_VALUE, 4);

        GeneralTestUtilities.addSymbolInLines(dbTable, "var3", SymbolType.LOOKUP_TABLE);
        GeneralTestUtilities.addSymbolInLines(parsedTable, "var3", SymbolType.SUBSCRIPT, 5);

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

        GeneralTestUtilities.addSymbolInLines(parsedTable, "var", SymbolType.VARIABLE, 1, 2, 3);


        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, new DataBaseRepresentation());
        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryTypeMismatchCheck.class);

    }

    @Test
    public void testRuleIsIgnoredIfDbTableIsNull() {
        SymbolTable parsedTable = new SymbolTable();

        GeneralTestUtilities.addSymbolInLines(parsedTable, "var", SymbolType.VARIABLE, 1, 2, 3);


        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, null);
        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryTypeMismatchCheck.class);


    }

    @Test
    public void testRuleIsIgnoredIfSymbolDoenstHaveDefinitionLine() {
        SymbolTable parsedTable = new SymbolTable();
        parsedTable.addSymbol(new Symbol("var", SymbolType.SUBSCRIPT));

        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());
        SymbolTable dbTable = dbData.getDataBaseSymbolTable();
        dbTable.addSymbol(new Symbol("var", SymbolType.REALITY_CHECK));

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);


        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryTypeMismatchCheck.class);

    }

    @Test
    public void testRuleIgnoresFunctions() {

        SymbolTable parsedTable = new SymbolTable();
        addSymbolInLines(parsedTable, "function", SymbolType.FUNCTION, 1);
        addSymbolInLines(parsedTable, "constant", SymbolType.CONSTANT, 2);
        addSymbolInLines(parsedTable, "var", SymbolType.VARIABLE, 3);
        addSymbolInLines(parsedTable, "subscript", SymbolType.SUBSCRIPT, 4);
        addSymbolInLines(parsedTable, "subscriptValue", SymbolType.SUBSCRIPT_VALUE, 5);
        addSymbolInLines(parsedTable, "lookup", SymbolType.LOOKUP_TABLE, 6);
        addSymbolInLines(parsedTable, "realityCheck", SymbolType.REALITY_CHECK, 7);

        DataBaseRepresentation dbData = new DataBaseRepresentation();
        SymbolTable dbTable = new SymbolTable();
        dbData.setDataBaseSymbols(dbTable);

        addSymbolInLines(dbTable, "function", SymbolType.VARIABLE);
        addSymbolInLines(dbTable, "constant", SymbolType.VARIABLE);
        addSymbolInLines(dbTable, "var", SymbolType.CONSTANT);
        addSymbolInLines(dbTable, "subscript", SymbolType.VARIABLE);
        addSymbolInLines(dbTable, "subscriptValue", SymbolType.VARIABLE);
        addSymbolInLines(dbTable, "lookup", SymbolType.VARIABLE);
        addSymbolInLines(dbTable, "realityCheck", SymbolType.VARIABLE);

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
        SymbolTable dbTable = dbData.getDataBaseSymbolTable();
        SymbolTable parsedTable = new SymbolTable();


        List<Symbol> parsedSymbols = Constants.DEFAULT_VENSIM_SYMBOLS.stream().map(Symbol::new).collect(Collectors.toList());
        parsedSymbols.forEach(symbol -> {
            symbol.addLine(1);
            symbol.setType(SymbolType.VARIABLE);
            parsedTable.addSymbol(symbol);
        });

        List<Symbol> dbSymbols = Constants.DEFAULT_VENSIM_SYMBOLS.stream().map(Symbol::new).collect(Collectors.toList());
        dbSymbols.forEach(symbol -> {
            symbol.setType(SymbolType.CONSTANT);
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
        SymbolTable dbTable = dbData.getDataBaseSymbolTable();
        dbTable.addSymbol(new Symbol("var", SymbolType.REALITY_CHECK));

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);


        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryTypeMismatchCheck.class);
    }

}
