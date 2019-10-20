package es.uva.medeas.tests;

import es.uva.medeas.parser.*;
import org.junit.Ignore;
import org.junit.Test;



import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.*;
import static es.uva.medeas.tests.TestUtilities.*;


public class TestSymbolGenerator {


    @Test
    public void testSubscript(){
        String program = "name: OPTION1,\n" +
                " OPTION2 -> mappedSubscript ~~|";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol subscriptName = table.getSymbol("name");
        Symbol option1 = table.getSymbol("OPTION1");
        Symbol option2 = table.getSymbol("OPTION2");

        assertSymbol(subscriptName,SymbolType.SUBSCRIPT_NAME,1,NO_DEPENDENCIES);
        assertSymbol(option1,SymbolType.SUBSCRIPT_VALUE,1,NO_DEPENDENCIES);
        assertSymbol(option2,SymbolType.SUBSCRIPT_VALUE,2,NO_DEPENDENCIES);

    }


    @Test
    public void testSubscriptNameIsntOverridden() {
        String program = "subscriptBefore[country] = 7 ~~|\n" +
                "country : MEXICO, USA,CANADA ->  otherCountries~~|\n" +
                "subscriptAfter[country] = 6 ~~|\n";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol country = table.getSymbol("country");
        assertSymbolType(country,SymbolType.SUBSCRIPT_NAME);

    }


    @Test
    public void testSubscriptCopy(){
        String program =  "subscriptBefore[copy] = 7 ~~|\n" +
                "original: OPTION1 ~~|\n" +
                "copy <-> original  ~~|\n" +
                "subscriptAfter[copy] = 6 ~~|\n";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol copy = table.getSymbol("copy");
        assertSymbolType(copy,SymbolType.SUBSCRIPT_NAME);
        assertSymbolLine(copy,3);
    }

    @Test
    public void testSubscriptSequence(){
        String program = "age: (AGE 15-AGE 45)~~|\n";
        SymbolTable table = getSymbolTableFromString(program);

        Symbol age15 = table.getSymbol("AGE 15");
        Symbol age45 = table.getSymbol("AGE 45");

       assertSymbol(age15,SymbolType.SUBSCRIPT_VALUE,1,NO_DEPENDENCIES);
       assertSymbol(age45,SymbolType.SUBSCRIPT_VALUE,1,NO_DEPENDENCIES);
    }

    @Test
    public void testDirectAndXLSSubscript(){
        String program = "xlsSubscript: GET XLS SUBSCRIPT('?subscriptedInputs', 'subscripts' , 'c1', 'f1', '' )~~|\n" +
                "directSubscript: GET DIRECT SUBSCRIPT('?subscriptedInputs', 'subscripts' , 'c1', 'd1', '' )~~|";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol xlsSubscript = table.getSymbol("xlsSubscript");
        assertSymbolType(xlsSubscript,SymbolType.SUBSCRIPT_NAME);

        Symbol directSubscript = table.getSymbol("directSubscript");
        assertSymbolType(directSubscript,SymbolType.SUBSCRIPT_NAME);
    }
    
    //TODO Test linked list works
    @Test
    public void testLookupNew(){
        String program = "test lookup call before = myLookup(3)~~|\n" +
                "myLookup([(0,0)-(10,10)],(0,0),(1,1),(2,0.5),(3,1),(4,0))~~|\n" +
                "test lookup call after = myLookup(5)~~|\n";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol myLookup = table.getSymbol("myLookup");
        assertSymbol(myLookup,SymbolType.LOOKUP,2,NO_DEPENDENCIES);

    }

    @Test
    public void testLookupOtherFormat() {
        String program = "myLookup(\n" +
                         "0,0.2,0.4,0.6,0.8,1,\n" +
                         "0,0.2,0.4,0.6,0.8,1)~~|";
        SymbolTable table = getSymbolTableFromString(program);

        Symbol myLookup = table.getSymbol("myLookup");
        assertSymbolType(myLookup,SymbolType.LOOKUP);
    }

    @Test
    public void testDirectAndXLSLookup(){
        String program = "testXLSLookup[City]( GET XLS LOOKUPS('subscriptedInputs.xlsx', 'ssData' , 'a', 'b3' ))~~|\n" +
                "testDirectLookup[City]( GET DIRECT LOOKUPS('subscriptedInputs.xlsx', 'ssData' , 'a', 'b3' )) ~~|\n";


        SymbolTable table = getSymbolTableFromString(program);

        Symbol xlsLookup = table.getSymbol("testXLSLookup");
        assertSymbolType(xlsLookup,SymbolType.LOOKUP);

        Symbol directLookup = table.getSymbol("testDirectLookup");
        assertSymbolType(directLookup,SymbolType.LOOKUP);
    }

