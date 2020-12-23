package es.uva.locomotion.parser;

import es.uva.locomotion.model.View;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class TestView {

    @Test
    public void getName() {
        View v = new View("name");
        assertEquals("name", v.getModule());
    }

    @Test
    public void getSymbols() {
        View v = new View("name");
        v.addPrimary("symbol1");
        v.addShadow("symbol2");

        Collection<String> result = v.getPrimary_symbols();
        assertTrue(result.contains("symbol1"));
        assertEquals(1,result.size());

         result = v.getShadow_symbols();
        assertTrue(result.contains("symbol2"));
        assertEquals(1,result.size());

         result = v.getSymbols();
        assertTrue(result.contains("symbol1"));
        assertTrue(result.contains("symbol2"));
        assertEquals(2,result.size());


        assertTrue(v.hasSymbol("symbol1"));
        assertTrue(v.hasSymbol("symbol2"));
        assertFalse(v.hasSymbol("symbol3"));

        assertTrue(v.hasPrimary("symbol1"));
        assertFalse(v.hasPrimary("symbol2"));

        assertFalse(v.hasShadow("symbol1"));
        assertTrue(v.hasShadow("symbol2"));

    }

    @Test
    public void hasSymbol() {
    }

    @Test
    public void hasPrimary() {
    }

    @Test
    public void hasShadow() {
    }
}