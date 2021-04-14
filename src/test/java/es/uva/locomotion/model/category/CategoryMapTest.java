package es.uva.locomotion.model.category;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CategoryMapTest {

    @Test
    public void contains() {
        CategoryMap cl = new CategoryMap();

        cl.createOrSelectCategory("categoryName");

        assertTrue(cl.contains("categoryName"));
        assertFalse(cl.contains("otherName"));
    }


    @Test
    public void createOrSelectCategory() {
        CategoryMap cl = new CategoryMap();
        Category c = cl.createOrSelectCategory("categoryName");


        assertEquals(c,cl.createOrSelectCategory(c.getName()));

        Category c2 = cl.createOrSelectCategory("newName");
        assertNotNull(c2);
        assertEquals("newName", c2.getName());
    }

    @Test
    public void getCategories() {
        CategoryMap cl = new CategoryMap();
        Category c =cl.createOrSelectCategory("categoryName");
        Category c2 = cl.createOrSelectCategory("categoryName2");

        List<Category> expected = new ArrayList<>();
        expected.add(c);
        expected.add(c2);
        assertEquals(expected, cl.getCategories());
    }


    @Test
    public void getCategoriesAndSubcategories() {
        CategoryMap cl = new CategoryMap();
        Category c = cl.createOrSelectCategory("categoryName");
        Category subc = cl.addSubcategoryTo(c, "subcategoryName");
        Category c2 =cl.createOrSelectCategory("categoryName2");

        List<Category> expected = new ArrayList<>();
        expected.add(c);
        expected.add(subc);
        expected.add(c2);
        assertEquals(expected, cl.getCategoriesAndSubcategories());
    }

    @Test
    public void hasCategory() {
        CategoryMap cl = new CategoryMap();
        Category c = cl.createOrSelectCategory("categoryName");
        Category subc = cl.addSubcategoryTo(c, "subcategoryName");
        Category c2 =cl.createOrSelectCategory("categoryName2");

        assertTrue(cl.hasCategory("categoryName"));
        assertFalse(cl.hasCategory("otherName"));
        assertFalse(cl.hasCategory("subcategoryName"));
    }

    @Test
    public void getSubcategories() {
        CategoryMap cl = new CategoryMap();
        Category c = cl.createOrSelectCategory("categoryName");
        Category subc = cl.addSubcategoryTo(c, "subcategoryName");
        Category c2 =cl.createOrSelectCategory("categoryName2");

        List<Category> expected = new ArrayList<>();
        expected.add(subc);
        assertEquals(expected, cl.getSubcategories());
    }
    @Test
    public void equalTest() {
        CategoryMap cl = new CategoryMap();
        assertEquals(cl,cl);
        assertNotEquals(null, cl);
        assertFalse(cl.equals(4));

    }
}