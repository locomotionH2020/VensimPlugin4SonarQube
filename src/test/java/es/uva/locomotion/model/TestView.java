package es.uva.locomotion.model;

import es.uva.locomotion.model.View;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class TestView {

    @Test
    public void getModule() {
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
    public void getIdentifier() {
        View v = new View("moduleName", "categoryName", "subcategoryName");
        assertEquals("moduleName_categoryName_subcategoryName", v.getIdentifier());
    }

    @Test
    public void getCategory() {
        View v = new View("moduleName", "categoryName");
        assertEquals("categoryName", v.getCategory());
    }
    @Test
    public void getCategoryIsNull() {
        View v = new View("moduleName");
        assertNull(v.getCategory());
    }
    @Test
    public void getSubcategory() {
        View v = new View("moduleName", "categoryName", "subcategoryName");
        assertEquals("subcategoryName", v.getSubcategory());
    }
    @Test
    public void getSubcategoryIsNull() {
        View v = new View("moduleName", "categoryName");
        assertNull( v.getSubcategory());
    }
    @Test
    public void getSubcategoryAndCategoryIsNull() {
        View v = new View("moduleName");
        assertNull( v.getSubcategory());
    }

    @Test
    public void getCategoryOrSubcategory() {
        View v = new View("moduleName", "categoryName", "subcategoryName");
        View v2 = new View("moduleName", "categoryName");
        View v3 = new View("moduleName");
        assertEquals("subcategoryName", v.getCategoryOrSubcategory());
        assertEquals("categoryName", v2.getCategoryOrSubcategory());
        assertNull( v3.getCategoryOrSubcategory());
    }

        @Test
        public void testEquals(){
            View v = new View("moduleName", "categoryName", "subcategoryName");
            View vSubDiff = new View("moduleName", "categoryName", "different");
            View vCatDiff = new View("moduleName", "different", "subcategoryName");
            View vModDiff = new View("diffenert", "categoryName", "subcategoryName");
            View vSame = new View("moduleName", "categoryName", "subcategoryName");

            View v2 = new View("moduleName", "categoryName");
            View v2CatDiff = new View("moduleName", "different");
            View v2ModDiff = new View("different", "categoryName");
            View v2Same = new View("moduleName", "categoryName");

            View v3 = new View("moduleName");
            View v3ModDiff = new View("different");
            View v3Same = new View("moduleName");

            assertNotEquals(v,vSubDiff);
            assertNotEquals(v,vCatDiff);
            assertNotEquals(v,vModDiff);
            assertEquals(v,vSame);

            assertNotEquals(v2,v2CatDiff);
            assertNotEquals(v2,v2ModDiff);
            assertEquals(v2,v2Same);

            assertNotEquals(v3,v3ModDiff);
            assertEquals(v3,v3Same);

            assertNotEquals(v,v2);
            assertNotEquals(v2,v3);
            assertNotEquals(v,v3);
        }
}