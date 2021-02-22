package es.uva.locomotion.rules;

import es.uva.locomotion.model.Symbol;
import es.uva.locomotion.model.SymbolTable;
import es.uva.locomotion.model.SymbolType;
import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static es.uva.locomotion.testutilities.RuleTestUtilities.assertHasIssueInLines;
import static es.uva.locomotion.testutilities.GeneralTestUtilities.addSymbolInLines;
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
        addSymbolInLines(table,"CONST", SymbolType.Constant,1,2,3);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);

        check.scan(context);

        assertHasIssueInLines(context,SymbolWithoutCommentCheck.class,1,2,3);
    }

    @Test
    public void testVariablesCreatesIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"var", SymbolType.Variable,9);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);

        check.scan(context);

        assertHasIssueInLines(context,SymbolWithoutCommentCheck.class,9);
    }

    @Test
    public void testRealityCheckCreatesIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"reality_check", SymbolType.Reality_Check,9);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);

        check.scan(context);

        assertHasIssueInLines(context,SymbolWithoutCommentCheck.class,9);

    }

    @Test
    public void testSubscriptNameCreatesIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"MATERIALS_I", SymbolType.Subscript,9);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);

        check.scan(context);

        assertHasIssueInLines(context,SymbolWithoutCommentCheck.class,9);

    }

    @Test
    public void testLookupCreatesIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"my_lookup_lt", SymbolType.Lookup_Table,9);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);

        check.scan(context);

        assertHasIssueInLines(context,SymbolWithoutCommentCheck.class,9);
    }

    @Test
    public void testFunctionDoesntCreateIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"my_macro", SymbolType.Function,9);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);

        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testSubscriptValueDoesntCreateIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"SCENARIO1", SymbolType.Subscript_Value,9);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);

        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testCaseWithoutIssue(){
        SymbolTable table = new SymbolTable();
        Symbol symbol = addSymbolInLines(table,"CONST", SymbolType.Constant,1,2,3,4,5);
        symbol.setComment(" comment ");

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());

    }

    @Test
    public void testSymbolWithoutDefinitionLine(){
        SymbolTable table = new SymbolTable();

        Symbol symbol = new Symbol("CONST");
        symbol.setType(SymbolType.Constant);
        table.addSymbol(symbol);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }


    @Test
    public void testEmptyCommentWithSpaces(){
        SymbolTable table = new SymbolTable();
        Symbol symbol = addSymbolInLines(table,"CONST", SymbolType.Constant,7);
        symbol.setComment("                                                                                 ");

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);
        check.scan(context);

        assertHasIssueInLines(context,SymbolWithoutCommentCheck.class,7);
    }

    @Test
    public void testFailingRuleMakesSymbolInvalid(){
        SymbolWithoutCommentCheck check = new SymbolWithoutCommentCheck();

        SymbolTable table = new SymbolTable();
        Symbol invalid = new Symbol("invalid", SymbolType.Constant);
        invalid.addDefinitionLine(1);
        Symbol valid = new Symbol("VALID", SymbolType.Constant);
        valid.setComment("comment");
        valid.addDefinitionLine(2);
        table.addSymbol(invalid);
        table.addSymbol(valid);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);
        check.scan(context);

        Assert.assertTrue(valid.isValid());
        assertFalse(invalid.isValid());
    }
}
