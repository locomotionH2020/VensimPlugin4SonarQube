package es.uva.locomotion.model;

import es.uva.locomotion.model.category.Category;
import es.uva.locomotion.model.category.CategoryMap;
import es.uva.locomotion.model.symbol.Symbol;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class TestView {

    @Test
    public void getModule() {
        View v = new View(new Module("name"));
        assertEquals("name", v.getModule().getName());
    }

    @Test
    public void getSymbols() {
        View v = new View(new Module("name"));

        Symbol s1 = new Symbol("symbol1");
        Symbol s2 = new Symbol("symbol2");
        v.addPrimary(s1);
        v.addShadow(s2);

        Set<Symbol> result = v.getPrimarySymbols();
        assertTrue(result.contains(s1));//HERE
        assertEquals(1,result.size());

         result = v.getShadowSymbols();
        assertTrue(result.contains(s2));
        assertEquals(1,result.size());

         result = v.getSymbols();
        assertTrue(result.contains(s1));
        assertTrue(result.contains(s2));
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
        CategoryMap cl = new CategoryMap();
        Category c = cl.createOrSelectCategory("categoryName");
        Category c2 = cl.addSubcategoryTo(c, "subcategoryName");
        View v = new View(new Module("moduleName"), c, c2);
        assertEquals("moduleName_categoryName_subcategoryName", v.getIdentifier());
    }

    @Test
    public void getCategory() {
        CategoryMap cl = new CategoryMap();
        Category c = cl.createOrSelectCategory("categoryName");
        Category c2 = cl.addSubcategoryTo(c, "subcategoryName");
        View v = new View(new Module("moduleName"), c,c2);

        assertEquals("categoryName", v.getCategory().getName());
    }
    @Test
    public void getCategoryIsNull() {
        View v = new View(new Module("moduleName"));
        assertNull(v.getCategory());
    }
    @Test
    public void getSubcategory() {
        CategoryMap cl = new CategoryMap();
        Category c = cl.createOrSelectCategory("categoryName");
        Category c2 = cl.addSubcategoryTo(c, "subcategoryName");
        View v = new View(new Module("moduleName"), c,c2);
        assertEquals("subcategoryName", v.getSubcategory().getName());
    }
    @Test
    public void getSubcategoryIsNull() {
        CategoryMap cl = new CategoryMap();
        Category c = cl.createOrSelectCategory("categoryName");
        View v = new View(new Module("moduleName"), c,null);
        assertNull( v.getSubcategory());
    }
    @Test
    public void getSubcategoryAndCategoryIsNull() {
        View v = new View(new Module("moduleName"));
        assertNull( v.getCategoryOrSubcategory());
    }

    @Test
    public void getCategoryOrSubcategory() {
        CategoryMap cl = new CategoryMap();
        Category c = cl.createOrSelectCategory("categoryName");
        Category c2 = cl.addSubcategoryTo(c, "subcategoryName");
        View v = new View(new Module("moduleName"), c,c2);

        Category c3 = cl.createOrSelectCategory("categoryName");
        View v2 = new View(new Module("moduleName"), c3, null);


        View v3 = new View(new Module("moduleName"), null, null);

        assertEquals("subcategoryName", v.getCategoryOrSubcategory().getName());
        assertEquals("categoryName", v2.getCategoryOrSubcategory().getName());
        assertNull( v3.getCategoryOrSubcategory());
    }

        @Test
        public void testEquals(){
            CategoryMap cl = new CategoryMap();
            Category c = cl.createOrSelectCategory("categoryName");
            Category c2 = cl.createOrSelectCategory("DIFERENTcategoryName");
            Category sc = cl.addSubcategoryTo(c, "subcategoryName");
            Category scDiff = cl.addSubcategoryTo(c, "DIFERENTsubcategoryName");
            Category sc2 = cl.addSubcategoryTo(c2, "subcategoryName");
            Category sc2Diff = cl.addSubcategoryTo(c2, "DIFERENTsubcategoryName");

            Module m = new Module("moduleName");
            Module mDiff = new Module("DIFFERENTmoduleName");

            View v = new View(m, c,sc);
            View vSubDiff = new View(m, c,scDiff);
            View vCatDiff = new View(m,c2,sc2);
            View vModDiff = new View(mDiff,c,sc);
            View vSame = new View(m,c,sc);


            View v2 = new View(m, c, null);
            View v2Same = new View(m, c, null);
            View v2CatDiff = new View(mDiff, c, null);
            View v2ModDiff = new View(mDiff, c, null);


            View v3 = new View(m);
            View v3ModDiff = new View(mDiff);
            View v3Same = new View(m);

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