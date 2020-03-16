package es.uva.medeas.parser.visitors;

import es.uva.medeas.parser.ModelParser;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static es.uva.medeas.testutilities.TestUtilities.*;

import static org.junit.Assert.*;

public class TestRawSymbolTableVisitor {




    @Test
    public void testSubscript(){
        String program = "\nname: OPTION1,\n" +
                " OPTION2 -> mappedSubscript ~ units ~ comment|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol subscriptName = table.getSymbol("name");
        Symbol option1 = table.getSymbol("OPTION1");
        Symbol option2 = table.getSymbol("OPTION2");


        assertSymbol(subscriptName, SymbolType.Subscript,2, new HashSet<>(Arrays.asList(option1,option2)));
        assertSymbol(option1,SymbolType.Subscript_Value,2,NO_DEPENDENCIES);
        assertSymbol(option2,SymbolType.Subscript_Value,3,NO_DEPENDENCIES);
        assertEquals("units",subscriptName.getUnits());
        assertEquals("comment",subscriptName.getComment());
        assertTrue(subscriptName.getIndexes().isEmpty());
        assertTrue(option1.getIndexes().isEmpty());

    }


    @Test
    public void testSubscriptMappingDoesntOverrideOriginal(){
        String program = "subscriptBefore: ONE, TWO -> mappedSubscript~~|\n" +
                "mappedSubscript: ONE, TWO~~|\n" +
                "subscriptAfter: ONE, TWO -> mappedSubscript~~|\n";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol mappedSubscript = table.getSymbol("mappedSubscript");

        assertSymbolDefinedOnlyIn(2,mappedSubscript);


    }

    @Test
    public void testSubscriptNameIsntOverridden() {
        String program = "subscriptBefore[country] = 7 ~~|\n" +
                "country : MEXICO, USA,CANADA ->  otherCountries~~|\n" +
                "subscriptAfter[country] = 6 ~~|\n";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol country = table.getSymbol("country");
        assertSymbolType(country,SymbolType.Subscript);

    }


    @Test
    public void testSubscriptCopy(){
        String program =  "subscriptBefore[copy] = 7 ~~|\n" +
                "original: OPTION1 ~~|\n" +
                "copy\n <-> original  ~ units ~ comment|\n" +
                "subscriptAfter[copy] = 6 ~~|\n";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol original = table.getSymbol("original");

        Symbol copy = table.getSymbol("copy");
        assertSymbolType(copy,SymbolType.Subscript);
        assertSymbolDefinedOnlyIn(3,copy);
        assertEquals("units",copy.getUnits());
        assertEquals("comment",copy.getComment());
        assertTrue(copy.getIndexes().isEmpty());
        assertEquals(copy.getDependencies(),original.getDependencies());
        assertFalse(copy.getDependencies().isEmpty());
    }

    @Test
    public void testSubscriptCopyWhenOriginalNotDefinedYet(){
        String program = "copy\n <-> original  ~ units ~ comment|\n" +
                "original: OPTION1, OPTION2 ~~|\n";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol copy = table.getSymbol("copy");
        Symbol option1 = table.getSymbol("OPTION1");
        Symbol option2 = table.getSymbol("OPTION2");

        assertEquals(new HashSet<>(Arrays.asList(option1,option2)),copy.getDependencies());


    }


    @Test
    public void testSubscriptSequence(){
        String program = "age: (AGE_15-AGE_45)~~|\n";
        SymbolTable table = getRAWSymbolTableFromString(program);


        Symbol subscript = table.getSymbol("age");

        for(int i=15;i<=45;i++){
            Symbol value = table.getSymbol("AGE_"+i);
            assertSymbolType(value,SymbolType.Subscript_Value);
            assertSymbolDefinedOnlyIn(1,value);
            assertTrue(subscript.getDependencies().contains(value));
        }

    }

    @Test
    public void testSubscriptSequenceWithSpaces(){
        String program = "age: (     AGE 15-AGE 45      )~~|\n";
        SymbolTable table = getRAWSymbolTableFromString(program);

        for(int i=15;i<=45;i++){
            assertTrue(table.hasSymbol("AGE "+ i));
        }
    }

