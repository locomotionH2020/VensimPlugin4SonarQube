package es.uva.locomotion.rules;

import es.uva.locomotion.model.DataBaseRepresentation;
import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.utilities.Constants;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static es.uva.locomotion.testutilities.GeneralTestUtilities.addSymbolInLines;
import static es.uva.locomotion.testutilities.RuleTestUtilities.assertDoesntHaveIssueInLines;
import static es.uva.locomotion.testutilities.RuleTestUtilities.assertHasIssueInLines;
import static org.junit.Assert.assertTrue;


public class TestSymbolNotDefinedInDictionaryCheck {


    @Test
    public void testCompareEmptyTables() {
        VensimVisitorContext context = new VensimVisitorContext(null, new SymbolTable(), new ViewTable(), null, new DataBaseRepresentation());

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testDbTableIsNull() {
        VensimVisitorContext context = new VensimVisitorContext(null, new SymbolTable(), new ViewTable(), null, null);

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testMissingSymbolInDB() {
        SymbolTable parsedTable = new SymbolTable();
        addSymbolInLines(parsedTable, "foo", SymbolType.CONSTANT, 1, 2);

        DataBaseRepresentation dbdata= new DataBaseRepresentation();
        dbdata.setDataBaseSymbols(new SymbolTable());
        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbdata);

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);

        assertHasIssueInLines(context, SymbolNotDefinedInDictionaryCheck.class, 1, 2);
    }

    @Test
    public void testDbHasExtraSymbols() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());
        SymbolTable dbTable = dbData.getDataBaseSymbols();
        Symbol symbol = new Symbol("foo");
        dbTable.addSymbol(symbol);

        VensimVisitorContext context = new VensimVisitorContext(null, new SymbolTable(), new ViewTable(), null, dbData);

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);


        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testCheckWithMultipleSymbolsWithoutIssues() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());
        SymbolTable dbTable = dbData.getDataBaseSymbols();
        SymbolTable parsedTable = new SymbolTable();

        addSymbolInLines(parsedTable, "var1", SymbolType.CONSTANT, 1, 2, 3);
        dbTable.addSymbol(new Symbol("var1"));

        addSymbolInLines(parsedTable, "foo", SymbolType.VARIABLE, 4);
        dbTable.addSymbol(new Symbol("foo"));

        addSymbolInLines(parsedTable, "easter_egg", SymbolType.LOOKUP_TABLE, 2, 3);
        dbTable.addSymbol(new Symbol("easter_egg"));

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);


    }

    @Test
    public void testCheckWithMultipleSymbolsWithIssues() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());
        SymbolTable dbTable = dbData.getDataBaseSymbols();
        SymbolTable parsedTable = new SymbolTable();

        dbTable.addSymbol(new Symbol("var1"));
        dbTable.addSymbol(new Symbol("var2"));

        addSymbolInLines(parsedTable, "var0", SymbolType.VARIABLE, 1);
        addSymbolInLines(parsedTable, "var1", SymbolType.CONSTANT, 2);
        addSymbolInLines(parsedTable, "var1.5", SymbolType.SUBSCRIPT, 3);
        addSymbolInLines(parsedTable, "var2", SymbolType.SUBSCRIPT_VALUE, 4);
        addSymbolInLines(parsedTable, "var2.5", SymbolType.LOOKUP_TABLE, 5);


        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);


        assertHasIssueInLines(context, SymbolNotDefinedInDictionaryCheck.class, 1, 3, 5);

        assertDoesntHaveIssueInLines(context, SymbolNotDefinedInDictionaryCheck.class, 2, 4);

    }


    @Test
    public void testMissingInDBDoesntHaveDefinitionLine() {
        SymbolTable parsedTable = new SymbolTable();
        parsedTable.addSymbol(new Symbol("foo"));

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, new DataBaseRepresentation());

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testRuleIgnoresFunctions() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());
        SymbolTable dbTable = dbData.getDataBaseSymbols();
        SymbolTable parsedTable = new SymbolTable();


        addSymbolInLines(parsedTable, "funko", SymbolType.FUNCTION, 1);
        addSymbolInLines(parsedTable, "constant", SymbolType.CONSTANT, 2);
        addSymbolInLines(parsedTable, "var", SymbolType.VARIABLE, 3);
        addSymbolInLines(parsedTable, "subscript", SymbolType.SUBSCRIPT, 4);
        addSymbolInLines(parsedTable, "funko2", SymbolType.FUNCTION, 5);
        addSymbolInLines(parsedTable, "subscriptValue", SymbolType.SUBSCRIPT_VALUE, 6);
        addSymbolInLines(parsedTable, "lookup", SymbolType.LOOKUP_TABLE, 7);
        addSymbolInLines(parsedTable, "realityCheck", SymbolType.REALITY_CHECK, 8);
        addSymbolInLines(parsedTable, "funko3", SymbolType.FUNCTION, 9);


        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);


        assertDoesntHaveIssueInLines(context, SymbolNotDefinedInDictionaryCheck.class, 1, 5, 9);

        assertHasIssueInLines(context, SymbolNotDefinedInDictionaryCheck.class, 2, 3, 4, 6, 7, 8);
    }

    @Test
    public void testControllerIgnoresDefaultSymbols() {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());
        SymbolTable dbTable = dbData.getDataBaseSymbols();
        SymbolTable parsedTable = new SymbolTable();


        List<Symbol> symbols = Constants.DEFAULT_VENSIM_SYMBOLS.stream().map(Symbol::new).collect(Collectors.toList());
        symbols.forEach(symbol -> {
            symbol.addLine(1);
            parsedTable.addSymbol(symbol);
        });

        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);

        assertDoesntHaveIssueInLines(context, SymbolNotDefinedInDictionaryCheck.class, 1);

    }

    @Test
    public void testFailingRuleDoesntMakeSymbolInvalid() {
        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();

        SymbolTable parsedTable = new SymbolTable();
        Symbol symbolNotInDB = new Symbol("invalid", SymbolType.SUBSCRIPT_VALUE);
        symbolNotInDB.addLine(1);
        Symbol valid = new Symbol("valid", SymbolType.SUBSCRIPT_VALUE);
        valid.addLine(2);
        parsedTable.addSymbol(symbolNotInDB);
        parsedTable.addSymbol(valid);

        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setDataBaseSymbols(new SymbolTable());
        SymbolTable dbTable = dbData.getDataBaseSymbols();
        dbTable.addSymbol(new Symbol("valid"));


        VensimVisitorContext context = new VensimVisitorContext(null, parsedTable, new ViewTable(), null, dbData);
        check.scan(context);

        assertTrue(valid.isValid());
        assertTrue(symbolNotInDB.isValid());
    }

}
