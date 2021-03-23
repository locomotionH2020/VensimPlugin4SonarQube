package es.uva.locomotion.model;

import es.uva.locomotion.model.category.Category;
import es.uva.locomotion.model.category.CategoryMap;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class TestViewTable {

    @Test
    public void viewOptions() {
        ViewTable vt = new ViewTable();
        View v = new View(new Module("name"));
        vt.addView(v);

        assertEquals(v, vt.getView("name"));
        assertTrue(vt.hasView("name"));
        assertFalse(vt.hasView("no This name"));

        View v2 = new View(new Module("name2"));
        vt.addView(v2);

        Collection<View> viewsReturned = vt.getViews();


        assertTrue(viewsReturned.contains(v));
        assertTrue(viewsReturned.contains(v2));
        assertEquals(2, viewsReturned.size());

    }

    @Test
    public void viewCreateOrSelectView() {
        ViewTable vt = new ViewTable();
        View v = vt.createOrSelectView("name");

        assertEquals(v, vt.getView("name"));
        assertTrue(vt.hasView("name"));
        assertFalse(vt.hasView("no This name"));

        View vIgual = vt.createOrSelectView("name");

        assertEquals(vIgual, v);

    }

    @Test
    public void viewCreateOrSelectViewWithCategory() {
        CategoryMap cl = new CategoryMap();
        ViewTable vt = new ViewTable();
        View v = vt.createOrSelectView("module", "category");
        Category c = v.getCategory();

        assertEquals(v, vt.getView(v.getIdentifier()));
        assertTrue(vt.hasView(v.getIdentifier()));
        assertFalse(vt.hasView("no This name"));

        assertTrue(vt.getModules().contains(new Module("module")));
        assertFalse(vt.getModules().contains(new Module("not this module")));
        assertEquals(1, vt.getModules().size());

        assertTrue(vt.getCategories().contains(cl.createOrSelectCategory("category")));
        assertFalse(vt.getCategories().contains(cl.createOrSelectCategory("not this category")));
        assertEquals(1, vt.getCategories().size());

        View vIgual = vt.createOrSelectView("module", "category");
        assertEquals(vIgual, v);

        View vDistnta = vt.createOrSelectView("module", "category2");
        assertNotEquals(vDistnta, v);

    }

    @Test
    public void viewCreateOrSelectViewWithCategoryAndSubCategory() {
        CategoryMap cl = new CategoryMap();
        ViewTable vt = new ViewTable();
        View v = vt.createOrSelectView("module", "category", "subcategory");

        assertEquals(v, vt.getView(v.getIdentifier()));
        assertTrue(vt.hasView(v.getIdentifier()));
        assertFalse(vt.hasView("no This name"));

        assertTrue(vt.getModules().contains(new Module("module")));
        assertFalse(vt.getModules().contains(new Module("not this module")));
        assertEquals(1, vt.getModules().size());

        assertTrue(vt.getCategories().contains(cl.createOrSelectCategory("category")));
        assertFalse(vt.getCategories().contains(cl.createOrSelectCategory("not this category")));
        assertEquals(1, vt.getCategories().size());

        assertNotNull(vt.getCategory("category").getSubcategory("subcategory"));
        assertNull(vt.getCategory("category").getSubcategory("not this subtategory"));

        assertNull(vt.getCategory("not this category"));
        assertEquals(1, vt.getCategory("category").getSubcategories().size());

        View vIgual = vt.createOrSelectView("module", "category", "subcategory");
        assertEquals(vIgual, v);

        View vDistnta = vt.createOrSelectView("module", "category2", "not this subcategory");
        assertNotEquals(vDistnta, v);

    }
}