    @Test
    public void testSubscriptSequenceWithoutSpacesOrUnderscore(){
        String program = "age: (AGE15-AGE45)~~|\n";
        SymbolTable table = getRAWSymbolTableFromString(program);

        for(int i=15;i<=45;i++)
            assertTrue(table.hasSymbol("AGE"+ i));

    }

    @Test
    public void testValuesAndSequencesInTheSameSubscript(){
        String program = "age: ONE_VALUE, (AGE15-AGE45), ANOTHER_VALUE ~~|\n";
        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol subscript = table.getSymbol("age");

        for(int i=15;i<=45;i++)
            assertTrue(table.hasSymbol("AGE"+ i));

        Symbol one_value = table.getSymbol("ONE_VALUE");
        Symbol another_value = table.getSymbol("ANOTHER_VALUE");

        assertTrue(subscript.getDependencies().contains(one_value));
        assertTrue(subscript.getDependencies().contains(another_value));


    }

    @Test
    public void testDirectAndXLSSubscript(){
        String program = "xlsSubscript: GET XLS SUBSCRIPT('?subscriptedInputs', 'subscripts' , 'c1', 'f1', '' )~~|\n" +
                "directSubscript: GET DIRECT SUBSCRIPT('?subscriptedInputs', 'subscripts' , 'c1', 'd1', '' )~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol xlsSubscript = table.getSymbol("xlsSubscript");
        assertSymbolType(xlsSubscript,SymbolType.Subscript);

        Symbol directSubscript = table.getSymbol("directSubscript");
        assertSymbolType(directSubscript,SymbolType.Subscript);
    }


    @Test
    public void testLookup(){
        String program = "test lookup call before = myLookup(3)~~|\n" +
                "myLookup([(0,0)-(10,10)],(0,0),(1,1),(2,0.5),(3,1),(4,0))~units~ comment|\n" +
                "test lookup call after = myLookup(5)~~|\n";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol myLookup = table.getSymbol("myLookup");
        assertSymbol(myLookup,SymbolType.Lookup_Table,2,NO_DEPENDENCIES);
        assertEquals("units",myLookup.getUnits());
        assertEquals("comment",myLookup.getComment());

    }

    @Test
    public void testLookupOtherFormat() {
        String program = "myLookup(\n" +
                "0,0.2,0.4,0.6,0.8,1,\n" +
                "0,0.2,0.4,0.6,0.8,1)~~|";
        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol myLookup = table.getSymbol("myLookup");
        assertSymbolType(myLookup,SymbolType.Lookup_Table);
    }

    @Test
    public void testDirectAndXLSLookup(){
        String program = "testXLSLookup[City]( GET XLS LOOKUPS('subscriptedInputs.xlsx', 'ssData' , 'a', 'b3' ))~~|\n" +
                "testDirectLookup[City]( GET DIRECT LOOKUPS('subscriptedInputs.xlsx', 'ssData' , 'a', 'b3' )) ~~|\n";


        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol xlsLookup = table.getSymbol("testXLSLookup");
        assertSymbolType(xlsLookup,SymbolType.Lookup_Table);

        Symbol directLookup = table.getSymbol("testDirectLookup");
        assertSymbolType(directLookup,SymbolType.Lookup_Table);
    }

    @Test
    public void testMacro(){
        String program = "\n\n:MACRO: myMacro(input,timeVar)\n" +
                "myMacro = INTEG((input - 3)/timeVar, input)~~|\n" +
                "supportValue = 6 * input * timeVar ~~|\n"+
                ":END OF MACRO:";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol myMacro = table.getSymbol("myMacro");
        assertSymbolType(myMacro,SymbolType.Function);
        assertEquals(Arrays.asList(3,4),myMacro.getDefinitionLines());
        assertTrue(table.hasSymbol("supportValue"));
        assertTrue(myMacro.getIndexes().isEmpty());

    }

