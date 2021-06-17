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


public class TestDictionaryUnitsMismatchCheck {
    @Test
    public void testIssue() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        SymbolTable dbTable = new SymbolTable();
        dbData.setDataBaseSymbols(dbTable);
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = new Symbol("var");
        parsedVar.setUnits("kg");
        parsedVar.addLine(1);
        parsedVar.addLine(2);
        parsedTable.addSymbol(parsedVar);

        Symbol dbVar = new Symbol("var");
        dbVar.setUnits("meters");
        dbTable.addSymbol(dbVar);

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);


        DictionaryUnitsMismatchCheck check = new DictionaryUnitsMismatchCheck();
        check.scan(context);

        assertEquals(2, context.getIssues().size());
        assertHasIssueInLines(context, DictionaryUnitsMismatchCheck.class, 1, 2);

        assertFalse(parsedVar.isValid());
        for (Issue issue : context.getIssues())
            assertEquals("The symbol 'var' has 'kg' as units but the dictionary has 'meters'.", issue.getMessage());
    }


    @Test
    public void testBothUnitsAreTrimmed() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());
        SymbolTable dbTable = dbData.getDataBaseSymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = new Symbol("var");
        parsedVar.setUnits("                                                                               kg                                              ");
        parsedVar.addLine(1);
        parsedTable.addSymbol(parsedVar);

        Symbol dbVar = new Symbol("var");
        dbVar.setUnits("    kg   ");
        dbTable.addSymbol(dbVar);

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);

        DictionaryUnitsMismatchCheck check = new DictionaryUnitsMismatchCheck();
        check.scan(context);

        assertTrue(parsedVar.isValid());
        assertDoesntHaveIssueOfType(context, DictionaryUnitsMismatchCheck.class);
    }


    @Test
    public void testParsedSymbolDoesntHaveUnits() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());
        SymbolTable dbTable = dbData.getDataBaseSymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = new Symbol("var");
        parsedVar.addLine(1);
        parsedVar.addLine(2);
        parsedTable.addSymbol(parsedVar);

        Symbol dbVar = new Symbol("var");
        dbVar.setUnits("l");
        dbTable.addSymbol(dbVar);

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);


        DictionaryUnitsMismatchCheck check = new DictionaryUnitsMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryUnitsMismatchCheck.class);
    }

    @Test
    public void testIssueInDifferentSymbols() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        SymbolTable dbTable = new SymbolTable();
        dbData.setDataBaseSymbols(dbTable);
        SymbolTable parsedTable = new SymbolTable();

        Symbol var = GeneralTestUtilities.addSymbolInLines(parsedTable, "var", SymbolType.VARIABLE, 1);
        Symbol valid1 = GeneralTestUtilities.addSymbolInLines(parsedTable, "valid1", SymbolType.VARIABLE, 2);
        Symbol var2 = GeneralTestUtilities.addSymbolInLines(parsedTable, "var2", SymbolType.VARIABLE, 3);
        Symbol valid2 = GeneralTestUtilities.addSymbolInLines(parsedTable, "valid2", SymbolType.VARIABLE, 4);
        Symbol var3 = GeneralTestUtilities.addSymbolInLines(parsedTable, "var3", SymbolType.VARIABLE, 5);

        var.setUnits("different units");
        var2.setUnits("different units");
        var3.setUnits("different units");
        valid1.setUnits("Same units");
        valid2.setUnits("Same units");

        Symbol dbVar = GeneralTestUtilities.addSymbolInLines(dbTable, "var", SymbolType.VARIABLE);
        Symbol dbValid = GeneralTestUtilities.addSymbolInLines(dbTable, "valid1", SymbolType.VARIABLE);
        Symbol dbVar2 = GeneralTestUtilities.addSymbolInLines(dbTable, "var2", SymbolType.VARIABLE, 3);
        Symbol dbValid2 = GeneralTestUtilities.addSymbolInLines(dbTable, "valid2", SymbolType.VARIABLE);
        Symbol dbVar3 = GeneralTestUtilities.addSymbolInLines(dbTable, "var3", SymbolType.VARIABLE);

        dbVar.setUnits("mismatch");
        dbVar2.setUnits("mismatch");
        dbVar3.setUnits("mismatch");
        dbValid.setUnits("Same units");
        dbValid2.setUnits("Same units");

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);

        DictionaryUnitsMismatchCheck check = new DictionaryUnitsMismatchCheck();
        check.scan(context);


        assertHasIssueInLines(context, DictionaryUnitsMismatchCheck.class, 1, 3, 5);
        assertDoesntHaveIssueInLines(context, DictionaryUnitsMismatchCheck.class, 2, 4);


    }


    @Test
    public void testSymbolNotFoundInDB() {
        SymbolTable parsedTable = new SymbolTable();


        Symbol var = new Symbol("var");
        var.setUnits("kg");
        parsedTable.addSymbol(var);

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, new DataBaseRepresentation());


        DictionaryUnitsMismatchCheck check = new DictionaryUnitsMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryTypeMismatchCheck.class);
    }

    @Test
    public void testSymbolInDbButNotInFile() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());
        SymbolTable dbTable = dbData.getDataBaseSymbolTable();


        Symbol dbVar = new Symbol("var");
        dbVar.setUnits("kg");
        dbTable.addSymbol(dbVar);

        VensimVisitorContext context = new VensimVisitorContext(null, new SymbolTable(), new ViewTable(), null, dbData);


        DictionaryUnitsMismatchCheck check = new DictionaryUnitsMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryTypeMismatchCheck.class);
    }


    @Test
    public void testDoesntRaiseIssueIfThereIsntDefinitionLines() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());
        SymbolTable dbTable = dbData.getDataBaseSymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = new Symbol("var");
        parsedVar.setUnits("kg");
        parsedTable.addSymbol(parsedVar);

        Symbol dbVar = new Symbol("var");
        dbVar.setUnits("l");
        dbTable.addSymbol(dbVar);

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);


        DictionaryUnitsMismatchCheck check = new DictionaryUnitsMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryUnitsMismatchCheck.class);
    }

    @Test
    public void testRuleIsIgnoredIfDbTableIsNull() {

        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = new Symbol("var");
        parsedVar.setUnits("Units");
        parsedTable.addSymbol(parsedVar);

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, null);


        DictionaryUnitsMismatchCheck check = new DictionaryUnitsMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryUnitsMismatchCheck.class);
    }

    @Test
    public void testIgnoresDefaultSymbols() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());
        SymbolTable dbTable = dbData.getDataBaseSymbolTable();
        SymbolTable parsedTable = new SymbolTable();


        List<Symbol> parsedSymbols = Constants.DEFAULT_VENSIM_SYMBOLS.stream().map(Symbol::new).collect(Collectors.toList());
        parsedSymbols.forEach(symbol -> {
            symbol.addLine(1);
            symbol.setUnits("Parsed units");
            parsedTable.addSymbol(symbol);
        });

        List<Symbol> dbSymbols = Constants.DEFAULT_VENSIM_SYMBOLS.stream().map(Symbol::new).collect(Collectors.toList());
        dbSymbols.forEach(symbol -> {
            symbol.setUnits("DB units");
            dbTable.addSymbol(symbol);
        });

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);
        DictionaryUnitsMismatchCheck check = new DictionaryUnitsMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryUnitsMismatchCheck.class);
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
            s.setUnits("Parsed units");

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
            s.setUnits("Db units");


        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);
        DictionaryUnitsMismatchCheck check = new DictionaryUnitsMismatchCheck();
        check.scan(context);

        assertHasIssueInLines(context, DictionaryUnitsMismatchCheck.class, 2, 3, 4, 6, 7);
        assertDoesntHaveIssueInLines(context, DictionaryUnitsMismatchCheck.class, 1, 5);
    }
}
