package es.uva.medeas.rules;

import es.uva.medeas.plugin.Issue;
import es.uva.medeas.plugin.VensimScanner;
import es.uva.medeas.plugin.VensimVisitorContext;
import es.uva.medeas.testutilities.TestUtilities;
import org.junit.Test;
import org.sonar.api.batch.rule.Severity;

import java.util.List;
import java.util.stream.Collectors;

import static es.uva.medeas.rules.TestMagicNumberCheck.DEFAULT_MINIMUM_REPETITIONS;
import static es.uva.medeas.testutilities.RuleTestUtilities.*;
import static org.junit.Assert.*;

public class TestIntegrationMagicNumberCheck {


    @Test
    public void testIssueWorks() {

        String program = "a = 3 * Time ~~|\n".repeat(DEFAULT_MINIMUM_REPETITIONS);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);


        for(int i=1;i<DEFAULT_MINIMUM_REPETITIONS+1;i++)
            assertHasIssue(visitorContext,MagicNumberCheck.class,i);

        for(Issue issue:issues) {
            assertEquals(Severity.MAJOR, issue.getSeverity());
            assertEquals("The number 3 is repeated "+DEFAULT_MINIMUM_REPETITIONS+" times. Consider replacing it by a constant",issue.getMessage());
        }



    }

    @Test
    public void testNumberRepeatsMinimumMinusOne(){
        String program = "A = 3 * 4 ~~|\n".repeat(DEFAULT_MINIMUM_REPETITIONS -1);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);

        assertEquals((DEFAULT_MINIMUM_REPETITIONS-1)*2,issues.size());
        for(Issue issue:issues)
            assertEquals(Severity.INFO,issue.getSeverity());
    }

    @Test
    public void testNumberRepeatedInTheSameLineCounts(){
        String program = "A = " + "3 * ".repeat(DEFAULT_MINIMUM_REPETITIONS -1) + "3~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);

        for(Issue issue: issues) {
            assertEquals(MagicNumberCheck.class, issue.getCheck().getClass());
            assertEquals(1,issue.getLine());
        }

        assertEquals(DEFAULT_MINIMUM_REPETITIONS,issues.size());

        assertHasIssue(visitorContext,MagicNumberCheck.class,1);
    }

    @Test
    public void testConstantDirectAssignsDoesntCountEquation(){
        String program = "A = 3 ~~|\n".repeat(DEFAULT_MINIMUM_REPETITIONS);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);
        assertTrue(issues.isEmpty());
    }

    @Test
    public void testConstantDirectAssignsDoesntCountDataEquation(){
        String program = "A := 3 ~~|\n".repeat(DEFAULT_MINIMUM_REPETITIONS);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);
        assertTrue(issues.isEmpty());;
    }

    @Test
    public void testConstantDirectAssignsDoesntCountUnchangeableConstant(){
        String program = "A == 3 ~~|\n".repeat(DEFAULT_MINIMUM_REPETITIONS);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);
        assertTrue(issues.isEmpty());
    }

    @Test
    public void testTabbedArrayDoesntCountEquation(){
        String program = "TABBED_ARRAY = TABBED ARRAY(3    3    3    3\n" +
                "3    3    3    3\n" +
                "3    3    3    3)\n" +
                "~ ~|";


        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);
        assertTrue(issues.isEmpty());

    }

    @Test
    public void testTabbedArrayDoesntCountDataEquation(){
        String program = "TABBED_ARRAY := TABBED ARRAY(3    3    3    3\n" +
                "3    3    3    3\n" +
                "3    3    3    3)\n" +
                "~ ~|";


        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);
        assertTrue(issues.isEmpty());

    }



    @Test
    public void testLookupsDontCount(){
        String program = "mylookup_lt([(3,0)-(10,10)],(1.10092,2.41228),(2.72171,3.24561),(6.11621,5.96491),(3,8.37719))\n~~|"
                .repeat(DEFAULT_MINIMUM_REPETITIONS);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);
        assertTrue(issues.isEmpty());


    }

    @Test
    public void testWithLookupSecondArgumentDoesntCountEquation(){
        String program = "var =WITH LOOKUP(Time,((0,1),(1,1),(2,2)))\n~~|".repeat(DEFAULT_MINIMUM_REPETITIONS);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);
        assertTrue(issues.isEmpty());

    }

    @Test
    public void testWithLookupSecondArgumentDoesntCountDataEquation(){
        String program = "var :=WITH LOOKUP(Time,((0,1),(1,1),(2,2)))\n~~|".repeat(DEFAULT_MINIMUM_REPETITIONS);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);
        assertTrue(issues.isEmpty());

    }


    @Test
    public void testWithLookupFirstArgument(){
        String program = "var =WITH LOOKUP(14,((0,1),(1,1),(2,2)))\n~~|" +
                "A = 14 * 0~~|\n".repeat(DEFAULT_MINIMUM_REPETITIONS -1);


        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext, MagicNumberCheck.class,1);
    }


    @Test
    public void testRealityCheckDoesntCount(){
        String program = "big_growth_test :TEST INPUT: divisions = 1e+022~~|\n".repeat(DEFAULT_MINIMUM_REPETITIONS);
        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);
        assertTrue(issues.isEmpty());

    }

    @Test
    public void testConstraintDoesntCount(){
        String program = "my_condition_test :THE CONDITION: firstVariable[subscript]>100 :IMPLIES: secondVariable<100 ~~|".repeat(DEFAULT_MINIMUM_REPETITIONS);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);
        assertTrue(issues.isEmpty());
    }


    @Test
    public void testBidimensionalArrayDoesntCount() {
        String program = "INITIAL_POPULATION = 1,1,1,1;1,1,1,1;\n" +
                "         1,1,1,1; ~Person~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);
        assertTrue(issues.isEmpty());
    }

    @Test
    public void testNumberListDoesntCountEquation(){
        String program = "A = 1,1,1,1,1~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);
        assertTrue(issues.isEmpty());

    }

    @Test
    public void testNumberListDoesntCountDataEquation(){
        String program = "A := 1,1,1,1,1~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);
        assertTrue(issues.isEmpty());

    }

    @Test
    public void testNumberListDoesntCountUnchangeableConstant(){
        String program = "A == 1,1,1,1,1~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);
        assertTrue(issues.isEmpty());

    }


    @Test
    public void testIgnoresZerosWithFractionDigits(){
        String program = "CONST = 0.000 + 0.000~~|".repeat(DEFAULT_MINIMUM_REPETITIONS +4);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);
        assertTrue(issues.isEmpty());
    }

    @Test
    public void testIgnoresOnesWithFractionDigits(){
        String program = "CONST = 1.000 + 1.000~~|".repeat(DEFAULT_MINIMUM_REPETITIONS +4);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);
        assertTrue(issues.isEmpty());
    }

    @Test
    public void testIgnores100WithFractionDigits(){
        String program = "CONST = 100.000 + 100.000~~|".repeat(DEFAULT_MINIMUM_REPETITIONS +4);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        List<Issue> issues = TestUtilities.getIssuesFromType(visitorContext,MagicNumberCheck.class);
        assertTrue(issues.isEmpty());
    }


    @Test
    public void testRuleConsidersFloatsThatAreIntsAsInts(){
        String program = "var = 6.00 * Time~~|".repeat(DEFAULT_MINIMUM_REPETITIONS -1);
        program += "var2 = 6 * Time ~~|".repeat(DEFAULT_MINIMUM_REPETITIONS-1);


        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        List<Issue> magicNumberIssue = visitorContext.getIssues().stream()
                .filter(issue -> issue.getCheck().getClass()==MagicNumberCheck.class).collect(Collectors.toList());

        assertHasIssue(visitorContext,MagicNumberCheck.class,1);
        for(Issue issue:magicNumberIssue)
            assertEquals(Severity.MAJOR,issue.getSeverity());
    }

    @Test
    public void testExceptionSwitch(){
        String program = "Policy change energy speed H[scenarios,final sources]=\n" +
                "IF THEN ELSE(SWTICH_OPTION_ONE=1,Time, IF THEN ELSE(2=SWITCH_OPTION_TWO))~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        List<Issue> magicNumberIssue = visitorContext.getIssues().stream()
                .filter(issue -> issue.getCheck().getClass()==MagicNumberCheck.class).collect(Collectors.toList());


        assertTrue(magicNumberIssue.isEmpty());

    }



}
