package es.uva.medeas.rules;

import es.uva.medeas.VensimScanner;
import es.uva.medeas.VensimVisitorContext;
import org.junit.Test;

import static es.uva.medeas.rules.RuleTestUtilities.*;
import static org.junit.Assert.assertTrue;

public class TestVariableNameCheck {

    @Test
    public void testCorrectName() {

        String program = "expected_consumption_2020 = Time ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());

    }

    @Test
    public void testNameCanContainAnyNumber(){
        String program = "numbers0_1_2_3_4_5_6_7_8_9 = Time ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());
    }

    @Test
    public void testUppercaseAnyWord(){
        String program = "EXPECTED_consumption_2020 = Time ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,VariableNameCheck.class,1);
    }

    @Test
    public void testUppercaseLastWord(){
        String program = "expected_CONSUMPTION = Time ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,VariableNameCheck.class,1);
    }

    @Test
    public void testSeveralUnderscore(){
        String program = "\nexpected__consumption_2020 = Time ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,VariableNameCheck.class,2);
    }

    @Test
    public void testNameBeginningWithUnderscore(){
        String program = "_expected_consumption_2020 = Time ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,VariableNameCheck.class,1);
    }

    @Test
    public void testNameEndingWithUnderscore(){
        String program = "expected_consumption_2020_ = Time ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,VariableNameCheck.class,1);
    }

    @Test
    public void testWeirdCharacters(){
        String program = "wèird= Time~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,VariableNameCheck.class,1);
    }

    @Test
    public void testNameBeginsWithNumber(){
        String program = "\"2020_expected_consumption\" = Time ~|\n"+
                "\"2020expected_consumption\" = Time ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,VariableNameCheck.class,1);
        assertHasIssue(visitorContext,VariableNameCheck.class,2);

    }

    @Test
    public void testMultipleDefinitionCreateDifferentIssues() {
        String program = "VAR[aSubscript] = Time~|\n" +
                "         VAR[anotherSubscript] = Time~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext, VariableNameCheck.class, 1);
        assertHasIssue(visitorContext, VariableNameCheck.class, 2);
    }
}
