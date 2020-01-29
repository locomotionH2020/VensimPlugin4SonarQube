package es.uva.medeas.rules;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.plugin.VensimVisitorContext;

import org.junit.Test;

import static es.uva.medeas.testutilities.RuleTestUtilities.assertDoesntHaveIssue;
import static es.uva.medeas.testutilities.RuleTestUtilities.assertHasIssue;
import static org.junit.Assert.*;

public class TestSymbolNotFoundInDBCheck {


    private void addSymbolInLines(SymbolTable table, String token, int... lines){
        Symbol symbol = new Symbol(token);
        for(int line: lines)
            symbol.addDefinitionLine(line);

        table.addSymbol(symbol);
    }

    @Test
    public void testCompareEmptyTables(){
        VensimVisitorContext context = new VensimVisitorContext(null,new SymbolTable(),new SymbolTable());

        SymbolNotFoundInDBCheck check = new SymbolNotFoundInDBCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testDbTableIsNull(){
        VensimVisitorContext context = new VensimVisitorContext(null,new SymbolTable(),null);

        SymbolNotFoundInDBCheck check = new SymbolNotFoundInDBCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testMissingSymbolInDB(){
        SymbolTable parsedTable = new SymbolTable();
        addSymbolInLines(parsedTable,"foo",1,2);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,new SymbolTable());

        SymbolNotFoundInDBCheck check = new SymbolNotFoundInDBCheck();
        check.scan(context);

        assertHasIssue(context,SymbolNotFoundInDBCheck.class,1);
        assertHasIssue(context,SymbolNotFoundInDBCheck.class,2);
    }

    @Test
    public void testDbHasExtraSymbols(){
        SymbolTable dbTable = new SymbolTable();
        Symbol symbol = new Symbol("foo");
        dbTable.addSymbol(symbol);

        VensimVisitorContext context = new VensimVisitorContext(null,new SymbolTable(), dbTable);

        SymbolNotFoundInDBCheck check = new SymbolNotFoundInDBCheck();
        check.scan(context);


        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testCheckWithMultipleSymbolsWithoutIssues(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        addSymbolInLines(parsedTable,"var1",1,2,3);
        dbTable.addSymbol(new Symbol("var1"));

        addSymbolInLines(parsedTable,"foo",4);
        dbTable.addSymbol(new Symbol("foo"));

        addSymbolInLines(parsedTable,"easter_egg",1,2,3);
        dbTable.addSymbol(new Symbol("easter_egg"));

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);

        SymbolNotFoundInDBCheck check = new SymbolNotFoundInDBCheck();
        check.scan(context);




    }

    @Test
    public void testCheckWithMultipleSymbolsWithIssues(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        dbTable.addSymbol(new Symbol("var1"));
        dbTable.addSymbol(new Symbol("var2"));

        addSymbolInLines(parsedTable,"var0",1);
        addSymbolInLines(parsedTable,"var1",2);
        addSymbolInLines(parsedTable,"var1.5",3);
        addSymbolInLines(parsedTable, "var2",4);
        addSymbolInLines(parsedTable,"var2.5",5);


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);

        SymbolNotFoundInDBCheck check = new SymbolNotFoundInDBCheck();
        check.scan(context);


        assertHasIssue(context,SymbolNotFoundInDBCheck.class,1);
        assertHasIssue(context,SymbolNotFoundInDBCheck.class,3);
        assertHasIssue(context,SymbolNotFoundInDBCheck.class,5);

        assertDoesntHaveIssue(context,SymbolNotFoundInDBCheck.class,2);
        assertDoesntHaveIssue(context,SymbolNotFoundInDBCheck.class,4);

    }


    @Test
    public void testMissingInDBDoesntHaveDefinitionLine(){
        SymbolTable parsedTable = new SymbolTable();
        parsedTable.addSymbol(new Symbol("foo"));

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,new SymbolTable());

        SymbolNotFoundInDBCheck check = new SymbolNotFoundInDBCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }


}
