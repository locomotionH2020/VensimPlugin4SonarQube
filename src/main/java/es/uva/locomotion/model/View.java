package es.uva.locomotion.model;

import es.uva.locomotion.model.category.Category;
import es.uva.locomotion.model.symbol.Symbol;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class View extends IssuableAbs {

    private final Module module;
    private final Category category;
    private final Category subcategory;

    private final Set<Symbol> primarySymbols;
    private final Set<Symbol> shadowSymbols;

    public View(Module module, Category category, Category subcategory) {
        super();

        this.module = module;
        this.category = category;
        this.subcategory = subcategory;
        primarySymbols = new HashSet<>();
        shadowSymbols = new HashSet<>();
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
        collection.addAll(primarySymbols);
        collection.addAll(shadowSymbols);
        return collection;
    }

    public Set<Symbol> getPrimarySymbols() {
        return primarySymbols;
    }

    public Set<Symbol> getShadowSymbols() {
        return shadowSymbols;
    }

    public void addPrimary(Symbol token) {
        token.setPrimaryModule(getModule());
        token.setCategory(getCategory());
        primarySymbols.add(token);
    }

    public void addShadow(Symbol token) {
        token.addShadowModule(getModule());
        shadowSymbols.add(token);

    }

    public boolean hasSymbol(String token) {
        return getSymbols().stream().map(Symbol::getToken).collect(Collectors.toList()).contains(token);
    }

    public Symbol getSymbol(String token) {
        return getSymbols().stream().filter(s -> s.getToken().equals(token)).collect(Collectors.toList()).get(0);
    }

    public boolean hasPrimary(String token) {
        return getPrimarySymbols().stream().map(Symbol::getToken).collect(Collectors.toList()).contains(token);
    }

    public boolean hasShadow(String token) {
        return getShadowSymbols().stream().map(Symbol::getToken).collect(Collectors.toList()).contains(token);
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
                ", primary_symbols=" + primarySymbols +
                ", shadow_symbols=" + shadowSymbols +
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
        if (getSubcategory() != null)
            getSubcategory().addLine(line);
        getModule().addLine(line);
    }

    @Override
    public void setAsInvalid(String invalidReason) {
        super.setAsInvalid(invalidReason);
        primarySymbols.forEach(symbol -> symbol.setAsInvalid(invalidReason));
    }
}
