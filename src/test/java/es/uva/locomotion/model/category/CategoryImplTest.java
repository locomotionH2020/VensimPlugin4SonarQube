package es.uva.locomotion.model.category;

import org.junit.Test;

import static org.junit.Assert.*;

public class CategoryImplTest {
    @Test
    public void getSubcategoryNoSubcategories() {
        Category c = Category.create("Category");
        assertNull(c.getSubcategory("Subcategory"));
    }

    @Test
    public void addSubcategoryToSubcategory() {
        CategoryImpl c = (CategoryImpl) Category.create("Category");
        CategoryImpl sc = (CategoryImpl) Category.create("SubCategory");
        c.addSubcategory(sc);
        CategoryImpl ssc = (CategoryImpl) Category.create("SubSubCategory");

        assertThrows(IllegalStateException.class, () -> {sc.addSubcategory(ssc);});
    }
    @Test
    public void addSuperCategoryToSubcategory() {
        CategoryImpl c = (CategoryImpl) Category.create("Category");
        CategoryImpl sc = (CategoryImpl) Category.create("SubCategory");
        c.addSubcategory(sc);
        CategoryImpl ssc = (CategoryImpl) Category.create("SubSubCategory");

        assertThrows(IllegalStateException.class, () -> {ssc.addSubcategory(c);});
    }
    @Test
    public void addSubcategoryToAnotherCategory() {
        CategoryImpl c = (CategoryImpl) Category.create("Category");
        CategoryImpl c2 = (CategoryImpl) Category.create("Category2");
        CategoryImpl sc = (CategoryImpl) Category.create("SubCategory");
        c.addSubcategory(sc);
        assertThrows(IllegalStateException.class, () -> {c2.addSubcategory(sc);});
    }

    @Test
    public void toStringTest() {
        CategoryImpl c = (CategoryImpl) Category.create("Category");
        Category sc = Category.create("Sub");
        c.addSubcategory((CategoryImpl) sc);
        String expected= "Category{superCategory=null, name='Category', subcategories=[Category{superCategory=Category, name='Sub', subcategories=null}]}";
        String expectedsc= "Category{superCategory=Category, name='Sub', subcategories=null}";
        assertEquals(expected, c.toString());
        assertEquals(expectedsc, sc.toString());
    }

    @Test
    public void equalTest() {
        CategoryImpl c = (CategoryImpl) Category.create("Category");
        assertEquals(c,c);
        assertNotEquals(null, c);
        assertFalse(c.equals(4));

    }
}