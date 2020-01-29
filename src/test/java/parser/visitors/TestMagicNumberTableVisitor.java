package parser.visitors;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.plugin.VensimVisitorContext;
import es.uva.medeas.parser.visitors.MagicNumberTableVisitor;
import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;
import org.sonar.api.internal.apachecommons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collections;

import static es.uva.medeas.testutilities.RuleTestUtilities.getVisitorContextFromString;

public class TestMagicNumberTableVisitor {

    private MagicNumberTableVisitor visitor;

    @Before
    public void setUp(){
        visitor = new MagicNumberTableVisitor();
    }

    @Test
    public void testValid(){
        String program = "A = 3 * 4 ~~|\n" +
                "B := 3 * Time~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(Arrays.asList(1,2),table.getSymbol("3").getDefinitionLines() );
        assertEquals(Collections.singletonList(1),table.getSymbol("4").getDefinitionLines() );

    }


    @Test
    public void testExceptionDirectConstantEquation(){
        String program = "A = 3 ~~|\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertFalse(table.hasSymbol("3"));

    }

    @Test
    public void testExceptionDirectConstantUnchangeableConstant(){
        String program = "A == 3 ~~|\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertFalse(table.hasSymbol("3"));
    }

    @Test
    public void testExceptionDirectConstantDataEquation(){
        String program = "A := 3 ~~|\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertFalse(table.hasSymbol("3"));
    }


    @Test
    public void testRepeatedNumbersInTheSameLineCount(){
        String program = "A = 3 * 3 * 3 * 3 ~~|\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(Arrays.asList(1,1,1,1),table.getSymbol("3").getDefinitionLines() );

    }

    @Test
    public void testExceptionTabbedArray(){
        String program = "TABBED_ARRAY = TABBED ARRAY(3    3    3    3\n" +
                "3    3    3    3\n" +
                "3    3    3    3)\n" +
                "~ |";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertFalse(table.hasSymbol("3") );

    }

    @Test
    public void testExceptionInBidimensionalArraysEquation(){
        String program = "initial population[country,blood type] = 1,2,3,4;5,6,7,8;\n" +
                "         9,10,11,12; ~Person~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty());
    }

    @Test
    public void testExceptionBidimensionalArraysDataEquation(){
        String program = "initial population[country,blood type] := 1,2,3,4;5,6,7,8;\n" +
                "         9,10,11,12; ~Person~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty());
    }

    @Test
    public void testExceptionBidimensionalArraysUnchangeableConstant(){
        String program = "initial population[country,blood type] == 1,2,3,4;5,6,7,8;\n" +
                "         9,10,11,12; ~Person~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty());
    }

    @Test
    public void testTraversesFunctionPath(){
        String program = "var = - FUNCTION( 3, ANOTHER FUNCTION(3),3)~ |";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(Arrays.asList(1,1,1),table.getSymbol("3").getDefinitionLines() );
    }

    @Test
    public void testTableIncludesFloats(){
        String program = "\nFOO = 0.3 * 0.3~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(Arrays.asList(2,2),table.getSymbol("0.3").getDefinitionLines() );

    }

    @Test
    public void testTransversesExprOperationsAndKeyword(){
        String program = "FOO = (3 ^ (3)) * 3 / 3 - 3 + (3<3 :AND: 3>3 :AND: 3<=3 :AND: 3>=3 :AND: :NOT: 3 = 3 :OR: 3 <> 3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());


        int expectedThrees = StringUtils.countMatches(program,"3");
        assertEquals(Collections.nCopies(expectedThrees,1),table.getSymbol("3").getDefinitionLines() );
    }

    @Test
    public void testParenthesisCount(){
        String program = "A = ((3)*3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(Arrays.asList(1,1),table.getSymbol("3").getDefinitionLines() );
    }

    @Test
    public void testSignNumbers(){
        String program = "A = -3 * +4 ~|";


        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(Collections.singletonList(1),table.getSymbol("-3").getDefinitionLines() );
        assertEquals(Collections.singletonList(1),table.getSymbol("4").getDefinitionLines() );
    }

