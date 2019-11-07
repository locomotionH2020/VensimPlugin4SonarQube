package es.uva.medeas.rules;


import es.uva.medeas.VensimScanner;
import es.uva.medeas.VensimVisitorContext;
import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader;

import static es.uva.medeas.rules.RuleTestUtilities.*;
import static org.junit.Assert.assertTrue;

public class TestSubscriptValueNameCheck {

    @Test
    public void testCorrectName() {

        String program = "MY_COUNTRIES_ENUM: FIRST_COUNTRY, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());


    }

    @Test
    public void testNameCanContainAnyNumber() {
        String program = "NUMBERS_INSIDE_SUBSCRIPTS_ENUM:\n COUNTRY_0_1_2_3_4_5_6_7_8_9, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());
    }


    @Test
    public void testLowerCaseAnyWord() {

        String program = "COUNTRIES_ENUM: COUNTRY,\n my_COUNTRY~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext, SubscriptValueNameCheck.class, 2);

    }

    @Test
    public void testLowerCaseLastWord() {

        String program = "COUNTRIES_ENUM: COUNTRY1,\n country2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptValueNameCheck.class,2);

    }

    @Test
    public void testSeveralUnderscore(){
        String program = "MY_COUNTRIES_ENUM: ONE__COUNTRY,\n COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptValueNameCheck.class,1);
    }


    @Test
    public void testNameBeginningWithUnderscore(){
        String program = "MY_COUNTRIES_ENUM:\n _COUNTRY1\n, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptValueNameCheck.class,2);
    }

    @Test
    public void testNameEndingWithUnderscore(){
        String program = "MY_COUNTRIES_ENUM_:\n COUNTRY1_, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptValueNameCheck.class,2);
    }


    @Test
    public void testWeirdCharacters(){
        String program = "COUNTRIES_ENUM:\n WEIRD_È, COUNTRY2~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptValueNameCheck.class,2);
    }

    @Test
    public void testBeginsWithNumber(){
        String program = "COUNTRIES_ENUM: \"1_COUNTRY\", COUNTRY2~|\n" +
                         "ANOTHER_ENUM: \"1COUNTRY\"~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,SubscriptValueNameCheck.class,1);
        assertHasIssue(visitorContext,SubscriptValueNameCheck.class,2);

    }




}
