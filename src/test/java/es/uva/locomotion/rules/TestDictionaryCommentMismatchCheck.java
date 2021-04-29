package es.uva.locomotion.rules;

import es.uva.locomotion.model.DataBaseRepresentation;
import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.testutilities.GeneralTestUtilities;
import es.uva.locomotion.utilities.Constants;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static es.uva.locomotion.testutilities.GeneralTestUtilities.addSymbolInLines;
import static es.uva.locomotion.testutilities.RuleTestUtilities.*;
import static org.junit.Assert.*;


public class TestDictionaryCommentMismatchCheck {
    @Test
    public void testIssue() {
        DataBaseRepresentation dbTable = new DataBaseRepresentation();
        SymbolTable dbSymbolTable = new SymbolTable();
        dbTable.setDataBaseSymbols(dbSymbolTable);
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = new Symbol("var");
        parsedVar.setComment("A comment");
        parsedVar.addLine(1);
        parsedVar.addLine(2);
        parsedTable.addSymbol(parsedVar);

        Symbol dbVar = new Symbol("var");
        dbVar.setComment("Doesn't match");
        dbSymbolTable.addSymbol(dbVar);

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbTable);


        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);

        assertEquals(2, context.getIssues().size());
        assertHasIssueInLines(context, DictionaryCommentMismatchCheck.class, 1, 2);


        assertFalse(parsedVar.isValid());
        for (Issue issue : context.getIssues())
            assertEquals("The symbol 'var' has a comment 'A comment' but the dictionary has 'Doesn't match'.", issue.getMessage());
    }

    @Test
    public void testParsedSymbolDoesntHaveComment() {
        DataBaseRepresentation dbTable = new DataBaseRepresentation();
        dbTable.setDataBaseSymbols(new SymbolTable());
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = new Symbol("var");
        parsedVar.addLine(1);
        parsedVar.addLine(2);
        parsedTable.addSymbol(parsedVar);

        Symbol dbVar = new Symbol("var");
        dbVar.setComment("comment");
        dbTable.getDataBaseSymbolTable().addSymbol(dbVar);

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbTable);


        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);

        assertTrue(parsedVar.isValid());
        assertDoesntHaveIssueOfType(context, DictionaryCommentMismatchCheck.class);
    }

    @Test
    public void testBothCommentsAreTrimmed() {
        DataBaseRepresentation dbTable = new DataBaseRepresentation();
        dbTable.setDataBaseSymbols(new SymbolTable());
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = new Symbol("var");
        parsedVar.setComment("                                                                               Some comment                                              ");
        parsedVar.addLine(1);
        parsedTable.addSymbol(parsedVar);

        Symbol dbVar = new Symbol("var");
        dbVar.setComment("    Some comment    ");
        dbTable.getDataBaseSymbolTable().addSymbol(dbVar);

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbTable);

        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryCommentMismatchCheck.class);
    }


    @Test
    public void testIssueInDifferentSymbols() {
        DataBaseRepresentation dbTable = new DataBaseRepresentation();
        SymbolTable dbSymbolTable = new SymbolTable();
        dbTable.setDataBaseSymbols(dbSymbolTable);
        SymbolTable parsedTable = new SymbolTable();

        Symbol var = GeneralTestUtilities.addSymbolInLines(parsedTable, "var", SymbolType.VARIABLE, 1);
        Symbol valid1 = GeneralTestUtilities.addSymbolInLines(parsedTable, "valid1", SymbolType.VARIABLE, 2);
        Symbol var2 = GeneralTestUtilities.addSymbolInLines(parsedTable, "var2", SymbolType.VARIABLE, 3);
        Symbol valid2 = GeneralTestUtilities.addSymbolInLines(parsedTable, "valid2", SymbolType.VARIABLE, 4);
        Symbol var3 = GeneralTestUtilities.addSymbolInLines(parsedTable, "var3", SymbolType.VARIABLE, 5);

        var.setComment("different comment");
        var2.setComment("different comment");
        var3.setComment("different comment");
        valid1.setComment("Same comment");
        valid2.setComment("Same comment");

        Symbol dbVar = GeneralTestUtilities.addSymbolInLines(dbSymbolTable, "var", SymbolType.VARIABLE);
        Symbol dbValid = GeneralTestUtilities.addSymbolInLines(dbSymbolTable, "valid1", SymbolType.VARIABLE);
        Symbol dbVar2 = GeneralTestUtilities.addSymbolInLines(dbSymbolTable, "var2", SymbolType.VARIABLE, 3);
        Symbol dbValid2 = GeneralTestUtilities.addSymbolInLines(dbSymbolTable, "valid2", SymbolType.VARIABLE);
        Symbol dbVar3 = GeneralTestUtilities.addSymbolInLines(dbSymbolTable, "var3", SymbolType.VARIABLE);

        dbVar.setComment("mismatch");
        dbVar2.setComment("mismatch");
        dbVar3.setComment("mismatch");
        dbValid.setComment("Same comment");
        dbValid2.setComment("Same comment");

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbTable);

        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);


        assertHasIssueInLines(context, DictionaryCommentMismatchCheck.class, 1, 3, 5);
        assertDoesntHaveIssueInLines(context, DictionaryCommentMismatchCheck.class, 2, 4);


    }


    @Test
    public void testSymbolNotFoundInDB() {
        SymbolTable parsedTable = new SymbolTable();


        Symbol var = new Symbol("var");
        var.setComment("Some comment");
        parsedTable.addSymbol(var);

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, new DataBaseRepresentation());


        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryTypeMismatchCheck.class);
    }

    @Test
    public void testSymbolInDbButNotInFile() {
        DataBaseRepresentation dbTable = new DataBaseRepresentation();
        dbTable.setDataBaseSymbols(new SymbolTable());


        Symbol dbVar = new Symbol("var");
        dbVar.setComment("Some comment");
        dbTable.getDataBaseSymbolTable().addSymbol(dbVar);

        VensimVisitorContext context = new VensimVisitorContext(null, new SymbolTable(), new ViewTable(), null, dbTable);


        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryTypeMismatchCheck.class);
    }


    @Test
    public void testDoesntRaiseIssueIfThereIsntDefinitionLines() {
        DataBaseRepresentation dbTable = new DataBaseRepresentation();
        dbTable.setDataBaseSymbols(new SymbolTable());
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = new Symbol("var");
        parsedVar.setComment("A comment");
        parsedTable.addSymbol(parsedVar);

        Symbol dbVar = new Symbol("var");
        dbVar.setComment("Doesn't match");
        dbTable.getDataBaseSymbolTable().addSymbol(dbVar);

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbTable);


        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryCommentMismatchCheck.class);
    }

    @Test
    public void testRuleIsIgnoredIfDbTableIsNull() {

        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = new Symbol("var");
        parsedVar.setComment("A comment");
        parsedTable.addSymbol(parsedVar);

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, null);


        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryCommentMismatchCheck.class);
    }

    @Test
    public void testIgnoresDefaultSymbols() {
        DataBaseRepresentation dbTable = new DataBaseRepresentation();
        dbTable.setDataBaseSymbols(new SymbolTable());
        SymbolTable parsedTable = new SymbolTable();


        List<Symbol> parsedSymbols = Constants.DEFAULT_VENSIM_SYMBOLS.stream().map(Symbol::new).collect(Collectors.toList());
        parsedSymbols.forEach(symbol -> {
            symbol.addLine(1);
            symbol.setComment("Parsed comment");
            parsedTable.addSymbol(symbol);
        });

        List<Symbol> dbSymbols = Constants.DEFAULT_VENSIM_SYMBOLS.stream().map(Symbol::new).collect(Collectors.toList());
        dbSymbols.forEach(symbol -> {
            symbol.setComment("DB comment");
            dbTable.getDataBaseSymbolTable().addSymbol(symbol);
        });

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbTable);
        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryCommentMismatchCheck.class);
    }

    @Test
    public void testIgnoresFunctions() {
        SymbolTable parsedTable = new SymbolTable();
        addSymbolInLines(parsedTable, "function", SymbolType.FUNCTION, 1);
        addSymbolInLines(parsedTable, "constant", SymbolType.CONSTANT, 2);
        addSymbolInLines(parsedTable, "var", SymbolType.VARIABLE, 3);
        addSymbolInLines(parsedTable, "subscript", SymbolType.SUBSCRIPT, 4);
        addSymbolInLines(parsedTable, "subscriptValue", SymbolType.SUBSCRIPT_VALUE, 5);
        addSymbolInLines(parsedTable, "lookup", SymbolType.LOOKUP_TABLE, 6);
        addSymbolInLines(parsedTable, "realityCheck", SymbolType.REALITY_CHECK, 7);

        for (Symbol s : parsedTable.getSymbols())
            s.setComment("Parsed Comment");

        DataBaseRepresentation dbData = new DataBaseRepresentation();
        SymbolTable dbTable = new SymbolTable();
        dbData.setDataBaseSymbols(dbTable);
        addSymbolInLines(dbTable, "function", SymbolType.VARIABLE);
        addSymbolInLines(dbTable, "constant", SymbolType.VARIABLE);
        addSymbolInLines(dbTable, "var", SymbolType.VARIABLE);
        addSymbolInLines(dbTable, "subscript", SymbolType.VARIABLE);
        addSymbolInLines(dbTable, "subscriptValue", SymbolType.VARIABLE);
        addSymbolInLines(dbTable, "lookup", SymbolType.VARIABLE);
        addSymbolInLines(dbTable, "realityCheck", SymbolType.VARIABLE);

        for (Symbol s : dbTable.getSymbols())
            s.setComment("Db Comment");


        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);
        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);

        assertHasIssueInLines(context, DictionaryCommentMismatchCheck.class, 2, 3, 4, 5, 6, 7);
        assertDoesntHaveIssueInLines(context, DictionaryCommentMismatchCheck.class, 1);
    }
}
