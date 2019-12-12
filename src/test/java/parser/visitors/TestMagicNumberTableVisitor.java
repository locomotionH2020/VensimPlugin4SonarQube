package parser.visitors;

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
                "B = 3 * Time~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertEquals(Arrays.asList(1,2),table.getSymbol("3").getDefinitionLines() );
        assertEquals(Collections.singletonList(1),table.getSymbol("4").getDefinitionLines() );

    }

    @Test
    public void testDirectConstantsDoesntCountEquation(){
        String program = "A = 3 ~~|\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertFalse(table.hasSymbol("3"));

    }

    @Test
    public void testDirectConstantDoesntCountUnchangeableConstant(){
        String program = "A == 3 ~~|\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertFalse(table.hasSymbol("3"));
    }

    @Test
    public void testDirectConstantDoesntCountDataEquation(){
        String program = "A := 3 ~~|\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertFalse(table.hasSymbol("3"));
    }


    @Test
    public void testRepeatedNumbersInTheSameLineCount(){
        String program = "A = 3 * 3 * 3 * 3 ~~|\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertEquals(Arrays.asList(1,1,1,1),table.getSymbol("3").getDefinitionLines() );

    }

    @Test
    public void testIgnoresTabbedArray(){
        String program = "TABBED_ARRAY = TABBED ARRAY(3    3    3    3\n" +
                "3    3    3    3\n" +
                "3    3    3    3)\n" +
                "~ |";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertFalse(table.hasSymbol("3") );

    }

    @Test
    public void testBidimensionalArrayDoesntCountInEquation(){
        String program = "initial population[country,blood type] = 1,2,3,4;5,6,7,8;\n" +
                "         9,10,11,12; ~Person~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty());
    }

    @Test
    public void testBidimensionalArrayDoesntCountInDataEquation(){
        String program = "initial population[country,blood type] := 1,2,3,4;5,6,7,8;\n" +
                "         9,10,11,12; ~Person~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty());
    }

    @Test
    public void testBidimensionalArrayDoesntCountInUnchangeableConstant(){
        String program = "initial population[country,blood type] == 1,2,3,4;5,6,7,8;\n" +
                "         9,10,11,12; ~Person~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty());
    }

    @Test
    public void testTraversesFunctionPath(){
        String program = "var = - FUNCTION( 3, ANOTHER FUNCTION(3),3)~ |";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertEquals(Arrays.asList(1,1,1),table.getSymbol("3").getDefinitionLines() );
    }

    @Test
    public void testTableIncludesFloats(){
        String program = "\nFOO = 0.3 * 0.3~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertEquals(Arrays.asList(2,2),table.getSymbol("0.3").getDefinitionLines() );

    }

    @Test
    public void testTransversesExprOperationsAndKeyword(){
        String program = "FOO = (3 ^ (3)) * 3 / 3 - 3 + (3<3 :AND: 3>3 :AND: 3<=3 :AND: 3>=3 :AND: :NOT: 3 = 3 :OR: 3 <> 3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);


        int expectedThrees = StringUtils.countMatches(program,"3");
        assertEquals(Collections.nCopies(expectedThrees,1),table.getSymbol("3").getDefinitionLines() );
    }

    @Test
    public void testParenthesisCount(){
        String program = "A = ((3)*3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertEquals(Arrays.asList(1,1),table.getSymbol("3").getDefinitionLines() );
    }

    @Test
    public void testSignNumbers(){
        String program = "A = -3 * +4 ~|";


        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertEquals(Collections.singletonList(1),table.getSymbol("-3").getDefinitionLines() );
        assertEquals(Collections.singletonList(1),table.getSymbol("4").getDefinitionLines() );
    }

    @Test
    public void testExponentNumbers(){
        String program = "A = -10e+5 * +10E-9 ~|";


        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertEquals(Collections.singletonList(1),table.getSymbol("-1000000.0").getDefinitionLines() );
        assertEquals(Collections.singletonList(1),table.getSymbol("1.0E-8").getDefinitionLines() );
    }

    @Test
    public void testNumbersInEquationIdDoesntCount(){
        String program = "NUMBER 1 = Time~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertFalse(table.hasSymbol("1") );

    }

    @Test
    public void testNumberInSymbolIdDoesntCount(){
        String program = "FOO = NUMBER 1 ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertFalse(table.hasSymbol("1") );
    }

    @Test
    public void testNumbersInCalledFunctionIdDoesntCount(){
        String program = "foo = FUNCTION 1(Time)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertFalse(table.hasSymbol("1") );

    }

    @Test
    public void testLookupDoesntCount(){
        String program = "myLookup(" + "[(0,0)-(10,10),(1.25382,3.55263),(3.85321,4.5614),(7.15596,7.67544),(8.74618,9.21053)]," +
        "(1.25382,3.55263),(3.42508,3.64035),(3.85321,4.5614),(4.8318,7.80702),(6.36086,4.91228 ),(7.15596,7.67544)," +
                "(7.18654,5.92105),(8.74618,9.21053))~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testLookupNNumberListFormatDoesntCount(){
        String program = "accomplishments per hour lookup(0,0.2,0.4,0.6,0.8,1,\n" +
                "                                         0,0.2,0.4,0.6,0.8,1)~|\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testDelayPCounts(){
        String program = "a = DELAYP(1,1:Time)~|";
        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertEquals(Arrays.asList(1,1),table.getSymbol("1").getDefinitionLines() );
    }

    @Test
    public void testTransversesMacros(){
        String program = ":MACRO: VSMOOTH(input,SMOOTH TIME)\n" +
                "Vsmooth = INTEG((input - Vsmooth)/SMOOTH TIME*3, input)\n ~ The first order smoothed value of a variable.|\n" +
                ":END OF MACRO:";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertEquals(Collections.singletonList(2),table.getSymbol("3").getDefinitionLines() );


    }

    @Test
    public void testWithLookupIgnoresLookupEquation(){
        String program = "var =WITH LOOKUP(6,((0,1),(1,1),(2,2)))\n~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertEquals(1,table.getSymbols().size());
        assertEquals("6",table.getSymbols().iterator().next().getToken());
    }


    @Test
    public void testWithLookupIgnoresLookupDataEquation(){
        String program = "var :=WITH LOOKUP(6,((0,1),(1,1),(2,2)))\n~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertEquals(1,table.getSymbols().size());
        assertEquals("6",table.getSymbols().iterator().next().getToken());
    }


    @Test
    public void testWithLookupIgnoresLookupUnchangeableConstant(){
        String program = "var ==WITH LOOKUP(6,((0,1),(1,1),(2,2)))\n~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertEquals(1,table.getSymbols().size());
        assertEquals("6",table.getSymbols().iterator().next().getToken());
    }

    @Test
    public void testNumberListDoesntCountEquation(){
        String program = "A = 1,2,3,4,5~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );

    }

    @Test
    public void testNumberListDoesntCountDataEquation(){
        String program = "A := 1,2,3,4,5~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );

    }

    @Test
    public void testNumberListDoesntCountUnchangeableConstant(){
        String program = "A := 1,2,3,4,5~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );

    }




    @Test
    public void testSameNumberInDifferentFormatIsStillTheSame(){
        String program = "A = 3 * +3 * 1.0 * 10e-1 ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertEquals(Arrays.asList(1,1),table.getSymbol("3").getDefinitionLines() );
        assertEquals(Arrays.asList(1,1),table.getSymbol("1.0").getDefinitionLines() );

    }

    @Test
    public void testFunctionExceptionsOnlyWorkIfAloneEquation(){
        String program = "A = Time * SQRT(3) ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertEquals(Collections.singletonList(1),table.getSymbol("3").getDefinitionLines() );
    }

    @Test
    public void testFunctionExceptionsOnlyWorkIfAloneDataEquation(){
        String program = "A := Time * SQRT(3) ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertEquals(Collections.singletonList(1),table.getSymbol("3").getDefinitionLines() );
    }

    @Test
    public void testExceptionSQRTEquation(){
        String program = "A = SQRT(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionSQRTDataEquation(){
        String program = "A := SQRT(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionTANEquation(){
        String program = "A = TAN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionTANDataEquation(){
        String program = "A := TAN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionTANHEquation(){
        String program = "A = TANH(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionTANHDataEquation(){
        String program = "A := TANH(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionSINEquation(){
        String program = "A = SIN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionSINDataEquation(){
        String program = "A := SIN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionSINHEquation(){
        String program = "A = SINH(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionSINHDataEquation(){
        String program = "A := SINH(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }
    @Test
    public void testExceptionCOSEquation(){
        String program = "A = COS(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionCOSDataEquation(){
        String program = "A := COS(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }
    @Test
    public void testExceptionCOSHEquation(){
        String program = "CONST = COSH(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionCOSHDataEquation(){
        String program = "A := COSH(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }
    @Test
    public void testExceptionARCTANEquation(){
        String program = "A = ARCTAN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionARCTANDataEquation(){
        String program = "A := ARCTAN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }
    @Test
    public void testExceptionARCSINEquation(){
        String program = "A = ARCSIN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionARCSINDataEquation(){
        String program = "A := ARCSIN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }
    @Test
    public void testExceptionARCCOSEquation(){
        String program = "A = ARCCOS(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionARCCOSDataEquation(){
        String program = "A := ARCCOS(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }
    @Test
    public void testExceptionABSEquation(){
        String program = "A = ABS(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionABSDataEquation(){
        String program = "A := ABS(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }
    @Test
    public void testExceptionLNEquation(){
        String program = "A = LN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionLNDataEquation(){
        String program = "A := LN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }
    @Test
    public void testExceptionGAMMALNEquation(){
        String program = "A = GAMMA LN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionGAMMALNDataEquation(){
        String program = "A := GAMMA LN(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }
    @Test
    public void testExceptionINTEGEREquation(){
        String program = "A = INTEGER(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionINTEGERDataEquation(){
        String program = "A := INTEGER(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }
    @Test
    public void testExceptionGAMEEquation(){
        String program = "A = GAME(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionGAMEDataEquation(){
        String program = "A := GAME(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }
    @Test
    public void testExceptionEXPEquation(){
        String program = "A = EXP(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }

    @Test
    public void testExceptionEXPDataEquation(){
        String program = "A := EXP(3)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.getSymbols().isEmpty() );
    }


    @Test
    public void testCompoundNumberMOD(){
        String program = " A = MODULO(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.hasSymbol("MODULO(3,4)"));
        assertFalse(table.hasSymbol("3"));
        assertFalse(table.hasSymbol("4"));

    }

    @Test
    public void testCompoundNumberPOWER(){
        String program = "A = POWER(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.hasSymbol("POWER(3,4)"));
        assertEquals(1,table.getSymbols().size());

    }

    @Test
    public void testCompoundNumberLOG(){
        String program = "A = LOG(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.hasSymbol("LOG(3,4)"));
        assertEquals(1,table.getSymbols().size());

    }

    @Test
    public void testCompoundNumberQUANTUM(){
        String program = "A = QUANTUM(3,4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.hasSymbol("QUANTUM(3,4)"));
        assertEquals(1,table.getSymbols().size());

    }

    @Test
    public void testCompoundNumberIsTrimmed(){
        String program = " A = MODULO(  3,   4)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.hasSymbol("MODULO(3,4)"));
        assertEquals(1,table.getSymbols().size());
    }

    @Test
    public void testNestedCompoundNumber(){
        String program = " A = MODULO(POWER(MODULO(5,5),4),LOG(QUANTUM(4,6),5))~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertTrue(table.hasSymbol("MODULO(POWER(MODULO(5,5),4),LOG(QUANTUM(4,6),5))"));
        assertEquals(1,table.getSymbols().size());
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
                "                               ARCTAN(9))))))))"+
                " ,ARCSIN(" +
                "   ARCCOS(" +
                "       ABS(" +
                "           LN(" +
                "               GAMMA LN(" +
                "                   INTEGER(" +
                "                       GAME(" +
                "                           EXP(5))))))))" +
                ")";

        String program = "A = " + function + "~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertEquals(1,table.getSymbols().size());
        String expected = "POWER(SQRT(TAN(TANH(SIN(SINH(COS(COSH(ARCTAN(9)))))))),ARCSIN(ARCCOS(ABS(LN(GAMMA LN(INTEGER(GAME(EXP(5)))))))))";
        assertEquals(expected,table.getSymbols().iterator().next().getToken());
    }

    @Test
    public void testCompoundFunctionsInMiddleOfExpression(){
        String program = "a = Time * MODULO(3,4) * DELAYP(Time,var:foo)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertEquals(1,table.getSymbols().size());
        assertTrue(table.hasSymbol("MODULO(3,4)"));

    }

    @Test
    public void testFunctionIsTransversedIfIsntCompoundDueToVariables(){
        String program = "A = MODULO(3,Time)~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        SymbolTable table = visitor.getSymbolTable(visitorContext);

        assertEquals(1,table.getSymbols().size());
        assertTrue(table.hasSymbol("3"));
    }


    //TODO Test Compound in check
    //TODO Test compound in data equations


}
