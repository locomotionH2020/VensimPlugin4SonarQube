package es.uva.medeas.rules;


import org.junit.Test;
import static  es.uva.medeas.rules.TestUtilities.*;

public class TestGrammar {

    @Test
    public void testFloatNotation(){
        String program = "const = -1e-3 ~|";

        getParseTreeFromString(program);
    }


    @Test
    public void testNegativeFloatsAreDetected(){
        String program = "const = -0.3-4.1 ~|";
        getParseTreeFromString(program);
    }

    @Test
    public void testNegativeIntegersAreDetected(){
        String program = "const = -5-4 ~|";
        getParseTreeFromString(program);
    }

    @Test
    public void testParseTwoDimensionalArrays(){
        String program = "initial population equiv comma semicolon syntax[country row,blood type column]=\t\n" +
                "1,2,3,4;\n" +
                "5,6,7,8;\n" +
                "9,10,11,12;\n~|";

        getParseTreeFromString(program);
    }

    @Test
    public void testWeirdChars(){
        String program = "URR tot agg oil Laherrère 2006= 10  ~|";

        getParseTreeFromString(program);
    }

}
