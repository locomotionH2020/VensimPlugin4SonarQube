package es.uva.locomotion.rules;


import es.uva.locomotion.model.symbol.NumberTable;
import es.uva.locomotion.model.symbol.Number;
import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.parser.visitors.MagicNumberTableVisitor;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import static org.junit.Assert.*;

import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.batch.rule.Severity;

import java.util.List;
import java.util.stream.Collectors;

import static es.uva.locomotion.testutilities.RuleTestUtilities.*;
import static org.mockito.Mockito.*;

public class TestMagicNumberCheck {

    public static final int DEFAULT_MINIMUM_REPETITIONS = Integer.parseInt(MagicNumberCheck.DEFAULT_REPETITIONS);



    public MagicNumberCheck getMagicNumberCheckWithTable(VensimVisitorContext context, NumberTable table){
        MagicNumberCheck check = spy(new MagicNumberCheck());
        check.repetitions = String.valueOf(DEFAULT_MINIMUM_REPETITIONS);
        MagicNumberTableVisitor visitor = Mockito.mock(MagicNumberTableVisitor.class);
        when(visitor.getNumberTable(context.getRootNode())).thenReturn(table);

        doCallRealMethod().when(check).scan(any());
        when(check.getVisitor()).thenReturn(visitor);

        return check;
    }

    @BeforeClass
    public static void preTest(){
        assertTrue(DEFAULT_MINIMUM_REPETITIONS>0);
    }