    @Test
    public void testExponentNumbers(){
        String program = "A = -10e+5 * +10E-9 ~|";


        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(Collections.singletonList(1),table.getSymbol("-1000000.0").getDefinitionLines() );
        assertEquals(Collections.singletonList(1),table.getSymbol("1.0E-8").getDefinitionLines() );
    }

    @Test
    public void testNumbersInEquationIdDontCount(){
        String program = "NUMBER 1 = Time~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertFalse(table.hasSymbol("1") );

    }

    @Test
    public void testNumbersInSymbolIdDontCount(){
        String program = "FOO = NUMBER 1 ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertFalse(table.hasSymbol("1") );
    }

    @Test
    public void testNumbersInCalledFunctionIdDontCount(){
        String program = "foo = FUNCTION 1(Time)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertFalse(table.hasSymbol("1") );

    }

    @Test
    public void testExceptionLookup(){
        String program = "myLookup(" + "[(0,0)-(10,10),(1.25382,3.55263),(3.85321,4.5614),(7.15596,7.67544),(8.74618,9.21053)]," +
        "(1.25382,3.55263),(3.42508,3.64035),(3.85321,4.5614),(4.8318,7.80702),(6.36086,4.91228 ),(7.15596,7.67544)," +
                "(7.18654,5.92105),(8.74618,9.21053))~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionLookupNNumberListFormat(){
        String program = "accomplishments per hour lookup(0,0.2,0.4,0.6,0.8,1,\n" +
                "                                         0,0.2,0.4,0.6,0.8,1)~|\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testDelayPCounts(){
        String program = "a = DELAYP(1,1:Time)~|";
        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(Arrays.asList(1,1),table.getSymbol("1").getDefinitionLines() );
    }

    @Test
    public void testTransversesMacros(){
        String program = ":MACRO: VSMOOTH(input,SMOOTH TIME)\n" +
                "Vsmooth = INTEG((input - Vsmooth)/SMOOTH TIME*3, input)\n ~ The first order smoothed value of a variable.|\n" +
                ":END OF MACRO:";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(Collections.singletonList(2),table.getSymbol("3").getDefinitionLines() );


    }

    @Test
    public void testExceptionLookupInsideFunctionEquation(){
        String program = "var =WITH LOOKUP(6,((0,1),(1,1),(2,2)))\n~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(1,table.getSymbols().size());
        assertEquals("6",table.getSymbols().iterator().next().getToken());
    }


    @Test
    public void testExceptionLookupInsideFunctionDataEquation(){
        String program = "var :=WITH LOOKUP(6,((0,1),(1,1),(2,2)))\n~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(1,table.getSymbols().size());
        assertEquals("6",table.getSymbols().iterator().next().getToken());
    }


    @Test
    public void testExceptionNumberListEquation(){
        String program = "A = 1,2,3,4,5~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );

    }

    @Test
    public void testExceptionNumberListDataEquation(){
        String program = "A := 1,2,3,4,5~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );

    }

    @Test
    public void testNumberListDoesntCountUnchangeableConstant(){ //TODO revisar si tiene sentido
        String program = "A := 1,2,3,4,5~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );

    }




    @Test
    public void testSameNumberInDifferentFormatIsStillTheSame(){
        String program = "A = 3 * +3 * 1.0 * 10e-1 ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(Arrays.asList(1,1),table.getSymbol("3").getDefinitionLines() );
        assertEquals(Arrays.asList(1,1),table.getSymbol("1.0").getDefinitionLines() );

    }

    @Test
    public void testSameNumberButWithDifferentNumberOfSignsIsTheSame(){
        String program = "A = 3 +  --3 + 4.3 + --4.3 ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(Arrays.asList(1,1),table.getSymbol("3").getDefinitionLines() );
        assertEquals(Arrays.asList(1,1),table.getSymbol("4.3").getDefinitionLines() );
    }


    @Test
    public void testCompoundNumberIsTrimmed(){
        String program = " A = Time * MODULO(  3,   4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("MODULO(3,4)"));
        assertEquals(1,table.getSymbols().size());
    }

    @Test
    public void testCompoundNumberTextIncludesParenthesis(){
        String program = " A = Time * GAME(((2)))~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("GAME(((2)))"));
        assertEquals(1,table.getSymbols().size());
    }

