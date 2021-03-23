package es.uva.locomotion.model.category;

import es.uva.locomotion.model.category.Category;
import es.uva.locomotion.model.category.CategoryImpl;
import es.uva.locomotion.model.category.CategoryMap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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


}