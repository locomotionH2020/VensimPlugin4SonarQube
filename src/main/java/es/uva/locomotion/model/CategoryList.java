package es.uva.locomotion.model;

import java.util.*;

public class CategoryList {

    private Map<String, Category> categoryMap;

    public CategoryList() {
        categoryMap = new HashMap<>();
    }

    public void add(Category category) {
        categoryMap.put( category.getName(), category);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryList that = (CategoryList) o;
        return Objects.equals(categoryMap, that.categoryMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryMap);
    }

    public Category getCategory(String categoryName) {
        Category category =  categoryMap.get(categoryName);
        if (category == null)
            throw new IllegalArgumentException("The category:  " + categoryName + " does not exists.");
        return category;
    }


    public List<String> getCategoriesName() {
        List<String> cat = new ArrayList<>(categoryMap.keySet());
        cat.sort(Comparator.comparing(String::toString));
        return cat;
    }

    public List<Category> getCategoriesList() {
        return new ArrayList<>(categoryMap.values());
    }

    public boolean containsCategory(String category){
        return categoryMap.containsKey(category);
    }
}