    @Test
    public void testCompoundNumberInSeveralLines(){
        String program = "\n\n A = Time * LN(" +
                "SQRT(2))~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        Symbol number = table.getSymbol("LN(SQRT(2))");
        assertNotNull(number);
        assertEquals(Collections.singletonList(3),number.getDefinitionLines());
    }


    @Test
    public void testNestedCompoundNumberInIgnoredFunctions(){
        String function= "POWER" +
                "   (SQRT" +
                "       (TAN" +
                "            (TANH" +
                "               (SIN" +
                "                   (SINH(" +
                "                       COS(" +
                "                           COSH(" +
                "                               ARCTAN(" +
                "                                   ARCSIN(" +
                "                                       ARCCOS(ABS(9)))))))))))"+
                " ,LN(" +
                "     GAMMA LN(" +
                "             INTEGER(" +
                "                    GAME(" +
                "                         EXP(" +
                "                           MODULO(QUANTUM(3,7),LOG(3,7)))))))" +
                ")";

        String program = "A = Time *" + function + "~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(1,table.getSymbols().size());
        String expected = "POWER(SQRT(TAN(TANH(SIN(SINH(COS(COSH(ARCTAN(ARCSIN(ARCCOS(ABS(9))))))))))),LN(GAMMA LN(INTEGER(GAME(EXP(MODULO(QUANTUM(3,7),LOG(3,7))))))))";
        assertEquals(expected,table.getSymbols().iterator().next().getToken());
    }



    @Test
    public void testFunctionIsTransversedIfIsntCompoundDueToVariablesNotAlone(){
        String program = "A = Time * MODULO(3,Time)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(1,table.getSymbols().size());
        assertTrue(table.hasSymbol("3"));
    }

    @Test
    public void testFunctionIsTransversedIfIsntCompoundDueToVariablesAlone(){
        String program = "A = MODULO(3,Time)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(1,table.getSymbols().size());
        assertTrue(table.hasSymbol("3"));
    }

    @Test
    public void testFunctionIsTransversedIfIsntCompoundDueToCall(){
        String program = "A = Time * QUANTUM(3,RANDOM(3))~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(1,table.getSymbols().size());
        assertTrue(table.hasSymbol("3"));
    }

    @Test
    public void testFunctionIsTransversedIfIsntCompoundDueToCallAlone(){
        String program = "A = Time * QUANTUM(3,RANDOM(3))~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(1,table.getSymbols().size());
        assertTrue(table.hasSymbol("3"));
    }



    @Test
    public void testFunctionIsTransversedIfIsntCompoundDueToConstantNotAlone(){
        String program = "CONST = 4~|" +
                "C = Time * LOG(3,CONST)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(1,table.getSymbols().size());
        assertTrue(table.hasSymbol("3"));
    }

    @Test
    public void testFunctionIsTransversedIfIsntCompoundDueToConstantAlone(){
        String program = "CONST = 4~|" +
                "C = LOG(3,CONST)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(1,table.getSymbols().size());
        assertTrue(table.hasSymbol("3"));
    }

    @Test
    public void testTransversesCompoundsIfAlmostNested(){
        String program = "A = FUNC(Time,MODULO(3,4),5)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertEquals(2,table.getSymbols().size());
        assertTrue(table.hasSymbol("MODULO(3,4)"));
        assertTrue(table.hasSymbol("5"));
    }


    @Test
    public void testExceptionAloneSQRTEquation(){
        String program = "A = SQRT(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberSQRTEquation(){
        String program = "A = Time *  SQRT(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("SQRT(3)"));
    }

    @Test
    public void testAloneExceptionSQRTDataEquation(){
        String program = "A := SQRT(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberSQRTDataEquation(){
        String program = "A := Time * SQRT(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("SQRT(3)"));
    }





    @Test
    public void testExceptionAloneTANEquation(){
        String program = "A = TAN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberTANEquation(){
        String program = "A = Time *  TAN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("TAN(3)"));
    }

    @Test
    public void testAloneExceptionTANDataEquation(){
        String program = "A := TAN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberTANDataEquation(){
        String program = "A := Time * TAN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("TAN(3)"));
    }





    @Test
    public void testExceptionAloneTANHEquation(){
        String program = "A = TANH(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberTANHEquation(){
        String program = "A = Time *  TANH(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("TANH(3)"));
    }

