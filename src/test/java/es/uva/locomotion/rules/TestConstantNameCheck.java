package es.uva.locomotion.rules;

import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.plugin.VensimScanner;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import org.junit.Test;

import static es.uva.locomotion.testutilities.RuleTestUtilities.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestConstantNameCheck {

    @Test
    public void testCorrectName() {

        String program = "STARTING_PRODUCTIVITY = -99999 ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        assertDoesntHaveIssueOfType(visitorContext,ConstantNameCheck.class);

    }

    @Test
    public void testNameCanContainAnyNumber(){
        String program = "NUMBERS0_1_2_3_4_5_6_7_8_9 = 3 ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        assertDoesntHaveIssueOfType(visitorContext,ConstantNameCheck.class);
    }

    @Test
    public void testLowerCaseAnyWord(){
        String program = "starting_PRODUCTIVITY = -99999 ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,ConstantNameCheck.class,1);
    }

    @Test
    public void testLowerCaseLastWord(){
        String program = "STARTING_productivity = -999 ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,ConstantNameCheck.class,1);
    }

    @Test
    public void testSeveralUnderscore(){
        String program = "\nSTARTING__CONSUMPTION = 100 ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,ConstantNameCheck.class,2);
    }

    @Test
    public void testNameBeginningWithUnderscore(){
        String program = "_EXPECTED_PRODUCT_2020 = 1 ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,ConstantNameCheck.class,1);
    }

    @Test
    public void testNameEndingWithUnderscore(){
        String program = "EXPECTED_PRODUCT_2020_ = 2 ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,ConstantNameCheck.class,1);
    }

    @Test
    public void testWeirdCharacters(){
        String program = "WÉIRD= 3~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,ConstantNameCheck.class,1);
    }

    @Test
    public void testNameBeginsWithNumber(){
        String program = "\"2020_EXPECTED_PRODUCT\" = 5 ~~|\n"+
                "\"2020EXPECTED_PRODUCT\" = 5 ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,ConstantNameCheck.class,1,2);

    }

    @Test
    public void testDoesntGenerateIssuesInUndefinedSymbols(){
        String program = "CONSTANT= undefINED~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);

        assertDoesntHaveIssueOfType(visitorContext,ConstantNameCheck.class);
    }

    @Test
    public void testMultipleDefinitionCreateDifferentIssues() {
        String program = "constant[aSubscript] = 3~~|\n" +
                "         constant[anotherSubscript] = 4~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext, ConstantNameCheck.class, 1,2);
    }

    @Test
    public void testRuleIgnoresDefaultSymbols(){
        String program = "FINAL TIME  = 100 ~ ~|\n"+
                "INITIAL TIME  = 0  ~ ~|\n"+
                "SAVEPER =  TIME STEP ~ ~|\n"+
                "TIME STEP  = 1 ~ ~|";



        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        assertDoesntHaveIssueOfType(visitorContext,ConstantNameCheck.class);
    }

    @Test
    public void testFailingRuleMakesSymbolInvalid(){
        ConstantNameCheck check = new ConstantNameCheck();

        SymbolTable table = new SymbolTable();
        Symbol invalid = new Symbol("invalid", SymbolType.Constant);
        invalid.addLine(1);
        Symbol valid = new Symbol("VALID", SymbolType.Constant);
        valid.addLine(2);
        table.addSymbol(invalid);
        table.addSymbol(valid);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);
        check.scan(context);

        assertTrue(valid.isValid());
        assertFalse(invalid.isValid());
    }



}
