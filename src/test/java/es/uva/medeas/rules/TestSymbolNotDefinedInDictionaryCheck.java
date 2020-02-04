package es.uva.medeas.rules;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import es.uva.medeas.plugin.VensimVisitorContext;

import org.junit.Test;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static es.uva.medeas.testutilities.RuleTestUtilities.assertDoesntHaveIssue;
import static es.uva.medeas.testutilities.RuleTestUtilities.assertHasIssue;
import static es.uva.medeas.testutilities.TestUtilities.addSymbolInLines;
import static org.junit.Assert.*;


public class TestSymbolNotDefinedInDictionaryCheck {




    @Test
    public void testCompareEmptyTables(){
        VensimVisitorContext context = new VensimVisitorContext(null,new SymbolTable(),new SymbolTable());

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testDbTableIsNull(){
        VensimVisitorContext context = new VensimVisitorContext(null,new SymbolTable(),null);

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testMissingSymbolInDB(){
        SymbolTable parsedTable = new SymbolTable();
        addSymbolInLines(parsedTable,"foo",SymbolType.CONSTANT,1,2);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,new SymbolTable());

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);

        assertHasIssue(context, SymbolNotDefinedInDictionaryCheck.class,1);
        assertHasIssue(context, SymbolNotDefinedInDictionaryCheck.class,2);
    }

    @Test
    public void testDbHasExtraSymbols(){
        SymbolTable dbTable = new SymbolTable();
        Symbol symbol = new Symbol("foo");
        dbTable.addSymbol(symbol);

        VensimVisitorContext context = new VensimVisitorContext(null,new SymbolTable(), dbTable);

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);


        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testCheckWithMultipleSymbolsWithoutIssues(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        addSymbolInLines(parsedTable,"var1",SymbolType.CONSTANT,1,2,3);
        dbTable.addSymbol(new Symbol("var1"));

        addSymbolInLines(parsedTable,"foo",SymbolType.VARIABLE,4);
        dbTable.addSymbol(new Symbol("foo"));

        addSymbolInLines(parsedTable,"easter_egg",SymbolType.LOOKUP,2,3);
        dbTable.addSymbol(new Symbol("easter_egg"));

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);




    }

    @Test
    public void testCheckWithMultipleSymbolsWithIssues(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        dbTable.addSymbol(new Symbol("var1"));
        dbTable.addSymbol(new Symbol("var2"));

        addSymbolInLines(parsedTable,"var0",SymbolType.VARIABLE,1);
        addSymbolInLines(parsedTable,"var1",SymbolType.CONSTANT,2);
        addSymbolInLines(parsedTable,"var1.5",SymbolType.SUBSCRIPT_NAME,3);
        addSymbolInLines(parsedTable, "var2",SymbolType.SUBSCRIPT_VALUE,4);
        addSymbolInLines(parsedTable,"var2.5",SymbolType.LOOKUP,5);


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);


        assertHasIssue(context, SymbolNotDefinedInDictionaryCheck.class,1);
        assertHasIssue(context, SymbolNotDefinedInDictionaryCheck.class,3);
        assertHasIssue(context, SymbolNotDefinedInDictionaryCheck.class,5);

        assertDoesntHaveIssue(context, SymbolNotDefinedInDictionaryCheck.class,2);
        assertDoesntHaveIssue(context, SymbolNotDefinedInDictionaryCheck.class,4);

    }


    @Test
    public void testMissingInDBDoesntHaveDefinitionLine(){
        SymbolTable parsedTable = new SymbolTable();
        parsedTable.addSymbol(new Symbol("foo"));

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,new SymbolTable());

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testRuleIgnoresFunctions(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();


        addSymbolInLines(parsedTable,"funko",SymbolType.FUNCTION,1);
        addSymbolInLines(parsedTable,"constant",SymbolType.CONSTANT,2);
        addSymbolInLines(parsedTable,"var",SymbolType.VARIABLE,3);
        addSymbolInLines(parsedTable, "subscript",SymbolType.SUBSCRIPT_NAME,4);
        addSymbolInLines(parsedTable,"funko2",SymbolType.FUNCTION,5);
        addSymbolInLines(parsedTable,"subscriptValue",SymbolType.SUBSCRIPT_VALUE,6);
        addSymbolInLines(parsedTable,"lookup",SymbolType.LOOKUP,7);
        addSymbolInLines(parsedTable,"realityCheck",SymbolType.REALITY_CHECK,8);
        addSymbolInLines(parsedTable,"funko3",SymbolType.FUNCTION,9);


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);


        assertDoesntHaveIssue(context, SymbolNotDefinedInDictionaryCheck.class,1);
        assertDoesntHaveIssue(context, SymbolNotDefinedInDictionaryCheck.class,5);
        assertDoesntHaveIssue(context, SymbolNotDefinedInDictionaryCheck.class,9);

        assertHasIssue(context, SymbolNotDefinedInDictionaryCheck.class,2);
        assertHasIssue(context, SymbolNotDefinedInDictionaryCheck.class,3);
        assertHasIssue(context, SymbolNotDefinedInDictionaryCheck.class,4);
        assertHasIssue(context, SymbolNotDefinedInDictionaryCheck.class,6);
        assertHasIssue(context, SymbolNotDefinedInDictionaryCheck.class,7);
        assertHasIssue(context, SymbolNotDefinedInDictionaryCheck.class,8);
    }

    @Test
    public void testControllerIgnoresDefaultSymbols(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();


        List<Symbol> symbols = Arrays.asList("FINAL TIME", "INITIAL TIME", "SAVEPER", "TIME STEP").stream().map(Symbol::new).collect(Collectors.toList());
        symbols.forEach(symbol -> {symbol.addDefinitionLine(1);
                                    parsedTable.addSymbol(symbol);
                                    });

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);

        assertDoesntHaveIssue(context, SymbolNotDefinedInDictionaryCheck.class,1);

    }


}
