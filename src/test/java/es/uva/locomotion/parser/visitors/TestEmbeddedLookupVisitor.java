package es.uva.locomotion.parser.visitors;

import com.ibm.icu.impl.Pair;
import es.uva.locomotion.model.VensimVisitorContext;
import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static es.uva.locomotion.testutilities.RuleTestUtilities.getVisitorContextFromString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TestEmbeddedLookupVisitor {

    private EmbeddedLookupVisitor visitor;

    @Before
    public void setUp() {
        visitor = new EmbeddedLookupVisitor();
    }

    @Test
    public void testValid() {
        String program = "A ([(0,0)-(10,10)],(1,2),(3,4))\n" +
                "~ MToe/Year\n" +
                "~ |\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        List<Pair<Symbol, Integer>> list = visitor.getSymbolTable(visitorContext.getRootNode());


        assertEquals(1, list.size());

        assertEquals(2, (int)list.get(0).second);

        assertEquals(Set.of(1), list.get(0).first.getLines());

    }

    @Test
    public void testValidWithoutRange() {
        String program = "A ((1,2),(3,4))\n" +
                "~ MToe/Year\n" +
                "~ |\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        List<Pair<Symbol, Integer>> list = visitor.getSymbolTable(visitorContext.getRootNode());


        assertEquals(1, list.size());

        assertEquals(2, (int)list.get(0).second);


        assertEquals(Set.of(1), list.get(0).first.getLines());
    }

    @Test
    public void testValidMultiline() {
        String program = "A ([(0,0)-(10,10)],(1,2),\n(3,4))\n" +
                "~ MToe/Year\n" +
                "~ |\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        List<Pair<Symbol, Integer>> list = visitor.getSymbolTable(visitorContext.getRootNode());


        assertEquals(1, list.size());

        assertEquals(2, (int)list.get(0).second);


        assertEquals(Set.of(1), list.get(0).first.getLines());
    }
    @Test
    public void testValidSingle() {
        String program = "A ([(0,0)-(10,10)],(1,2))\n" +
                "~ MToe/Year\n" +
                "~ |\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        List<Pair<Symbol, Integer>> list = visitor.getSymbolTable(visitorContext.getRootNode());


        assertEquals(1, list.size());

        assertEquals(1, (int)list.get(0).second);


        assertEquals(Set.of(1), list.get(0).first.getLines());
    }
    @Test
    public void testValidMultipleLookups() {
        String program = "A ([(0,0)-(10,10)],(1,2))\n" +
                "~ MToe/Year\n" +
                "~ |\n"+
                "B ([(0,0)-(10,10)],(1,2))\n" +
                "~ MToe/Year\n" +
                "~ |\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        List<Pair<Symbol, Integer>> list = visitor.getSymbolTable(visitorContext.getRootNode());


        assertEquals(2, list.size());
        assertEquals(1, (int)list.get(0).second);


        assertEquals(Set.of(4), list.get(1).first.getLines());
    }
    @Test
    public void testValidNumberMixMultipleLookups() {
        String program = "A ([(0,0)-(10,10)],(1,2))\n" +
                "~ MToe/Year\n" +
                "~ |\n"+
                "B (1,2,3,4,5,6,7,8,9,0)\n" +
                "~ MToe/Year\n" +
                "~ |\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        List<Pair<Symbol, Integer>> list = visitor.getSymbolTable(visitorContext.getRootNode());


        assertEquals(2, list.size());

        assertEquals(1, (int)list.get(0).second);
        assertEquals(10, (int)list.get(1).second);

        assertEquals(Set.of(4), list.get(1).first.getLines());
    }
    @Test
    public void testValidNumberMultipleLines() {
        String program = "B (1\n,2\n,3\n,4\n,5\n,6\n,7\n,8\n,9\n,0\n)\n" +
                "~ MToe/Year\n" +
                "~ |\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        List<Pair<Symbol, Integer>> list = visitor.getSymbolTable(visitorContext.getRootNode());


        assertEquals(1, list.size());

        assertEquals(10, (int)list.get(0).second);

        assertEquals(Set.of(1), list.get(0).first.getLines());
    }

    @Test
    public void testValidNotALookup() {
        String program = "B = 1" +
                "~ MToe/Year\n" +
                "~ |\n";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        List<Pair<Symbol, Integer>> list = visitor.getSymbolTable(visitorContext.getRootNode());


        assertEquals(0, list.size());
    }

    @Test
    public void testFilterSymbolNotInSymbolTable(){
        String program = "A ([(0,0)-(10,10)],(1,2),(3,4))\n" +
                "~ MToe/Year\n" +
                "~ |\n"+
                "B ([(0,0)-(10,10)],(1,2),(3,4))\n" +
                "~ MToe/Year\n" +
                "~ |\n";

        SymbolTable symbolTable = new SymbolTable();
        Symbol symbol1 = new Symbol("A");
        symbol1.setFiltered(false);
        symbolTable.addSymbol(symbol1);

        VensimLogger logger = mock(VensimLogger.class);
        EmbeddedLookupVisitor.logger = logger;


        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        visitor.setSymbols(symbolTable);
        List<Pair<Symbol, Integer>> list = visitor.getSymbolTable(visitorContext.getRootNode());

        verify(logger).error("Found symbol \"B\" that is not in the symbol table");


    }

    @Test
    public void testFilterSymbolTableUnassigned(){
        String program = "A ([(0,0)-(10,10)],(1,2),(3,4))\n" +
                "~ MToe/Year\n" +
                "~ |\n";



        VensimLogger logger = mock(VensimLogger.class);
        EmbeddedLookupVisitor.logger = logger;


        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        List<Pair<Symbol, Integer>> list = visitor.getSymbolTable(visitorContext.getRootNode());

        verify(logger).unique("Symbol table unassigned in EmbeddedLookupVisitor", LoggingLevel.INFO);

    }

    @Test
    public void testFilterSymbolTableIsNull(){
        String program = "A ([(0,0)-(10,10)],(1,2),(3,4))\n" +
                "~ MToe/Year\n" +
                "~ |\n";

        VensimLogger logger = mock(VensimLogger.class);
        EmbeddedLookupVisitor.logger = logger;

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        visitor.setSymbols(null);
        List<Pair<Symbol, Integer>> list = visitor.getSymbolTable(visitorContext.getRootNode());

        verify(logger).unique("Symbol table unassigned in EmbeddedLookupVisitor", LoggingLevel.INFO);

    }

}
