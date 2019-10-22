package es.uva.medeas.tests;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import  static org.junit.Assert.*;

import es.uva.medeas.parser.SymbolType;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static es.uva.medeas.tests.TestUtilities.*;

public class TestSymbolTable {

    SymbolTable table;

    @Before
    public void setUp(){
        table = new SymbolTable();
    }
    @Test
    public void testSymbolTableConstructor(){
        assertEquals(new HashSet<Symbol>(), new HashSet<>(table.getSymbols()));

    }

    @Test
    public void testCreateSymbol(){
        Symbol symbol = table.createSymbol("mySymbol");

        assertEquals(symbol.getToken(),"mySymbol");
        assertTrue(table.hasSymbol("mySymbol"));
        assertSymbolLine(symbol, -1);
        assertEquals(NO_DEPENDENCIES,symbol.getDependencies());
        assertSymbolType(symbol, SymbolType.UNDETERMINED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateSymbolAlreadyExists(){
        table.createSymbol("symbol");
        table.createSymbol("symbol");
    }


    @Test
    public void testGetSymbolOrCreateNewSymbol(){
        Symbol symbol = table.getSymbolOrCreate("mySymbol");

        assertEquals(symbol.getToken(),"mySymbol");
        assertTrue(table.hasSymbol("mySymbol"));
        assertSymbolLine(symbol, -1);
        assertEquals(NO_DEPENDENCIES,symbol.getDependencies());
        assertSymbolType(symbol, SymbolType.UNDETERMINED);
    }

    @Test
    public void testGetSymbolOrCreateExistingSymbol(){
        Symbol mySymbol = table.createSymbol("mySymbol");
        Symbol actualSymbol = table.getSymbolOrCreate("mySymbol");

        assertSame(mySymbol,actualSymbol);
    }

    @Test
    public void testGetSymbols(){
        Symbol first = table.createSymbol("firstSymbol");
        Symbol second = table.createSymbol("secondSymbol");
        Symbol third = table.createSymbol("thirdSymbol");

        Set<Symbol> tableSymbols = new HashSet<>(table.getSymbols());
        assertEquals(createSet(first,second,third), tableSymbols);
    }

    @Test
    public void testGetSymbol(){
        Symbol expectedSymbol = table.createSymbol("testSymbol");
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
            Symbol symbol = table.createSymbol(type.toString());
            symbol.setType(type);
        }

        Set<Symbol> expected = createSet(table.getSymbol(SymbolType.UNDETERMINED.toString()));
        assertEquals(expected, table.getUndeterminedSymbols());
    }

}
