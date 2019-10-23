package es.uva.medeas.rules;

import es.uva.medeas.parser.*;
import org.junit.Ignore;
import org.junit.Test;



import java.io.IOException;


import static org.junit.Assert.*;
import static es.uva.medeas.rules.TestUtilities.*;


public class TestSymbolGenerator {








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



    @Test
    public void testMedeas1() throws IOException{ //TODO move
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
    public void testTabbedArray(){
        String program = "initial population[country,blood type] = TABBED ARRAY(\n" +
                "       1       2.0        -3.7        4\n" +
                "       0        6          7          8\n" +
                "       9       -10        11          12) ~~|";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol population = table.getSymbol("initial population");
        Symbol tabbedArrayFunc = table.getSymbol("TABBED ARRAY");
        assertSymbol(population,SymbolType.CONSTANT,1,createSet(tabbedArrayFunc));
    }



    @Test
    public void testEquationEqualsNAIsConstant(){
        String program = "testKeywordNA = :NA:  ~|";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol na = table.getSymbol("testKeywordNA");
        assertSymbolType(na,SymbolType.CONSTANT);

    }



    @Test
    public void testTimeVariableIsCreated(){
        SymbolTable table = getSymbolTableFromString("");

        Symbol time = table.getSymbol("Time");

        assertSymbolType(time,SymbolType.VARIABLE);
        assertSymbolLine(time,Symbol.LINE_NOT_DEFINED);

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
        assertEquals(SymbolType.VARIABLE,table.getSymbol("pipeline").getType());


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
