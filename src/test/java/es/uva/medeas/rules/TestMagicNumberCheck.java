package es.uva.medeas.rules;


import es.uva.medeas.VensimPlugin;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.visitors.MagicNumberTableVisitor;
import es.uva.medeas.plugin.Issue;
import es.uva.medeas.plugin.VensimVisitorContext;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.batch.rule.Severity;
import org.sonar.api.utils.log.Logger;

import java.util.List;
import java.util.stream.Collectors;

import static es.uva.medeas.testutilities.RuleTestUtilities.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TestMagicNumberCheck {

    public static final int DEFAULT_MINIMUM_REPETITIONS = Integer.parseInt(MagicNumberCheck.DEFAULT_REPETITIONS);



    public MagicNumberCheck getMagicNumberCheckWithTable(VensimVisitorContext context, SymbolTable table){
        MagicNumberCheck check = spy(new MagicNumberCheck());
        check.repetitions = String.valueOf(DEFAULT_MINIMUM_REPETITIONS);
        MagicNumberTableVisitor visitor = Mockito.mock(MagicNumberTableVisitor.class);
        when(visitor.getSymbolTable(context.getRootNode())).thenReturn(table);

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

        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            symbol.addDefinitionLine(i);


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
        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS+1; i++)
            symbol.addDefinitionLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.scan(context);


        assertEquals(DEFAULT_MINIMUM_REPETITIONS+1,context.getIssues().size());
        for(Issue issue:context.getIssues())
            assertEquals(Severity.MAJOR,issue.getSeverity());

    }



    @Test
    public void testRepetitionsLowerThanMinimumParameterAreInfo(){
        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS-1; i++)
            symbol.addDefinitionLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.scan(context);

        assertEquals(DEFAULT_MINIMUM_REPETITIONS-1,context.getIssues().size());
        for(Issue issue:context.getIssues())
            assertEquals(Severity.INFO,issue.getSeverity());

    }

    @Test
    public void testWithNumberNotDefined(){
        SymbolTable table = new SymbolTable();
        table.addSymbol(new Symbol("51"));
        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.scan(context);

        assertEquals(0,context.getIssues().size());
    }

    @Test
    public void testCountsRepetitionsInSameLine(){
        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            symbol.addDefinitionLine(10);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.scan(context);


        for(Issue issue:context.getIssues())
            assertEquals("The number 51 is repeated "+ DEFAULT_MINIMUM_REPETITIONS +" times. Consider replacing it by a constant",issue.getMessage());

        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            assertHasIssueInLines(context,MagicNumberCheck.class,10);
    }

     @Test
    public void testTableWithBothTypesOfIssues(){
         SymbolTable table = new SymbolTable();
         VensimVisitorContext context = new VensimVisitorContext(null,null,null);

         Symbol magicNumber = table.addSymbol(new Symbol("51"));
         for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
             magicNumber.addDefinitionLine(10);
         magicNumber.addDefinitionLine(1);

         Symbol normalNumber1 = table.addSymbol(new Symbol("70"));
         for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS-1; i++)
             normalNumber1.addDefinitionLine(10);

         Symbol normalNumber2 = table.addSymbol(new Symbol("3.1415"));
         normalNumber2.addDefinitionLine(1);


         MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
         check.scan(context);


         List<Issue> majorIssues = context.getIssues().stream().filter(issue -> issue.getSeverity() == Severity.MAJOR).collect(Collectors.toList());
         List<Issue> infoIssues = context.getIssues().stream().filter(issue -> issue.getSeverity() == Severity.INFO).collect(Collectors.toList());

         assertEquals(DEFAULT_MINIMUM_REPETITIONS+1,majorIssues.size());
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
        SymbolTable table = new SymbolTable();
        Symbol magicNumber = new Symbol("0");
        for(int i=0; i<DEFAULT_MINIMUM_REPETITIONS+4;i++){
            magicNumber.addDefinitionLine(3);
        }

         VensimVisitorContext context = new VensimVisitorContext(null,null,null);

         MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
         check.scan(context);

         assertTrue(context.getIssues().isEmpty());

     }

    @Test
    public void testOnesAreIgnored(){
        SymbolTable table = new SymbolTable();
        Symbol magicNumber = new Symbol("1");
        for(int i=0; i<DEFAULT_MINIMUM_REPETITIONS+4;i++){
            magicNumber.addDefinitionLine(3);
        }

        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());

    }

    @Test
    public void testOneHundredsAreIgnored(){
        SymbolTable table = new SymbolTable();
        Symbol magicNumber = new Symbol("100");
        for(int i=0; i<DEFAULT_MINIMUM_REPETITIONS+4;i++){
            magicNumber.addDefinitionLine(3);
        }

        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());

    }

     @Test
    public void  testDifferentNumberOfMinimumRepetitionsMagicNumber(){
       final int REPETITIONS = 9;

         SymbolTable table = new SymbolTable();
         VensimVisitorContext context = new VensimVisitorContext(null,null,null);

         Symbol symbol = table.addSymbol(new Symbol("51"));
         for(int i = 0; i< REPETITIONS; i++)
             symbol.addDefinitionLine(i);


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

         SymbolTable table = new SymbolTable();
         VensimVisitorContext context = new VensimVisitorContext(null,null,null);

         Symbol symbol = table.addSymbol(new Symbol("51"));
         for(int i = 0; i< REPETITIONS-1; i++)
             symbol.addDefinitionLine(i);


         MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
         check.repetitions = String.valueOf(REPETITIONS);
         check.scan(context);

         assertEquals(REPETITIONS-1,context.getIssues().size());
         for(Issue issue:context.getIssues())
             assertEquals(Severity.INFO,issue.getSeverity());
     }

    @Test
    public void testZeroAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsMagic(){

        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            symbol.addDefinitionLine(i);


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

        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS-1; i++)
            symbol.addDefinitionLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions = "0";
        check.scan(context);

        assertEquals(DEFAULT_MINIMUM_REPETITIONS-1,context.getIssues().size());
        for(Issue issue:context.getIssues())
            assertEquals(Severity.INFO,issue.getSeverity());



    }



    @Test
    public void testOneAsMinimumRepetitionsOnlyCreatesMajorIssues(){
        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        symbol.addDefinitionLine(1);
        symbol.addDefinitionLine(2);

        Symbol symbol2 = new Symbol("52");
        symbol2.addDefinitionLine(3);
        table.addSymbol(symbol2);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions = "1";
        check.scan(context);


        assertHasIssueInLines(context,MagicNumberCheck.class,1,2,3);
        for(Issue issue:context.getIssues())
            assertEquals(Severity.MAJOR,issue.getSeverity());

    }


    @Test
    public void testNegativeNumberAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsMagic(){
        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            symbol.addDefinitionLine(i);


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

        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS-1; i++)
            symbol.addDefinitionLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions = "-1";
        check.scan(context);

        assertEquals(DEFAULT_MINIMUM_REPETITIONS-1,context.getIssues().size());
        for(Issue issue:context.getIssues())
            assertEquals(Severity.INFO,issue.getSeverity());


    }


    @Test
    public void testEmptyStringAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsMagic(){
        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            symbol.addDefinitionLine(i);


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

        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS-1; i++)
            symbol.addDefinitionLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions = "";
        check.scan(context);

        assertEquals(DEFAULT_MINIMUM_REPETITIONS-1,context.getIssues().size());
        for(Issue issue: context.getIssues())
            assertEquals(Severity.INFO,issue.getSeverity());


    }


    @Test
    public void testStringAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsMagic(){
        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            symbol.addDefinitionLine(i);


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

        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS-1; i++)
            symbol.addDefinitionLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions =  "F";
        check.scan(context);

        assertEquals(DEFAULT_MINIMUM_REPETITIONS-1,context.getIssues().size());
        for(Issue issue:context.getIssues())
            assertEquals(Severity.INFO,issue.getSeverity());


    }


    @Test
    public void testWarningIsLoggedIfMinimumRepetitionsIsAString(){
        SymbolTable table = new SymbolTable();
        table.addSymbol(new Symbol("1"));
        table.addSymbol(new Symbol("2"));
        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        Logger logger = Mockito.mock(Logger.class);
        MagicNumberCheck.LOG = logger;

        check.repetitions =  "F";
        check.scan(context);

        verify(logger,times(1))
                .warn("The rule MagicNumberCheck has an invalid configuration: The selected minimum repetitions must be a number greater than 0."+"["+ VensimPlugin.PLUGIN_KEY +"]" );
    }


    @Test
    public void testWarningIsLoggedIfMinimumRepetitionsIsZero(){
        SymbolTable table = new SymbolTable();
        table.addSymbol(new Symbol("1"));
        table.addSymbol(new Symbol("2"));
        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        Logger logger = Mockito.mock(Logger.class);
        MagicNumberCheck.LOG = logger;

        check.repetitions =  "0";
        check.scan(context);

        verify(logger,times(1))
                .warn("The rule MagicNumberCheck has an invalid configuration: The selected minimum repetitions must be a number greater than 0."+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }



    @Test
    public void testWarningIsLoggedIfMinimumRepetitionsIsNegative(){
        SymbolTable table = new SymbolTable();
        table.addSymbol(new Symbol("1"));
        table.addSymbol(new Symbol("2"));
        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        Logger logger = Mockito.mock(Logger.class);
        MagicNumberCheck.LOG = logger;

        check.repetitions =  "-1";
        check.scan(context);

        verify(logger,times(1))
                .warn("The rule MagicNumberCheck has an invalid configuration: The selected minimum repetitions must be a number greater than 0."+"["+ VensimPlugin.PLUGIN_KEY +"]");
    }


    @Test
    public void testWarningsDueToPropertyErrorsAreLoggedOnlyOnce(){
        SymbolTable table = new SymbolTable();
        table.addSymbol(new Symbol("1"));
        table.addSymbol(new Symbol("2"));
        VensimVisitorContext context = new VensimVisitorContext(null,null,null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        Logger logger = Mockito.mock(Logger.class);
        MagicNumberCheck.LOG = logger;

        check.repetitions =  "-1";
        check.scan(context);
        check.scan(context);
        check.scan(context);
        check.scan(context);


        verify(logger,times(1))
                .warn("The rule MagicNumberCheck has an invalid configuration: The selected minimum repetitions must be a number greater than 0."+"["+ VensimPlugin.PLUGIN_KEY +"]");

    }

}
