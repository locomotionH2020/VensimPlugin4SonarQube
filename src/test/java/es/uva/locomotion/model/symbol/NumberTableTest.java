package es.uva.locomotion.model.symbol;

import org.junit.Test;

import static org.junit.Assert.*;

public class NumberTableTest {
    @Test
    public void numberAlreadyExists() {
        NumberTable nt = new NumberTable();
        nt.addNumber(new Number("4"));
        assertThrows(IllegalArgumentException.class, () -> {
            nt.addNumber(new Number("4"));
        });
    }
    @Test
    public void removeNumber() {
        NumberTable nt = new NumberTable();
        nt.addNumber(new Number("4"));
        assertEquals(1, nt.getNumbers().size());
        nt.removeNumber("4");
        assertEquals(0, nt.getNumbers().size());

    }
    @Test
    public void removeNumberNotFound() {
        NumberTable nt = new NumberTable();
        nt.addNumber(new Number("5"));
        assertThrows(IllegalArgumentException.class, () -> {
            nt.removeNumber("4");
        });

    }

    @Test
    public void testEquals() {
        NumberTable nt = new NumberTable();
        nt.addNumber(new Number("5"));

        NumberTable nt2 = new NumberTable();
        nt2.addNumber(new Number("5"));

        NumberTable nt3 = new NumberTable();
        nt3.addNumber(new Number("4"));

        assertEquals(nt,nt);
        assertEquals(nt,nt2);
        assertNotEquals(nt,nt3);

        assertFalse(nt.equals("5"));
    }
}