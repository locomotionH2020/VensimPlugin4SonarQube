package es.uva.locomotion.utilities;

import es.uva.locomotion.model.Symbol;
import es.uva.locomotion.model.SymbolTable;
import es.uva.locomotion.model.SymbolType;
import org.junit.Test;



import java.io.IOException;


import static org.junit.Assert.*;
import static es.uva.locomotion.testutilities.GeneralTestUtilities.*;


public class TestSymbolGenerator {

    @Test
    public void testEquationDirectLookup(){
        String program = "myLookup= GET DIRECT LOOKUPS('simpleInputs.xlsx', 'data' , '1', 'e2' ) ~~|";
        SymbolTable table = getSymbolTableFromString(program);

        Symbol myLookup = table.getSymbol("myLookup");
        assertSymbolType(myLookup, SymbolType.Lookup_Table);
    }

    @Test
    public void testEquationXLSLookup(){
        String program = "myLookup= GET XLS LOOKUPS('simpleInputs.xlsx', 'data' , 'a', 'b2' ) ~~|";
        SymbolTable table = getSymbolTableFromString(program);

        Symbol myLookup = table.getSymbol("myLookup");
        assertSymbolType(myLookup,SymbolType.Lookup_Table);
    }







    @Test
    public void testEquationEqualsNAIsConstant(){
        String program = "testKeywordNA = :NA:  ~~|";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol na = table.getSymbol("testKeywordNA");
        assertSymbolType(na,SymbolType.Constant);

    }



    @Test
    public void testTimeVariableIsCreated(){
        SymbolTable table = getSymbolTableFromString("");

        Symbol time = table.getSymbol("Time");

        assertSymbolType(time,SymbolType.Variable);
        assertTrue(time.getLines().isEmpty());

    }

    @Test
    public void testResolveMethodCreatesTimeVariable(){
        SymbolTable table = new SymbolTable();

        assertFalse(table.hasSymbol("Time"));
        SymbolTableGenerator.resolveSymbolTable(table);

        Symbol time = table.getSymbol("Time");
        assertSymbolType(time,SymbolType.Variable);
        assertTrue(time.getLines().isEmpty());
    }




    @Test
    public void testTypeInferenceInConstantsAndVariables() throws IOException{
        SymbolTable table = getSymbolTable("testTypeInference1.mdl");

        Symbol directConstant = table.getSymbol("directConstant");
        Symbol indirectConstant = table.getSymbol("indirectConstant");
        Symbol directVariable = table.getSymbol("directVariable");
        Symbol indirectVariable = table.getSymbol("indirectVariable");

        assertEquals(SymbolType.Constant,directConstant.getType());
        assertEquals(SymbolType.Constant,indirectConstant.getType());
        assertEquals(SymbolType.Variable,directVariable.getType());
        assertEquals(SymbolType.Variable,indirectVariable.getType());


    }

