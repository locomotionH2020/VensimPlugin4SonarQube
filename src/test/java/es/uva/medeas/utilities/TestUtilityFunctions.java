package es.uva.medeas.utilities;

import org.junit.Test;

import static es.uva.medeas.utilities.UtilityFunctions.stringToFloat;
import static es.uva.medeas.utilities.UtilityFunctions.stringToInt;
import static junit.framework.TestCase.assertEquals;

public class TestUtilityFunctions {

    private static final double FLOAT_COMPARISON_DELTA = 0.000001;

    @Test
    public void testStringToIntNoSign(){
        int result = stringToInt("5");
        assertEquals(5,result);
    }

    @Test
    public void testStringToIntPositive(){
        int result = stringToInt("+3");

        assertEquals(3,result);
    }

    @Test
    public void testStringToIntNegative(){
        int result = stringToInt("-3");

        assertEquals(-3,result);
    }

    @Test
    public void testStringToIntZero(){
        int result = stringToInt("-0");
        assertEquals(0,result);
    }



    @Test(expected = NumberFormatException.class)
    public void testStringToIntNotANumber(){
        stringToInt("a");
    }

    @Test(expected = NumberFormatException.class)
    public void testStringToIntButIsFloat(){
        stringToInt("3.0");
    }

    @Test(expected = NumberFormatException.class)
    public void testStringToIntWithSymbolsInTheMiddle(){
        stringToInt("1a1");
    }


    @Test(expected = NumberFormatException.class)
    public void testStringToIntWithSignAtTheEnd(){
        stringToInt("0-");
    }

    @Test
    public void testStringToIntMultipleSignsPositive(){
        int result = stringToInt("-+--+-5");
        assertEquals(5,result);
    }

    @Test
    public void testStringToIntMultipleSignsNegative(){
        int result = stringToInt("--+-5");
        assertEquals(-5,result);
    }

    @Test
    public void testStringToIntWithSpaces(){
        int result = stringToInt("  --+-5  ");
        assertEquals(-5,result);
    }

    @Test(expected = NumberFormatException.class)
    public void testStringToIntWithSignsInTheMiddle(){
        stringToInt("55+3");
    }

    @Test
    public void testStringToFloatNoSign(){
        float result = stringToFloat("0.5");
        assertEquals(0.5,result,FLOAT_COMPARISON_DELTA);
    }

    @Test
    public void testStringToFloatPositive(){
        float result = stringToFloat("+0.3");
        assertEquals(0.3,result,FLOAT_COMPARISON_DELTA);
    }

    @Test
    public void testStringToFloatNegative(){
        float result = stringToFloat("-0.3");
        assertEquals(-0.3,result,FLOAT_COMPARISON_DELTA);
    }

    @Test
    public void testStringToFloatZero(){
        float result = stringToFloat("-0.0");
        assertEquals(0,result,FLOAT_COMPARISON_DELTA);
    }


    @Test(expected = NumberFormatException.class)
    public void testStringToFloatNotANumber(){
        stringToFloat("a");
    }

    @Test
    public void testStringToFloatButIsInteger(){
        float result = stringToFloat("1");
        assertEquals(1.0,result,FLOAT_COMPARISON_DELTA);
    }

    @Test(expected = NumberFormatException.class)
    public void testStringToFloatWithSymbolsInTheMiddle(){
        stringToFloat("1.01a1");
    }


    @Test(expected = NumberFormatException.class)
    public void testStringToFloatWithSignAtTheEnd(){
        stringToFloat("0.3-");
    }

    @Test
    public void testStringToFloatMultipleSignsPositive(){
        float result = stringToFloat("-+--+-5.3");
        assertEquals(5.3,result,FLOAT_COMPARISON_DELTA);
    }

    @Test
    public void testStringToFloatMultipleSignsNegative(){
        float result = stringToFloat("--+-5.3");
        assertEquals(-5.3,result,FLOAT_COMPARISON_DELTA);
    }

    @Test(expected = NumberFormatException.class)
    public void testStringToFloatWithSignsInTheMiddle(){
        stringToFloat("55.1+3");
    }

    @Test
    public void testStringToFloatScientificNotation(){
        float result = stringToFloat("--+-5.3e2");
        assertEquals(-5.3e2,result,FLOAT_COMPARISON_DELTA);
    }

    @Test
    public void testStringToFloatScientificNotationWithSign(){
        float result = stringToFloat("--+-5.3e---2");
        assertEquals(-5.3e-2,result,FLOAT_COMPARISON_DELTA);
    }

    @Test
    public void testStringToFloatWithSpaces(){
        float result = stringToFloat("   --+-5.3  ");
        assertEquals(-5.3,result,FLOAT_COMPARISON_DELTA);
    }

}
