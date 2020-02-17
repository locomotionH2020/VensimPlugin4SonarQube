package es.uva.medeas.rules;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import es.uva.medeas.plugin.VensimVisitorContext;
import org.junit.Before;
import org.junit.Test;

import static es.uva.medeas.testutilities.RuleTestUtilities.assertHasIssue;
import static es.uva.medeas.testutilities.TestUtilities.addSymbolInLines;
import static junit.framework.TestCase.assertTrue;

public class TestSymbolWithoutUnitsCheck {

    private SymbolWithoutUnitsCheck check;

    @Before
    public void setUp(){
        check = new SymbolWithoutUnitsCheck();
    }

    @Test
    public void testConstantCreatesIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"CONST", SymbolType.CONSTANT,1,2,3);

        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        check.scan(context);

        assertHasIssue(context,SymbolWithoutUnitsCheck.class,1);
        assertHasIssue(context,SymbolWithoutUnitsCheck.class,2);
        assertHasIssue(context,SymbolWithoutUnitsCheck.class,3);

    }

    @Test
    public void testVariablesCreatesIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"var", SymbolType.VARIABLE,9);

        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        check.scan(context);

        assertHasIssue(context,SymbolWithoutUnitsCheck.class,9);
    }

    @Test
    public void testRealityCheckCreatesIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"reality_check", SymbolType.REALITY_CHECK,9);

        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        check.scan(context);

        assertHasIssue(context,SymbolWithoutUnitsCheck.class,9);

    }

    @Test
    public void testSubscriptNameCreatesIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"MATERIALS_I", SymbolType.SUBSCRIPT_NAME,9);

        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        check.scan(context);

        assertHasIssue(context,SymbolWithoutUnitsCheck.class,9);

    }

    @Test
    public void testLookupCreatesIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"my_lookup_lt", SymbolType.LOOKUP,9);

        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        check.scan(context);

        assertHasIssue(context,SymbolWithoutUnitsCheck.class,9);
    }

    @Test
    public void testFunctionDoesntCreateIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"my_macro", SymbolType.FUNCTION,9);

        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testSubscriptValueDoesntCreateIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"SCENARIO1", SymbolType.SUBSCRIPT_VALUE,9);

        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testCaseWithoutIssue(){
        SymbolTable table = new SymbolTable();
        Symbol symbol = addSymbolInLines(table,"CONST", SymbolType.CONSTANT,1,2,3,4,5);
        symbol.setUnits("    units ");

        VensimVisitorContext context = new VensimVisitorContext(null,table,null);
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());

    }

    @Test
    public void testSymbolWithoutDefinitionLine(){
        SymbolTable table = new SymbolTable();

        Symbol symbol = new Symbol("CONST");
        symbol.setType(SymbolType.CONSTANT);
        table.addSymbol(symbol);

        VensimVisitorContext context = new VensimVisitorContext(null,table,null);
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }


    @Test
    public void testEmptyUnitsWithSpaces(){
        SymbolTable table = new SymbolTable();
        Symbol symbol = addSymbolInLines(table,"CONST", SymbolType.CONSTANT,7);
        symbol.setUnits("                                                                                 ");

        VensimVisitorContext context = new VensimVisitorContext(null,table,null);
        check.scan(context);

        assertHasIssue(context,SymbolWithoutUnitsCheck.class,7);
    }



}