    @Test
    public void testEquationDirectLookup(){
        String program = "myLookup= GET DIRECT LOOKUPS('simpleInputs.xlsx', 'data' , '1', 'e2' ) ~~|\n";
        SymbolTable table = getSymbolTableFromString(program);

        Symbol myLookup = table.getSymbol("myLookup");
        assertSymbolType(myLookup,SymbolType.LOOKUP);
    }

    @Test
    public void testEquationXLSLookup(){
        String program = "myLookup= GET XLS LOOKUPS('simpleInputs.xlsx', 'data' , 'a', 'b2' ) ~~|\n";
        SymbolTable table = getSymbolTableFromString(program);

        Symbol myLookup = table.getSymbol("myLookup");
        assertSymbolType(myLookup,SymbolType.LOOKUP);
    }

    //TODO Testear que pasa si se define la misma ecuación con dos subscripts diferentes  si en uno es variable y en otro constante
    // (entiendo que pillará ambos como dependencias y al final acabará siendo variable?)

    @Test
    public void testEquationInsideQuotes(){
        String program = "\"equation inside quotes\"= 3 ~~|";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol equation = table.getSymbol("\"equation inside quotes\"");
        assertNotNull(equation);
        assertSymbolType(equation,SymbolType.CONSTANT);


    }
    //TODO test nested quotes "\""
    //TODO test lookup with reference line


    @Test
    public void testEquation() throws IOException{
        SymbolTable table = getSymbolTable("testEquation.mdl");
        Symbol time = table.getSymbol("Time");

        Symbol var = table.getSymbol("var");
        assertSymbol(var,SymbolType.VARIABLE,3,createSet(time));


        Symbol foo = table.getSymbol("foo");
        assertSymbol(foo,SymbolType.VARIABLE,5,createSet(var));

        Symbol constant = table.getSymbol("constant");
        assertSymbol(constant,SymbolType.CONSTANT,7,NO_DEPENDENCIES);

        Symbol something = table.getSymbol("something");
        assertSymbol(something,SymbolType.VARIABLE,9,createSet(foo));

        Symbol integ = table.getSymbol("INTEG");
        assertUndefinedSymbol(integ,SymbolType.FUNCTION);

        Symbol z = table.getSymbol("Z");
        assertSymbol(z,SymbolType.VARIABLE,11,createSet(var,foo,constant,something,integ));

        Symbol quotedEquation = table.getSymbol("\"equation inside quotes\"");
        assertSymbol(quotedEquation,SymbolType.VARIABLE,13,createSet(var,foo));




    }

    @Test
    public void testMacro(){
        String program = ":MACRO: myMacro(input,timeVar)\n" +
                "myMacro = INTEG((input - 3)/timeVar, input)\n" +
                "~~|\n"+
                ":END OF MACRO:";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol myMacro = table.getSymbol("myMacro");
        assertSymbol(myMacro,SymbolType.FUNCTION,1,NO_DEPENDENCIES);

        assertNull(table.getSymbol("input1"));
        assertNull(table.getSymbol("timeVar"));
    }

    @Test
    public void testMacroArgumentsDontOverrideOtherSymbols(){
        String program = "testArgumentBefore = 5 ~~ |\n" +
                ":MACRO: myMacro(testArgumentBefore,testArgumentAfter)\n" +
                "myMacro = INTEG((testArgumentBefore - 3)/testArgumentAfter, testArgumentBefore) ~~|\n" +
                ":END OF MACRO:\n"+
                "testArgumentAfter= 10~|\n";


        SymbolTable table = getSymbolTableFromString(program);

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

        SymbolTable table = getSymbolTableFromString(program);

        Symbol valueBefore = table.getSymbol("testValueBefore");
        assertSymbolLine(valueBefore,1);

        Symbol valueAfter = table.getSymbol("testValueAfter");
        assertSymbolLine(valueAfter,7);

    }


    //TODO test lines are found correctly for every type

    @Test
    public void testUnchangeableConstant(){
        String program = "PI== 3.14159 ~~|";
        SymbolTable table = getSymbolTableFromString(program);


        Symbol pi = table.getSymbol("PI");
        assertSymbol(pi,SymbolType.CONSTANT,1,NO_DEPENDENCIES);
    }

    @Test
    public void testStringConstant(){
        String program = "filename:IS: 'simpleInputs.xls'~~|";
        SymbolTable table = getSymbolTableFromString(program);


        Symbol filename = table.getSymbol("filename");
        assertSymbol(filename,SymbolType.CONSTANT,1,NO_DEPENDENCIES);
    }

