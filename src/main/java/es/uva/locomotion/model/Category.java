package es.uva.locomotion.model;


import java.util.*;
import java.util.stream.Collectors;

public class Category {


    private Category superCategory;
    private String name;
    private Set<Category> subcategories;

    public Category(Category superCategory, String name){
        this.superCategory = superCategory;
        this.name = name;
        this.subcategories = null;
    }
    public Category(String name){
        this.superCategory = null;
        this.name = name;
        this.subcategories = new HashSet<>();
    }


    public String getName() {
        return name;
    }

    public Category getSuperCategory() {
        return superCategory;
    }
    public Set<String> getSubcategoriesNames() {
        return subcategories.stream().map(Category::getName).collect(Collectors.toSet());
    }
    public void setName(String name) {
        this.name = name;
    }

    public Set<Category> getSubcategories() {
        return subcategories;
    }

    public void addSubcategory(Category subcategory) {
        this.subcategories.add(subcategory);
    }
    public void setSubcategories(Set<Category> subcategories) {
        this.subcategories = subcategories;
    }

}
