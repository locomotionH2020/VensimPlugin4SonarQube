package es.uva.locomotion.rules;

import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.SymbolTable;
import es.uva.locomotion.parser.SymbolType;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;

import es.uva.locomotion.utilities.Constants;
import org.junit.Test;


import java.util.List;
import java.util.stream.Collectors;

import static es.uva.locomotion.testutilities.RuleTestUtilities.assertDoesntHaveIssueInLines;
import static es.uva.locomotion.testutilities.RuleTestUtilities.assertHasIssueInLines;
import static es.uva.locomotion.testutilities.GeneralTestUtilities.addSymbolInLines;
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
        addSymbolInLines(parsedTable,"foo",SymbolType.Constant,1,2);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,new SymbolTable());

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);

        assertHasIssueInLines(context, SymbolNotDefinedInDictionaryCheck.class,1,2);
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

        addSymbolInLines(parsedTable,"var1",SymbolType.Constant,1,2,3);
        dbTable.addSymbol(new Symbol("var1"));

        addSymbolInLines(parsedTable,"foo",SymbolType.Variable,4);
        dbTable.addSymbol(new Symbol("foo"));

        addSymbolInLines(parsedTable,"easter_egg",SymbolType.Lookup_Table,2,3);
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

        addSymbolInLines(parsedTable,"var0",SymbolType.Variable,1);
        addSymbolInLines(parsedTable,"var1",SymbolType.Constant,2);
        addSymbolInLines(parsedTable,"var1.5",SymbolType.Subscript,3);
        addSymbolInLines(parsedTable, "var2",SymbolType.Subscript_Value,4);
        addSymbolInLines(parsedTable,"var2.5",SymbolType.Lookup_Table,5);


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);


        assertHasIssueInLines(context, SymbolNotDefinedInDictionaryCheck.class,1,3,5);

        assertDoesntHaveIssueInLines(context, SymbolNotDefinedInDictionaryCheck.class,2,4);

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


        addSymbolInLines(parsedTable,"funko",SymbolType.Function,1);
        addSymbolInLines(parsedTable,"constant",SymbolType.Constant,2);
        addSymbolInLines(parsedTable,"var",SymbolType.Variable,3);
        addSymbolInLines(parsedTable, "subscript",SymbolType.Subscript,4);
        addSymbolInLines(parsedTable,"funko2",SymbolType.Function,5);
        addSymbolInLines(parsedTable,"subscriptValue",SymbolType.Subscript_Value,6);
        addSymbolInLines(parsedTable,"lookup",SymbolType.Lookup_Table,7);
        addSymbolInLines(parsedTable,"realityCheck",SymbolType.Reality_Check,8);
        addSymbolInLines(parsedTable,"funko3",SymbolType.Function,9);


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);


        assertDoesntHaveIssueInLines(context, SymbolNotDefinedInDictionaryCheck.class,1,5,9);

        assertHasIssueInLines(context, SymbolNotDefinedInDictionaryCheck.class,2,3,4,6,7,8);
    }

    @Test
    public void testControllerIgnoresDefaultSymbols(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();


        List<Symbol> symbols = Constants.DEFAULT_VENSIM_SYMBOLS.stream().map(Symbol::new).collect(Collectors.toList());
        symbols.forEach(symbol -> {symbol.addDefinitionLine(1);
                                    parsedTable.addSymbol(symbol);
                                    });

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);

        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();
        check.scan(context);

        assertDoesntHaveIssueInLines(context, SymbolNotDefinedInDictionaryCheck.class,1);

    }

    @Test
    public void testFailingRuleDoesntMakeSymbolInvalid(){
        SymbolNotDefinedInDictionaryCheck check = new SymbolNotDefinedInDictionaryCheck();

        SymbolTable parsedTable = new SymbolTable();
        Symbol symbolNotInDB = new Symbol("invalid", SymbolType.Subscript_Value);
        symbolNotInDB.addDefinitionLine(1);
        Symbol valid = new Symbol("valid", SymbolType.Subscript_Value);
        valid.addDefinitionLine(2);
        parsedTable.addSymbol(symbolNotInDB);
        parsedTable.addSymbol(valid);

        SymbolTable dbTable = new SymbolTable();
        dbTable.addSymbol(new Symbol("valid"));


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        check.scan(context);

        assertTrue(valid.isValid());
        assertTrue(symbolNotInDB.isValid());
    }

}
