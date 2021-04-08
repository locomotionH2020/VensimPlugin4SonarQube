package es.uva.locomotion.model.category;

import es.uva.locomotion.model.Issuable;

import java.util.Set;

public interface Category extends Issuable {

    String getName();
    String getWholeName();

    Category getSuperCategory();
    Set<Category> getSubcategories();
    Category getSubcategory(String subcategory);

    static Category create(String name) {
       return new CategoryImpl(name);
    }
}