    @Test
    public void testAloneExceptionTANHDataEquation(){
        String program = "A := TANH(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberTANHDataEquation(){
        String program = "A := Time * TANH(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("TANH(3)"));
    }



    @Test
    public void testExceptionAloneSINEquation(){
        String program = "A = SIN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberSINEquation(){
        String program = "A = Time *  SIN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("SIN(3)"));
    }

    @Test
    public void testAloneExceptionSINDataEquation(){
        String program = "A := SIN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberSINDataEquation(){
        String program = "A := Time * SIN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("SIN(3)"));
    }





    @Test
    public void testExceptionAloneSINHEquation(){
        String program = "A = SINH(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberSINHEquation(){
        String program = "A = Time *  SINH(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("SINH(3)"));
    }

    @Test
    public void testAloneExceptionSINHDataEquation(){
        String program = "A := SINH(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberSINHDataEquation(){
        String program = "A := Time * SINH(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("SINH(3)"));
    }





    @Test
    public void testExceptionAloneCOSEquation(){
        String program = "A = COS(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberCOSEquation(){
        String program = "A = Time *  COS(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("COS(3)"));
    }

    @Test
    public void testAloneExceptionCOSDataEquation(){
        String program = "A := COS(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberCOSDataEquation(){
        String program = "A := Time * COS(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("COS(3)"));
    }





    @Test
    public void testExceptionAloneCOSHEquation(){
        String program = "A = COSH(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberCOSHEquation(){
        String program = "A = Time *  COSH(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("COSH(3)"));
    }

    @Test
    public void testAloneExceptionCOSHDataEquation(){
        String program = "A := COSH(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberCOSHDataEquation(){
        String program = "A := Time * COSH(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("COSH(3)"));
    }





    @Test
    public void testExceptionAloneARCTANEquation(){
        String program = "A = ARCTAN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberARCTANEquation(){
        String program = "A = Time *  ARCTAN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("ARCTAN(3)"));
    }

    @Test
    public void testAloneExceptionARCTANDataEquation(){
        String program = "A := ARCTAN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberARCTANDataEquation(){
        String program = "A := Time * ARCTAN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("ARCTAN(3)"));
    }





    @Test
    public void testExceptionAloneARCSINEquation(){
        String program = "A = ARCSIN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberARCSINEquation(){
        String program = "A = Time *  ARCSIN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("ARCSIN(3)"));
    }

    @Test
    public void testAloneExceptionARCSINDataEquation(){
        String program = "A := ARCSIN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberARCSINDataEquation(){
        String program = "A := Time * ARCSIN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("ARCSIN(3)"));
    }





    @Test
    public void testExceptionAloneARCCOSEquation(){
        String program = "A = ARCCOS(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberARCCOSEquation(){
        String program = "A = Time *  ARCCOS(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("ARCCOS(3)"));
    }

    @Test
    public void testAloneExceptionARCCOSDataEquation(){
        String program = "A := ARCCOS(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberARCCOSDataEquation(){
        String program = "A := Time * ARCCOS(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("ARCCOS(3)"));
    }





    @Test
    public void testExceptionAloneABSEquation(){
        String program = "A = ABS(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberABSEquation(){
        String program = "A = Time *  ABS(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("ABS(3)"));
    }

    @Test
    public void testAloneExceptionABSDataEquation(){
        String program = "A := ABS(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberABSDataEquation(){
        String program = "A := Time * ABS(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("ABS(3)"));
    }





    @Test
    public void testExceptionAloneLNEquation(){
        String program = "A = LN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberLNEquation(){
        String program = "A = Time *  LN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("LN(3)"));
    }

    @Test
    public void testAloneExceptionLNDataEquation(){
        String program = "A := LN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberLNDataEquation(){
        String program = "A := Time * LN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("LN(3)"));
    }





    @Test
    public void testExceptionAloneGAMMALNEquation(){
        String program = "A = GAMMA LN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberGAMMALNEquation(){
        String program = "A = Time *  GAMMA LN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("GAMMA LN(3)"));
    }

    @Test
    public void testAloneExceptionGAMMALNDataEquation(){
        String program = "A := GAMMA LN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberGAMMALNDataEquation(){
        String program = "A := Time * GAMMA LN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("GAMMA LN(3)"));
    }





