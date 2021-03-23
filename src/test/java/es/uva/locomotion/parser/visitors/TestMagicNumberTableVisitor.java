package es.uva.locomotion.parser.visitors;

import es.uva.locomotion.model.*;
import es.uva.locomotion.model.Number;
import es.uva.locomotion.model.NumberTable;

import static org.junit.Assert.*;


import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.internal.apachecommons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static es.uva.locomotion.testutilities.RuleTestUtilities.getVisitorContextFromString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(Set.of(1,2),table.getNumber("3").getLines() );
        assertEquals(Set.of(1),table.getNumber("4").getLines() );

    }


    @Test
    public void testExceptionDirectConstantEquation(){
        String program = "A = 3 ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertFalse(table.hasNumber("3"));

    }

    @Test
    public void testExceptionDirectConstantUnchangeableConstant(){
        String program = "A == 3 ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertFalse(table.hasNumber("3"));
    }

    @Test
    public void testExceptionDirectConstantDataEquation(){
        String program = "A := 3 ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertFalse(table.hasNumber("3"));
    }


    @Test
    public void testRepeatedNumbersInTheSameLineCount(){
        String program = "A = 3 * 3 * 3 * 3 ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(Set.of(1),table.getNumber("3").getLines() );

    }

    @Test
    public void testExceptionTabbedArray(){
        String program = "TABBED_ARRAY = TABBED ARRAY(3    3    3    3\n" +
                "3    3    3    3\n" +
                "3    3    3    3)\n" +
                "~~ |";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertFalse(table.hasNumber("3") );

    }

    @Test
    public void testExceptionInBidimensionalArraysEquation(){
        String program = "initial population[country,blood type] = 1,2,3,4;5,6,7,8;\n" +
                "         9,10,11,12; ~Person~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty());
    }

    @Test
    public void testExceptionBidimensionalArraysDataEquation(){
        String program = "initial population[country,blood type] := 1,2,3,4;5,6,7,8;\n" +
                "         9,10,11,12; ~Person~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty());
    }

    @Test
    public void testExceptionBidimensionalArraysUnchangeableConstant(){
        String program = "initial population[country,blood type] == 1,2,3,4;5,6,7,8;\n" +
                "         9,10,11,12; ~Person~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty());
    }

    @Test
    public void testExceptionSwitchLeftSide(){
        String program = "Other Forcings RCP[scenarios]:INTERPOLATE::=\n" +
                "IF THEN ELSE(SWITCH_RCP[scenarios]=1, Other Forcings RCP Scenario[RCP26], \n" +
                "IF THEN ELSE(SWITCH_RCP[scenarios]=2, Other Forcings RCP Scenario[RCP45],\n" +
                "IF THEN ELSE(SWITCH_RCP[scenarios]=3, Other Forcings RCP Scenario[RCP60], Other Forcings RCP Scenario\\\n" +
                "[RCP85])))~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());


        assertTrue(table.getNumbers().isEmpty());
    }

    @Test
    public void testExceptionSwitchRightSide(){
        String program = "Other Forcings RCP[scenarios]:INTERPOLATE::=\n" +
                "IF THEN ELSE(1=SWITCH_RCP[scenarios], Other Forcings RCP Scenario[RCP26], \n" +
                "IF THEN ELSE(2=SWITCH_RCP[scenarios], Other Forcings RCP Scenario[RCP45],\n" +
                "IF THEN ELSE(3=SWITCH_RCP[scenarios], Other Forcings RCP Scenario[RCP60], Other Forcings RCP Scenario\\\n" +
                "[RCP85])))~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty());
    }


    @Test
    public void testExceptionSwitchAloneRightSide(){
        String program = "Other Forcings RCP[scenarios]=\n"+
                "                IF THEN ELSE(1=SWITCH, Time,Time*CONST)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("1"));
    }

    @Test
    public void testExceptionSwitchAndUnderscoreAloneRightSide(){
        String program = "Other Forcings RCP[scenarios]=\n"+
                "                IF THEN ELSE(1=SWITCH_, Time,Time*CONST)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("1"));
    }

    @Test
    public void testExceptionSwitchContainsWordButNotAtTheBeginningRightSide(){
        String program = "Other Forcings RCP[scenarios]=\n"+
                "                IF THEN ELSE(1=EMISSIONS_SWITCH, Time,Time*CONST)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("1"));
    }

    @Test
    public void testExceptionSwitchAndMoreTextRightSide(){
        String program = "Other Forcings RCP[scenarios]=\n"+
                "                IF THEN ELSE(1=SWITCHOPTION_ONE, Time,Time*CONST)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("1"));
    }



    @Test
    public void testExceptionSwitchAloneLeftSide(){
        String program = "Other Forcings RCP[scenarios]=\n"+
                "                IF THEN ELSE(SWITCH=1, Time,Time*CONST)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("1"));
    }

    @Test
    public void testExceptionSwitchAndUnderscoreAloneLeftSide(){
        String program = "Other Forcings RCP[scenarios]=\n"+
                "                IF THEN ELSE(SWITCH_=1, Time,Time*CONST)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("1"));
    }

    @Test
    public void testExceptionSwitchContainsWordButNotAtTheBeginningLeftSide(){
        String program = "Other Forcings RCP[scenarios]=\n"+
                "                IF THEN ELSE(EMISSIONS_SWITCH=1, Time,Time*CONST)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("1"));
    }

    @Test
    public void testExceptionSwitchAndMoreTextLeftSide(){
        String program = "Other Forcings RCP[scenarios]=\n"+
                "                IF THEN ELSE(SWITCHOPTION_ONE=1, Time,Time*CONST)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("1"));
    }

    @Test
    public void testTraversesFunctionPath(){
        String program = "var = - Function( 3, ANOTHER Function(3),3)~ ~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(Set.of(1),table.getNumber("3").getLines() );
    }

    @Test
    public void testTableIncludesFloats(){
        String program = "\nFOO = 0.3 * 0.3~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(Set.of(2),table.getNumber("0.3").getLines() );

    }


    @Test
    public void testTransversesExprOperationsAndKeyword(){
        String program = "FOO = (3 ^ (3)) * 3 / 3 - 3 + (3<3 :AND: 3>3 :AND: 3<=3 :AND: 3>=3 :AND: :NOT: 3 = 3 :OR: 3 <> 3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());


        int expectedThrees = StringUtils.countMatches(program,"3");
        assertEquals(Set.of(1),table.getNumber("3").getLines() );
    }

    @Test
    public void testParenthesisCount(){
        String program = "A = ((3)*3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(Set.of(1),table.getNumber("3").getLines() );
    }

    @Test
    public void testSignNumbers(){
        String program = "A = -3 * +4 ~~|";


        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(Set.of(1),table.getNumber("-3").getLines() );
        assertEquals(Set.of(1),table.getNumber("4").getLines() );
    }

    @Test
    public void testExponentNumbers(){
        String program = "A = -10e+5 * +10E-9 ~~|";


        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(Set.of(1),table.getNumber("-1000000").getLines() );
        assertEquals(Set.of(1),table.getNumber("1.0E-8").getLines() );
    }

    @Test
    public void testNumbersInEquationIdDontCount(){
        String program = "NUMBER 1 = Time~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertFalse(table.hasNumber("1") );

    }

    @Test
    public void testNumbersInNumberIdDontCount(){
        String program = "FOO = NUMBER 1 ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertFalse(table.hasNumber("1") );
    }

    @Test
    public void testNumbersInCalledFunctionIdDontCount(){
        String program = "foo = Function 1(Time)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertFalse(table.hasNumber("1") );

    }

    @Test
    public void testExceptionLookup(){
        String program = "myLookup(" + "[(0,0)-(10,10),(1.25382,3.55263),(3.85321,4.5614),(7.15596,7.67544),(8.74618,9.21053)]," +
        "(1.25382,3.55263),(3.42508,3.64035),(3.85321,4.5614),(4.8318,7.80702),(6.36086,4.91228 ),(7.15596,7.67544)," +
                "(7.18654,5.92105),(8.74618,9.21053))~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testExceptionLookupNNumberListFormat(){
        String program = "accomplishments per hour lookup(0,0.2,0.4,0.6,0.8,1,\n" +
                "                                         0,0.2,0.4,0.6,0.8,1)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testDelayPCounts(){
        String program = "a = DELAYP(1,1:Time)~~|";
        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(Set.of(1),table.getNumber("1").getLines() );
    }

    @Test
    public void testTransversesMacros(){
        String program = ":MACRO: VSMOOTH(input,SMOOTH TIME)\n" +
                "Vsmooth = INTEG((input - Vsmooth)/SMOOTH TIME*3, input)\n ~ The first order smoothed value of a variable.~|\n" +
                ":END OF MACRO:";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(Set.of(2),table.getNumber("3").getLines() );


    }

    @Test
    public void testExceptionLookupInsideFunctionEquation(){
        String program = "var =WITH LOOKUP(6,((0,1),(1,1),(2,2)))\n~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(1,table.getNumbers().size());
        assertEquals("6",table.getNumbers().iterator().next().getToken());
    }


    @Test
    public void testExceptionLookupInsideFunctionDataEquation(){
        String program = "var :=WITH LOOKUP(6,((0,1),(1,1),(2,2)))\n~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(1,table.getNumbers().size());
        assertEquals("6",table.getNumbers().iterator().next().getToken());
    }


    @Test
    public void testExceptionNumberListEquation(){
        String program = "A = 1,2,3,4,5~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );

    }

    @Test
    public void testExceptionNumberListDataEquation(){
        String program = "A := 1,2,3,4,5~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );

    }

    @Test
    public void testNumberListDoesntCountUnchangeableConstant(){
        String program = "A := 1,2,3,4,5~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );

    }


    @Test
    public void testSameNumberWithScientificNotationAndWithoutItIsTheSame(){
        String program = "A = 1.0 * 10e-1  ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(1,table.getNumbers().size());
        assertEquals(Set.of(1),table.getNumber("1").getLines() );
    }

    @Test
    public void testSameNumberWithAndWithoutSignIsTheSame(){
        String program = "A = 3 * +3  ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(1,table.getNumbers().size());
        assertEquals(Set.of(1),table.getNumber("3").getLines() );
    }

    @Test
    public void testSameNumberAsIntAndFloatIsTheSame(){
        String program = "A = 6 * 6.00000 ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(1,table.getNumbers().size());
        assertEquals(Set.of(1),table.getNumber("6").getLines() );

    }

    @Test
    public void testSameNumberButWithDifferentNumberOfSignsIsTheSame(){
        String program = "A = 3 +  --3 + 4.3 + --4.3 ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(Set.of(1),table.getNumber("3").getLines() );
        assertEquals(Set.of(1),table.getNumber("4.3").getLines() );
    }


    @Test
    public void testCompoundNumberIsTrimmed(){
        String program = " A = Time * MODULO(  3,   4)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("MODULO(3,4)"));
        assertEquals(1,table.getNumbers().size());
    }

    @Test
    public void testCompoundNumberTextIncludesParenthesis(){
        String program = " A = Time * GAME(((2)))~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("GAME(((2)))"));
        assertEquals(1,table.getNumbers().size());
    }

    @Test
    public void testCompoundNumberInSeveralLines(){
        String program = "\n\n A = Time * LN(" +
                "SQRT(2))~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        Number number = table.getNumber("LN(SQRT(2))");
        assertNotNull(number);
        assertEquals(Set.of(3),number.getLines());
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

        String program = "A = Time *" + function + "~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(1,table.getNumbers().size());
        String expected = "POWER(SQRT(TAN(TANH(SIN(SINH(COS(COSH(ARCTAN(ARCSIN(ARCCOS(ABS(9))))))))))),LN(GAMMA LN(INTEGER(GAME(EXP(MODULO(QUANTUM(3,7),LOG(3,7))))))))";
        assertEquals(expected,table.getNumbers().iterator().next().getToken());
    }



    @Test
    public void testFunctionIsTransversedIfIsntCompoundDueToVariablesNotAlone(){
        String program = "A = Time * MODULO(3,Time)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(1,table.getNumbers().size());
        assertTrue(table.hasNumber("3"));
    }

    @Test
    public void testFunctionIsTransversedIfIsntCompoundDueToVariablesAlone(){
        String program = "A = MODULO(3,Time)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(1,table.getNumbers().size());
        assertTrue(table.hasNumber("3"));
    }

    @Test
    public void testFunctionIsTransversedIfIsntCompoundDueToCall(){
        String program = "A = Time * QUANTUM(3,RANDOM(3))~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(1,table.getNumbers().size());
        assertTrue(table.hasNumber("3"));
    }

    @Test
    public void testFunctionIsTransversedIfIsntCompoundDueToCallAlone(){
        String program = "A = Time * QUANTUM(3,RANDOM(3))~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(1,table.getNumbers().size());
        assertTrue(table.hasNumber("3"));
    }



    @Test
    public void testFunctionIsTransversedIfIsntCompoundDueToConstantNotAlone(){
        String program = "CONST = 4~~|" +
                "C = Time * LOG(3,CONST)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(1,table.getNumbers().size());
        assertTrue(table.hasNumber("3"));
    }

    @Test
    public void testFunctionIsTransversedIfIsntCompoundDueToConstantAlone(){
        String program = "CONST = 4~~|" +
                "C = LOG(3,CONST)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(1,table.getNumbers().size());
        assertTrue(table.hasNumber("3"));
    }

    @Test
    public void testTransversesCompoundsIfAlmostNested(){
        String program = "A = FUNC(Time,MODULO(3,4),5)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertEquals(2,table.getNumbers().size());
        assertTrue(table.hasNumber("MODULO(3,4)"));
        assertTrue(table.hasNumber("5"));
    }


    @Test
    public void testExceptionAloneSQRTEquation(){
        String program = "A = SQRT(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberSQRTEquation(){
        String program = "A = Time *  SQRT(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("SQRT(3)"));
    }

    @Test
    public void testAloneExceptionSQRTDataEquation(){
        String program = "A := SQRT(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberSQRTDataEquation(){
        String program = "A := Time * SQRT(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("SQRT(3)"));
    }





    @Test
    public void testExceptionAloneTANEquation(){
        String program = "A = TAN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberTANEquation(){
        String program = "A = Time *  TAN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("TAN(3)"));
    }

    @Test
    public void testAloneExceptionTANDataEquation(){
        String program = "A := TAN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberTANDataEquation(){
        String program = "A := Time * TAN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("TAN(3)"));
    }





    @Test
    public void testExceptionAloneTANHEquation(){
        String program = "A = TANH(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberTANHEquation(){
        String program = "A = Time *  TANH(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("TANH(3)"));
    }

    @Test
    public void testAloneExceptionTANHDataEquation(){
        String program = "A := TANH(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberTANHDataEquation(){
        String program = "A := Time * TANH(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("TANH(3)"));
    }



    @Test
    public void testExceptionAloneSINEquation(){
        String program = "A = SIN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberSINEquation(){
        String program = "A = Time *  SIN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("SIN(3)"));
    }

    @Test
    public void testAloneExceptionSINDataEquation(){
        String program = "A := SIN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberSINDataEquation(){
        String program = "A := Time * SIN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("SIN(3)"));
    }





    @Test
    public void testExceptionAloneSINHEquation(){
        String program = "A = SINH(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberSINHEquation(){
        String program = "A = Time *  SINH(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("SINH(3)"));
    }

    @Test
    public void testAloneExceptionSINHDataEquation(){
        String program = "A := SINH(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberSINHDataEquation(){
        String program = "A := Time * SINH(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("SINH(3)"));
    }





    @Test
    public void testExceptionAloneCOSEquation(){
        String program = "A = COS(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberCOSEquation(){
        String program = "A = Time *  COS(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("COS(3)"));
    }

    @Test
    public void testAloneExceptionCOSDataEquation(){
        String program = "A := COS(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberCOSDataEquation(){
        String program = "A := Time * COS(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("COS(3)"));
    }





    @Test
    public void testExceptionAloneCOSHEquation(){
        String program = "A = COSH(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberCOSHEquation(){
        String program = "A = Time *  COSH(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("COSH(3)"));
    }

    @Test
    public void testAloneExceptionCOSHDataEquation(){
        String program = "A := COSH(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberCOSHDataEquation(){
        String program = "A := Time * COSH(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("COSH(3)"));
    }





    @Test
    public void testExceptionAloneARCTANEquation(){
        String program = "A = ARCTAN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberARCTANEquation(){
        String program = "A = Time *  ARCTAN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("ARCTAN(3)"));
    }

    @Test
    public void testAloneExceptionARCTANDataEquation(){
        String program = "A := ARCTAN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberARCTANDataEquation(){
        String program = "A := Time * ARCTAN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("ARCTAN(3)"));
    }





    @Test
    public void testExceptionAloneARCSINEquation(){
        String program = "A = ARCSIN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberARCSINEquation(){
        String program = "A = Time *  ARCSIN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("ARCSIN(3)"));
    }

    @Test
    public void testAloneExceptionARCSINDataEquation(){
        String program = "A := ARCSIN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberARCSINDataEquation(){
        String program = "A := Time * ARCSIN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("ARCSIN(3)"));
    }





    @Test
    public void testExceptionAloneARCCOSEquation(){
        String program = "A = ARCCOS(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberARCCOSEquation(){
        String program = "A = Time *  ARCCOS(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("ARCCOS(3)"));
    }

    @Test
    public void testAloneExceptionARCCOSDataEquation(){
        String program = "A := ARCCOS(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberARCCOSDataEquation(){
        String program = "A := Time * ARCCOS(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("ARCCOS(3)"));
    }





    @Test
    public void testExceptionAloneABSEquation(){
        String program = "A = ABS(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberABSEquation(){
        String program = "A = Time *  ABS(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("ABS(3)"));
    }

    @Test
    public void testAloneExceptionABSDataEquation(){
        String program = "A := ABS(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberABSDataEquation(){
        String program = "A := Time * ABS(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("ABS(3)"));
    }





    @Test
    public void testExceptionAloneLNEquation(){
        String program = "A = LN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberLNEquation(){
        String program = "A = Time *  LN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("LN(3)"));
    }

    @Test
    public void testAloneExceptionLNDataEquation(){
        String program = "A := LN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberLNDataEquation(){
        String program = "A := Time * LN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("LN(3)"));
    }





    @Test
    public void testExceptionAloneGAMMALNEquation(){
        String program = "A = GAMMA LN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberGAMMALNEquation(){
        String program = "A = Time *  GAMMA LN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("GAMMA LN(3)"));
    }

    @Test
    public void testAloneExceptionGAMMALNDataEquation(){
        String program = "A := GAMMA LN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberGAMMALNDataEquation(){
        String program = "A := Time * GAMMA LN(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("GAMMA LN(3)"));
    }





    @Test
    public void testExceptionAloneINTEGEREquation(){
        String program = "A = INTEGER(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberINTEGEREquation(){
        String program = "A = Time *  INTEGER(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("INTEGER(3)"));
    }

    @Test
    public void testAloneExceptionINTEGERDataEquation(){
        String program = "A := INTEGER(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberINTEGERDataEquation(){
        String program = "A := Time * INTEGER(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("INTEGER(3)"));
    }





    @Test
    public void testExceptionAloneGAMEEquation(){
        String program = "A = GAME(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberGAMEEquation(){
        String program = "A = Time *  GAME(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("GAME(3)"));
    }

    @Test
    public void testAloneExceptionGAMEDataEquation(){
        String program = "A := GAME(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberGAMEDataEquation(){
        String program = "A := Time * GAME(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("GAME(3)"));
    }





    @Test
    public void testExceptionAloneEXPEquation(){
        String program = "A = EXP(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberEXPEquation(){
        String program = "A = Time *  EXP(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("EXP(3)"));
    }

    @Test
    public void testAloneExceptionEXPDataEquation(){
        String program = "A := EXP(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberEXPDataEquation(){
        String program = "A := Time * EXP(3)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("EXP(3)"));
    }


    @Test
    public void testExceptionAloneModuloEquation(){
        String program = "A = MODULO(3,4)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberModuloEquation(){
        String program = "A = Time *  MODULO(3,4)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("MODULO(3,4)"));
        assertFalse(table.hasNumber("3"));
        assertFalse(table.hasNumber("4"));
    }

    @Test
    public void testAloneExceptionModuloDataEquation(){
        String program = "A := MODULO(3,4)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }



    @Test
    public void testCompoundNumberModuloDataEquation(){
        String program = "A := Time * MODULO(3,4)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("MODULO(3,4)"));
        assertFalse(table.hasNumber("3"));
        assertFalse(table.hasNumber("4"));
    }


    @Test
    public void testExceptionAlonePowerEquation(){
        String program = "A = POWER(3,4)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberPowerEquation(){
        String program = "A = Time *  POWER(3,4)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("POWER(3,4)"));
        assertFalse(table.hasNumber("3"));
        assertFalse(table.hasNumber("4"));
    }

    @Test
    public void testAloneExceptionPowerDataEquation(){
        String program = "A := POWER(3,4)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }



    @Test
    public void testCompoundNumberPowerDataEquation(){
        String program = "A := Time * POWER(3,4)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("POWER(3,4)"));
        assertFalse(table.hasNumber("3"));
        assertFalse(table.hasNumber("4"));
    }

    @Test
    public void testExceptionAloneLOGEquation(){
        String program = "A = LOG(3,4)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberLOGEquation(){
        String program = "A = Time *  LOG(3,4)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("LOG(3,4)"));
        assertFalse(table.hasNumber("3"));
        assertFalse(table.hasNumber("4"));
    }

    @Test
    public void testAloneExceptionLOGDataEquation(){
        String program = "A :=  LOG(3,4)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }



    @Test
    public void testCompoundNumberLOGDataEquation(){
        String program = "A := Time * LOG(3,4)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("LOG(3,4)"));
        assertFalse(table.hasNumber("3"));
        assertFalse(table.hasNumber("4"));
    }



    @Test
    public void testExceptionAloneQuantumEquation(){
        String program = "A = QUANTUM(3,4)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }

    @Test
    public void testCompoundNumberQuantumEquation(){
        String program = "A = Time *  QUANTUM(3,4)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("QUANTUM(3,4)"));
        assertFalse(table.hasNumber("3"));
        assertFalse(table.hasNumber("4"));
    }

    @Test
    public void testAloneExceptionQuantumDataEquation(){
        String program = "A := QUANTUM(3,4)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.getNumbers().isEmpty() );
    }



    @Test
    public void testCompoundNumberQuantumDataEquation(){
        String program = "A := Time * QUANTUM(3,4)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("QUANTUM(3,4)"));
        assertFalse(table.hasNumber("3"));
        assertFalse(table.hasNumber("4"));
    }

    

    @Test
    public void testCompoundNumberQUANTUM(){
        String program = "A = Time * QUANTUM(3,4)~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("QUANTUM(3,4)"));
        assertEquals(1,table.getNumbers().size());

    }

    @Test
    public void testMagicNumberFilter(){
        String program = "A = 3*3*3*3*3~~|\n B = 4*4*4*4*4~~|";

        SymbolTable SymbolTable = new SymbolTable();
        Symbol Symbol1 = new Symbol("A");
        Symbol1.setFiltered(true);
        SymbolTable.addSymbol(Symbol1);
        Symbol Symbol2 = new Symbol("B");
        Symbol2.setFiltered(false);
        SymbolTable.addSymbol(Symbol2);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        visitor.setSymbols(SymbolTable);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertFalse(table.hasNumber("3"));
        assertTrue(table.hasNumber("4"));
        assertEquals(1,table.getNumbers().size());

    }

    @Test
    public void testMagicNumberFilterAllFiltered(){
        String program = "A = 3*3*3*3*3~~|\n B = 4*4*4*4*4~~|";

        SymbolTable SymbolTable = new SymbolTable();
        Symbol Symbol1 = new Symbol("A");
        Symbol1.setFiltered(true);
        SymbolTable.addSymbol(Symbol1);
        Symbol Symbol2 = new Symbol("B");
        Symbol2.setFiltered(true);
        SymbolTable.addSymbol(Symbol2);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        visitor.setSymbols(SymbolTable);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertFalse(table.hasNumber("3"));
        assertFalse(table.hasNumber("4"));
        assertEquals(0,table.getNumbers().size());

    }

    @Test
    public void testMagicNumberFilterSameNumber(){
        String program = "A = 3*3*3*3*3~~|\n B = 3*3*3*5~~| \n C=3*4~~|";

        SymbolTable SymbolTable = new SymbolTable();
        Symbol Symbol1 = new Symbol("A");
        Symbol1.setFiltered(false);
        SymbolTable.addSymbol(Symbol1);
        Symbol Symbol2 = new Symbol("B");
        Symbol2.setFiltered(true);
        SymbolTable.addSymbol(Symbol2);
        Symbol Symbol3 = new Symbol("C");
        Symbol3.setFiltered(false);
        SymbolTable.addSymbol(Symbol3);

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        visitor.setSymbols(SymbolTable);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        assertTrue(table.hasNumber("3"));
        assertEquals(2, table.getNumber("3").getLines().size());
        assertEquals(2,table.getNumbers().size());

    }

    @Test
    public void testMagicNumberFilterNumberNotInNumberTable(){
        String program = "A = 3*3*3*3*3~~|\n B = 3*3*3~~|";

        SymbolTable SymbolTable = new SymbolTable();
        Symbol Symbol1 = new Symbol("A");
        Symbol1.setFiltered(false);
        SymbolTable.addSymbol(Symbol1);

        VensimLogger logger = mock(VensimLogger.class);
        MagicNumberTableVisitor.LOG = logger;


        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        visitor.setSymbols(SymbolTable);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        verify(logger).error("Found symbol \"B\" that is not in the symbol table");


    }

    @Test
    public void testMagicNumberFilterNumberTableUnassigned(){
        String program = "A = 3*3*3*3*3~~|";



        VensimLogger logger = mock(VensimLogger.class);
        MagicNumberTableVisitor.LOG = logger;


        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        verify(logger).unique("Symbol table unassigned in MagicNumberVisitor", LoggingLevel.INFO);

    }

    @Test
    public void testMagicNumberFilterNumberTableIsNull(){
        String program = "A = 3*3*3*3*3~~|";

        VensimLogger logger = mock(VensimLogger.class);
        MagicNumberTableVisitor.LOG = logger;

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        visitor.setSymbols(null);
        NumberTable table = visitor.getNumberTable(visitorContext.getRootNode());

        verify(logger).unique("Symbol table unassigned in MagicNumberVisitor", LoggingLevel.INFO);

    }
}
