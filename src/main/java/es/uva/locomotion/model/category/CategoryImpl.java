package es.uva.locomotion.model.category;


import es.uva.locomotion.model.IssuableAbs;

import java.util.*;

public class CategoryImpl extends IssuableAbs implements Comparable<Category>, Category {


    private Category superCategory;
    private final String name;
    private Set<Category> subcategories;

    CategoryImpl(String name) {
        super();
        this.superCategory = null;
        this.name = name;
        this.subcategories = null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getWholeName() {
        if (getSuperCategory() != null) {
            return getSuperCategory().getWholeName() + "." + getName();
        } else {
            return getName();
        }
    }

    public Category getSuperCategory() {
        return superCategory;
    }

    protected void setSuperCategory(Category superCategory) {
        this.superCategory = superCategory;
    }

    @Override
    public Set<Category> getSubcategories() {
        return subcategories;
    }

    @Override
    public Category getSubcategory(String subcategory) {
        if(getSubcategories() == null) return null;
        return subcategories.stream().filter((subcat) -> subcat.getName().equals(subcategory)).findFirst().orElse(null);
    }


    public void addSubcategory(CategoryImpl subcategory) {
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

    public void setSubcategories(Set<Category> subcategories) {
        this.subcategories = subcategories;
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
        CategoryImpl category = (CategoryImpl) o;
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

    @Override
    public void addLine(int line) {
        super.addLine(line);
        if (getSubcategories() != null)
            for (Category sub : getSubcategories()) {
                sub.addLine(line);
            }
    }

    @Override
    public void setAsInvalid(String invalidReason) {
        super.setAsInvalid(invalidReason);
        subcategories.forEach((symbol) -> symbol.setAsInvalid("Supercategory inheritance: " + invalidReason));
    }
}
