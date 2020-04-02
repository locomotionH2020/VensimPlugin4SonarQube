package es.uva.medeas.rules;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import es.uva.medeas.plugin.Issue;
import es.uva.medeas.plugin.VensimVisitorContext;
import es.uva.medeas.testutilities.TestUtilities;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class TestDictionaryIndexMismatchCheck {

    @Test
    public void testBacktrackingFirstOrder(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbFirstYearSubscript = TestUtilities.createSubscript(dbTable,"YEARS_1_I","YEAR 2016","YEAR 2017");
        Symbol dbSecondYearSubscript = TestUtilities.createSubscript(dbTable,"YEARS_2_I","YEAR 2017","YEAR 2018");

        Symbol dbVar = new Symbol("var");
        dbVar.addIndexLine(List.of(dbFirstYearSubscript,dbSecondYearSubscript));
        dbTable.addSymbol(dbVar);



        Symbol parsedVar = TestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1,2);
        Symbol year2017 = TestUtilities.addSymbolInLines(parsedTable,"YEAR 2017",SymbolType.Subscript_Value,3);
        Symbol year2016 = TestUtilities.addSymbolInLines(parsedTable,"YEAR 2016",SymbolType.Subscript_Value,3);

        parsedVar.addIndexLine(List.of(year2017,year2017));
        parsedVar.addIndexLine(List.of(year2017,year2016));


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);


        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertTrue(context.getIssues().isEmpty());

    }

    @Test
    public void testBacktrackingSecondOrder(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbFirstYearSubscript = TestUtilities.createSubscript(dbTable,"YEARS_1_I","YEAR 2016","YEAR 2017");
        Symbol dbSecondYearSubscript = TestUtilities.createSubscript(dbTable,"YEARS_2_I","YEAR 2017","YEAR 2018");

        Symbol dbVar = new Symbol("var");
        dbVar.addIndexLine(List.of(dbSecondYearSubscript,dbFirstYearSubscript));
        dbTable.addSymbol(dbVar);

        Symbol parsedVar = TestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1,2);
        Symbol year2017 = TestUtilities.addSymbolInLines(parsedTable,"YEAR 2017",SymbolType.Subscript_Value,3);
        Symbol year2016 = TestUtilities.addSymbolInLines(parsedTable,"YEAR 2016",SymbolType.Subscript_Value,3);

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

        Symbol dbScenario = TestUtilities.createSubscript(dbTable,"SCENARIO","SCENARIO_1","SCENARIO_2");
        Symbol dbTypeOfEnergy = TestUtilities.createSubscript(dbTable,"TYPE_OF_ENERGY","CARBON","SUN","WIND");

        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbVar.addIndexLine(List.of(dbTypeOfEnergy,dbScenario));
        dbTable.addSymbol(dbVar);


        Symbol parsedVar = TestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1,2);
        Symbol scenario = TestUtilities.createSubscript(parsedTable,"SCENARIO","SCENARIO_1","SCENARIO_2");
        Symbol typeOfEnergy = TestUtilities.createSubscript(parsedTable,"TYPE_OF_ENERGY","CARBON","SUN","WIND");

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

        Symbol dbScenario = TestUtilities.createSubscript(dbTable,"SCENARIO","SCENARIO_1","SCENARIO_2");
        Symbol dbTypeOfEnergy = TestUtilities.createSubscript(dbTable,"TYPE_OF_ENERGY","CARBON","SUN","wIND");

        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbVar.addIndexLine(List.of(dbTypeOfEnergy,dbScenario));
        dbTable.addSymbol(dbVar);


        Symbol parsedVar = TestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1,2);
        Symbol scenario = TestUtilities.createSubscript(parsedTable,"SCENARIO","SCENARIO_1","SCENARIO_2");
        TestUtilities.createSubscript(parsedTable,"TYPE_OF_ENERGY","CARBON","SUN","WIND");

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

        Symbol dbScenario = TestUtilities.createSubscript(dbTable,"SCENARIO","SCENARIO_1","SCENARIO_2");


        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbVar.addIndexLine(List.of(dbScenario));
        dbTable.addSymbol(dbVar);


        Symbol parsedVar = TestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1,2);
        Symbol scenarioCopy = TestUtilities.createSubscript(parsedTable,"SCENARIO_COPY","SCENARIO_1","SCENARIO_2");

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

        Symbol dbScenario = TestUtilities.createSubscript(dbTable,"SCENARIO","SCENARIO_1","SCENARIO_2");


        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbVar.addIndexLine(List.of(dbScenario));
        dbTable.addSymbol(dbVar);


        Symbol parsedVar = TestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1);
        Symbol scenario = TestUtilities.createSubscript(parsedTable,"SCENARIO","SCENARIO_1","SCENARIO_3","SCENARIO_2");

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

        Symbol dbScenario = TestUtilities.createSubscript(dbTable,"SCENARIO","SCENARIO_1","SCENARIO_2");


        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbVar.addIndexLine(List.of(dbScenario));
        dbTable.addSymbol(dbVar);


        Symbol parsedVar = TestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1);
        Symbol scenario = TestUtilities.createSubscript(parsedTable,"SCENARIO","SCENARIO_2");

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

        Symbol dbScenario = TestUtilities.createSubscript(dbTable,"SCENARIO","SCENARIO_1","SCENARIO_2");
        Symbol dbEnergyType = TestUtilities.createSubscript(parsedTable,"ENERGY_TYPE","SOLAR","CARBON");

        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbVar.addIndexLine(List.of(dbScenario,dbEnergyType));
        dbTable.addSymbol(dbVar);


        Symbol parsedVar = TestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1,2);
        Symbol fooSubscript = TestUtilities.createSubscript(parsedTable,"FOO","VAR","TEST");


        parsedVar.addIndexLine(List.of(fooSubscript));

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(2,context.getIssues().size());

        Set<Integer> foundInLines = new HashSet<>();

        for(Issue issue: context.getIssues()){
            assertEquals("The symbol is indexed by values that aren't stored in the database.'\n" +
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

        Symbol parsedVar = TestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1,2);
        Symbol fooSubscript = TestUtilities.createSubscript(parsedTable,"FOO","VAR","TEST");


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

        Symbol dbSubscript = TestUtilities.createSubscript(dbTable,"DBTest","VAR","TEST");
        dbVar.addIndexLine(List.of(dbSubscript));


        TestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1,2);

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


        TestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1,2);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(0,context.getIssues().size());
    }

    @Test
    public void testAllowsASubsetOfIndexes(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol dbScenario = TestUtilities.createSubscript(dbTable,"SCENARIO","SCENARIO_1","SCENARIO_2");
        Symbol dbEnergyType = TestUtilities.createSubscript(dbTable,"ENERGY_TYPE","SOLAR","WIND");
        Symbol dbExtraSubscript = TestUtilities.createSubscript(dbTable,"EXTRA","VALUE 1", "VALUE 2","VALUE 3");

        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbVar.addIndexLine(List.of(dbEnergyType,dbScenario,dbExtraSubscript));
        dbTable.addSymbol(dbVar);


        Symbol parsedVar = TestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1);
        Symbol fileScenario = TestUtilities.createSubscript(parsedTable,"SCENARIO","SCENARIO_2");
        Symbol fileEnergyType = TestUtilities.createSubscript(dbTable,"FILE_ENERGY_TYPE","SOLAR","WIND");


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

        Symbol parsedVar = TestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1);
        Symbol parsedSubscript = TestUtilities.createSubscript(parsedTable,"EMPTY_SUBSCRIPT","SCENARIO_2");

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
        Symbol dbSubscript = TestUtilities.createSubscript(dbTable,"EMPTY_SUBSCRIPT","VALUE");
        dbVar.addIndexLine(List.of(dbSubscript));
        dbTable.addSymbol(dbVar);

        Symbol parsedVar = TestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1);

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

        Symbol dbScenario = TestUtilities.createSubscript(dbTable,"    SCENARIO    ","    SCENARIO_1    ","    SCENARIO_2    ");

        Symbol dbVar = new Symbol("var",SymbolType.Variable);
        dbVar.addIndexLine(List.of(dbScenario));
        dbTable.addSymbol(dbVar);


        Symbol parsedVar = TestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1);
        Symbol fileScenario = TestUtilities.createSubscript(parsedTable,"  SCENARIO  ","  SCENARIO_1  ");

        parsedVar.addIndexLine(List.of(fileScenario));

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(0,context.getIssues().size());

    }



    @Test
    public void testDbTableNull(){

        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = TestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1);
        Symbol fileSubscript = TestUtilities.createSubscript(parsedTable,"SUBSCRIPT","S1");
        parsedVar.addIndexLine(List.of(fileSubscript));


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,null);
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(0,context.getIssues().size());


    }

    @Test
    public void testSymbolNotFoundInDictionary(){
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = TestUtilities.addSymbolInLines(parsedTable,"var",SymbolType.Variable,1);
        Symbol fileSubscript = TestUtilities.createSubscript(parsedTable,"SUBSCRIPT","S1");
        parsedVar.addIndexLine(List.of(fileSubscript));


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,new SymbolTable());
        DictionaryIndexMismatchCheck check = new DictionaryIndexMismatchCheck();
        check.scan(context);

        assertEquals(0,context.getIssues().size());
    }




}
