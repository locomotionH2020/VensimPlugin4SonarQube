package es.uva.locomotion.model.symbol;

import es.uva.locomotion.model.Module;
import es.uva.locomotion.model.category.Category;
import org.junit.Test;

import static org.junit.Assert.*;

public class SymbolTest {

    @Test
    public void testdbEquals() {
        Symbol s = new Symbol("symbol");
        Symbol s2 = new Symbol("symbol");
        Symbol s3 = new Symbol("symbol other name");

        assertEquals(s,s);
        assertEquals(s,s2);
        assertNotEquals(s,s3);
    }

    @Test
    public void isValid() {
        Symbol s = new Symbol("symbol");
        assertTrue(s.isValid());

        Category c = Category.create("cat");
        c.setAsInvalid("invalidReason");
        s.setCategory(c);
        assertFalse(s.isValid());
        assertEquals("invalidReason",s.getInvalidReason());

        s = new Symbol("symbol");
        Module m = new Module("module");
        s.setPrimaryModule(m);
        m.setAsInvalid("notValid");
        assertFalse(s.isValid());
    }
}