package es.uva.locomotion.parser.visitors;



import es.uva.locomotion.parser.Model;
import org.junit.Before;
import org.junit.Test;

import static es.uva.locomotion.testutilities.GeneralTestUtilities.getParseTreeFromString;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class TestCompoundNumberVisitor {

    private CompoundMagicNumberVisitor visitor;

    @Before
    public void setUp(){
        visitor = new CompoundMagicNumberVisitor();
    }

    private Model.CallContext getAloneCallExprFromProgram(String program) {
        Model.FileContext tree = getParseTreeFromString(program);

        return ((Model.CallExprContext) tree.model().symbolWithDoc(0).symbolWithDocDefinition().equation().expr().children.get(0)).call();
    }

    @Test
    public void testValidCompoundNestedCalls(){
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
                "                           EXP(" +
                "                               LOG(3," +
                "                                   MODULO(3," +
                "                                       QUANTUM(2)))))))))))" +
                ")";

        String program = "A = " + function + "~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call));
    }

    @Test
    public void testCallContainsVariables(){
        String program = "A = LOG(3,Time)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertFalse(visitor.callIsACompoundNumber(call));
    }

    @Test
    public void testCallContainsConstants(){
        String program = "A = QUANTUM(3,UNDEFINED_CONSTANT)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertFalse(visitor.callIsACompoundNumber(call));
    }

    @Test
    public void tesCallContainsWildcard(){
        String program = "A = MODULO(3,*)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertFalse(visitor.callIsACompoundNumber(call));
    }

    @Test
    public void testNestedCallWithVariable(){
        String program = "A = SQRT(LOG(3,Time))~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertFalse(visitor.callIsACompoundNumber(call));

    }

    @Test
    public void testNestedCallWithConstant(){
        String program = "A = MODULO(3,LN(UNDEFINED_CONSTANT))~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertFalse(visitor.callIsACompoundNumber(call));
    }

    @Test
    public void testNestedCallWithNonCompoundCall(){
        String program = "A = MODULO(3,RANDOM(4))~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertFalse(visitor.callIsACompoundNumber(call));
    }

    @Test
    public void testNestedCallWithWildcard(){
        String program = "A = MODULO(3,SQRT(*))~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertFalse(visitor.callIsACompoundNumber(call));
    }


    @Test
    public void testCallToNonCompoundFunction(){
        String program = "A = RANDOM_FUNCTION(3)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertFalse(visitor.callIsACompoundNumber(call));
    }

    @Test
    public void testValidCompoundWithExtraParenthesis(){
        String program = "A = GAMMA LN((((3))))~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call));
    }


    @Test
    public void testValidCompoundTwoExpressionsWithKeyword(){
        String program = "A = ARCCOS(3 :AND: 4)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call));
    }

    @Test
    public void testValidCompoundExpressionWithKeyword(){
        String program = "A = ARCSIN(:NOT: 4)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call));
    }

    @Test
    public void testValidCompoundWithFloatingLiteral(){
        String program = "A = ARCTAN(-3e-4)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call));

    }

    @Test
    public void testCallWithStringLiteral(){
        String program = "A = MODULO(3,'Honk')~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertFalse(visitor.callIsACompoundNumber(call));
    }

    @Test
    public void testValidCompoundWithOperations(){
        String program = "A = COS(3*4+1 - SIN(5))~~|";
        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call));
    }



    @Test
    public void testValidCompoundSQRT(){
        String program = "A = SQRT(3)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }



    @Test
    public void testValidCompoundTAN(){
        String program = "A = TAN(3)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }



    @Test
    public void testValidCompoundTANH(){
        String program = "A = TANH(3)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }



    @Test
    public void testValidCompoundSIN(){
        String program = "A = SIN(3)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }



    @Test
    public void testValidCompoundSINH(){
        String program = "A = SINH(3)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }



    @Test
    public void testValidCompoundCOS(){
        String program = "A = COS(3)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }



    @Test
    public void testValidCompoundCOSH(){
        String program = "A = COSH(3)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }



    @Test
    public void testValidCompoundARCTAN(){
        String program = "A = ARCTAN(3)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }



    @Test
    public void testValidCompoundARCSIN(){
        String program = "A = ARCSIN(3)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }



    @Test
    public void testValidCompoundARCCOS(){
        String program = "A = ARCCOS(3)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }



    @Test
    public void testValidCompoundABS(){
        String program = "A = ABS(3)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }



    @Test
    public void testValidCompoundLN(){
        String program = "A = LN(3)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }



    @Test
    public void testValidCompoundGAMMALN(){
        String program = "A = GAMMA LN(3)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }



    @Test
    public void testValidCompoundINTEGER(){
        String program = "A = INTEGER(3)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }



    @Test
    public void testValidCompoundGAME(){
        String program = "A = GAME(3)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }



    @Test
    public void testValidCompoundEXP(){
        String program = "A = EXP(3)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }



    @Test
    public void testValidCompoundPOWER(){
        String program = "A = POWER(3,4)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }



    @Test
    public void testValidCompoundLOG(){
        String program = "A = LOG(3,4)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }



    @Test
    public void testValidCompoundMODULO(){
        String program = "A = MODULO(3,4)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }



    @Test
    public void testValidCompoundQUANTUM(){
        String program = "A = QUANTUM(3,4)~~|";

        Model.CallContext call = getAloneCallExprFromProgram(program);
        assertTrue(visitor.callIsACompoundNumber(call))  ;
    }


}