    @Test
    public void testMacroArgumentsArentConsideredDefinition(){
        String program = "testArgumentBefore = 5 ~~ |\n" +
                ":MACRO: myMacro(testArgumentBefore,testArgumentAfter)\n" +
                "myMacro = INTEG((testArgumentBefore - 3)/testArgumentAfter, testArgumentBefore) ~~|\n" +
                ":END OF MACRO:\n"+
                "testArgumentAfter= 10~~|\n";


        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol argumentBefore = table.getSymbol("testArgumentBefore");
        assertSymbolDefinedOnlyIn(1,argumentBefore);

        Symbol argumentAfter = table.getSymbol("testArgumentAfter");
        assertSymbolDefinedOnlyIn(5,argumentAfter);
    }




    @Test
    public void testUnchangeableConstant(){
        String program = "\n\n\nPI== 3.14159 ~ units ~ comment|";
        SymbolTable table = getRAWSymbolTableFromString(program);


        Symbol pi = table.getSymbol("PI");
        assertSymbol(pi,SymbolType.Constant,4,NO_DEPENDENCIES);
        assertEquals("units",pi.getUnits());
        assertEquals("comment",pi.getComment());
    }

    @Test
    public void testStringConstant(){
        String program = "\n\nfilename:IS: 'simpleInputs.xls'~ units~ comment|";
        SymbolTable table = getRAWSymbolTableFromString(program);


        Symbol filename = table.getSymbol("filename");
        assertSymbol(filename,SymbolType.Constant,3,NO_DEPENDENCIES);

        assertEquals("units",filename.getUnits());
        assertEquals("comment",filename.getComment());
    }


    @Test
    public void testDelayPDependencies(){
        String program = "result= DELAYP( input, delay time: \n" +
                "pipeline) ~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol result = table.getSymbol("result");
        Symbol input = table.getSymbol("input");
        Symbol delayTime = table.getSymbol("delay time");
        Symbol pipeline = table.getSymbol("pipeline");
        Symbol delayP = table.getSymbol("DELAYP");

        assertEquals(createSet(input,delayTime,pipeline,delayP),result.getDependencies());
        assertEquals(createSet(delayP,input,delayTime),pipeline.getDependencies());
        assertNoDependencies(input);
        assertNoDependencies(delayTime);

    }

    @Test
    public void testDelayPGetsPipelineLineCorrectly(){
        String program = "result= DELAYP( input, delay time: \n" +
                "pipeline) ~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol pipeline = table.getSymbol("pipeline");
        Symbol result = table.getSymbol("result");

        assertSymbolDefinedOnlyIn(1,result);
        assertSymbolDefinedOnlyIn(2,pipeline);
    }

    @Test
    public void testEquationEqualsNADoesntHaveDependencies(){
        String program = "testKeywordNA = :NA:  ~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol na = table.getSymbol("testKeywordNA");
        assertNoDependencies(na);
    }

