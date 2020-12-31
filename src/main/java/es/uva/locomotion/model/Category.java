package es.uva.locomotion.model;


import java.util.*;
import java.util.stream.Collectors;

public class Category implements Comparable<Category> {


    private Category superCategory;
    private String name;
    private Set<Category> subcategories;


    public Category(String name) {
        this.superCategory = null;
        this.name = name;
        this.subcategories = null;
    }


    public String getName() {
        return name;
    }

    public Category getSuperCategory() {
        return superCategory;
    }

    private void setSuperCategory(Category superCategory) {
        this.superCategory = superCategory;
    }

    public Set<String> getSubcategoriesNames() {
        if(subcategories == null){
            return null;
        }
        return subcategories.stream().map(Category::getName).collect(Collectors.toSet());
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Category> getSubcategories() {
        return subcategories;
    }

    public void addSubcategory(Category subcategory) {
        if (this.getSuperCategory() != null) {
            throw new IllegalStateException("Subcategories can't have new subcategories");
        }

        if (subcategory.getSubcategories() != null) {
            throw new IllegalStateException("Supercategories can't be added as subcategories");
        }
        if (subcategory.equals(this)) {
            throw new IllegalArgumentException("Category can have himself as subcategory.");
        }

        if (subcategory.getSuperCategory() != null && !subcategory.getSuperCategory().equals(this)) {
            throw new IllegalStateException("Category " + subcategory.getName() + " already have a supercategory: " + subcategory.getSuperCategory().getName());
        }
        subcategory.setSuperCategory(this);
        if (subcategories == null) {
            subcategories = new HashSet<>();
        }
        this.subcategories.add(subcategory);
    }

    @Override
    public String toString() {
        return "Category{" +
                "superCategory=" + (superCategory == null ? "null" : superCategory.getName()) +
                ", name='" + name + '\'' +
                ", subcategories=" + subcategories +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        if (superCategory != null && category.getSuperCategory() != null) {
            return superCategory.getName().equals(category.superCategory.getName()) && name.equals(category.name);
        } else if (superCategory != null ^ category.getSuperCategory() != null) {
            return false; // ^ is XOR operand
        } else {
            return name.equals(category.name);
        }
    }

    @Override
    public int hashCode() {

        if (superCategory != null) return Objects.hash(superCategory.getName(), name);
        else return Objects.hash(name);
    }

    @Override
    public int compareTo(Category o) {
        return name.compareTo(o.getName());
    }
}
