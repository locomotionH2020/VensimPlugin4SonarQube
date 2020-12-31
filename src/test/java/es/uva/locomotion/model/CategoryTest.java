package es.uva.locomotion.model;

import es.uva.locomotion.service.DBFacade;
import es.uva.locomotion.utilities.exceptions.ServiceResponseFormatNotValid;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class CategoryTest {

    @Test
    public void getName() {
        Category c = new Category("categoryName");
        assertEquals("categoryName", c.getName());
    }

    @Test
    public void getSuperCategory() {
        Category superc = new Category("superCategoryName");
        Category c = new Category("categoryName");
        superc.addSubcategory(c);

        assertEquals(superc, c.getSuperCategory());
    }
    @Test
    public void getSuperCategoryNull() {
        Category c = new Category("categoryName");
        assertNull(c.getSuperCategory());
    }
    @Test
    public void getSubcategoriesNames() {
        Category c = new Category("categoryName");
        Category subc1 = new Category("subcategoryName1");
        Category subc2 = new Category("subcategoryName2");
        c.addSubcategory(subc1);
        c.addSubcategory(subc2);

        Set<String> expected = new HashSet<>();
        expected.add("subcategoryName1");
        expected.add("subcategoryName2");
        assertEquals(expected, c.getSubcategoriesNames());

    }

    @Test
    public void getSubcategories() {
        Category c = new Category("categoryName");
        Category subc1 = new Category("subcategoryName1");
        Category subc2 = new Category("subcategoryName2");
        c.addSubcategory(subc1);
        c.addSubcategory(subc2);

        Set<Category> expected = new HashSet<>();
        expected.add(subc1);
        expected.add(subc2);
        assertEquals(expected, c.getSubcategories());
    }

    @Test
    public void addSubcategory() {
        Category c = new Category("categoryName");
        Category subc1 = new Category("subcategoryName1");
        Category subc2 = new Category("subcategoryName2");

        Set<Category> expected = null;
        assertEquals(expected, c.getSubcategories());

        c.addSubcategory(subc1);

        expected = new HashSet<>();
        expected.add(subc1);
        assertEquals(expected, c.getSubcategories());

        c.addSubcategory(subc2);

        expected = new HashSet<>();
        expected.add(subc1);
        expected.add(subc2);
        assertEquals(expected, c.getSubcategories());

    }

    @Test
    public void addSubcategoryRecuersion() {
        Category c = new Category("categoryName");
        assertThrows(IllegalArgumentException.class, () -> c.addSubcategory(c));
    }
    @Test
    public void addSubcategoryMultipleSupers() {
        Category c = new Category("categoryName");
        Category c2 = new Category("categoryName2");
        Category subc = new Category("subcategoryName");
        c.addSubcategory(subc);
        assertThrows(IllegalStateException.class, () -> c2.addSubcategory(subc));
    }
    @Test
    public void addSubcategoryOtherSubcategory() {
        Category c = new Category("categoryName");
        Category subc = new Category("subcategoryName");
        Category subsubc = new Category("subsubcategoryName");
        subc.addSubcategory(subsubc);
        assertThrows(IllegalStateException.class, () -> c.addSubcategory(subc));
    }
    @Test
    public void addSubcategorySuperAsSub() {
        Category c = new Category("categoryName");
        Category subc = new Category("subcategoryName");
        Category subsubc = new Category("subsubcategoryName");
        c.addSubcategory(subc);

        assertThrows(IllegalStateException.class, () -> subc.addSubcategory(subsubc));

    }
    @Test
    public void testEquals() {
        Category c = new Category("categoryName");
        Category subc1 = new Category("subcategoryName");
        c.addSubcategory(subc1);

        Category cSame = new Category("categoryName");
        Category subc2 = new Category("subcategoryName");
        cSame.addSubcategory(subc2);

        Category categoryNameDifferent = new Category("categoryNameDifferent");
        Category subc3 = new Category("subcategoryName");
        categoryNameDifferent.addSubcategory(subc3);

        Category categorySubcatDifferent = new Category("categoryName");
        Category subc4 = new Category("subcategoryNameDifferent");
        categorySubcatDifferent.addSubcategory(subc4);


        Category c1 = new Category("categoryName");
        Category c2 = new Category("categoryNameDifferent");
        Category subc = new Category("subcategoryName");
        Category subcSame = new Category("subcategoryName");
        Category subcNameDifferent = new Category("subcategoryNameDifferent");
        Category subcSupercatDifferent = new Category("subcategoryName");

        c1.addSubcategory(subc);
        c1.addSubcategory(subcSame);
        c1.addSubcategory(subcNameDifferent);
        c2.addSubcategory(subcSupercatDifferent);

        assertEquals(c,cSame);
        assertNotEquals(c,categoryNameDifferent);
        assertEquals(c,categorySubcatDifferent);

        assertEquals(subc,subcSame);
        assertNotEquals(subc,subcNameDifferent);
        assertNotEquals(subc,subcSupercatDifferent);



    }
}