    @Test
    public void testFunctionDelayPIsntPure(){
        String program = "variable = DELAYP( 5, 6 : pipeline)~~|";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());
        assertEquals(SymbolType.Variable,table.getSymbol("pipeline").getType());


    }

    @Test
    public void testFunctionDelay1IsntPure(){
       String program =  "variable =DELAY1(3, 4)~~|";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");
        assertEquals(SymbolType.Variable, variable.getType());

    }


    @Test
    public void testFunctionDelay1WithInitialValueIsntPure(){
        String program =  "variable = DELAY1I(3, 4, 6)~~|";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());

    }

    @Test
    public void testFunctionDelay3IsntPure(){
        String program =  "variable = DELAY3(3, 4)~~|";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());

    }

    @Test
    public void testFunctionDelay3WithInitialValueIsntPure(){
        String program =  "variable = DELAY3I(3, 4, 6)~~|";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());

    }

    @Test
    public void testFunctionForecastIsntPure(){
        String program =  "variable = FORECAST(3, 4, 5)~~|";


        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());

    }


    @Test
    public void testFunctionSmoothIsntPure(){
        String program =  "variable = SMOOTH(3, 4)~~|";


        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());

    }

    @Test
    public void testFunctionSmoothWithInitialValueIsntPure(){
        String program =  "variable = SMOOTHI(3, 4, 6)~~|";


        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());

    }


    @Test
    public void testFunctionSmooth3IsntPure(){
        String program =  "variable = SMOOTH3(3, 4)~~|";


        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());

    }

    @Test
    public void testFunctionSmooth3WithInitialValueIsntPure(){
        String program =  "variable = SMOOTH3I(3, 4, 6)~~|";


        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());

    }

    @Test
    public void testFunctionTrendIsntPure(){
        String program =  "variable = TREND(3, 4, 5)~~|";


        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());

    }

    @Test
    public void testFunctionStepIsntPure(){
        String program =  "variable = STEP(3, 4)~~|";


        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());

    }

    @Test
    public void testFunctionRampIsntPure(){
        String program =  "variable = RAMP(3, 4,5)~~|";
        SymbolTable table = getSymbolTableFromString(program);

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());

    }

    @Test
    public void testRandom01IsntPure(){
        String program = "variable=" +
                "RANDOM 0 1()~~|";

        SymbolTable table = getSymbolTableFromString(program);
        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());
    }

    @Test
    public void testRandomBetaIsntPure(){
        String program = "variable=" +
                "RANDOM BETA(1,1,1,1,1,1,1)~~|";

        SymbolTable table = getSymbolTableFromString(program);
        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());
    }

    @Test
    public void testRandomBinomialIsntPure(){
        String program = "variable=" +
                "RANDOM BINOMIAL(1,1,1,1,1,1,1)~~|";

        SymbolTable table = getSymbolTableFromString(program);
        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());
    }

    @Test
    public void testRandomExponentialIsntPure(){
        String program = "variable=" +
                "RANDOM EXPONENTIAL(1,1,1,1,1)~~|";

        SymbolTable table = getSymbolTableFromString(program);
        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());
    }

    @Test
    public void testRandomGammaIsntPure(){
        String program = "variable=" +
                "RANDOM GAMMA(1,1,1,1,1,1)~~|";

        SymbolTable table = getSymbolTableFromString(program);
        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());
    }

    @Test
    public void testRandomLookupIsntPure(){
        String program = "variable=" +
                "RANDOM LOOKUP(1,1,1,1,1,1)~~|";

        SymbolTable table = getSymbolTableFromString(program);
        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());
    }

    @Test
    public void testRandomNegativeBinomialIsntPure(){
        String program = "variable=" +
                "RANDOM NEGATIVE BINOMIAL(1,1,1,1,1,1,1)~~|";

        SymbolTable table = getSymbolTableFromString(program);
        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());
    }

    @Test
    public void testRandomNormalIsntPure(){
        String program = "variable=" +
                "RANDOM NORMAL(1,1,1,1,1)~~|";

        SymbolTable table = getSymbolTableFromString(program);
        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());
    }

    @Test
    public void testRandomPinkNoiseIsntPure(){
        String program = "variable=" +
                "RANDOM PINK NOISE(1,1,1,1)~~|";

        SymbolTable table = getSymbolTableFromString(program);
        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());
    }

    @Test
    public void testRandomPoissonIsntPure(){
        String program = "variable=" +
                "RANDOM POISSON(1,1,1,1,1,1)~~|";

        SymbolTable table = getSymbolTableFromString(program);
        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());

    }

    @Test
    public void testRandomTriangularIsntPure(){
        String program = "variable=" +
                " RANDOM TRIANGULAR(1,1,1,1,1,1)~~|";

        SymbolTable table = getSymbolTableFromString(program);
        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());

    }

    @Test
    public void testRandomUniformIsntPure(){
        String program = "variable=" +
                " RANDOM UNIFORM(1,1,1)~~|";

        SymbolTable table = getSymbolTableFromString(program);
        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());

    }

    @Test
    public void testRandomWeibullIsntPure(){
        String program = "variable=" +
                "RANDOM WEIBULL(1,1,1,1,1,1)~~|";

        SymbolTable table = getSymbolTableFromString(program);
        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());

    }


    @Test
    public void testIndirectTypesAreResolved() throws IOException {
        SymbolTable table = getSymbolTable("testResolveIndirectTypes.mdl");

        Symbol variable = table.getSymbol("variable");

        assertEquals(SymbolType.Variable, variable.getType());

    }


    @Test
    public void testCyclicDependenciesInLevelType() throws IOException{
        SymbolTable table = getSymbolTable("testCyclicDependencies.mdl");

        Symbol level = table.getSymbol("level");
        assertEquals(SymbolType.Variable,level.getType());

        Symbol inputRate = table.getSymbol("inputRate");
        assertEquals(SymbolType.Variable,inputRate.getType());

        Symbol outputRate = table.getSymbol("outputRate");
        assertEquals(SymbolType.Variable,outputRate.getType());

    }

    @Test
    public void testVariableTypeInference(){
        SymbolTable table = new SymbolTable();

        Symbol undeterminedType = table.addSymbol(new Symbol("undeterminedType"));
        Symbol variable = table.addSymbol(new Symbol("variable"));
        variable.setType(SymbolType.Variable);

        undeterminedType.addDependency(variable);
        SymbolTableGenerator.resolveSymbolTable(table);

        assertSymbolType(undeterminedType,SymbolType.Variable);


    }

    @Test
    public void testInfersVarTypeIfThereAreUndeterminedDependencies(){
        SymbolTable table = new SymbolTable();

        Symbol undeterminedType = table.addSymbol(new Symbol("undeterminedType"));
        Symbol variable = table.addSymbol(new Symbol("variable"));
        variable.setType(SymbolType.Variable);

        undeterminedType.addDependency(table.addSymbol(new Symbol("undeterminedBefore")));
        undeterminedType.addDependency(variable);
        undeterminedType.addDependency(table.addSymbol(new Symbol("undeterminedAfter")));
        SymbolTableGenerator.resolveSymbolTable(table);


        assertSymbolType(undeterminedType,SymbolType.Variable);
    }

    @Test(expected = IllegalStateException.class)
    public void testThrowsExceptionIfCantBeResolved(){
        SymbolTable table = new SymbolTable();

        Symbol undeterminedType = table.addSymbol(new Symbol("undeterminedType"));
        undeterminedType.addDependency(undeterminedType);

        SymbolTableGenerator.resolveSymbolTable(table);

    }

    @Test
    public void testUndefinedSymbolsAreConsideredConstants(){
        String program =  "variable = undefined~~|";
        SymbolTable table = getRAWSymbolTableFromString(program);

        Symbol undefined = table.getSymbol("undefined");

        assertSymbolType(undefined,SymbolType.UNDETERMINED);
        SymbolTableGenerator.resolveSymbolTable(table);
        assertSymbolType(undefined,SymbolType.Constant);
    }

    @Test
    public void testRegularFunctionsAreConsideredPure(){
        String program =  "constant = DELAY(4.7) ~~|"; // Vensim doesn't have a DELAY function, it's made up
        SymbolTable table = getSymbolTableFromString(program);

        Symbol constant = table.getSymbol("constant");

        assertSymbolType(constant,SymbolType.Constant);

    }

    @Test
    public void testDependenciesWithSubscriptValues(){
        SymbolTable table = new SymbolTable();


        Symbol constant = table.addSymbol(new Symbol("constant"));
        Symbol subscriptValue = table.addSymbol(new Symbol("subscriptValue"));
        subscriptValue.setType(SymbolType.Subscript_Value);

        constant.addDependency(subscriptValue);
        SymbolTableGenerator.resolveSymbolTable(table);

        assertSymbolType(constant,SymbolType.Constant);
    }


    @Test
    public void testLookupCalledBeforeDefinedAndLoadedByFunction(){
        String program = "variableCaller= myLookup(3)~~|\n" +
                "myLookup= GET XLS LOOKUPS('inputs_W.xlsx', 'Constants', '242', 'C243')~~|";

        SymbolTable table = getSymbolTableFromString(program);

        Symbol constant = table.getSymbol("myLookup");

        assertSymbolType(constant,SymbolType.Lookup_Table);


    }



}
