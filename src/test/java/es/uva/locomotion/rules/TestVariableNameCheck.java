package es.uva.locomotion.rules;

import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.SymbolTable;
import es.uva.locomotion.parser.SymbolType;
import es.uva.locomotion.plugin.VensimScanner;
import es.uva.locomotion.plugin.VensimVisitorContext;
import org.junit.Test;

import static es.uva.locomotion.testutilities.RuleTestUtilities.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestVariableNameCheck {

    @Test
    public void testCorrectName() {

        String program = "expected_consumption_2020 = Time ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        assertDoesntHaveIssueOfType(visitorContext,VariableNameCheck.class);

    }

    @Test
    public void testNameCanContainAnyNumber(){
        String program = "numbers0_1_2_3_4_5_6_7_8_9 = Time ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        assertDoesntHaveIssueOfType(visitorContext,VariableNameCheck.class);

    }

    @Test
    public void testUppercaseAnyWord(){
        String program = "EXPECTED_consumption_2020 = Time ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,VariableNameCheck.class,1);
    }

    @Test
    public void testUppercaseLastWord(){
        String program = "expected_CONSUMPTION = Time ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,VariableNameCheck.class,1);
    }

    @Test
    public void testSeveralUnderscore(){
        String program = "\nexpected__consumption_2020 = Time ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,VariableNameCheck.class,2);
    }

    @Test
    public void testNameBeginningWithUnderscore(){
        String program = "_expected_consumption_2020 = Time ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,VariableNameCheck.class,1);
    }

    @Test
    public void testNameEndingWithUnderscore(){
        String program = "expected_consumption_2020_ = Time ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,VariableNameCheck.class,1);
    }

    @Test
    public void testWeirdCharacters(){
        String program = "wèird= Time~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,VariableNameCheck.class,1);
    }

    @Test
    public void testNameBeginsWithNumber(){
        String program = "\"2020_expected_consumption\" = Time ~~|\n"+
                "\"2020expected_consumption\" = Time ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,VariableNameCheck.class,1,2);

    }

    @Test
    public void testMultipleDefinitionCreateDifferentIssues() {
        String program = "VAR[aSubscript] = Time~~|\n" +
                "         VAR[anotherSubscript] = Time~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext, VariableNameCheck.class, 1,2);
    }

    @Test
    public void testFailingRuleMakesSymbolInvalid(){
        VariableNameCheck check = new VariableNameCheck();

        SymbolTable table = new SymbolTable();
        Symbol invalid = new Symbol("INVALID", SymbolType.Variable);
        invalid.addDefinitionLine(1);
        Symbol valid = new Symbol("valid", SymbolType.Variable);
        valid.addDefinitionLine(2);
        table.addSymbol(invalid);
        table.addSymbol(valid);

        VensimVisitorContext context = new VensimVisitorContext(null,table,null);
        check.scan(context);

        assertTrue(valid.isValid());
        assertFalse(invalid.isValid());
    }

}
