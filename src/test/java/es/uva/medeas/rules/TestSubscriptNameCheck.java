package es.uva.medeas.rules;


import es.uva.medeas.VensimScanner;
import es.uva.medeas.VensimVisitorContext;

import org.junit.Test;

import static org.junit.Assert.*;


import static es.uva.medeas.rules.RuleTestUtilities.*;

public class TestSubscriptNameCheck {

    @Test
    public void testCorrectName() {

        String program = "MY_10_FAVORITE_COUNTRIES_ENUM: COUNTRY1, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());


    }

    @Test
    public void testNameCanContainAnyNumber() {
        String program = "NUMBERS1_2_3_4_5_6_7_8_9S_ENUM:\n COUNTRY1, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());
    }

    @Test
    public void testLowercaseAnyWord() {

        String program = "some_COUNTRIES_ENUM:\n COUNTRY1, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptNameCheck.class,1);

    }

    @Test
    public void testLowercaseLastWord() {

        String program = "countrieS_ENUM:\n COUNTRY1, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptNameCheck.class,1);

    }

    @Test
    public void testNonPluralName() {


        String program = "\nCOUNTRY_ENUM:\n COUNTRY1, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptNameCheck.class,2);

    }

    @Test
    public void testNameWithoutSuffix() {

        String program = "COUNTRIES:\n COUNTRY1, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptNameCheck.class,1);

    }

    @Test
    public void testNameWithLowercaseSuffix() {

        String program = "COUNTRIES_enum:\n COUNTRY1, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptNameCheck.class,1);

    }

    @Test
    public void testOnlySAndSuffix(){
        String program = "S_ENUM:\n COUNTRY1, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptNameCheck.class,1);
    }

    @Test
    public void testSeveralUnderscore(){
        String program = "MY__COUNTRIES_ENUM:\n COUNTRY1, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptNameCheck.class,1);
    }

    @Test
    public void testSeveralUnderscoreBeforeSuffix(){
        String program = "MY_COUNTRIES__ENUM:\n COUNTRY1, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptNameCheck.class,1);
    }

    @Test
    public void testNameBeginningWithUnderscore(){
        String program = "_MY_COUNTRIES_ENUM:\n COUNTRY1, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptNameCheck.class,1);
    }

    @Test
    public void testNameEndingWithUnderscore(){
        String program = "MY_COUNTRIES_ENUM_:\n COUNTRY1, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptNameCheck.class,1);
    }

    @Test
    public void testUnderscoreBeforePlural(){
        String program = "MY_COUNTRIE_S_ENUM:\n COUNTRY1, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptNameCheck.class,1);
    }

    @Test
    public void testWeirdCharacters(){
        String program = "A_WEIRD_È_ENUM:\n COUNTRY1, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptNameCheck.class,1);
    }

    @Test
    public void testBeginsWithNumber(){
        String program = "\"2_COUNTRIES_ENUM\": COUNTRY1, COUNTRY2~|\n" +
                        " \"3_COUNTRIES_ENUM\": COUNTRY1, COUNTRY2, COUNTRY3~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptNameCheck.class,1);
        assertHasIssue(visitorContext,SubscriptNameCheck.class,2);

    }


    @Test
    public void testEndsWithNumber(){
        String program = "MY_SUBSCRIPT10S_ENUM:\n COUNTRY1, COUNTRY2~|\n"+
                    "MY_10SUBSCRIPTS_ENUM:\n COUNTRY1, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptNameCheck.class,1);
        assertDoesntHaveIssue(visitorContext,SubscriptNameCheck.class,2);
    }

}