    @Test
    public void testRealityChecks() throws  IOException{
        SymbolTable table = getSymbolTable("testRealityCheck.mdl");

        Symbol rc = table.getSymbol("RC no investment");
        assertSymbol(rc,SymbolType.REALITY_CHECK,2,NO_DEPENDENCIES);

        Symbol ti = table.getSymbol("TI no capital");
        assertSymbol(ti,SymbolType.REALITY_CHECK , 8,NO_DEPENDENCIES);

    }




    @Test
    public void testMedeas1() throws IOException{
        SymbolTable table =getSymbolTable("medeas1.mdl");
    }


    @Ignore
    @Test
    public void testMedeas2() throws IOException{
        getSymbolTable("medeas2.mdl");

    }

    @Ignore
    @Test
    public void testMedeasEU() throws IOException{
        getSymbolTable("medeasEU.mdl");
    }


    @Test
    public void testDelayP() throws IOException{
        SymbolTable table = getSymbolTable("testDelayP.mdl");

        Symbol receivingP = table.getSymbol("receiving P");
        Symbol delayPipeline = table.getSymbol("DELAYP pipeline");
        Symbol delayP = table.getSymbol("DELAYP");
        Symbol shippingTime = table.getSymbol("shipping time");
        Symbol shipping = table.getSymbol("shipping");

        assertSymbol(delayPipeline,SymbolType.VARIABLE,3,createSet(delayP,shipping,shippingTime));
        assertSymbol(receivingP, SymbolType.VARIABLE,2,createSet(delayP,delayPipeline, shipping,shippingTime));

    }

    @Test
    public void testTabbedArray() throws  IOException{
        SymbolTable table = getSymbolTable("testTabbedArray.mdl");

        Symbol population = table.getSymbol("initial population");
        Symbol tabbedArrayFunc = table.getSymbol("TABBED ARRAY");
        assertSymbol(population,SymbolType.CONSTANT,5,createSet(tabbedArrayFunc));
    }






    @Test
    public void testExpr() throws IOException{
        SymbolTable table = getSymbolTable("testExpr.mdl");

        Symbol constant = table.getSymbol("constVensim");
        Symbol otherConstant = table.getSymbol("otherConstant");
        Symbol testCall = table.getSymbol("testCall");
        Symbol ifThenElse = table.getSymbol("IF THEN ELSE");
        Symbol madeUpCall = table.getSymbol("MADE UP CALL");

        Symbol testParenthesis = table.getSymbol("expr_inside_parenthesis");
        Symbol testWildCard = table.getSymbol("testWildcard");
        Symbol testKeyWordWithoutExpression = table.getSymbol("testKeywordNA");
        Symbol testKeywordExpr = table.getSymbol("testKeywordExpr");
        Symbol testOperations = table.getSymbol("testOperations");

        Symbol testComparisonOperators = table.getSymbol("testComparisonOperators");
        Symbol testNegative = table.getSymbol("testNegative");
        Symbol testEvenMoreComparisonOperators = table.getSymbol("testEvenMoreComparisonOperators");


        assertSymbol(testCall,SymbolType.CONSTANT,29,createSet(constant,madeUpCall));
        assertSymbol(testParenthesis,SymbolType.CONSTANT,9,createSet(constant));
        assertSymbol(testWildCard,SymbolType.REALITY_CHECK,11,NO_DEPENDENCIES);
        assertSymbol(testKeyWordWithoutExpression,SymbolType.CONSTANT,15,NO_DEPENDENCIES);
        assertSymbol(testKeywordExpr,SymbolType.CONSTANT,17,createSet(constant,testParenthesis,testKeyWordWithoutExpression,ifThenElse));
        assertUndefinedSymbol(madeUpCall,SymbolType.FUNCTION);


        assertSymbol(testOperations,SymbolType.CONSTANT,32,createSet(constant,testParenthesis,testKeyWordWithoutExpression,testKeywordExpr,testCall,otherConstant));
        assertSymbol(testComparisonOperators,SymbolType.CONSTANT,35,createSet(constant,testParenthesis,otherConstant,testKeyWordWithoutExpression,testOperations,testCall,testNegative,ifThenElse));

        assertSymbol(testEvenMoreComparisonOperators, SymbolType.CONSTANT,38,createSet(constant,otherConstant,testNegative,testParenthesis,ifThenElse));

        assertSymbol(testNegative,SymbolType.CONSTANT,41,createSet(otherConstant));
    }


    @Test
    public void testDataEquation() throws IOException{
        SymbolTable table = getSymbolTable("testDataEquation.mdl");

        Symbol emptyEquation = table.getSymbol("emptyEquation");
        assertSymbol(emptyEquation,SymbolType.CONSTANT,13,NO_DEPENDENCIES);

        Symbol emptyWithKeyword = table.getSymbol("emptyEquationWithInterpolate");
        assertSymbol(emptyWithKeyword,SymbolType.CONSTANT,9,NO_DEPENDENCIES);

        Symbol dependency = table.getSymbol("planned production raw");
        Symbol equationWithKeyword = table.getSymbol("production smooth");
        assertSymbol(equationWithKeyword,SymbolType.CONSTANT,3,createSet(dependency));
    }


