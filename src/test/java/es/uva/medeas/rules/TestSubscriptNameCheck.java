package es.uva.medeas.rules;


import es.uva.medeas.plugin.VensimScanner;
import es.uva.medeas.plugin.VensimVisitorContext;

import org.junit.Test;


import static es.uva.medeas.testutilities.RuleTestUtilities.*;

public class TestSubscriptNameCheck {

    @Test
    public void testCorrectName() {

        String program = "MY_10_FAVORITE_COUNTRIES_I: COUNTRY1, COUNTRY2~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        assertDoesntHaveIssueOfType(visitorContext,SubscriptNameCheck.class);

    }

    @Test
    public void testRuleDoesntEnforcePlural(){
        String program = "TEST_I: OPTION_1, OPTION_2 ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        assertDoesntHaveIssueOfType(visitorContext,SubscriptNameCheck.class);

    }

    @Test
    public void testNameCanContainAnyNumber() {
        String program = "NUMBERS1_2_3_4_5_6_7_8_9NUMBERS_I:\n COUNTRY1, COUNTRY2~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);

        assertDoesntHaveIssueOfType(visitorContext,SubscriptNameCheck.class);
    }

    @Test
    public void testLowercaseAnyWord() {

        String program = "some_COUNTRIES_I:\n COUNTRY1, COUNTRY2~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,SubscriptNameCheck.class,1);

    }

    @Test
    public void testLowercaseLastWord() {

        String program = "countrieS_I:\n COUNTRY1, COUNTRY2~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,SubscriptNameCheck.class,1);

    }


    @Test
    public void testNameWithoutSuffix() {

        String program = "COUNTRIES:\n COUNTRY1, COUNTRY2~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,SubscriptNameCheck.class,1);

    }

    @Test
    public void testNameWithLowercaseSuffix() {

        String program = "COUNTRIES_i:\n COUNTRY1, COUNTRY2~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,SubscriptNameCheck.class,1);

    }

    @Test
    public void testOnlySuffix(){
        String program = "I:\n COUNTRY1, COUNTRY2~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,SubscriptNameCheck.class,1);
    }

    @Test
    public void testSeveralUnderscore(){
        String program = "MY__COUNTRIES_I:\n COUNTRY1, COUNTRY2~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,SubscriptNameCheck.class,1);
    }

    @Test
    public void testSeveralUnderscoreBeforeSuffix(){
        String program = "MY_COUNTRIES__I:\n COUNTRY1, COUNTRY2~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,SubscriptNameCheck.class,1);
    }

    @Test
    public void testNameBeginningWithUnderscore(){
        String program = "_MY_COUNTRIES_I:\n COUNTRY1, COUNTRY2~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,SubscriptNameCheck.class,1);
    }

    @Test
    public void testNameEndingWithUnderscore(){
        String program = "MY_COUNTRIES_I_:\n COUNTRY1, COUNTRY2~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,SubscriptNameCheck.class,1);
    }


    @Test
    public void testWeirdCharacters(){
        String program = "A_WEIRD_È_I:\n COUNTRY1, COUNTRY2~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,SubscriptNameCheck.class,1);
    }

    @Test
    public void testQuotedName(){
        String program = "\"2_COUNTRIES_I\": COUNTRY1, COUNTRY2~~|\n" +
                        " \"3_COUNTRIES_I\": COUNTRY1, COUNTRY2, COUNTRY3~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,SubscriptNameCheck.class,1,2);

    }




}