    @Test
    public void testExceptionAloneINTEGEREquation(){
        String program = "A = INTEGER(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberINTEGEREquation(){
        String program = "A = Time *  INTEGER(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("INTEGER(3)"));
    }

    @Test
    public void testAloneExceptionINTEGERDataEquation(){
        String program = "A := INTEGER(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberINTEGERDataEquation(){
        String program = "A := Time * INTEGER(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("INTEGER(3)"));
    }





    @Test
    public void testExceptionAloneGAMEEquation(){
        String program = "A = GAME(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberGAMEEquation(){
        String program = "A = Time *  GAME(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("GAME(3)"));
    }

    @Test
    public void testAloneExceptionGAMEDataEquation(){
        String program = "A := GAME(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberGAMEDataEquation(){
        String program = "A := Time * GAME(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("GAME(3)"));
    }





    @Test
    public void testExceptionAloneEXPEquation(){
        String program = "A = EXP(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberEXPEquation(){
        String program = "A = Time *  EXP(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("EXP(3)"));
    }

    @Test
    public void testAloneExceptionEXPDataEquation(){
        String program = "A := EXP(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberEXPDataEquation(){
        String program = "A := Time * EXP(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("EXP(3)"));
    }


    @Test
    public void testExceptionAloneModuloEquation(){
        String program = "A = MODULO(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberModuloEquation(){
        String program = "A = Time *  MODULO(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("MODULO(3,4)"));
        assertFalse(table.hasSymbol("3"));
        assertFalse(table.hasSymbol("4"));
    }

    @Test
    public void testAloneExceptionModuloDataEquation(){
        String program = "A := MODULO(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }



    @Test
    public void testCompoundNumberModuloDataEquation(){
        String program = "A := Time * MODULO(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("MODULO(3,4)"));
        assertFalse(table.hasSymbol("3"));
        assertFalse(table.hasSymbol("4"));
    }


    @Test
    public void testExceptionAlonePowerEquation(){
        String program = "A = POWER(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberPowerEquation(){
        String program = "A = Time *  POWER(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("POWER(3,4)"));
        assertFalse(table.hasSymbol("3"));
        assertFalse(table.hasSymbol("4"));
    }

    @Test
    public void testAloneExceptionPowerDataEquation(){
        String program = "A := POWER(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }



    @Test
    public void testCompoundNumberPowerDataEquation(){
        String program = "A := Time * POWER(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("POWER(3,4)"));
        assertFalse(table.hasSymbol("3"));
        assertFalse(table.hasSymbol("4"));
    }

    @Test
    public void testExceptionAloneLOGEquation(){
        String program = "A = LOG(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberLOGEquation(){
        String program = "A = Time *  LOG(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("LOG(3,4)"));
        assertFalse(table.hasSymbol("3"));
        assertFalse(table.hasSymbol("4"));
    }

    @Test
    public void testAloneExceptionLOGDataEquation(){
        String program = "A :=  LOG(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }



    @Test
    public void testCompoundNumberLOGDataEquation(){
        String program = "A := Time * LOG(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("LOG(3,4)"));
        assertFalse(table.hasSymbol("3"));
        assertFalse(table.hasSymbol("4"));
    }



    @Test
    public void testExceptionAloneQuantumEquation(){
        String program = "A = QUANTUM(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testCompoundNumberQuantumEquation(){
        String program = "A = Time *  QUANTUM(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("QUANTUM(3,4)"));
        assertFalse(table.hasSymbol("3"));
        assertFalse(table.hasSymbol("4"));
    }

    @Test
    public void testAloneExceptionQuantumDataEquation(){
        String program = "A := QUANTUM(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.getSymbols().isEmpty() );
    }



    @Test
    public void testCompoundNumberQuantumDataEquation(){
        String program = "A := Time * QUANTUM(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("QUANTUM(3,4)"));
        assertFalse(table.hasSymbol("3"));
        assertFalse(table.hasSymbol("4"));
    }

    

    @Test
    public void testCompoundNumberQUANTUM(){
        String program = "A = Time * QUANTUM(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext.getRootNode());

        assertTrue(table.hasSymbol("QUANTUM(3,4)"));
        assertEquals(1,table.getSymbols().size());

    }


}
