import es.uva.medeas.VensimVisitorContext;
import es.uva.medeas.parser.*;
import es.uva.medeas.rules.TableGeneratorVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Ignore;
import org.junit.Test;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;


public class TestSymbolGenerator {

    public static final Set<Symbol> NO_DEPENDENCIES = new HashSet<>();

    private ParseTree getParseTree(String file_path) throws IOException {
        File file = new File(
                getClass().getClassLoader().getResource(file_path).getFile()
        );



        FileInputStream fileInputStream = new FileInputStream(file.getPath());
        byte[] value = new byte[(int) file.length()];
        fileInputStream.read(value);
        fileInputStream.close();





        String fileContent = new String(value, StandardCharsets.UTF_8);


        ModelLexer lexer = new ModelLexer(CharStreams.fromString(fileContent));

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ModelParser parser = new ModelParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new VensimErrorListener());

        ParseTree p = parser.file();


        return p;
    }


    private SymbolTable getSymbolTable(String file_path) throws IOException {
        ParseTree tree = getParseTree(file_path);

        TableGeneratorVisitor visitor = new TableGeneratorVisitor();
        VensimVisitorContext context = new VensimVisitorContext(tree);
        return visitor.getSymbolTable(context);
    }

    private Set<Symbol> createSet(Symbol... symbols){

        return new HashSet<>(Arrays.asList(symbols));

    }


    private void assertSymbol(Symbol symbol, SymbolType expectedType, int expectedLine, Set<Symbol> expectedDependencies){
        assertNotNull("The symbol is null",symbol);
        assertEquals(expectedType,symbol.getType());
        assertEquals("Symbol '" + symbol.getToken() +"' expected at line " + expectedLine + " found at: " + symbol.getLine(),
                expectedLine,symbol.getLine());
        assertEquals(expectedDependencies,symbol.getDependencies());
        assertFalse("The symbol depends on null",expectedDependencies.contains(null));
    }


    private void assertUndefinedSymbol(Symbol symbol, SymbolType expectedType){
        assertEquals(expectedType,symbol.getType());
        assertEquals(NO_DEPENDENCIES,symbol.getDependencies());
        assertNull(symbol.getContext());
    }

    @Test
    public void testSubscript() throws IOException{

        SymbolTable table = getSymbolTable("testSubscript.mdl");


        Symbol mexico = table.getSymbol("MEXICO");
        assertSymbol(mexico,SymbolType.SUBSCRIPT_VALUE,6,NO_DEPENDENCIES);

        Symbol USA = table.getSymbol("USA");
        assertSymbol(USA,SymbolType.SUBSCRIPT_VALUE,6,NO_DEPENDENCIES);

        Symbol canada = table.getSymbol("CANADA");
        assertSymbol(canada,SymbolType.SUBSCRIPT_VALUE,7,NO_DEPENDENCIES);

        Symbol country = table.getSymbol("country");
        assertSymbol(country,SymbolType.SUBSCRIPT_NAME,6,createSet(mexico,USA,canada));

        Symbol countryCopy = table.getSymbol("copy");
        assertSymbol(countryCopy,SymbolType.SUBSCRIPT_NAME,13,NO_DEPENDENCIES);

    }

    @Test
    public void testLookup() throws IOException{

        SymbolTable table = getSymbolTable("testLookupDefinition.mdl");

        Symbol lookup = table.getSymbol("lookup distribution");
        assertSymbol(lookup,SymbolType.LOOKUP,4,NO_DEPENDENCIES);


        Symbol lookupOtherNotation = table.getSymbol("accomplishments per hour lookup");
        assertSymbol(lookupOtherNotation,SymbolType.LOOKUP,12,NO_DEPENDENCIES);

        Symbol getXLSLookup = table.getSymbol("GET XLS LOOKUPS");
        Symbol xlsLookup = table.getSymbol("testXLSLookup");

        assertSymbol(xlsLookup,SymbolType.LOOKUP,20,createSet(getXLSLookup));
    }

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
        assertSymbol(something,SymbolType.VARIABLE,9,createSet(foo)); //TODO Test constant that depends on another constant

        Symbol integ = table.getSymbol("INTEG");
        assertUndefinedSymbol(integ,SymbolType.FUNCTION);

        Symbol z = table.getSymbol("Z");
        assertSymbol(z,SymbolType.VARIABLE,11,createSet(var,foo,constant,something,integ));

        Symbol quotedEquation = table.getSymbol("\"equation inside quotes\"");
        assertSymbol(quotedEquation,SymbolType.VARIABLE,13,createSet(var,foo));




    }

    @Test
    public void testMacro() throws IOException{
        SymbolTable table = getSymbolTable("testMacro.mdl");

        Symbol var = table.getSymbol("VSMOOTH");
        assertSymbol(var,SymbolType.FUNCTION,1,NO_DEPENDENCIES);

        Symbol mynpve = table.getSymbol("MYNPVE");
        assertSymbol(mynpve,SymbolType.FUNCTION,18,NO_DEPENDENCIES);

        Symbol argumentBefore = table.getSymbol("testArgumentBefore");
        assertSymbol(argumentBefore,SymbolType.CONSTANT,14,NO_DEPENDENCIES);

        Symbol argumentAfter = table.getSymbol("testArgumentAfter");
        assertSymbol(argumentAfter,SymbolType.CONSTANT,29,NO_DEPENDENCIES);

        Symbol valueAfter = table.getSymbol("testValueAfter");
        assertSymbol(valueAfter,SymbolType.CONSTANT,32,NO_DEPENDENCIES);

        Symbol valueBefore = table.getSymbol("testValueBefore");
        assertSymbol(valueBefore,SymbolType.CONSTANT,16,NO_DEPENDENCIES);





    }

    @Test
    public void testInmediateConstant() throws IOException{
        SymbolTable table = getSymbolTable("testConstants.mdl");

        Symbol pi = table.getSymbol("PI");
        assertSymbol(pi,SymbolType.CONSTANT,1,NO_DEPENDENCIES);

        Symbol filename = table.getSymbol("filename");
        assertSymbol(filename,SymbolType.CONSTANT, 6,NO_DEPENDENCIES);
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
    public void testSubscriptSequence() throws IOException{
        SymbolTable table = getSymbolTable("subscriptSequence.mdl");


        Symbol age = table.getSymbol("age");

        Symbol age15 = table.getSymbol("AGE 15");
        Symbol age45 = table.getSymbol("AGE 45");

        assertSymbol(age,SymbolType.SUBSCRIPT_NAME,2,createSet(age15,age45));
    }

    @Test
    public void testDirectSubscripts() throws  IOException{
        SymbolTable table = getSymbolTable("testDirectSubscript.mdl");


        Symbol commodity = table.getSymbol("Commodity");
        Symbol dairy = table.getSymbol("Dairy");
        Symbol getXLS = table.getSymbol("GET XLS SUBSCRIPT");
        Symbol getDirect = table.getSymbol("GET DIRECT SUBSCRIPT");


        assertSymbol(commodity,SymbolType.SUBSCRIPT_NAME,2,createSet(getXLS));
        assertSymbol(dairy,SymbolType.SUBSCRIPT_NAME,9,createSet(getDirect));
    }


        //TODO test #lookupArg


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

        //TODO Test calling a function that isnt pure
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
        //TODO test time
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

    //TODO testear que pilla las dependencias bien a pesar del orden en el que esten puestas.
}
