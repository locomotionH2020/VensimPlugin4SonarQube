package es.uva.medeas.rules;

import es.uva.medeas.VensimScanner;
import es.uva.medeas.VensimVisitorContext;
import org.junit.Test;

import static es.uva.medeas.rules.RuleTestUtilities.*;
import static org.junit.Assert.assertTrue;

public class TestConstantNameCheck {

    @Test
    public void testCorrectName() {

        String program = "STARTING_PRODUCTIVITY = -99999 ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());

    }

    @Test
    public void testNameCanContainAnyNumber(){
        String program = "NUMBERS0_1_2_3_4_5_6_7_8_9 = 3 ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());
    }

    @Test
    public void testLowerCaseAnyWord(){
        String program = "starting_PRODUCTIVITY = -99999 ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,ConstantNameCheck.class,1);
    }

    @Test
    public void testLowerCaseLastWord(){ //TODO Diferenciarlo también en los otros tests
        String program = "STARTING_productivity = -999 ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,ConstantNameCheck.class,1);
    }

    @Test
    public void testSeveralUnderscore(){
        String program = "\nSTARTING__CONSUMPTION = 100 ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,ConstantNameCheck.class,2);
    }

    @Test
    public void testNameBeginningWithUnderscore(){
        String program = "_EXPECTED_PRODUCT_2020 = 1 ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,ConstantNameCheck.class,1);
    }

    @Test
    public void testNameEndingWithUnderscore(){
        String program = "EXPECTED_PRODUCT_2020_ = 2 ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,ConstantNameCheck.class,1);
    }

    @Test
    public void testWeirdCharacters(){
        String program = "WÉIRD= 3~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,ConstantNameCheck.class,1);
    }

    @Test
    public void testNameBeginsWithNumber(){
        String program = "\"2020_EXPECTED_PRODUCT\" = 5 ~|\n"+
                "\"2020EXPECTED_PRODUCT\" = 5 ~|\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,ConstantNameCheck.class,1);
        assertHasIssue(visitorContext,ConstantNameCheck.class,2);

    }

    @Test
    public void testDoesntGenerateIssuesInUndefinedSymbols(){
        String program = "CONSTANT= undefINED~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());    }


}