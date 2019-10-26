package es.uva.medeas.rules;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static es.uva.medeas.rules.TestUtilities.*;

import static org.junit.Assert.*;

public class TestRawSymbolTableVisitor {

    @Test
    public void testMacroArgumentsDontOverrideOtherSymbols(){
        String program = "testArgumentBefore = 5 ~~ |\n" +
                ":MACRO: myMacro(testArgumentBefore,testArgumentAfter)\n" +
                "myMacro = INTEG((testArgumentBefore - 3)/testArgumentAfter, testArgumentBefore) ~~|\n" +
                ":END OF MACRO:\n"+
                "testArgumentAfter= 10~|\n";


        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol argumentBefore = table.getSymbol("testArgumentBefore");
        assertSymbolLine(argumentBefore,1);

        Symbol argumentAfter = table.getSymbol("testArgumentAfter");
        assertSymbolLine(argumentAfter,5);
    }

    @Test
    public void testMacroValuesDontOverrideOtherSymbols(){
        String program = "testValueBefore = 5 ~~ |\n" +
                ":MACRO: myMacro(arg1, otherArg)\n" +
                "myMacro = INTEG((arg1 - 3)/otherArg, arg1) ~~|\n" +
                "testValueBefore = 9 ~~ |\n" +
                "testValueAfter = 99 ~~ |\n" +
                ":END OF MACRO:\n"+
                "testValueAfter= 10~|\n";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol valueBefore = table.getSymbol("testValueBefore");
        assertSymbolLine(valueBefore,1);

        Symbol valueAfter = table.getSymbol("testValueAfter");
        assertSymbolLine(valueAfter,7);

    }

    @Test
    public void testSubscript(){
        String program = "\nname: OPTION1,\n" +
                " OPTION2 -> mappedSubscript ~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol subscriptName = table.getSymbol("name");
        Symbol option1 = table.getSymbol("OPTION1");
        Symbol option2 = table.getSymbol("OPTION2");

        assertSymbol(subscriptName, SymbolType.SUBSCRIPT_NAME,2,NO_DEPENDENCIES);
        assertSymbol(option1,SymbolType.SUBSCRIPT_VALUE,2,NO_DEPENDENCIES);
        assertSymbol(option2,SymbolType.SUBSCRIPT_VALUE,3,NO_DEPENDENCIES);

    }


    @Test
    public void testSubscriptNameIsntOverridden() {
        String program = "subscriptBefore[country] = 7 ~~|\n" +
                "country : MEXICO, USA,CANADA ->  otherCountries~~|\n" +
                "subscriptAfter[country] = 6 ~~|\n";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol country = table.getSymbol("country");
        assertSymbolType(country,SymbolType.SUBSCRIPT_NAME);

    }


    @Test
    public void testSubscriptCopy(){
        String program =  "subscriptBefore[copy] = 7 ~~|\n" +
                "original: OPTION1 ~~|\n" +
                "copy\n <-> original  ~~|\n" +
                "subscriptAfter[copy] = 6 ~~|\n";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol copy = table.getSymbol("copy");
        assertSymbolType(copy,SymbolType.SUBSCRIPT_NAME);
        assertSymbolLine(copy,3);
    }


    @Test
    public void testSubscriptSequence(){
        String program = "age: (AGE 15-AGE 45)~~|\n";
        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol age15 = table.getSymbol("AGE 15");
        Symbol age45 = table.getSymbol("AGE 45");

        assertSymbol(age15,SymbolType.SUBSCRIPT_VALUE,1,NO_DEPENDENCIES);
        assertSymbol(age45,SymbolType.SUBSCRIPT_VALUE,1,NO_DEPENDENCIES);
    }

