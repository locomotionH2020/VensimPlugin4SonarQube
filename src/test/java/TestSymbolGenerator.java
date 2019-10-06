
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import es.uva.medeas.VensimVisitorContext;
import es.uva.medeas.parser.*;
import es.uva.medeas.rules.TableGeneratorVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


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

        return parser.file();
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
        assertEquals(expectedType,symbol.getType());
        assertEquals(expectedLine,symbol.getLine());
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

        assertEquals(4,table.getSymbols().size());
    }

    @Test
    public void testLookup() throws IOException{
        SymbolTable table = getSymbolTable("testLookup.mdl");

        Symbol lookup = table.getSymbol("lookup distribution");
        assertSymbol(lookup,SymbolType.LOOKUP,1,NO_DEPENDENCIES);

        assertEquals(1,table.getSymbols().size());

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




    }




}