    @Test
    public void testRepetitionsEqualsTheMinimumParameterAreMajor(){

        NumberTable table = new NumberTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        Number Number = table.addNumber(new Number("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            Number.addLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.scan(context);


        assertEquals(DEFAULT_MINIMUM_REPETITIONS,context.getIssues().size());
        for(Issue issue:context.getIssues()) {
            assertEquals("The number 51 is repeated " + DEFAULT_MINIMUM_REPETITIONS + " times. Consider replacing it by a constant", issue.getMessage());
            assertEquals(Severity.MAJOR,issue.getSeverity());
        }

        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            assertHasIssueInLines(context,MagicNumberCheck.class,i);

    }

    @Test
    public void testRepetitionsBiggerThanMinimumParameterAreMajor(){
        NumberTable table = new NumberTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        Number Number = table.addNumber(new Number("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS+1; i++)
            Number.addLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.scan(context);


        assertEquals(DEFAULT_MINIMUM_REPETITIONS+1,context.getIssues().size());
        for(Issue issue:context.getIssues())
            assertEquals(Severity.MAJOR,issue.getSeverity());

    }



    @Test
    public void testRepetitionsLowerThanMinimumParameterAreInfo(){
        NumberTable table = new NumberTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        Number Number = table.addNumber(new Number("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS-1; i++)
            Number.addLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.scan(context);

        assertEquals(DEFAULT_MINIMUM_REPETITIONS-1,context.getIssues().size());
        for(Issue issue:context.getIssues())
            assertEquals(Severity.INFO,issue.getSeverity());

    }

    @Test
    public void testWithNumberNotDefined(){
        NumberTable table = new NumberTable();
        table.addNumber(new Number("51"));
        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.scan(context);

        assertEquals(0,context.getIssues().size());
    }

    @Test
    public void testCountsRepetitionsInSameLine(){
        NumberTable table = new NumberTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        Number Number = table.addNumber(new Number("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            Number.addLine(10);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.scan(context);


        for(Issue issue:context.getIssues())
            assertEquals("The number 51 is repeated "+ DEFAULT_MINIMUM_REPETITIONS +" times. Consider replacing it by a constant",issue.getMessage());

        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            assertHasIssueInLines(context,MagicNumberCheck.class,10);
    }

     @Test
    public void testTableWithBothTypesOfIssues(){
         NumberTable table = new NumberTable();
         VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

         Number magicNumber = table.addNumber(new Number("51"));
         for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
             magicNumber.addLine(10);
         magicNumber.addLine(1);

         Number normalNumber1 = table.addNumber(new Number("70"));
         for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS-1; i++)
             normalNumber1.addLine(10);

         Number normalNumber2 = table.addNumber(new Number("3.1415"));
         normalNumber2.addLine(1);


         MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
         check.scan(context);


         List<Issue> majorIssues = context.getIssues().stream().filter(issue -> issue.getSeverity() == Severity.MAJOR).collect(Collectors.toList());
         List<Issue> infoIssues = context.getIssues().stream().filter(issue -> issue.getSeverity() == Severity.INFO).collect(Collectors.toList());

         assertEquals(2,majorIssues.size());
         for(Issue issue:majorIssues)
             assertEquals("The number 51 is repeated " + (DEFAULT_MINIMUM_REPETITIONS + 1) + " times. Consider replacing it by a constant", issue.getMessage());
         for(Issue issue:infoIssues) {
             if(issue.getLine()==10)
                assertEquals("The number 70 is repeated " + (DEFAULT_MINIMUM_REPETITIONS -1) + " times. Consider replacing it by a constant", issue.getMessage());
            else if(issue.getLine()==1)
                 assertEquals("The number 3.1415 is repeated 1 times. Consider replacing it by a constant", issue.getMessage());

         }


     }

     @Test
     public void testZerosAreIgnored(){
        NumberTable table = new NumberTable();
        Number magicNumber = new Number("0");
        for(int i=0; i<DEFAULT_MINIMUM_REPETITIONS+4;i++){
            magicNumber.addLine(3);
        }

         VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

         MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
         check.scan(context);

         assertTrue(context.getIssues().isEmpty());

     }

    @Test
    public void testOnesAreIgnored(){
        NumberTable table = new NumberTable();
        Number magicNumber = new Number("1");
        for(int i=0; i<DEFAULT_MINIMUM_REPETITIONS+4;i++){
            magicNumber.addLine(3);
        }

        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());

    }

    @Test
    public void testOneHundredsAreIgnored(){
        NumberTable table = new NumberTable();
        Number magicNumber = new Number("100");
        for(int i=0; i<DEFAULT_MINIMUM_REPETITIONS+4;i++){
            magicNumber.addLine(3);
        }

        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());

    }

    @Test
    public void testMinusOnesAreIgnored(){
        NumberTable table = new NumberTable();
        Number magicNumber = new Number("-1");
        for(int i=0; i<DEFAULT_MINIMUM_REPETITIONS+4;i++){
            magicNumber.addLine(3);
        }

        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());

    }

     @Test
    public void  testDifferentNumberOfMinimumRepetitionsMagicNumber(){
       final int REPETITIONS = 9;

         NumberTable table = new NumberTable();
         VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

         Number Number = table.addNumber(new Number("51"));
         for(int i = 0; i< REPETITIONS; i++)
             Number.addLine(i);


         MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
         check.repetitions = String.valueOf(REPETITIONS);
         check.scan(context);


         assertEquals(REPETITIONS,context.getIssues().size());
         for(Issue issue:context.getIssues())
             assertEquals("The number 51 is repeated "+ REPETITIONS +" times. Consider replacing it by a constant",issue.getMessage());

         for(int i = 0; i< REPETITIONS; i++)
             assertHasIssueInLines(context,MagicNumberCheck.class,i);
     }

     @Test
     public void testDifferentMinimumRepetitionsNotAMagicNumber(){
         final int REPETITIONS = 9;

         NumberTable table = new NumberTable();
         VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

         Number Number = table.addNumber(new Number("51"));
         for(int i = 0; i< REPETITIONS-1; i++)
             Number.addLine(i);


         MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
         check.repetitions = String.valueOf(REPETITIONS);
         check.scan(context);

         assertEquals(REPETITIONS-1,context.getIssues().size());
         for(Issue issue:context.getIssues())
             assertEquals(Severity.INFO,issue.getSeverity());
     }

    @Test
    public void testZeroAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsMagic(){

        NumberTable table = new NumberTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        Number Number = table.addNumber(new Number("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            Number.addLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions = "0";
        check.scan(context);

        assertEquals(DEFAULT_MINIMUM_REPETITIONS,context.getIssues().size());
        for(Issue issue:context.getIssues())
            assertEquals("The number 51 is repeated "+ DEFAULT_MINIMUM_REPETITIONS +" times. Consider replacing it by a constant",issue.getMessage());

        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            assertHasIssueInLines(context,MagicNumberCheck.class,i);

    }

    @Test
    public void testZeroAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsntMagic(){

        NumberTable table = new NumberTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        Number Number = table.addNumber(new Number("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS-1; i++)
            Number.addLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions = "0";
        check.scan(context);

        assertEquals(DEFAULT_MINIMUM_REPETITIONS-1,context.getIssues().size());
        for(Issue issue:context.getIssues())
            assertEquals(Severity.INFO,issue.getSeverity());



    }



    @Test
    public void testOneAsMinimumRepetitionsOnlyCreatesMajorIssues(){
        NumberTable table = new NumberTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        Number Number = table.addNumber(new Number("51"));
        Number.addLine(1);
        Number.addLine(2);

        Number Number2 = new Number("52");
        Number2.addLine(3);
        table.addNumber(Number2);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions = "1";
        check.scan(context);


        assertHasIssueInLines(context,MagicNumberCheck.class,1,2,3);
        for(Issue issue:context.getIssues())
            assertEquals(Severity.MAJOR,issue.getSeverity());

    }


    @Test
    public void testNegativeNumberAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsMagic(){
        NumberTable table = new NumberTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        Number Number = table.addNumber(new Number("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            Number.addLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions = "-1";
        check.scan(context);

        assertEquals(DEFAULT_MINIMUM_REPETITIONS,context.getIssues().size());
        for(Issue issue:context.getIssues())
            assertEquals("The number 51 is repeated "+ DEFAULT_MINIMUM_REPETITIONS +" times. Consider replacing it by a constant",issue.getMessage());

        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            assertHasIssueInLines(context,MagicNumberCheck.class,i);

    }

    @Test
    public void testNegativeNumberAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsntMagic(){

        NumberTable table = new NumberTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        Number Number = table.addNumber(new Number("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS-1; i++)
            Number.addLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions = "-1";
        check.scan(context);

        assertEquals(DEFAULT_MINIMUM_REPETITIONS-1,context.getIssues().size());
        for(Issue issue:context.getIssues())
            assertEquals(Severity.INFO,issue.getSeverity());


    }


    @Test
    public void testEmptyStringAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsMagic(){
        NumberTable table = new NumberTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        Number Number = table.addNumber(new Number("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            Number.addLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions = "";
        check.scan(context);

        assertEquals(DEFAULT_MINIMUM_REPETITIONS,context.getIssues().size());
        for(Issue issue:context.getIssues())
            assertEquals("The number 51 is repeated "+ DEFAULT_MINIMUM_REPETITIONS +" times. Consider replacing it by a constant",issue.getMessage());

        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            assertHasIssueInLines(context,MagicNumberCheck.class,i);

    }

    @Test
    public void testEmptyStringAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsntMagic(){

        NumberTable table = new NumberTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        Number Number = table.addNumber(new Number("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS-1; i++)
            Number.addLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions = "";
        check.scan(context);

        assertEquals(DEFAULT_MINIMUM_REPETITIONS-1,context.getIssues().size());
        for(Issue issue: context.getIssues())
            assertEquals(Severity.INFO,issue.getSeverity());


    }


    @Test
    public void testStringAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsMagic(){
        NumberTable table = new NumberTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        Number Number = table.addNumber(new Number("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            Number.addLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions = "F";
        check.scan(context);

        assertEquals(DEFAULT_MINIMUM_REPETITIONS,context.getIssues().size());
        for(Issue issue:context.getIssues())
            assertEquals("The number 51 is repeated "+ DEFAULT_MINIMUM_REPETITIONS +" times. Consider replacing it by a constant",issue.getMessage());

        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            assertHasIssueInLines(context,MagicNumberCheck.class,i);

    }

    @Test
    public void testStringAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsntMagic(){

        NumberTable table = new NumberTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        Number Number = table.addNumber(new Number("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS-1; i++)
            Number.addLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions =  "F";
        check.scan(context);

        assertEquals(DEFAULT_MINIMUM_REPETITIONS-1,context.getIssues().size());
        for(Issue issue:context.getIssues())
            assertEquals(Severity.INFO,issue.getSeverity());


    }


    @Test
    public void testWarningIsLoggedIfMinimumRepetitionsIsAString(){
        NumberTable table = new NumberTable();
        table.addNumber(new Number("1"));
        table.addNumber(new Number("2"));
        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        MagicNumberCheck.LOG = logger;

        check.repetitions =  "F";
        check.scan(context);

        verify(logger,atLeastOnce())
                .unique("The rule MagicNumberCheck has an invalid configuration: The selected minimum repetitions must be a number greater than 0.", LoggingLevel.ERROR );
    }


    @Test
    public void testWarningIsLoggedIfMinimumRepetitionsIsZero(){
        NumberTable table = new NumberTable();
        table.addNumber(new Number("1"));
        table.addNumber(new Number("2"));
        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        MagicNumberCheck.LOG = logger;

        check.repetitions =  "0";
        check.scan(context);

        verify(logger,atLeastOnce())
                .unique("The rule MagicNumberCheck has an invalid configuration: The selected minimum repetitions must be a number greater than 0.",LoggingLevel.ERROR);
    }



    @Test
    public void testWarningIsLoggedIfMinimumRepetitionsIsNegative(){
        NumberTable table = new NumberTable();
        table.addNumber(new Number("1"));
        table.addNumber(new Number("2"));
        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        MagicNumberCheck.LOG = logger;

        check.repetitions =  "-1";
        check.scan(context);

        verify(logger,atLeastOnce())
                .unique("The rule MagicNumberCheck has an invalid configuration: The selected minimum repetitions must be a number greater than 0.",LoggingLevel.ERROR);
    }


    @Test
    public void testWarningsDueToPropertyErrorsAreLoggedOnlyOnce(){
        NumberTable table = new NumberTable();
        table.addNumber(new Number("1"));
        table.addNumber(new Number("2"));
        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        VensimLogger logger = Mockito.mock(VensimLogger.class);
        MagicNumberCheck.LOG = logger;

        check.repetitions =  "-1";
        check.scan(context);
        check.scan(context);
        check.scan(context);
        check.scan(context);


        verify(logger,atLeastOnce()).unique("The rule MagicNumberCheck has an invalid configuration: The selected minimum repetitions must be a number greater than 0.",LoggingLevel.ERROR);

    }

}