    @Test
    public void testTimeVariableIsCreated(){
        SymbolTable table = getSymbolTableFromString("");

        Symbol time = table.getSymbol("Time");

        assertUndefinedSymbol(time,SymbolType.VARIABLE);

    }

    @Test
    public void testDependenciesInvertedOrder() throws IOException {
        SymbolTable table = getSymbolTable("invertedDependencies.mdl");

        Symbol before = table.getSymbol("before");
        Symbol after = table.getSymbol("after");
        Symbol variable = table.getSymbol("variable");

        Set<Symbol> dependencies = createSet(before,after);

        assertFalse(dependencies.contains(null));
        assertEquals(dependencies,variable.getDependencies());

    }

    @Test
    public void testTypeInferenceInConstantsAndVariables() throws IOException{
        SymbolTable table = getSymbolTable("testTypeInference1.mdl");

        Symbol directConstant = table.getSymbol("directConstant");
        Symbol indirectConstant = table.getSymbol("indirectConstant");
        Symbol directVariable = table.getSymbol("directVariable");
        Symbol indirectVariable = table.getSymbol("indirectVariable");

        assertEquals(SymbolType.CONSTANT,directConstant.getType());
        assertEquals(SymbolType.CONSTANT,indirectConstant.getType());
        assertEquals(SymbolType.VARIABLE,directVariable.getType());
        assertEquals(SymbolType.VARIABLE,indirectVariable.getType());


    }

    @Test
    public void testFunctionDelayPIsntPure(){
        String program = "variable = DELAYP( 5, 6 : pipeline)~~|";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.VARIABLE, variable.getType());


    }

    @Test
    public void testFunctionDelay1IsntPure(){
       String program =  "variable =DELAY1(3, 4)~~|";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.VARIABLE, variable.getType());

    }


    @Test
    public void testFunctionDelay1WithInitialValueIsntPure(){
        String program =  "variable = DELAY1I(3, 4, 6)~~|";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.VARIABLE, variable.getType());

    }

    @Test
    public void testFunctionDelay3IsntPure(){
        String program =  "variable = DELAY3(3, 4)~~|";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.VARIABLE, variable.getType());

    }

    @Test
    public void testFunctionDelay3WithInitialValueIsntPure(){
        String program =  "variable = DELAY3I(3, 4, 6)~~|";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.VARIABLE, variable.getType());

    }

    @Test
    public void testFunctionForecastIsntPure(){
        String program =  "variable = FORECAST(3, 4, 5)~~|";


        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.VARIABLE, variable.getType());

    }


    @Test
    public void testFunctionSmoothIsntPure(){
        String program =  "variable = SMOOTH(3, 4)~~|";


        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.VARIABLE, variable.getType());

    }

    @Test
    public void testFunctionSmoothWithInitialValueIsntPure(){
        String program =  "variable = SMOOTHI(3, 4, 6)~~|";


        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.VARIABLE, variable.getType());

    }


    @Test
    public void testFunctionSmooth3IsntPure(){
        String program =  "variable = SMOOTH3(3, 4)~~|";


        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.VARIABLE, variable.getType());

    }

    @Test
    public void testFunctionSmooth3WithInitialValueIsntPure(){
        String program =  "variable = SMOOTH3I(3, 4, 6)~~|";


        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.VARIABLE, variable.getType());

    }

    @Test
    public void testFunctionTrendIsntPure(){
        String program =  "variable = TREND(3, 4, 5)~~|";


        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.VARIABLE, variable.getType());

    }

    @Test
    public void testFunctionStepIsntPure(){
        String program =  "variable = STEP(3, 4)~~|";


        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.VARIABLE, variable.getType());

    }

    @Test
    public void testFunctionRampIsntPure(){
        String program =  "variable = RAMP(3, 4,5)~~|";
        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.VARIABLE, variable.getType());

    }



    @Test
    public void testIndirectTypesAreResolved() throws IOException {
        SymbolTable table = getSymbolTable("testResolveIndirectTypes.mdl");

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.VARIABLE, variable.getType());

    }


    @Test
    public void testCyclicDependenciesInLevelType() throws IOException{
        SymbolTable table = getSymbolTable("testCyclicDependencies.mdl");

        Symbol level = table.getSymbol("level");
        assertEquals(SymbolType.VARIABLE,level.getType());

        Symbol inputRate = table.getSymbol("inputRate");
        assertEquals(SymbolType.VARIABLE,inputRate.getType());

        Symbol outputRate = table.getSymbol("outputRate");
        assertEquals(SymbolType.VARIABLE,outputRate.getType());

    }
}
