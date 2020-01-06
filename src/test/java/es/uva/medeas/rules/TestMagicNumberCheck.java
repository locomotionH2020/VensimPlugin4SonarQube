package es.uva.medeas.rules;


import es.uva.medeas.plugin.Issue;
import es.uva.medeas.plugin.VensimScanner;
import es.uva.medeas.plugin.VensimVisitorContext;
import static org.junit.Assert.*;

import org.junit.Test;

import static es.uva.medeas.testutilities.RuleTestUtilities.*;

public class TestMagicNumberCheck {

    private final int  DEFAULT_REPETITIONS = Integer.parseInt(MagicNumberCheck.DEFAULT_REPETITIONS);

    @Test
    public void testIssueWorks() {

        String program = "A = 3 * Time ~~|\n" +
                "B = 3 * Time ~~|\n" +
                "C = 3 * Time ~~|\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext,MagicNumberCheck.class,1);
        assertHasIssue(visitorContext,MagicNumberCheck.class,2);
        assertHasIssue(visitorContext,MagicNumberCheck.class,3);

    }

    @Test
    public void testNumberRepeatsMinimumMinusOne(){
        String program = "A = 3 * 4 ~~|\n".repeat(DEFAULT_REPETITIONS-1);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());
    }

    @Test
    public void testNumberRepeatedInTheSameLineCounts(){
        String program = "A = " + "3 * ".repeat(DEFAULT_REPETITIONS-1) + "3~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        for(Issue issue: visitorContext.getIssues()) {
            assertEquals(MagicNumberCheck.class, issue.getCheck().getClass());
            assertEquals(1,issue.getLine());
        }

        assertEquals(DEFAULT_REPETITIONS,visitorContext.getIssues().size());

        assertHasIssue(visitorContext,MagicNumberCheck.class,1);
    }

    @Test
    public void testConstantDirectAssignsDoesntCountEquation(){
        String program = "A = 3 ~~|\n".repeat(DEFAULT_REPETITIONS);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());
    }

    @Test
    public void testConstantDirectAssignsDoesntCountDataEquation(){
        String program = "A := 3 ~~|\n".repeat(DEFAULT_REPETITIONS);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());
    }

    @Test
    public void testConstantDirectAssignsDoesntCountUnchangeableConstant(){
        String program = "A == 3 ~~|\n".repeat(DEFAULT_REPETITIONS);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());
    }

    @Test
    public void testTabbedArrayDoesntCountEquation(){
        String program = "TABBED_ARRAY = TABBED ARRAY(3    3    3    3\n" +
                "3    3    3    3\n" +
                "3    3    3    3)\n" +
                "~ |";


        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());

    }

    @Test
    public void testTabbedArrayDoesntCountDataEquation(){
        String program = "TABBED_ARRAY := TABBED ARRAY(3    3    3    3\n" +
                "3    3    3    3\n" +
                "3    3    3    3)\n" +
                "~ |";


        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());

    }



    @Test
    public void testLookupsDontCount(){
        String program = "mylookup_lt([(3,0)-(10,10)],(1.10092,2.41228),(2.72171,3.24561),(6.11621,5.96491),(3,8.37719))\n~|"
                .repeat(DEFAULT_REPETITIONS);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());


    }

    @Test
    public void testWithLookupSecondArgumentDoesntCountEquation(){
        String program = "var =WITH LOOKUP(Time,((0,1),(1,1),(2,2)))\n~|".repeat(DEFAULT_REPETITIONS);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());

    }

    @Test
    public void testWithLookupSecondArgumentDoesntCountDataEquation(){
        String program = "var :=WITH LOOKUP(Time,((0,1),(1,1),(2,2)))\n~|".repeat(DEFAULT_REPETITIONS);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());

    }


    @Test
    public void testWithLookupFirstArgument(){
        String program = "var =WITH LOOKUP(14,((0,1),(1,1),(2,2)))\n~|" +
                "A = 14 * 0~|\n".repeat(DEFAULT_REPETITIONS-1);


        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssue(visitorContext, MagicNumberCheck.class,1);
    }


    @Test
    public void testRealityCheckDoesntCount(){
        String program = "big_growth_test :TEST INPUT: divisions = 1e+022~|\n".repeat(DEFAULT_REPETITIONS);
        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());

    }

    @Test
    public void testConstraintDoesntCount(){
        String program = "my_condition_test :THE CONDITION: firstVariable[subscript]>100 :IMPLIES: secondVariable<100 ~|".repeat(DEFAULT_REPETITIONS);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());
    }


    @Test
    public void testBidimensionalArrayDoesntCount() {
        String program = "INITIAL_POPULATION = 1,1,1,1;1,1,1,1;\n" +
                "         1,1,1,1; ~Person~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());
    }

    @Test
    public void testNumberListDoesntCountEquation(){
        String program = "A = 1,1,1,1,1~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());

    }

    @Test
    public void testNumberListDoesntCountDataEquation(){
        String program = "A := 1,1,1,1,1~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());

    }

    @Test
    public void testNumberListDoesntCountUnchangeableConstant(){
        String program = "A == 1,1,1,1,1~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertTrue(visitorContext.getIssues().isEmpty());

    }


}
