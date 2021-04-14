package es.uva.locomotion.model;

import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static es.uva.locomotion.testutilities.GeneralTestUtilities.*;
import static org.junit.Assert.*;

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
        assertTrue(symbol.getLines().isEmpty());
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

    @Test
    public void testGetSymbolDoesntExist(){
        assertNull(table.getSymbol("doesntExist"));
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

    @Test
    public void testRemoveSymbol() {
        SymbolTable symbolTable = new SymbolTable();
        Symbol symbol1 = new Symbol("symbol1");
        symbolTable.addSymbol(symbol1);
        Symbol symbol2 = new Symbol("symbol2");
        symbolTable.addSymbol(symbol2);
        Symbol symbol3 = new Symbol("symbol3");
        symbolTable.addSymbol(symbol3);

        symbolTable.removeSymbol(symbol1.getToken());
        Symbol[] returnedSymbols = symbolTable.getSymbols().toArray(new Symbol[0]);
        Symbol[] expectedSymbols ={symbol2,symbol3};
        assertArrayEquals(expectedSymbols,returnedSymbols);

        symbolTable.removeSymbol(symbol3.getToken());
        returnedSymbols = symbolTable.getSymbols().toArray(new Symbol[0]);
        Symbol[] expectedSymbols2 ={symbol2};
        assertArrayEquals(expectedSymbols2,returnedSymbols);

        symbolTable.removeSymbol(symbol2.getToken());
        returnedSymbols = symbolTable.getSymbols().toArray(new Symbol[0]);
        Symbol[] expectedSymbols3 ={};
        assertArrayEquals(expectedSymbols3,returnedSymbols);
    }

    @Test
    public void testRemoveSymbolNotFound() {
        SymbolTable symbolTable = new SymbolTable();
        Symbol symbol1 = new Symbol("symbol1");
        symbolTable.addSymbol(symbol1);
        Symbol symbol2 = new Symbol("symbol2");


        assertThrows(IllegalArgumentException.class, () -> symbolTable.removeSymbol(symbol2.getToken()));

    }
}
