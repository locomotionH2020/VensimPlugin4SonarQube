package es.uva.locomotion.model;

import es.uva.locomotion.model.category.Category;

import java.util.*;
import java.util.stream.Collectors;

public class View extends IssuableAbs {

    private final Module module;
    private final Category category;
    private final Category subcategory;

    private final Set<Symbol> primary_symbols;
    private final Set<Symbol> shadow_symbols;

    public View(Module module, Category category, Category subcategory) {
        super();

        this.module = module;
        this.category = category;
        this.subcategory = subcategory;
        primary_symbols = new HashSet<>();
        shadow_symbols = new HashSet<>();
    }

    public View(Module module) {
        this(module, null, null);
    }

    public Module getModule() {
        return module;
    }

    public Category getCategory() {
        return category;
    }

    public Category getSubcategory() {
        return subcategory;
    }

    public Category getCategoryOrSubcategory() {
        if (getSubcategory() == null) {
            return getCategory();
        }
        return getSubcategory();
    }

    public Set<Symbol> getSymbols() {
        Set<Symbol> collection = new HashSet<>();
        collection.addAll(primary_symbols);
        collection.addAll(shadow_symbols);
        return collection;
    }

    public Set<Symbol> getPrimary_symbols() {
        return primary_symbols;
    }

    public Set<Symbol> getShadow_symbols() {
        return shadow_symbols;
    }

    public void addPrimary(Symbol token) {
        token.setPrimary_module(getModule());
        token.setCategory(getCategory());
        primary_symbols.add(token);
    }

    public void addShadow(Symbol token) {
        token.addShadow_module(getModule());
        shadow_symbols.add(token);

    }

    public boolean hasSymbol(String token) {
        return getSymbols().stream().map(Symbol::getToken).collect(Collectors.toList()).contains(token);
    }

    public Symbol getSymbol(String token) {
        return getSymbols().stream().filter((s) -> s.getToken().equals(token)).collect(Collectors.toList()).get(0);
    }

    public boolean hasPrimary(String token) {
        return getPrimary_symbols().stream().map(Symbol::getToken).collect(Collectors.toList()).contains(token);
    }

    public boolean hasShadow(String token) {
        return getShadow_symbols().stream().map(Symbol::getToken).collect(Collectors.toList()).contains(token);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        View view = (View) o;
        return module.equals(view.module) && Objects.equals(category, view.category) && Objects.equals(subcategory, view.subcategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, category, subcategory);
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
        return generateIdentifier(getModule().getName(), getCategory() == null ? null : getCategory().getName(), getSubcategory() == null ? null : getSubcategory().getName());
    }


    public static String generateIdentifier(String module, String category, String subcategory) {
        return module + (category != null ? "_" + category : "") + (subcategory != null ? "_" + subcategory : "");
    }

    @Override
    public void addLine(int line) {
        super.addLine(line);

        if (getCategory() != null)
            getCategory().addLine(line);
        getModule().addLine(line);
    }

    @Override
    public void setAsInvalid(String invalidReason) {
        super.setAsInvalid(invalidReason);
        module.setAsInvalid(invalidReason);
        if (category != null) category.setAsInvalid(invalidReason);
        if (subcategory != null) subcategory.setAsInvalid(invalidReason);
        primary_symbols.forEach((symbol) -> symbol.setAsInvalid(invalidReason));
        shadow_symbols.forEach((symbol) -> symbol.setAsInvalid(invalidReason));
    }
}
