package es.uva.locomotion.model;

import java.util.*;

public class View {

    private final String module;
    private final String category;
    private final String subcategory;

    private final Collection<String> primary_symbols;


    private final Collection<String> shadow_symbols;

    public View(String module, String category, String subcategory) {
        this.module = module;
        this.category = category;
        this.subcategory = subcategory;

        primary_symbols = new HashSet<>();
        shadow_symbols = new HashSet<>();
    }

    public View(String module, String category) {
        this(module, category, null);
    }

    public View(String module) {
        this(module, null, null);
    }

    public String getModule() {
        return module;
    }

    public String getCategory() {
        return category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public Collection<String> getSymbols() {
        Collection<String> collection = new HashSet<>();
        collection.addAll(primary_symbols);
        collection.addAll(shadow_symbols);
        return collection;
    }

    public Collection<String> getPrimary_symbols() {
        return primary_symbols;
    }

    public Collection<String> getShadow_symbols() {
        return shadow_symbols;
    }

    public void addPrimary(String token) {
        primary_symbols.add(token);
    }

    public void addShadow(String token) {
        shadow_symbols.add(token);
    }

    public boolean hasSymbol(String token) {
        return getSymbols().contains(token);
    }

    public boolean hasPrimary(String token) {
        return getPrimary_symbols().contains(token);
    }

    public boolean hasShadow(String token) {
        return getShadow_symbols().contains(token);
    }

    public String getCategoryOrSubcategory() {
        if (getSubcategory() != null) return getSubcategory();
        if (getCategory() != null) return getCategory();
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        View view = (View) o;
        return Objects.equals(module, view.module) && Objects.equals(category, view.category) && Objects.equals(subcategory, view.subcategory) && Objects.equals(primary_symbols, view.primary_symbols) && Objects.equals(shadow_symbols, view.shadow_symbols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, category, subcategory, primary_symbols, shadow_symbols);
    }

    @Override
    public String toString() {
        return "View{" +
                "name='" + module + '\'' +
                ", primary_symbols=" + primary_symbols +
                ", shadow_symbols=" + shadow_symbols +
                '}';
    }

    public String getIdentifier() {
        return generateIdentifier(getModule(), getCategory(), getSubcategory());
    }

    public static String generateIdentifier(String module, String category, String subcategory) {
        return module + (category != null ? category : "") + (subcategory != null ? subcategory : "");
    }


}
