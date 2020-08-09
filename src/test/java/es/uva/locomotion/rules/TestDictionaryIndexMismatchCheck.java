package es.uva.locomotion.rules;

import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.SymbolTable;
import es.uva.locomotion.parser.SymbolType;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.testutilities.GeneralTestUtilities;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TestDictionaryIndexMismatchCheck {

    @Test
    public void testBacktrackingFirstOrder(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbFirstYearSubscript = GeneralTestUtilities.createSubscript(dbTable,"YEARS_1_I","YEAR 2016","YEAR 2017");
        Symbol dbSecondYearSubscript = GeneralTestUtilities.createSubscript(dbTable,"YEARS_2_I","YEAR 2017","YEAR 2018");

        Symbol dbVar = new Symbol("var");
        dbVar.addIndexLine(List.of(dbFirstYearSubscript,dbSecondYearSubscript));
        dbTable.addSymbol(dbVar);



        Symbol parsedVar = GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1,2);
        Symbol year2017 = GeneralTestUtilities.addSymbolInLines(parsedTable,"YEAR 2017",SymbolType.Subscript_Value,3);
        Symbol year2016 = GeneralTestUtilities.addSymbolInLines(parsedTable,"YEAR 2016",SymbolType.Subscript_Value,3);

        parsedVar.addIndexLine(List.of(year2017,year2017));
        parsedVar.addIndexLine(List.of(year2017,year2016));


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);


        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
        assertTrue(parsedVar.isValid());


    }

    @Test
    public void testBacktrackingSecondOrder(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbFirstYearSubscript = GeneralTestUtilities.createSubscript(dbTable,"YEARS_1_I","YEAR 2016","YEAR 2017");
        Symbol dbSecondYearSubscript = GeneralTestUtilities.createSubscript(dbTable,"YEARS_2_I","YEAR 2017","YEAR 2018");

        Symbol dbVar = new Symbol("var");
        dbVar.addIndexLine(List.of(dbSecondYearSubscript,dbFirstYearSubscript));
        dbTable.addSymbol(dbVar);

        Symbol parsedVar = GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1,2);
        Symbol year2017 = GeneralTestUtilities.addSymbolInLines(parsedTable,"YEAR 2017",SymbolType.Subscript_Value,3);
        Symbol year2016 = GeneralTestUtilities.addSymbolInLines(parsedTable,"YEAR 2016",SymbolType.Subscript_Value,3);

        parsedVar.addIndexLine(List.of(year2017,year2017));
        parsedVar.addIndexLine(List.of(year2017,year2016));


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());

    }

    @Test
    public void testMatchSubscripts(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbScenario = GeneralTestUtilities.createSubscript(dbTable,"SCENARIO","SCENARIO_1","SCENARIO_2");
        Symbol dbTypeOfEnergy = GeneralTestUtilities.createSubscript(dbTable,"TYPE_OF_ENERGY","CARBON","SUN","WIND");

        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbVar.addIndexLine(List.of(dbTypeOfEnergy,dbScenario));
        dbTable.addSymbol(dbVar);


        Symbol parsedVar = GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1,2);
        Symbol scenario = GeneralTestUtilities.createSubscript(parsedTable,"SCENARIO","SCENARIO_1","SCENARIO_2");
        Symbol typeOfEnergy = GeneralTestUtilities.createSubscript(parsedTable,"TYPE_OF_ENERGY","CARBON","SUN","WIND");

        parsedVar.addIndexLine(List.of(scenario,typeOfEnergy));

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);


        assertTrue(context.getIssues().isEmpty());


    }

    @Test
    public void testMatchMixOfSubscriptAndValues(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbScenario = GeneralTestUtilities.createSubscript(dbTable,"SCENARIO","SCENARIO_1","SCENARIO_2");
        Symbol dbTypeOfEnergy = GeneralTestUtilities.createSubscript(dbTable,"TYPE_OF_ENERGY","CARBON","SUN","wIND");

        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbVar.addIndexLine(List.of(dbTypeOfEnergy,dbScenario));
        dbTable.addSymbol(dbVar);


        Symbol parsedVar = GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1,2);
        Symbol scenario = GeneralTestUtilities.createSubscript(parsedTable,"SCENARIO","SCENARIO_1","SCENARIO_2");
        GeneralTestUtilities.createSubscript(parsedTable,"TYPE_OF_ENERGY","CARBON","SUN","WIND");

        parsedTable.getSymbol("SCENARIO_1").addDefinitionLine(1);

        parsedVar.addIndexLine(List.of(scenario,parsedTable.getSymbol("CARBON")));
        parsedVar.addIndexLine(List.of(scenario,parsedTable.getSymbol("SUN")));

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());

    }

    @Test
    public void testMatchesValuesAndNotTheSubscriptName_DifferentName(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbScenario = GeneralTestUtilities.createSubscript(dbTable,"SCENARIO","SCENARIO_1","SCENARIO_2");


        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbVar.addIndexLine(List.of(dbScenario));
        dbTable.addSymbol(dbVar);


        Symbol parsedVar = GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1,2);
        Symbol scenarioCopy = GeneralTestUtilities.createSubscript(parsedTable,"SCENARIO_COPY","SCENARIO_1","SCENARIO_2");

        parsedVar.addIndexLine(List.of(scenarioCopy));

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());
    }

    @Test
    public void testMatchesValuesAndNotTheSubscriptName_DifferentValuesSameName(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbScenario = GeneralTestUtilities.createSubscript(dbTable,"SCENARIO","SCENARIO_1","SCENARIO_2");


        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbVar.addIndexLine(List.of(dbScenario));
        dbTable.addSymbol(dbVar);


        Symbol parsedVar = GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1);
        Symbol scenario = GeneralTestUtilities.createSubscript(parsedTable,"SCENARIO","SCENARIO_1","SCENARIO_3","SCENARIO_2");

        parsedVar.addIndexLine(List.of(scenario));

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

       assertEquals(1,context.getIssues().size());

    }

    @Test
    public void testMatchesValuesAndNotTheSubscriptName_DifferentNameSubsetOfValues(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbScenario = GeneralTestUtilities.createSubscript(dbTable,"SCENARIO","SCENARIO_1","SCENARIO_2");


        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbVar.addIndexLine(List.of(dbScenario));
        dbTable.addSymbol(dbVar);


        Symbol parsedVar = GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1);
        Symbol scenario = GeneralTestUtilities.createSubscript(parsedTable,"SCENARIO","SCENARIO_2");

        parsedVar.addIndexLine(List.of(scenario));

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(0,context.getIssues().size());

    }

    @Test
    public void testIssue(){

        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbScenario = GeneralTestUtilities.createSubscript(dbTable,"SCENARIO","SCENARIO_1","SCENARIO_2");
        Symbol dbEnergyType = GeneralTestUtilities.createSubscript(parsedTable,"ENERGY_TYPE","SOLAR","CARBON");

        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbVar.addIndexLine(List.of(dbScenario,dbEnergyType));
        dbTable.addSymbol(dbVar);


        Symbol parsedVar = GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1,2);
        Symbol fooSubscript = GeneralTestUtilities.createSubscript(parsedTable,"FOO","VAR","TEST");


        parsedVar.addIndexLine(List.of(fooSubscript));

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(2,context.getIssues().size());
        assertFalse(parsedVar.isValid());

        Set<Integer> foundInLines = new HashSet<>();

        for(Issue issue: context.getIssues()){
            assertEquals("The symbol 'var' is indexed by values that aren't stored in the database.'\n" +
                    "Found:[[FOO]].\n" +
                    "Expected a subset of:[SCENARIO, ENERGY_TYPE]",issue.getMessage());
            foundInLines.add(issue.getLine());
        }

        assertEquals(Set.of(1,2),foundInLines);
    }

    @Test
    public void testNoIndexesInDictionary(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbTable.addSymbol(dbVar);

        Symbol parsedVar = GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1,2);
        Symbol fooSubscript = GeneralTestUtilities.createSubscript(parsedTable,"FOO","VAR","TEST");


        parsedVar.addIndexLine(List.of(fooSubscript));

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(2,context.getIssues().size());
    }

    @Test
    public void testNoIndexesInFile(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbTable.addSymbol(dbVar);

        Symbol dbSubscript = GeneralTestUtilities.createSubscript(dbTable,"DBTest","VAR","TEST");
        dbVar.addIndexLine(List.of(dbSubscript));


        GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1,2);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(0,context.getIssues().size());
    }

    @Test
    public void testNoIndexesInBothFileAndDictionary(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbTable.addSymbol(dbVar);


        GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1,2);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(0,context.getIssues().size());
    }

    @Test
    public void testAllowsASubsetOfIndexes(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbScenario = GeneralTestUtilities.createSubscript(dbTable,"SCENARIO","SCENARIO_1","SCENARIO_2");
        Symbol dbEnergyType = GeneralTestUtilities.createSubscript(dbTable,"ENERGY_TYPE","SOLAR","WIND");
        Symbol dbExtraSubscript = GeneralTestUtilities.createSubscript(dbTable,"EXTRA","VALUE 1", "VALUE 2","VALUE 3");

        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbVar.addIndexLine(List.of(dbEnergyType,dbScenario,dbExtraSubscript));
        dbTable.addSymbol(dbVar);


        Symbol parsedVar = GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1);
        Symbol fileScenario = GeneralTestUtilities.createSubscript(parsedTable,"SCENARIO","SCENARIO_2");
        Symbol fileEnergyType = GeneralTestUtilities.createSubscript(dbTable,"FILE_ENERGY_TYPE","SOLAR","WIND");


        parsedVar.addIndexLine(List.of(fileScenario,fileEnergyType));

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(0,context.getIssues().size());
    }

    @Test
    public void testSubscriptWithoutValuesInDictionary(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbVar.addIndexLine(List.of(new Symbol("EMPTY_SUBSCRIPT",SymbolType.Subscript)));
        dbTable.addSymbol(dbVar);

        Symbol parsedVar = GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1);
        Symbol parsedSubscript = GeneralTestUtilities.createSubscript(parsedTable,"EMPTY_SUBSCRIPT","SCENARIO_2");

        parsedVar.addIndexLine(List.of(parsedSubscript));


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(1,context.getIssues().size());
    }

    @Test
    public void testSubscriptWithoutValuesInFile(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        Symbol dbSubscript = GeneralTestUtilities.createSubscript(dbTable,"EMPTY_SUBSCRIPT","VALUE");
        dbVar.addIndexLine(List.of(dbSubscript));
        dbTable.addSymbol(dbVar);

        Symbol parsedVar = GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1);

        parsedVar.addIndexLine(List.of(new Symbol("EMPTY_SUBSCRIPT",SymbolType.Subscript)));


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(0,context.getIssues().size());
    }


    @Test
    public void testSubscriptNamesAndValuesAreTrimmed(){

        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbScenario = GeneralTestUtilities.createSubscript(dbTable,"    SCENARIO    ","    SCENARIO_1    ","    SCENARIO_2    ");

        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbVar.addIndexLine(List.of(dbScenario));
        dbTable.addSymbol(dbVar);


        Symbol parsedVar = GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1);
        Symbol fileScenario = GeneralTestUtilities.createSubscript(parsedTable,"  SCENARIO  ","  SCENARIO_1  ");

        parsedVar.addIndexLine(List.of(fileScenario));

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(0,context.getIssues().size());

    }



    @Test
    public void testDbTableNull(){

        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1);
        Symbol fileSubscript = GeneralTestUtilities.createSubscript(parsedTable,"SUBSCRIPT","S1");
        parsedVar.addIndexLine(List.of(fileSubscript));


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,null);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(0,context.getIssues().size());


    }

    @Test
    public void testSymbolNotFoundInDictionary(){
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1);
        Symbol fileSubscript = GeneralTestUtilities.createSubscript(parsedTable,"SUBSCRIPT","S1");
        parsedVar.addIndexLine(List.of(fileSubscript));


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,new SymbolTable());
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(0,context.getIssues().size());
    }

    @Test
    public void testIndexIsBothSubscriptAndValue(){
        SymbolTable parsedTable = new SymbolTable();
        SymbolTable dbTable = new SymbolTable();

        VensimLogger logger = mock(VensimLogger.class);
        DictionaryIndexMismatchCheck.LOG = logger;


        Symbol firstSubscript = GeneralTestUtilities.createSubscript(parsedTable,"FIRST_SUBSCRIPT","S1");
        Symbol secondSubscript = GeneralTestUtilities.createSubscript(parsedTable, "SECOND_SUBSCRIPT", "SUBSCRIPT_VALUE");

        Symbol varBefore = GeneralTestUtilities.addSymbolInLines(parsedTable,"varBefore",SymbolType.Variable,1,3);
        varBefore.addIndexLine(List.of(secondSubscript));


        Symbol invalidVariable = GeneralTestUtilities.addSymbolInLines(parsedTable,"invalidVariable",SymbolType.Variable,4);
        invalidVariable.addIndexLine(List.of(firstSubscript));
        invalidVariable.addIndexLine(List.of(parsedTable.getSymbol("SUBSCRIPT_VALUE")));

        Symbol varAfter = GeneralTestUtilities.addSymbolInLines(parsedTable,"varAfter",SymbolType.Variable,1,3);
        varAfter.addIndexLine(List.of(secondSubscript));

        Symbol dbSecondSubscript = GeneralTestUtilities.createSubscript(dbTable, "SECOND_SUBSCRIPT", "SUBSCRIPT_VALUE");

        Symbol dbVar = new Symbol("invalidVariable",SymbolType.Variable);
        dbVar.addIndexLine(List.of(dbSecondSubscript));
        dbTable.addSymbol(dbVar);

        Symbol dbVarBefore = new Symbol("varBefore",SymbolType.Variable);
        dbVarBefore.addIndexLine(List.of(dbSecondSubscript));
        dbTable.addSymbol(dbVarBefore);

        Symbol dbVarAfter = new Symbol("varAfter",SymbolType.Variable);
        dbVarAfter.addIndexLine(List.of(dbSecondSubscript));
        dbTable.addSymbol(dbVarAfter);


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

       verify(logger).info( "The symbol 'invalidVariable' is indexed by a subscript and a subscript value in the same column.");
        assertEquals(0,context.getIssues().size());
    }



    @Test
    public void testDictionaryIndexesAreConsumed(){
        SymbolTable parsedTable = new SymbolTable();
        SymbolTable dbTable = new SymbolTable();

        Symbol subscript = GeneralTestUtilities.createSubscript(parsedTable,"SUBSCRIPT","S1");


        Symbol var = GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,7);
        var.addIndexLine(List.of(subscript,subscript));


        Symbol dbSubscript = GeneralTestUtilities.createSubscript(dbTable,"SUBSCRIPT","S1");
        Symbol dbVar = new Symbol("var");
        dbVar.addIndexLine(List.of(dbSubscript));
        dbTable.addSymbol(dbVar);


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(1,context.getIssues().size());
        assertEquals(7,context.getIssues().get(0).getLine());
    }

    @Test
    public void testSameIndexTwiceInRow(){
        SymbolTable parsedTable = new SymbolTable();
        SymbolTable dbTable = new SymbolTable();

        Symbol subscript = GeneralTestUtilities.createSubscript(parsedTable,"SUBSCRIPT","S1");


        Symbol var = GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1);
        var.addIndexLine(List.of(subscript,subscript));


        Symbol dbSubscript = GeneralTestUtilities.createSubscript(dbTable,"SUBSCRIPT","S1");
        Symbol dbVar = new Symbol("var");
        dbVar.addIndexLine(List.of(dbSubscript,dbSubscript));
        dbTable.addSymbol(dbVar);


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(0,context.getIssues().size());
    }

    @Test
    public void testSameIndexTwiceInColumn_Subscript(){
        SymbolTable parsedTable = new SymbolTable();
        SymbolTable dbTable = new SymbolTable();

        Symbol subscript = GeneralTestUtilities.createSubscript(parsedTable,"SUBSCRIPT","S1");


        Symbol var = GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1);
        var.addIndexLine(List.of(subscript));
        var.addIndexLine(List.of(subscript));


        Symbol dbSubscript = GeneralTestUtilities.createSubscript(dbTable,"SUBSCRIPT","S1");
        Symbol dbVar = new Symbol("var");
        dbVar.addIndexLine(List.of(dbSubscript,dbSubscript));
        dbTable.addSymbol(dbVar);


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(0,context.getIssues().size());
    }


    @Test
    public void testSameIndexTwiceInColumn_Value(){
        SymbolTable parsedTable = new SymbolTable();
        SymbolTable dbTable = new SymbolTable();

        GeneralTestUtilities.createSubscript(parsedTable,"SUBSCRIPT","S1");


        Symbol var = GeneralTestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1);
        var.addIndexLine(List.of(parsedTable.getSymbol("S1")));
        var.addIndexLine(List.of(parsedTable.getSymbol("S1")));


        Symbol dbSubscript = GeneralTestUtilities.createSubscript(dbTable,"SUBSCRIPT","S1");
        Symbol dbVar = new Symbol("var");
        dbVar.addIndexLine(List.of(dbSubscript,dbSubscript));
        dbTable.addSymbol(dbVar);


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(0,context.getIssues().size());
    }


    @Test
    public void testIssuesInMultipleSymbols(){
        SymbolTable parsedTable = new SymbolTable();
        SymbolTable dbTable = new SymbolTable();

        Symbol subscript = GeneralTestUtilities.createSubscript(parsedTable,"SUBSCRIPT","S1");


        Symbol varOne = GeneralTestUtilities.addSymbolInLines(parsedTable,"varOne",SymbolType.Variable,1);
        varOne.addIndexLine(List.of(subscript));

        Symbol varTwo = GeneralTestUtilities.addSymbolInLines(parsedTable,"varTwo",SymbolType.Variable,2);
        varTwo.addIndexLine(List.of(subscript));

        dbTable.addSymbol(new Symbol("varOne"));
        dbTable.addSymbol(new Symbol("varTwo"));


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        Set<Integer> foundIn = new HashSet<>();
        for (Issue i:context.getIssues())
            foundIn.add(i.getLine());

        assertEquals(2,context.getIssues().size());
        assertEquals(Set.of(1,2),foundIn);
    }

}
