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
        assertNotNull(symbol);
        assertEquals(expectedType,symbol.getType());
        assertEquals("Symbol '" + symbol.getToken() +"' expected at line " + expectedLine + " found at: " + symbol.getLine(),
                expectedLine,symbol.getLine());
        assertEquals(expectedDependencies,symbol.getDependencies());
    }


    private void assertUndefinedSymbol(Symbol symbol, SymbolType expectedType){
        assertEquals(expectedType,symbol.getType());
        assertEquals(NO_DEPENDENCIES,symbol.getDependencies());
        assertNull(symbol.getContext());
    }

    @Test
    public void testSubscript() throws IOException{

        SymbolTable table = getSymbolTable("testSubscript.mdl");

        Symbol country = table.getSymbol("country");
        assertSymbol(country,SymbolType.SUBSCRIPT_NAME,1,NO_DEPENDENCIES);

        Symbol mexico = table.getSymbol("MEXICO");
        assertSymbol(mexico,SymbolType.SUBSCRIPT_VALUE,1,NO_DEPENDENCIES);

        Symbol USA = table.getSymbol("USA");
        assertSymbol(USA,SymbolType.SUBSCRIPT_VALUE,1,NO_DEPENDENCIES);

        Symbol canada = table.getSymbol("CANADA");
        assertSymbol(canada,SymbolType.SUBSCRIPT_VALUE,2,NO_DEPENDENCIES);

        Symbol countryCopy = table.getSymbol("copy");
        assertSymbol(countryCopy,SymbolType.SUBSCRIPT_NAME,8,NO_DEPENDENCIES);

        assertEquals(5,table.getSymbols().size());
    }

    @Test
    public void testLookup() throws IOException{
        SymbolTable table = getSymbolTable("testLookup.mdl");
        table.print();

        Symbol lookup = table.getSymbol("lookup distribution");
        assertSymbol(lookup,SymbolType.LOOKUP,1,NO_DEPENDENCIES);


        Symbol lookupOtherNotation = table.getSymbol("accomplishments per hour lookup");
        assertSymbol(lookupOtherNotation,SymbolType.LOOKUP,9,NO_DEPENDENCIES);

    //TODO testear después que una llamada  a dicho lookup no cambia el tipo del símbolo a función.

    }

    @Test
    public void testEquation() throws IOException{
        SymbolTable table = getSymbolTable("testEquation.mdl");

        Symbol var = table.getSymbol("var");
        assertSymbol(var,SymbolType.UNDETERMINED,3,NO_DEPENDENCIES);


        Symbol foo = table.getSymbol("foo");
        assertSymbol(foo,SymbolType.UNDETERMINED,5,createSet(var));

        Symbol constant = table.getSymbol("constant");
        assertSymbol(constant,SymbolType.UNDETERMINED,7,NO_DEPENDENCIES);

        Symbol something = table.getSymbol("something");
        assertSymbol(something,SymbolType.UNDETERMINED,9,createSet(foo));

        Symbol integ = table.getSymbol("INTEG");
        assertUndefinedSymbol(integ,SymbolType.FUNCTION);

        Symbol z = table.getSymbol("Z");
        assertSymbol(z,SymbolType.UNDETERMINED,11,createSet(var,foo,constant,something,integ));

        Symbol dataEquation = table.getSymbol("\"data_equation inside quotes\"");
        assertSymbol(dataEquation,SymbolType.UNDETERMINED,13,createSet(var,foo));




    }

    @Test
    public void testMacro() throws IOException{
        SymbolTable table = getSymbolTable("testMacro.mdl");

        Symbol var = table.getSymbol("VSMOOTH");
        assertSymbol(var,SymbolType.FUNCTION,1,NO_DEPENDENCIES);

        Symbol mynpve = table.getSymbol("MYNPVE");
        assertSymbol(mynpve,SymbolType.FUNCTION,13,NO_DEPENDENCIES);


        assertEquals(2,table.getSymbols().size());


    }

    @Test
    public void testInmediateConstant() throws IOException{
        //TODO puedo quitarme el IOException?
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



    @Ignore
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

        fail("Todavía no se cómo gestionar la variable 'DELAYP pipeline");
    }

    @Test
    public void testTabbedArray() throws  IOException{
        //TODO Comprobar si puede haber variables dentro de las tabbed arrays.
        SymbolTable table = getSymbolTable("testTabbedArray.mdl");

        Symbol population = table.getSymbol("initial population");
        Symbol tabbedArrayFunc = table.getSymbol("TABBED ARRAY");
        assertSymbol(population,SymbolType.UNDETERMINED,5,createSet(tabbedArrayFunc));
    }

    @Test
    public void testSubscriptSequence() throws IOException{
        SymbolTable table = getSymbolTable("subscriptSequence.mdl");
        table.print();

        Symbol age = table.getSymbol("age");

        Symbol age15 = table.getSymbol("AGE 15");
        Symbol age45 = table.getSymbol("AGE 45");

        assertSymbol(age,SymbolType.SUBSCRIPT_NAME,2,createSet(age15,age45));
    }

    @Test
    public void testDirectSubscripts() throws  IOException{
        SymbolTable table = getSymbolTable("testDirectSubscript.mdl");

        table.print();
        Symbol commodity = table.getSymbol("Commodity");
        Symbol dairy = table.getSymbol("Dairy");
        Symbol getXLS = table.getSymbol("GET XLS SUBSCRIPT");
        Symbol getDirect = table.getSymbol("GET DIRECT SUBSCRIPT");


        assertSymbol(commodity,SymbolType.SUBSCRIPT_NAME,2,createSet(getXLS));
        assertSymbol(dairy,SymbolType.SUBSCRIPT_NAME,9,createSet(getDirect));
    }


}
