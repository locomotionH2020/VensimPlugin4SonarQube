package es.uva.locomotion.model;

import java.util.*;
import java.util.stream.Collectors;

public class CategoryMap {

    private final Map<String, Category> categoryMap;

    public CategoryMap() {
        categoryMap = new HashMap<>();
    }

    public boolean contains(String category){
        return categoryMap.containsKey(category);
    }

    public void add(Category category) {

        if(categoryMap.containsKey(category.getName())){
            throw new IllegalStateException("Category already in list");
        }
        categoryMap.put( category.getName(), category);
    }

    public Category createOrSelectCategory(String categoryName){
        if(categoryMap.containsKey(categoryName)){
            return categoryMap.get(categoryName);
        }else{
            Category c = new Category(categoryName);
            add(c);
            return c;
        }
    }



    public List<String> getCategoriesName() {
        List<String> cat = new ArrayList<>(categoryMap.keySet());
        cat.sort(Comparator.comparing(String::toString));
        return cat;
    }

    public List<Category> getCategoriesList() {
        return categoryMap.values().stream().sorted().collect(Collectors.toList());
    }

    public List<Category> getCategoriesAndSubcategories() {

        List<Category> toReturn = new ArrayList<>();

        for(Category c : getCategoriesList()){
            toReturn.add(c);
            if(c.getSubcategories() != null) toReturn.addAll(c.getSubcategories());
        }

        return toReturn;
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
