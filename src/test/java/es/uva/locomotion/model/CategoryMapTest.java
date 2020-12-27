package es.uva.locomotion.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CategoryMapTest {

    @Test
    public void contains() {
        CategoryMap cl = new CategoryMap();
        Category c = new Category("categoryName");
        Category c2 = new Category("categoryName2");
        cl.add(c);

        assertTrue(cl.contains(c.getName()));
        assertFalse(cl.contains(c2.getName()));
    }


    @Test
    public void createOrSelectCategory() {
        CategoryMap cl = new CategoryMap();
        Category c = new Category("categoryName");
        cl.add(c);

        assertEquals(c,cl.createOrSelectCategory(c.getName()));

        Category c2 = cl.createOrSelectCategory("newName");
        assertNotNull(c2);
        assertEquals("newName", c2.getName());
    }

    @Test
    public void getCategoriesName() {
        CategoryMap cl = new CategoryMap();
        Category c = new Category("categoryName");
        Category c2 = new Category("categoryName2");
        cl.add(c);
        cl.add(c2);

        List<String> expected = new ArrayList<>();
        expected.add("categoryName");
        expected.add("categoryName2");
        assertEquals(expected, cl.getCategoriesName());
    }

    @Test
    public void getCategoriesList() {
        CategoryMap cl = new CategoryMap();
        Category c = new Category("categoryName");
        Category c2 = new Category("categoryName2");
        cl.add(c);
        cl.add(c2);

        List<Category> expected = new ArrayList<>();
        expected.add(c);
        expected.add(c2);
        assertEquals(expected, cl.getCategoriesList());
    }

    @Test
    public void getCategoriesAndSubcategories() {
        CategoryMap cl = new CategoryMap();
        Category c = new Category("categoryName");
        Category subc = new Category("subcategoryName");
        c.addSubcategory(subc);
        Category c2 = new Category("categoryName2");
        cl.add(c);
        cl.add(c2);

        List<Category> expected = new ArrayList<>();
        expected.add(c);
        expected.add(subc);
        expected.add(c2);
        assertEquals(expected, cl.getCategoriesAndSubcategories());
    }


}