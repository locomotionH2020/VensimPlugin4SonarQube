package es.uva.locomotion.model.category;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class CategoryTest {
    CategoryMap cl;

    @Before
    public void setUp() {
        cl = new CategoryMap();
    }

    @Test
    public void getName() {
        Category c = cl.createOrSelectCategory("categoryName");
        assertEquals("categoryName", c.getName());
    }

    @Test
    public void getSuperCategory() {
        Category superc = cl.createOrSelectCategory("superCategoryName");
        Category c = cl.addSubcategoryTo(superc, "categoryName");

        assertEquals(superc, c.getSuperCategory());
    }

    @Test
    public void getSuperCategoryNull() {
        Category c = cl.createOrSelectCategory("categoryName");
        assertNull(c.getSuperCategory());
    }

    @Test
    public void getSubcategoriesNames() {
        Category c = cl.createOrSelectCategory("categoryName");
        Category subc1 = cl.addSubcategoryTo(c, "subcategoryName1");
        Category subc2 = cl.addSubcategoryTo(c, "subcategoryName2");

        Set<Category> expected = new HashSet<>();
        expected.add(subc1);
        expected.add(subc2);
        assertEquals(expected, c.getSubcategories());

    }

    @Test
    public void getSubcategories() {
        Category c = cl.createOrSelectCategory("categoryName");
        Category subc1 = cl.addSubcategoryTo(c, "subcategoryName1");
        Category subc2 = cl.addSubcategoryTo(c, "subcategoryName2");


        Set<Category> expected = new HashSet<>();
        expected.add(subc1);
        expected.add(subc2);
        assertEquals(expected, c.getSubcategories());
    }

    @Test
    public void addSubcategory() {
        Category c = cl.createOrSelectCategory("categoryName");

        Set<Category> expected;
        assertEquals(0,c.getSubcategories().size());

        Category subc1 = cl.addSubcategoryTo(c, "subcategoryName1");

        expected = new HashSet<>();
        expected.add(subc1);
        assertEquals(expected, c.getSubcategories());

        Category subc2 = cl.addSubcategoryTo(c, "subcategoryName2");

        expected = new HashSet<>();
        expected.add(subc1);
        expected.add(subc2);
        assertEquals(expected, c.getSubcategories());

    }

    @Test
    public void addSubcategoryRecuersion() {
        Category c = cl.createOrSelectCategory("categoryName");
        assertThrows(IllegalArgumentException.class, () -> cl.addSubcategoryTo(c, "categoryName"));
    }


    @Test
    public void addSubcategoryOtherSubcategory() {
        Category c = cl.createOrSelectCategory("categoryName");
        Category subc = cl.addSubcategoryTo(c,"subcategoryName");
        assertThrows(IllegalArgumentException.class, () -> cl.addSubcategoryTo(subc, "subsubcategoryName"));
    }


    @Test
    public void testEquals() {
        Category c = cl.createOrSelectCategory("categoryName");
        Category subc1 = cl.addSubcategoryTo(c, "subcategoryName");

        Category cSame = cl.createOrSelectCategory("categoryName");
        Category subc2 = cl.addSubcategoryTo(cSame, "subcategoryName");

        Category categoryNameDifferent = cl.createOrSelectCategory("categoryNameDifferent");
        Category subc3 = cl.addSubcategoryTo(categoryNameDifferent, "subcategoryName");

        Category categorySubcatDifferent = cl.createOrSelectCategory("categoryName");
        Category subc4 = cl.addSubcategoryTo(categorySubcatDifferent, "subcategoryNameDifferent");


        Category c1 = cl.createOrSelectCategory("categoryName");
        Category c2 = cl.createOrSelectCategory("categoryNameDifferent");
        Category subc = cl.addSubcategoryTo(c1,"subcategoryName");
        Category subcSame = cl.addSubcategoryTo(c1,"subcategoryName");
        Category subcNameDifferent = cl.addSubcategoryTo(c1,"subcategoryNameDifferent");
        Category subcSupercatDifferent = cl.addSubcategoryTo(c2,"subcategoryName");


        assertEquals(c, cSame);
        assertNotEquals(c, categoryNameDifferent);
        assertEquals(c, categorySubcatDifferent);

        assertEquals(subc, subcSame);
        assertNotEquals(subc, subcNameDifferent);
        assertNotEquals(subc, subcSupercatDifferent);


    }
}