    @Test
    public void testDirectAndXLSSubscript(){
        String program = "xlsSubscript: GET XLS SUBSCRIPT('?subscriptedInputs', 'subscripts' , 'c1', 'f1', '' )~~|\n" +
                "directSubscript: GET DIRECT SUBSCRIPT('?subscriptedInputs', 'subscripts' , 'c1', 'd1', '' )~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol xlsSubscript = table.getSymbol("xlsSubscript");
        assertSymbolType(xlsSubscript,SymbolType.SUBSCRIPT_NAME);

        Symbol directSubscript = table.getSymbol("directSubscript");
        assertSymbolType(directSubscript,SymbolType.SUBSCRIPT_NAME);
    }


    @Test
    public void testLookup(){
        String program = "test lookup call before = myLookup(3)~~|\n" +
                "myLookup([(0,0)-(10,10)],(0,0),(1,1),(2,0.5),(3,1),(4,0))~~|\n" +
                "test lookup call after = myLookup(5)~~|\n";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol myLookup = table.getSymbol("myLookup");
        assertSymbol(myLookup,SymbolType.LOOKUP,2,NO_DEPENDENCIES);

    }

    @Test
    public void testLookupOtherFormat() {
        String program = "myLookup(\n" +
                "0,0.2,0.4,0.6,0.8,1,\n" +
                "0,0.2,0.4,0.6,0.8,1)~~|";
        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol myLookup = table.getSymbol("myLookup");
        assertSymbolType(myLookup,SymbolType.LOOKUP);
    }

    @Test
    public void testDirectAndXLSLookup(){
        String program = "testXLSLookup[City]( GET XLS LOOKUPS('subscriptedInputs.xlsx', 'ssData' , 'a', 'b3' ))~~|\n" +
                "testDirectLookup[City]( GET DIRECT LOOKUPS('subscriptedInputs.xlsx', 'ssData' , 'a', 'b3' )) ~~|\n";


        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol xlsLookup = table.getSymbol("testXLSLookup");
        assertSymbolType(xlsLookup,SymbolType.LOOKUP);

        Symbol directLookup = table.getSymbol("testDirectLookup");
        assertSymbolType(directLookup,SymbolType.LOOKUP);
    }

    @Test
    public void testMacro(){
        String program = "\n\n:MACRO: myMacro(input,timeVar)\n" +
                "myMacro = INTEG((input - 3)/timeVar, input)\n" +
                "~~|\n"+
                ":END OF MACRO:";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol myMacro = table.getSymbol("myMacro");
        assertSymbol(myMacro,SymbolType.FUNCTION,3,NO_DEPENDENCIES);

        assertFalse(table.hasSymbol("input1"));
        assertFalse(table.hasSymbol("timeVar"));
    }




    @Test
    public void testUnchangeableConstant(){
        String program = "\n\n\nPI== 3.14159 ~~|";
        SymbolTable table = getRAWSymbolTableFromString(program);


        Symbol pi = table.getSymbol("PI");
        assertSymbol(pi,SymbolType.CONSTANT,4,NO_DEPENDENCIES);
    }

    @Test
    public void testStringConstant(){
        String program = "\n\nfilename:IS: 'simpleInputs.xls'~~|";
        SymbolTable table = getRAWSymbolTableFromString(program);


        Symbol filename = table.getSymbol("filename");
        assertSymbol(filename,SymbolType.CONSTANT,3,NO_DEPENDENCIES);
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

        assertSymbolLine(result,1);
        assertSymbolLine(pipeline,2);
    }

    @Test
    public void testEquationEqualsNADoesntHaveDependencies(){
        String program = "testKeywordNA = :NA:  ~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol na = table.getSymbol("testKeywordNA");
        assertNoDependencies(na);
    }

