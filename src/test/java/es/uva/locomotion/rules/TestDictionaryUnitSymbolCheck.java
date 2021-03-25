package es.uva.locomotion.rules;

import es.uva.locomotion.model.*;
import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.testutilities.GeneralTestUtilities;
import es.uva.locomotion.utilities.Constants;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static es.uva.locomotion.testutilities.GeneralTestUtilities.addSymbolInLines;
import static es.uva.locomotion.testutilities.RuleTestUtilities.*;
import static org.junit.Assert.*;


public class TestDictionaryUnitSymbolCheck {

    @Test
    public void testIssue() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        SymbolTable parsedTable = new SymbolTable();
        Set<String> dbUnits = new HashSet<>();
        dbUnits.add("kg");
        dbData.setUnits(dbUnits);
        Symbol parsedVar = new Symbol("var");
        parsedVar.setUnits("                                                                               kg                                              ");
        parsedVar.addLine(1);
        parsedTable.addSymbol(parsedVar);


        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);

        DictionaryUnitSymbolCheck check = new DictionaryUnitSymbolCheck();
        check.scan(context);

        assertTrue(parsedVar.isValid());
        assertDoesntHaveIssueOfType(context, DictionaryUnitSymbolCheck.class);
    }

    @Test
    public void testBothUnitsAreTrimmed() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        Set<String> dbTable = new HashSet<>();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = new Symbol("var");
        parsedVar.setUnits("                                                                               kg                                              ");
        parsedVar.addLine(1);
        parsedTable.addSymbol(parsedVar);

        dbTable.add("    kg   ");
        dbData.setUnits(dbTable);
        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);

        DictionaryUnitSymbolCheck check = new DictionaryUnitSymbolCheck();
        check.scan(context);

        assertTrue(parsedVar.isValid());
        assertDoesntHaveIssueOfType(context, DictionaryUnitSymbolCheck.class);
    }


    @Test
    public void testParsedSymbolDoesntHaveUnits() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setUnits(new HashSet<>());
        Set<String> dbTable = dbData.getUnits();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = new Symbol("var");
        parsedVar.addLine(1);
        parsedVar.addLine(2);
        parsedTable.addSymbol(parsedVar);

        dbTable.add("l");

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);


        DictionaryUnitSymbolCheck check = new DictionaryUnitSymbolCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryUnitSymbolCheck.class);
    }

    @Test
    public void testIssueInDifferentSymbols() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        Set<String> dbTable = new HashSet<>();
        SymbolTable parsedTable = new SymbolTable();

        Symbol var = GeneralTestUtilities.addSymbolInLines(parsedTable, "var", SymbolType.Variable, 1);
        Symbol valid1 = GeneralTestUtilities.addSymbolInLines(parsedTable, "valid1", SymbolType.Variable, 2);
        Symbol var2 = GeneralTestUtilities.addSymbolInLines(parsedTable, "var2", SymbolType.Variable, 3);
        Symbol valid2 = GeneralTestUtilities.addSymbolInLines(parsedTable, "valid2", SymbolType.Variable, 4);
        Symbol var3 = GeneralTestUtilities.addSymbolInLines(parsedTable, "var3", SymbolType.Variable, 5);

        var.setUnits("different units");
        var2.setUnits("different units");
        var3.setUnits("different units");
        valid1.setUnits("Same units");
        valid2.setUnits("Same units");

        dbTable.add("Same units");
        dbData.setUnits(dbTable);

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);

        DictionaryUnitSymbolCheck check = new DictionaryUnitSymbolCheck();
        check.scan(context);


        assertHasIssueInLines(context, DictionaryUnitSymbolCheck.class, 1, 3, 5);
        assertDoesntHaveIssueInLines(context, DictionaryUnitSymbolCheck.class, 2, 4);


    }

    @Test
    public void testSymbolInDbButNotInFile() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setUnits(new HashSet<>());
        Set<String> dbTable = dbData.getUnits();

        dbTable.add("kg");

        VensimVisitorContext context = new VensimVisitorContext(null, new SymbolTable(), new ViewTable(), null, dbData);


        DictionaryUnitSymbolCheck check = new DictionaryUnitSymbolCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryTypeMismatchCheck.class);
    }


    @Test
    public void testDoesntRaiseIssueIfThereIsntDefinitionLines() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setUnits(new HashSet<>());
        Set<String> dbTable = dbData.getUnits();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = new Symbol("var");
        parsedVar.setUnits("kg");
        parsedTable.addSymbol(parsedVar);

        dbTable.add("l");

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);


        DictionaryUnitSymbolCheck check = new DictionaryUnitSymbolCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryUnitSymbolCheck.class);
    }

    @Test
    public void testRuleIsIgnoredIfDbTableIsNull() {

        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = new Symbol("var");
        parsedVar.setUnits("Units");
        parsedTable.addSymbol(parsedVar);

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, null);


        DictionaryUnitSymbolCheck check = new DictionaryUnitSymbolCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryUnitSymbolCheck.class);
    }

    @Test
    public void testIgnoresDefaultSymbols() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setUnits(new HashSet<>());
        Set<String> dbTable = dbData.getUnits();
        SymbolTable parsedTable = new SymbolTable();


        List<Symbol> parsedSymbols = Constants.DEFAULT_VENSIM_SYMBOLS.stream().map(Symbol::new).collect(Collectors.toList());
        parsedSymbols.forEach(symbol -> {
            symbol.addLine(1);
            symbol.setUnits("Parsed units");
            parsedTable.addSymbol(symbol);
        });

        List<Symbol> dbSymbols = Constants.DEFAULT_VENSIM_SYMBOLS.stream().map(Symbol::new).collect(Collectors.toList());
        dbSymbols.forEach(symbol -> symbol.setUnits("DB units"));

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);
        DictionaryUnitSymbolCheck check = new DictionaryUnitSymbolCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context, DictionaryUnitSymbolCheck.class);
    }

    @Test
    public void testIgnoresFunctions() {
        SymbolTable parsedTable = new SymbolTable();
        addSymbolInLines(parsedTable, "function", SymbolType.Function, 1);
        addSymbolInLines(parsedTable, "constant", SymbolType.Constant, 2);
        addSymbolInLines(parsedTable, "var", SymbolType.Variable, 3);
        addSymbolInLines(parsedTable, "subscript", SymbolType.Subscript, 4);
        addSymbolInLines(parsedTable, "subscriptValue", SymbolType.Subscript_Value, 5);
        addSymbolInLines(parsedTable, "lookup", SymbolType.Lookup_Table, 6);
        addSymbolInLines(parsedTable, "realityCheck", SymbolType.Reality_Check, 7);

        for (Symbol s : parsedTable.getSymbols())
            s.setUnits("Parsed units");

        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setUnits(new HashSet<>());
        Set<String> dbTable = dbData.getUnits();

        dbTable.add("Db units");


        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);
        DictionaryUnitSymbolCheck check = new DictionaryUnitSymbolCheck();
        check.scan(context);

        assertHasIssueInLines(context, DictionaryUnitSymbolCheck.class, 2, 3, 4, 6, 7);
        assertDoesntHaveIssueInLines(context, DictionaryUnitSymbolCheck.class, 1, 5);
    }
}
