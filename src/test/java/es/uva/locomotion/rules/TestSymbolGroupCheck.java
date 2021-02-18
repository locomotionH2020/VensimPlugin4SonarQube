package es.uva.locomotion.rules;

import es.uva.locomotion.model.Symbol;
import es.uva.locomotion.model.SymbolTable;
import es.uva.locomotion.model.SymbolType;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static es.uva.locomotion.testutilities.GeneralTestUtilities.addSymbolInLines;
import static es.uva.locomotion.testutilities.RuleTestUtilities.assertDoesntHaveIssueInLines;
import static es.uva.locomotion.testutilities.RuleTestUtilities.assertHasIssueInLines;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class TestSymbolGroupCheck {

    private SymbolGroupCheck check;

    @Before
    public void setUp(){
        check = new SymbolGroupCheck();
    }

    @Test
    public void testControlOutOfControlGroupCreatesIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"TIME STEP", SymbolType.Constant,"",1,2,3);

        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        check.scan(context);

        assertHasIssueInLines(context,SymbolGroupCheck.class,1,2,3);

    }
    @Test
    public void testControlInControlGroup(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"TIME STEP", SymbolType.Constant,"Control",1,2,3);

        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        check.scan(context);

        assertDoesntHaveIssueInLines(context,SymbolGroupCheck.class,1,2,3);
    }

    @Test
    public void testSymbolInControlGroupCreatesIssue(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"symbol", SymbolType.Constant,"Control",1,2,3);

        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        check.scan(context);

        assertHasIssueInLines(context,SymbolGroupCheck.class,1,2,3);

    }
    @Test
    public void testSymbolOutOfControlGroup(){
        SymbolTable table = new SymbolTable();
        addSymbolInLines(table,"symbol", SymbolType.Constant,"",1,2,3);

        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        check.scan(context);

        assertDoesntHaveIssueInLines(context,SymbolGroupCheck.class,1,2,3);

    }
}
