package es.uva.locomotion.rules;


import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.SymbolTable;
import es.uva.locomotion.parser.SymbolType;
import es.uva.locomotion.plugin.VensimScanner;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import org.junit.Test;

import static es.uva.locomotion.testutilities.RuleTestUtilities.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestSubscriptValueNameCheck {

    @Test
    public void testCorrectName() {

        String program = "MY_COUNTRIES_I: FIRST_COUNTRY, COUNTRY2~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertDoesntHaveIssueOfType(visitorContext,SubscriptValueNameCheck.class);

    }

    @Test
    public void testNameCanContainAnyNumber() {
        String program = "NUMBERS_INSIDE_SUBSCRIPTS_I:\n COUNTRY_0_1_2_3_4_5_6_7_8_9, COUNTRY2~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        assertDoesntHaveIssueOfType(visitorContext,SubscriptValueNameCheck.class);
    }


    @Test
    public void testLowerCaseAnyWord() {

        String program = "COUNTRIES_I: COUNTRY,\n my_COUNTRY~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext, SubscriptValueNameCheck.class, 2);

    }

    @Test
    public void testLowerCaseLastWord() {

        String program = "COUNTRIES_I: COUNTRY1,\n country2~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,SubscriptValueNameCheck.class,2);

    }

    @Test
    public void testSeveralUnderscore(){
        String program = "MY_COUNTRIES_I: ONE__COUNTRY,\n COUNTRY2~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,SubscriptValueNameCheck.class,1);
    }


    @Test
    public void testNameBeginningWithUnderscore(){
        String program = "MY_COUNTRIES_I:\n _COUNTRY1\n, COUNTRY2~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,SubscriptValueNameCheck.class,2);
    }

    @Test
    public void testNameEndingWithUnderscore(){
        String program = "MY_COUNTRIES_I_:\n COUNTRY1_, COUNTRY2~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,SubscriptValueNameCheck.class,2);
    }


    @Test
    public void testWeirdCharacters(){
        String program = "COUNTRIES_I:\n WEIRD_È, COUNTRY2~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,SubscriptValueNameCheck.class,2);
    }

    @Test
    public void testBeginsWithNumber(){
        String program = "COUNTRIES_I: \"1_COUNTRY\", COUNTRY2~~|\n" +
                         "ANOTHER_I: \"1COUNTRY\"~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,SubscriptValueNameCheck.class,1,2);

    }

    @Test
    public void testMultipleDefinitionCreateDifferentIssues() {

        String program = "MY_COUNTRIES_I: a_country, COUNTRY2~~|\n"+
                "OTHER_COUNTRIES_I: a_country, COUNTRY2~~|\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext, SubscriptValueNameCheck.class, 1,2);
    }

    @Test
    public void testFailingRuleMakesSymbolInvalid(){
        SubscriptValueNameCheck check = new SubscriptValueNameCheck();

        SymbolTable table = new SymbolTable();
        Symbol invalid = new Symbol("invalid", SymbolType.Subscript_Value);
        invalid.addDefinitionLine(1);
        Symbol valid = new Symbol("VALID", SymbolType.Subscript_Value);
        valid.addDefinitionLine(2);
        table.addSymbol(invalid);
        table.addSymbol(valid);

        VensimVisitorContext context = new VensimVisitorContext(null,table,null);
        check.scan(context);

        assertTrue(valid.isValid());
        assertFalse(invalid.isValid());
    }
}
