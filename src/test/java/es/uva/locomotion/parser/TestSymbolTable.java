package es.uva.locomotion.parser;

import  static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static es.uva.locomotion.testutilities.GeneralTestUtilities.*;

public class TestSymbolTable {

    SymbolTable table;

    @Before
    public void setUp(){
        table = new SymbolTable();
    }
    @Test
    public void testSymbolTableConstructor(){
        assertTrue(table.getSymbols().isEmpty());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testCreateSymbolAlreadyExists(){
        table.addSymbol(new Symbol("  symbol  "));
        table.addSymbol(new Symbol(" symbol   "));
    }


    @Test
    public void testAddSymbol(){
        Symbol symbol = table.addSymbol(new Symbol("mySymbol"));
        assertEquals(symbol.getToken(),"mySymbol");
        assertTrue(table.hasSymbol("mySymbol"));
        assertTrue(symbol.getDefinitionLines().isEmpty());
        assertEquals(NO_DEPENDENCIES,symbol.getDependencies());
        assertSymbolType(symbol, SymbolType.UNDETERMINED);
    }


    @Test
    public void testGetSymbols(){
        Symbol first = table.addSymbol(new Symbol("firstSymbol"));
        Symbol second = table.addSymbol(new Symbol("secondSymbol"));
        Symbol third = table.addSymbol(new Symbol("thirdSymbol"));

        Set<Symbol> tableSymbols = new HashSet<>(table.getSymbols());
        assertEquals(createSet(first,second,third), tableSymbols);
    }

    @Test
    public void testGetSymbol(){
        Symbol expectedSymbol = table.addSymbol(new Symbol("testSymbol"));
        Symbol actualSymbol = table.getSymbol("testSymbol");

        assertSame(expectedSymbol,actualSymbol);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSymbolDoesntExist(){
        table.getSymbol("doesntExist");
    }

    @Test
    public void testHasSymbolFalse(){
        assertFalse(table.hasSymbol("nope"));
    }

    @Test
    public void testGetUndeterminedSymbols(){
        for(SymbolType type: SymbolType.values()){
            Symbol symbol = table.addSymbol(new Symbol(type.toString()));
            symbol.setType(type);
        }

        Symbol undetermined = table.getSymbol(SymbolType.UNDETERMINED.toString());
        Symbol undetermined_function = table.getSymbol(SymbolType.UNDETERMINED_FUNCTION.toString());
        Set<Symbol> expected = createSet(undetermined,undetermined_function);
        assertEquals(expected, table.getUndeterminedSymbols());
    }

}
