package es.uva.locomotion.model.category;

import java.util.*;
import java.util.stream.Collectors;

public class CategoryMap {

    private final Map<String, CategoryImpl> categoryMap;

    public CategoryMap() {
        categoryMap = new HashMap<>();
    }

    public boolean contains(String category) {
        return categoryMap.containsKey(category);
    }

    public Category createOrSelectCategory(String categoryName) {
        if (categoryMap.containsKey(categoryName)) {
            return categoryMap.get(categoryName);
        } else {
            CategoryImpl c = new CategoryImpl(categoryName);
            c.setSubcategories(new HashSet<>());
            categoryMap.put(categoryName, c);
            return c;
        }
    }

    public Category getCategory(String categoryName) {
        return categoryMap.get(categoryName);
    }
    public boolean hasCategory(String categoryName) {
        return categoryMap.containsKey(categoryName);
    }

    public Category addSubcategoryTo(String category, String subcategory) {
        CategoryImpl categoryImpl;
        if (getCategory(category) == null) {
            throw new IllegalArgumentException("No existe la categoría");
        } else {
            categoryImpl = (CategoryImpl) getCategory(category);
        }
        CategoryImpl c = new CategoryImpl(subcategory);
        categoryImpl.addSubcategory(c);
        return c;
    }

    public Category addSubcategoryTo(Category category, String subcategory) {
        return addSubcategoryTo(category.getName(), subcategory);
    }

    public List<Category> getCategories() {
        return categoryMap.values().stream().sorted().collect(Collectors.toList());
    }

    public List<Category> getCategoriesAndSubcategories() {

        List<Category> toReturn = new ArrayList<>();
        for (Category c : getCategories()) {
            toReturn.add(c);
            if (c.getSubcategories() != null) toReturn.addAll(c.getSubcategories());
        }
        return toReturn;
    }

    public List<Category> getSubcategories() {
        return getCategories().stream().map(Category::getSubcategories).flatMap(Set::stream).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "CategoryMap{" +
                "categoryMap=" + categoryMap +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryMap that = (CategoryMap) o;
        return categoryMap.equals(that.categoryMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryMap);
    }
}