    @Test
    public void testExprInsideParenthesis(){
        String program = "variable = ((((someConstant)))/Time)~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");
        assertEquals(getSymbols(table,"someConstant","Time"), variable.getDependencies());

    }


    @Test
    public void testExprWithSign(){
        String program = "negativeVariable = -something~|\n" +
                "positiveVariable = +something~|";

        SymbolTable table = getRAWSymbolTableFromString(program);


        Symbol negativeVariable = table.getSymbol("negativeVariable");
        assertEquals(getSymbols(table,"something"),negativeVariable.getDependencies());

        Symbol positiveVariable = table.getSymbol("positiveVariable");
        assertEquals(getSymbols(table,"something"),positiveVariable.getDependencies());

    }

    @Test
    public void testExprLookupCal(){
        String program = "lookup_inside_expr= WITH LOOKUP ( constVensim,\n" +
                "([(0,0)-(4,2)],(0,0.9),(1,1),(2,1.2),(3,1.5),(4,2) ))~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol lookupExpr = table.getSymbol("lookup_inside_expr");

        assertEquals(getSymbols(table,"WITH LOOKUP","constVensim"),lookupExpr.getDependencies());
    }

    @Test
    public void testExprOperatorsDependencies(){
        String program = "testOperators = var1 *  var2 / var3 + (var4)\n" +
                "                  - var5  ^  var6  ~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol testOperators  = table.getSymbol("testOperators");

        assertEquals(getSymbols(table,"var1","var2","var3","var4","var5","var6"),testOperators.getDependencies());


    }

    @Test
    public void testExprComparisonOperatorsDependencies(){
        String program = " testComparisonOperators = IF THEN ELSE (var1 >= var2 :AND: var3 <= var4\n" +
                " :OR: var5 > var6 :OR: var7 < var8 :OR: var9 = var10 :OR: var11<>var12)  ~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol testComparisonOperators  = table.getSymbol("testComparisonOperators");
        Set<Symbol> dependencies = getSymbols(table,"IF THEN ELSE","var1","var2","var3","var4","var5","var6","var7","var8",
                "var9","var10","var11","var12");

        assertEquals(dependencies, testComparisonOperators.getDependencies());
    }

    @Test
    public void testExprCall(){
        String program = "result = MADE UP CALL(arg1,arg2,arg3) ~|\n";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol call = table.getSymbol("MADE UP CALL");
        assertSymbolType(call,SymbolType.FUNCTION);

        Symbol result = table.getSymbol("result");
        assertEquals(getSymbols(table,"MADE UP CALL","arg1","arg2","arg3"),result.getDependencies());
    }


    @Test
    public void testEmptyEquation(){
        String program = "\nemptyEquation ~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol emptyEquation = table.getSymbol("emptyEquation");
        assertNoDependencies(emptyEquation);
        assertSymbolLine(emptyEquation,2);
    }

    @Test
    public void testRealityCheckConditionImplies(){
        String program = "\n\n\n\n\nmyCondition :THE CONDITION: firstVariable[subscript]>100 :IMPLIES: secondVariable<100 ~|";
        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol myCondition = table.getSymbol("myCondition");
        assertSymbol(myCondition,SymbolType.REALITY_CHECK,6,NO_DEPENDENCIES);
    }


    @Test
    public void testRealityCheckTestInput(){
        String program = "\nmyTestInput :TEST INPUT: positiveVariable[subscript] >= 0 ~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol myTestInput = table.getSymbol("myTestInput");
        assertSymbol(myTestInput,SymbolType.REALITY_CHECK,2,NO_DEPENDENCIES);
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

        SymbolTable table = getSymbolTableFromString(program);

        Symbol equation = table.getSymbol("\"equation \\\"inside quotes\"");
        assertNotNull(equation);
        assertSymbolType(equation,SymbolType.CONSTANT);
        assertSymbolLine(equation,4);

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
    public void testNewLineInsideSubscript(){
        String program = "\"materials per new capacity installed - wind offshore\"[\"Electric/electronic components\"\\ \n" +
                "]=5~~|";

        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol subscript = table.getSymbol("\"Electric/electronic components\"");
        assertNotNull(subscript);
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
        // A error in the grammar caused 'e' to be tokenized as a 'e' token (used in scientific notation) rather than as an id.
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
}