    @Test
    public void testExprInsideParenthesis(){
        String program = "variable = ((((someConstant)))/Time)~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");
        assertEquals(getSymbols(table,"someConstant","Time"), variable.getDependencies());

    }


    @Test
    public void testExprWithSign(){
        String program = "negativeVariable = -something~~|\n" +
                "positiveVariable = +something~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);


        Symbol negativeVariable = table.getSymbol("negativeVariable");
        assertEquals(getSymbols(table,"something"),negativeVariable.getDependencies());

        Symbol positiveVariable = table.getSymbol("positiveVariable");
        assertEquals(getSymbols(table,"something"),positiveVariable.getDependencies());

    }

    @Test
    public void testExprLookupCal(){
        String program = "lookup_inside_expr= WITH Lookup_Table ( constVensim,\n" +
                "([(0,0)-(4,2)],(0,0.9),(1,1),(2,1.2),(3,1.5),(4,2) ))~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol lookupExpr = table.getSymbol("lookup_inside_expr");

        assertEquals(getSymbols(table,"WITH Lookup_Table","constVensim"),lookupExpr.getDependencies());
    }

    @Test
    public void testExprOperatorsDependencies(){
        String program = "testOperators = var1 *  var2 / var3 + (var4)\n" +
                "                  - var5  ^  var6  ~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol testOperators  = table.getSymbol("testOperators");

        assertEquals(getSymbols(table,"var1","var2","var3","var4","var5","var6"),testOperators.getDependencies());


    }

    @Test
    public void testExprComparisonOperatorsDependencies(){
        String program = " testComparisonOperators = IF THEN ELSE (var1 >= var2 :AND: var3 <= var4\n" +
                " :OR: var5 > var6 :OR: var7 < var8 :OR: var9 = var10 :OR: var11<>var12)  ~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol testComparisonOperators  = table.getSymbol("testComparisonOperators");
        Set<Symbol> dependencies = getSymbols(table,"IF THEN ELSE","var1","var2","var3","var4","var5","var6","var7","var8",
                "var9","var10","var11","var12");

        assertEquals(dependencies, testComparisonOperators.getDependencies());
    }

    @Test
    public void testExprCall(){
        String program = "result = MADE UP CALL(arg1,arg2,arg3) ~~|\n";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol call = table.getSymbol("MADE UP CALL");
        assertSymbolType(call,SymbolType.UNDETERMINED_FUNCTION);

        Symbol result = table.getSymbol("result");
        assertEquals(getSymbols(table,"MADE UP CALL","arg1","arg2","arg3"),result.getDependencies());
    }


    @Test
    public void testEmptyEquation(){
        String program = "\nemptyEquation ~ units~ comment|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol emptyEquation = table.getSymbol("emptyEquation");
        assertNoDependencies(emptyEquation);
        assertSymbolDefinedOnlyIn(2,emptyEquation);
        assertEquals("comment",emptyEquation.getComment());
        assertEquals("units",emptyEquation.getUnits());
    }

    @Test
    public void testRealityCheckConditionImplies(){
        String program = "\n\n\n\n\nmyCondition :THE CONDITION: firstVariable[subscript]>100 :IMPLIES: secondVariable<100 ~ units ~ comment|";
        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol myCondition = table.getSymbol("myCondition");
        assertSymbol(myCondition,SymbolType.Reality_Check,6,NO_DEPENDENCIES);
        assertEquals("comment", myCondition.getComment());
        assertEquals("units", myCondition.getUnits());
        assertTrue(myCondition.getIndexes().isEmpty());
    }


    @Test
    public void testRealityCheckTestInput(){
        String program = "\nmyTestInput :TEST INPUT: positiveVariable[subscript] >= 0 ~ units ~ comment|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol myTestInput = table.getSymbol("myTestInput");
        assertSymbol(myTestInput,SymbolType.Reality_Check,2,NO_DEPENDENCIES);
        assertEquals("comment", myTestInput.getComment());
        assertEquals("units", myTestInput.getUnits());
        assertTrue(myTestInput.getIndexes().isEmpty());
    }

    @Test
    public void testDependenciesInvertedOrder() throws IOException {
        SymbolTable table = getRAWSymbolTable("invertedDependencies.mdl");

        Symbol before = table.getSymbol("before");
        Symbol after = table.getSymbol("after");
        Symbol variable = table.getSymbol("variable");

        Set<Symbol> dependencies = createSet(before,after);

        assertFalse(dependencies.contains(null));
        assertEquals(dependencies,variable.getDependencies());

    }

    @Test
    public void testSubscriptsArentConsideredDependencies(){
        String program = "constant[country] = otherConstant[constant] ~~|\n" +
                "mySubscript : MEXICO, USA,CANADA~~|\n";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol constant = table.getSymbol("constant");
        Symbol subscript = table.getSymbol("mySubscript");
        assertFalse(constant.getDependencies().contains(subscript));

    }


    @Test
    public void testRepeatedEquationUsingExcept(){
        String program = "variable[subscript] :EXCEPT: [value1,value2] = firstSymbol~~|\n"+
                "variable[subscript] :EXCEPT: [value3,value4] = secondSymbol~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");
        assertEquals(getSymbols(table,"firstSymbol","secondSymbol"),variable.getDependencies());
    }

    @Test
    public void testRepeatedEquationSubscriptNotation(){
        String program = "variable[value1] = firstSymbol~~|\n"+
                "variable[value2] = secondSymbol~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);


        Symbol variable = table.getSymbol("variable");
        assertEquals(getSymbols(table,"firstSymbol","secondSymbol"),variable.getDependencies());
    }


    @Test
    public void testEquationInsideQuotes(){
        String program = "\n\n\n\"equation \\\"inside quotes\"= 3 ~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol equation = table.getSymbol("\"equation \\\"inside quotes\"");
        assertNotNull(equation);
        assertSymbolDefinedOnlyIn(4,equation);

    }

    @Test
    public void testTabbedArray(){
        String program = "initial population[country,blood type] = TABBED ARRAY(\n" +
                "       1       2.0        -3.7        4\n" +
                "       0        6          7          8\n" +
                "       9       -10        11          12) ~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol population = table.getSymbol("initial population");
        Symbol tabbedArrayFunc = table.getSymbol("TABBED ARRAY");
        assertSymbol(population,SymbolType.UNDETERMINED,1,createSet(tabbedArrayFunc));
    }


    @Test
    public void testDependenciesCreateALinkedList() throws IOException {
        SymbolTable table = getRAWSymbolTable("invertedDependencies.mdl");


        Symbol variable = table.getSymbol("variable");
        Symbol extraSymbol = table.getSymbol("extraSymbol");


        List<Symbol> before = variable.getDependencies().stream().filter(symbol -> "before".equals(symbol.getToken())).collect(Collectors.toList());
        List<Symbol> expectedExtraSymbol = before.get(0).getDependencies().stream().filter(symbol -> "extraSymbol".equals(symbol.getToken())).collect(Collectors.toList());


        assertSame(extraSymbol,expectedExtraSymbol.get(0));
    }





    @Test
    public void testNewLineBetweenIdAndSubscript(){
        String program = "share energy for material consumption for alt techn vs TFEC\\ \n" +
                "  [scenarios]=Total energy required for total material consumption for alt techn[scenarios]/Real TFEC \\ \n" +
                "      [scenarios]~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol subscript = table.getSymbol("share energy for material consumption for alt techn vs TFEC");
        assertNotNull(subscript);
    }


    @Test
    public void testVariableCalledE(){
        // An error in the grammar caused 'e' to be tokenized as a 'e' token (used in scientific notation) rather than as an id.
        String program = "e = 5~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        assertNotNull(table.getSymbol("e"));
    }

    @Test
    public void testVariableCalledE10(){
        String program = "e10 = 5~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        assertNotNull(table.getSymbol("e10"));
    }

    @Test
    public void testEquationWithoutCommentAndUnit(){
        String program = "var = 5 ~     ~    |";

        SymbolTable table = getRAWSymbolTableFromString(program);
        Symbol var = table.getSymbol("var");

        assertEquals("",var.getComment());
        assertEquals("",var.getUnits());
    }


    @Test
    public void testEquationInsideMacroCommentAndUnit(){
        String program = "\n\n:MACRO: myMacro(input,timeVar)\n" +
                "myMacro = INTEG((input - 3)/timeVar, input)~ main function units ~ main function comment|\n" +
                "supportValue = 6 * input * timeVar ~ support function units ~ support function comment|\n"+
                ":END OF MACRO:";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol myMacro = table.getSymbol("myMacro");
        Symbol supportValue = table.getSymbol("supportValue");

        assertEquals("main function units", myMacro.getUnits());
        assertEquals("main function comment", myMacro.getComment());

        assertEquals("support function units", supportValue.getUnits());
        assertEquals("support function comment", supportValue.getComment());

    }

    @Test
    public void testUnitsAndCommentSymbolDefinedMultipleTimes(){
        String program = "var = 5 ~  units    ~  comment |\n" +
                "var = 5 ~     ~    |";

        SymbolTable table = getRAWSymbolTableFromString(program);
        Symbol var = table.getSymbol("var");

        assertEquals("comment",var.getComment());
        assertEquals("units",var.getUnits());
    }

    @Test
    public void testUnitsAndCommentNotInTheFirstDefinition(){
        String program = "var = 5 ~     ~   |\n" +
                "var = 5 ~     ~    |\n"+
                "var = 5 ~ units    ~  comment   |\n";

        SymbolTable table = getRAWSymbolTableFromString(program);
        Symbol var = table.getSymbol("var");

        assertEquals("comment",var.getComment());
        assertEquals("units",var.getUnits());
    }


    @Test
    public void testMultipleUnitsAndCommentInDifferentDefinitions(){
        String program = "var = 5 ~ units first definition   ~  comment first definition   |\n" +
                "var = 5 ~ units second definition    ~ comment second definition    |\n";

        SymbolTable table = getRAWSymbolTableFromString(program);
        Symbol var = table.getSymbol("var");

        assertEquals("comment second definition",var.getComment());
        assertEquals("units second definition",var.getUnits());
        //TODO No tengo del todo claro lo que tendría que hacer en este caso, si lanzar excepción, ignorar el primer comentario o ignorar el segundo.
    }

    @Test
    public void testDetectSubscriptsOneDefinition(){
        String program = "var[firstSubscript,secondSubscript] = 5 ~     ~   |\n";

        SymbolTable table = getRAWSymbolTableFromString(program);
        Symbol var = table.getSymbol("var");

        List<List<Symbol>> indexes = var.getIndexes();

        assertEquals(2,indexes.size());
        List<Symbol> firstIndex = indexes.get(0);
        List<Symbol> secondIndex = indexes.get(1);


        assertEquals(List.of(table.getSymbol("firstSubscript")),firstIndex);
        assertEquals(List.of(table.getSymbol("secondSubscript")),secondIndex);

    }

    @Test
    public void testDetectSubscriptMultipleDefinition(){
        String program = "var[v11,v21] = 5 ~     ~   |\n"+
                "var[v12,v22] = 5 ~     ~   |\n"+
                "var[v13,v23] = 5 ~     ~   |\n";



        SymbolTable table = getRAWSymbolTableFromString(program);
        Symbol var = table.getSymbol("var");

        List<List<Symbol>> indexes = var.getIndexes();

        assertEquals(2,indexes.size());
        List<Symbol> firstIndex = indexes.get(0);
        List<Symbol> secondIndex = indexes.get(1);

        assertEquals(List.of(table.getSymbol("v11"),table.getSymbol("v12"),table.getSymbol("v13")),
                firstIndex);
        assertEquals(List.of(table.getSymbol("v21"),table.getSymbol("v22"),table.getSymbol("v23")),
                secondIndex);
    }

    @Test
    public void testInconsistentNumberOfIndexes(){
        String program = "var[SCENARIO_1] = 5 ~     ~   |\n"+
                "var[SCENARIO_2,ANOTHER_VALUE] = 3 ~     ~   |\n";

        SymbolTable table = getRAWSymbolTableFromString(program);
        Symbol var = table.getSymbol("var");

        List<List<Symbol>> indexes = var.getIndexes();

        assertEquals(2,indexes.size());
        List<Symbol> firstIndex = indexes.get(0);
        List<Symbol> secondIndex = indexes.get(1);

        assertEquals(List.of(table.getSymbol("SCENARIO_1"),table.getSymbol("SCENARIO_2")), firstIndex);
        assertEquals(List.of(table.getSymbol("ANOTHER_VALUE")), secondIndex);
    }


    @Test
    public void testIndexesInEquation(){
        String program = "withIndexes[FIRST_INDEX,SECOND_INDEX] = 5 ~     ~   |\n"+
                "withoutIndex = 3 ~     ~   |\n";

        SymbolTable table = getRAWSymbolTableFromString(program);
        Symbol withIndexes = table.getSymbol("withIndexes");
        Symbol withoutIndex = table.getSymbol("withoutIndex");

        assertEquals(List.of(
                List.of(table.getSymbol("FIRST_INDEX")),
                List.of(table.getSymbol("SECOND_INDEX")
                )),withIndexes.getIndexes());

        assertTrue(withoutIndex.getIndexes().isEmpty());
    }

    @Test
    public void testIndexesInDataEquation(){
        String program = "withIndexes[FIRST_INDEX,SECOND_INDEX] := 5 ~     ~   |\n"+
                "withoutIndex = 3 ~     ~   |\n";

        SymbolTable table = getRAWSymbolTableFromString(program);
        Symbol withIndexes = table.getSymbol("withIndexes");
        Symbol withoutIndex = table.getSymbol("withoutIndex");

        assertEquals(List.of(
                List.of(table.getSymbol("FIRST_INDEX")),
                List.of(table.getSymbol("SECOND_INDEX")
                )),withIndexes.getIndexes());

        assertTrue(withoutIndex.getIndexes().isEmpty());
    }

    @Test
    public void testIndexesInLookups(){
        String program = "withIndexes[FIRST_INDEX,SECOND_INDEX](\n" +
                "GET XLS LOOKUPS('subscriptedInputs.xlsx', 'ssData' , 'a', 'b3' ))~     ~   | \n"+
                "withoutIndex([(0,0)-(10,10)],(0,0),(1,1),(2,0.5),(3,1),(4,0))~units~ comment|\n";

        SymbolTable table = getRAWSymbolTableFromString(program);
        Symbol withIndexes = table.getSymbol("withIndexes");
        Symbol withoutIndex = table.getSymbol("withoutIndex");

        assertEquals(List.of(
                List.of(table.getSymbol("FIRST_INDEX")),
                List.of(table.getSymbol("SECOND_INDEX")
                )),withIndexes.getIndexes());

        assertTrue(withoutIndex.getIndexes().isEmpty());

    }



    @Test
    public void testIndexesInStringAssign(){
        String program = "withIndexes[FIRST_INDEX,SECOND_INDEX]:IS: 'string' ~~|\n" +
                " withoutIndex :IS: 'string' ~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);
        Symbol withIndexes = table.getSymbol("withIndexes");
        Symbol withoutIndex = table.getSymbol("withoutIndex");

        assertEquals(List.of(
                List.of(table.getSymbol("FIRST_INDEX")),
                List.of(table.getSymbol("SECOND_INDEX")
                )),withIndexes.getIndexes());

        assertTrue(withoutIndex.getIndexes().isEmpty());
    }

    @Test
    public void testIndexesInUnchangeableConstant(){
        String program = "withIndexes[FIRST_INDEX,SECOND_INDEX] == 5 ~     ~   |\n"+
                "withoutIndex == 3 ~     ~   |\n";

        SymbolTable table = getRAWSymbolTableFromString(program);
        Symbol withIndexes = table.getSymbol("withIndexes");
        Symbol withoutIndex = table.getSymbol("withoutIndex");

        assertEquals(List.of(
                List.of(table.getSymbol("FIRST_INDEX")),
                List.of(table.getSymbol("SECOND_INDEX")
                )),withIndexes.getIndexes());

        assertTrue(withoutIndex.getIndexes().isEmpty());
    }

    @Test
    public void testIndexesInRealityCheckConstraint(){
        String program = "withIndexes[FIRST_INDEX,SECOND_INDEX] :THE CONDITION: var=3 :IMPLIES: positiveVar>0 ~ ~ |"+
                "withoutIndex :THE CONDITION: var=3 :IMPLIES: positiveVar>0 ~ ~ |";


        SymbolTable table = getRAWSymbolTableFromString(program);
        Symbol withIndexes = table.getSymbol("withIndexes");
        Symbol withoutIndex = table.getSymbol("withoutIndex");

        assertEquals(List.of(
                List.of(table.getSymbol("FIRST_INDEX")),
                List.of(table.getSymbol("SECOND_INDEX")
                )),withIndexes.getIndexes());

        assertTrue(withoutIndex.getIndexes().isEmpty());
    }

    @Test
    public void testIndexesInRealityCheckTestInput(){
        String program = "withIndexes[FIRST_INDEX,SECOND_INDEX] :TEST INPUT: positiveVariable[subscript] >= 0 ~ units ~ comment|"+
                "withoutIndex :TEST INPUT: positiveVariable[subscript] >= 0 ~~ |";

        SymbolTable table = getRAWSymbolTableFromString(program);
        Symbol withIndexes = table.getSymbol("withIndexes");
        Symbol withoutIndex = table.getSymbol("withoutIndex");

        assertEquals(List.of(
                List.of(table.getSymbol("FIRST_INDEX")),
                List.of(table.getSymbol("SECOND_INDEX")
                )),withIndexes.getIndexes());

        assertTrue(withoutIndex.getIndexes().isEmpty());
    }
}
