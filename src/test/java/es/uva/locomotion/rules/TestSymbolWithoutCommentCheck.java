package es.uva.locomotion.rules;

import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import es.uva.locomotion.model.VensimVisitorContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static es.uva.locomotion.testutilities.GeneralTestUtilities.addSymbolInLines;
import static es.uva.locomotion.testutilities.RuleTestUtilities.assertHasIssueInLines;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class TestSymbolWithoutCommentCheck {

    private SymbolWithoutCommentCheck check;

    @Before
    public void setUp(){
        check = new SymbolWithoutCommentCheck();
    }

    @Test
    public void testConstantCreatesIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"CONST", SymbolType.CONSTANT,1,2,3);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);

        check.scan(context);

        assertHasIssueInLines(context,SymbolWithoutCommentCheck.class,1,2,3);
    }

    @Test
    public void testVariablesCreatesIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"var", SymbolType.VARIABLE,9);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);

        check.scan(context);

        assertHasIssueInLines(context,SymbolWithoutCommentCheck.class,9);
    }

    @Test
    public void testRealityCheckCreatesIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"reality_check", SymbolType.REALITY_CHECK,9);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);

        check.scan(context);

        assertHasIssueInLines(context,SymbolWithoutCommentCheck.class,9);

    }

    @Test
    public void testSubscriptNameCreatesIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"MATERIALS_I", SymbolType.SUBSCRIPT,9);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);

        check.scan(context);

        assertHasIssueInLines(context,SymbolWithoutCommentCheck.class,9);

    }

    @Test
    public void testLookupCreatesIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"my_lookup_lt", SymbolType.LOOKUP_TABLE,9);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);

        check.scan(context);

        assertHasIssueInLines(context,SymbolWithoutCommentCheck.class,9);
    }

    @Test
    public void testFunctionDoesntCreateIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"my_macro", SymbolType.FUNCTION,9);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);

        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testSubscriptValueDoesntCreateIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"SCENARIO1", SymbolType.SUBSCRIPT_VALUE,9);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);

        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testCaseWithoutIssue(){
        SymbolTable table = new SymbolTable();
        Symbol symbol = addSymbolInLines(table,"CONST", SymbolType.CONSTANT,1,2,3,4,5);
        symbol.setComment(" comment ");

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());

    }

    @Test
    public void testSymbolWithoutDefinitionLine(){
        SymbolTable table = new SymbolTable();

        Symbol symbol = new Symbol("CONST");
        symbol.setType(SymbolType.CONSTANT);
        table.addSymbol(symbol);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }


    @Test
    public void testEmptyCommentWithSpaces(){
        SymbolTable table = new SymbolTable();
        Symbol symbol = addSymbolInLines(table,"CONST", SymbolType.CONSTANT,7);
        symbol.setComment("                                                                                 ");

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);
        check.scan(context);

        assertHasIssueInLines(context,SymbolWithoutCommentCheck.class,7);
    }

    @Test
    public void testFailingRuleMakesSymbolInvalid(){
        SymbolWithoutCommentCheck check = new SymbolWithoutCommentCheck();

        SymbolTable table = new SymbolTable();
        Symbol invalid = new Symbol("invalid", SymbolType.CONSTANT);
        invalid.addLine(1);
        Symbol valid = new Symbol("VALID", SymbolType.CONSTANT);
        valid.setComment("comment");
        valid.addLine(2);
        table.addSymbol(invalid);
        table.addSymbol(valid);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);
        check.scan(context);

        Assert.assertTrue(valid.isValid());
        assertFalse(invalid.isValid());
    }
}
