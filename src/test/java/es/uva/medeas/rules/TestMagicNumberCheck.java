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
import org.sonar.api.utils.log.Logger;

import java.lang.reflect.Field;

import static es.uva.medeas.testutilities.RuleTestUtilities.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TestMagicNumberCheck {

    public static final int DEFAULT_MINIMUM_REPETITIONS = Integer.parseInt(MagicNumberCheck.DEFAULT_REPETITIONS);



    public MagicNumberCheck getMagicNumberCheckWithTable(VensimVisitorContext context, SymbolTable table){
        MagicNumberCheck check = Mockito.mock(MagicNumberCheck.class);
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
    public void testMagicNumbersCreateIssue(){

        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            symbol.addDefinitionLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.scan(context);


        assertEquals(DEFAULT_MINIMUM_REPETITIONS,context.getIssues().size());
        for(Issue issue:context.getIssues())
            assertEquals("The number 51 is repeated "+ DEFAULT_MINIMUM_REPETITIONS +" times. Consider replacing it by a constant",issue.getMessage());

        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            assertHasIssue(context,MagicNumberCheck.class,i);

    }

    @Test
    public void testNonMagicNumbersDontCreateIssue(){
        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS-1; i++)
            symbol.addDefinitionLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.scan(context);

        assertEquals(0,context.getIssues().size());
    }

    @Test
    public void testWithNumberNotDefined(){
        SymbolTable table = new SymbolTable();
        table.addSymbol(new Symbol("51"));
        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.scan(context);

        assertEquals(0,context.getIssues().size());
    }

    @Test
    public void testCountsRepetitionsInSameLine(){
        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            symbol.addDefinitionLine(10);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.scan(context);


        for(Issue issue:context.getIssues())
            assertEquals("The number 51 is repeated "+ DEFAULT_MINIMUM_REPETITIONS +" times. Consider replacing it by a constant",issue.getMessage());

        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            assertHasIssue(context,MagicNumberCheck.class,10);
    }

     @Test
    public void testTableWithBothMagicAndRegularNumbers(){
         SymbolTable table = new SymbolTable();
         VensimVisitorContext context = new VensimVisitorContext(null,table,null);

         Symbol magicNumber = table.addSymbol(new Symbol("51"));
         for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
             magicNumber.addDefinitionLine(10);
         magicNumber.addDefinitionLine(1);

         Symbol normalNumber1 = table.addSymbol(new Symbol("70"));
         for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS-1; i++)
             normalNumber1.addDefinitionLine(10);

         Symbol normalNumber2 = table.addSymbol(new Symbol("3.141592653"));
         normalNumber2.addDefinitionLine(1);


         MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
         check.scan(context);

         assertEquals(DEFAULT_MINIMUM_REPETITIONS+1,context.getIssues().size());
         for(Issue issue:context.getIssues())
             assertEquals("The number 51 is repeated "+ (DEFAULT_MINIMUM_REPETITIONS+1) +" times. Consider replacing it by a constant",issue.getMessage());
     }

     @Test
    public void  testDifferentNumberOfMinimumRepetitionsMagicNumber(){
       final int REPETITIONS = 9;

         SymbolTable table = new SymbolTable();
         VensimVisitorContext context = new VensimVisitorContext(null,table,null);

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
             assertHasIssue(context,MagicNumberCheck.class,i);
     }

     @Test
     public void testDifferentMinimumRepetitionsNotAMagicNumber(){
         final int REPETITIONS = 9;

         SymbolTable table = new SymbolTable();
         VensimVisitorContext context = new VensimVisitorContext(null,table,null);

         Symbol symbol = table.addSymbol(new Symbol("51"));
         for(int i = 0; i< REPETITIONS-1; i++)
             symbol.addDefinitionLine(i);


         MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
         check.repetitions = String.valueOf(REPETITIONS);
         check.scan(context);

         assertEquals(0,context.getIssues().size());
     }

    @Test
    public void testZeroAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsMagic(){

        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

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
            assertHasIssue(context,MagicNumberCheck.class,i);

    }

    @Test
    public void testZeroAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsntMagic(){

        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS-1; i++)
            symbol.addDefinitionLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions = "0";
        check.scan(context);

        assertEquals(0,context.getIssues().size());


    }

    @Test
    public void testOneAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsMagic(){
        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            symbol.addDefinitionLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions = "1";
        check.scan(context);

        assertEquals(DEFAULT_MINIMUM_REPETITIONS,context.getIssues().size());
        for(Issue issue:context.getIssues())
            assertEquals("The number 51 is repeated "+ DEFAULT_MINIMUM_REPETITIONS +" times. Consider replacing it by a constant",issue.getMessage());

        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS; i++)
            assertHasIssue(context,MagicNumberCheck.class,i);

    }

    @Test
    public void testOneAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsntMagic(){

        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS-1; i++)
            symbol.addDefinitionLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions = "1";
        check.scan(context);

        assertEquals(0,context.getIssues().size());


    }


    @Test
    public void testNegativeNumberAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsMagic(){
        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

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
            assertHasIssue(context,MagicNumberCheck.class,i);

    }

    @Test
    public void testNegativeNumberAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsntMagic(){

        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS-1; i++)
            symbol.addDefinitionLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions = "-1";
        check.scan(context);

        assertEquals(0,context.getIssues().size());


    }


    @Test
    public void testEmptyStringAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsMagic(){
        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

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
            assertHasIssue(context,MagicNumberCheck.class,i);

    }

    @Test
    public void testEmptyStringAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsntMagic(){

        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS-1; i++)
            symbol.addDefinitionLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions = "";
        check.scan(context);

        assertEquals(0,context.getIssues().size());


    }


    @Test
    public void testStringAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsMagic(){
        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

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
            assertHasIssue(context,MagicNumberCheck.class,i);

    }

    @Test
    public void testStringAsMinimumRepetitionsCountsAsDefaultRepetitionsAndIsntMagic(){

        SymbolTable table = new SymbolTable();
        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        Symbol symbol = table.addSymbol(new Symbol("51"));
        for(int i = 0; i< DEFAULT_MINIMUM_REPETITIONS-1; i++)
            symbol.addDefinitionLine(i);


        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        check.repetitions =  "F";
        check.scan(context);

        assertEquals(0,context.getIssues().size());


    }


    @Test
    public void testWarningIsLoggedIfMinimumRepetitionsIsAString() throws NoSuchFieldException, IllegalAccessException {
        SymbolTable table = new SymbolTable();
        table.addSymbol(new Symbol("1"));
        table.addSymbol(new Symbol("2"));
        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        Field logField = MagicNumberCheck.class.getDeclaredField("LOG");
        Logger logger = Mockito.mock(Logger.class);

        logField.set(null,logger);
        check.repetitions =  "F";
        check.scan(context);

        verify(logger,times(1))
                .warn("["+ VensimPlugin.LOG_NAME +"] The rule MagicNumberCheck has an invalid configuration: The selected minimum repetitions must be a number greater than 1.");
    }


    @Test
    public void testWarningIsLoggedIfMinimumRepetitionsIsZero() throws NoSuchFieldException, IllegalAccessException {
        SymbolTable table = new SymbolTable();
        table.addSymbol(new Symbol("1"));
        table.addSymbol(new Symbol("2"));
        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        Field logField = MagicNumberCheck.class.getDeclaredField("LOG");
        Logger logger = Mockito.mock(Logger.class);

        logField.set(null,logger);
        check.repetitions =  "0";
        check.scan(context);

        verify(logger,times(1))
                .warn("["+ VensimPlugin.LOG_NAME +"] The rule MagicNumberCheck has an invalid configuration: The selected minimum repetitions must be a number greater than 1.");
    }

    @Test
    public void testWarningIsLoggedIfMinimumRepetitionsIsOne() throws NoSuchFieldException, IllegalAccessException {
        SymbolTable table = new SymbolTable();
        table.addSymbol(new Symbol("1"));
        table.addSymbol(new Symbol("2"));
        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        Field logField = MagicNumberCheck.class.getDeclaredField("LOG");
        Logger logger = Mockito.mock(Logger.class);

        logField.set(null,logger);
        check.repetitions =  "1";
        check.scan(context);

        verify(logger,times(1))
                .warn("["+ VensimPlugin.LOG_NAME +"] The rule MagicNumberCheck has an invalid configuration: The selected minimum repetitions must be a number greater than 1.");
    }

    @Test
    public void testWarningIsLoggedIfMinimumRepetitionsIsNegative() throws NoSuchFieldException, IllegalAccessException {
        SymbolTable table = new SymbolTable();
        table.addSymbol(new Symbol("1"));
        table.addSymbol(new Symbol("2"));
        VensimVisitorContext context = new VensimVisitorContext(null,table,null);

        MagicNumberCheck check = getMagicNumberCheckWithTable(context,table);
        Field logField = MagicNumberCheck.class.getDeclaredField("LOG");
        Logger logger = Mockito.mock(Logger.class);

        logField.set(null,logger);
        check.repetitions =  "-1";
        check.scan(context);

        verify(logger,times(1))
                .warn("["+ VensimPlugin.LOG_NAME +"] The rule MagicNumberCheck has an invalid configuration: The selected minimum repetitions must be a number greater than 1.");
    }



}
