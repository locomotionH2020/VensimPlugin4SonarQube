package es.uva.locomotion.model;

import es.uva.locomotion.model.View;
import es.uva.locomotion.model.ViewTable;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class TestViewTable {

    @Test
    public void viewOptions() {
        ViewTable vt = new ViewTable();
        View v = new View("name");
        vt.addView(v);

        assertEquals(v, vt.getView("name"));
        assertTrue(vt.hasView("name"));
        assertFalse(vt.hasView("no This name"));

        View v2 = new View("name2");
        vt.addView(v2);

        Collection<View> viewsReturned = vt.getViews();


        assertTrue(viewsReturned.contains(v));
        assertTrue(viewsReturned.contains(v2));
        assertEquals(2, viewsReturned.size());

        vt.removeView(v);

        assertFalse(vt.hasView("name"));
        assertTrue(vt.hasView("name2"));


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
        ViewTable vt = new ViewTable();
        View v = vt.createOrSelectView("module", "category", true);

        assertEquals(v, vt.getView(v.getIdentifier()));
        assertTrue(vt.hasView(v.getIdentifier()));
        assertFalse(vt.hasView("no This name"));

        assertTrue(vt.hasModule("module"));
        assertFalse(vt.hasModule("not this module"));
        assertEquals(1, vt.getModules().size());

        assertTrue(vt.hasCategory("category"));
        assertFalse(vt.hasCategory("not this category"));
        assertEquals(1, vt.getCategoriesList().size());

        View vIgual = vt.createOrSelectView("module", "category", true);
        assertEquals(vIgual, v);

        View vDistnta = vt.createOrSelectView("module", "category2", true);
        assertNotEquals(vDistnta, v);

    }

    @Test
    public void viewCreateOrSelectViewWithCategoryAndSubCategory() {
        ViewTable vt = new ViewTable();
        View v = vt.createOrSelectView("module", "category", "subcategory");

        assertEquals(v, vt.getView(v.getIdentifier()));
        assertTrue(vt.hasView(v.getIdentifier()));
        assertFalse(vt.hasView("no This name"));

        assertTrue(vt.hasModule("module"));
        assertFalse(vt.hasModule("not this module"));
        assertEquals(1, vt.getModules().size());

        assertTrue(vt.hasCategory("category"));
        assertFalse(vt.hasCategory("not this category"));
        assertEquals(1, vt.getCategoriesList().size());

        assertTrue(vt.hasSubcategory("category", "subcategory"));
        assertFalse(vt.hasSubcategory("category", "not this subtategory"));
        assertThrows(IllegalArgumentException.class, () -> vt.hasSubcategory("not this category", "subtategory"));
        assertEquals(1, vt.getSubcategories("category").size());
        assertThrows(IllegalArgumentException.class, () -> assertEquals(1, vt.getSubcategories("not this category").size()));

        View vIgual = vt.createOrSelectView("module", "category", "subcategory");
        assertEquals(vIgual, v);

        View vDistnta = vt.createOrSelectView("module", "category2", "not this subcategory");
        assertNotEquals(vDistnta, v);

    }
}