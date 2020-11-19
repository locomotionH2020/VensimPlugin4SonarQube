package es.uva.locomotion.model;

import java.util.*;

public class View {

    private String name;

    private Collection<String> primary_symbols;


    private Collection<String> shadow_symbols;

    public View(String name){
        this.name = name;
        primary_symbols = new HashSet<>();
        shadow_symbols = new HashSet<>();
    }

    public String getName(){
        return name;
    }
    public Collection<String> getSymbols(){
        Collection <String> collection = new HashSet<>();
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

    public void addPrimary(String token){
        primary_symbols.add(token);
    }

    public void addShadow(String token){
        shadow_symbols.add(token);
    }

    public boolean hasSymbol(String token){
        return getSymbols().contains(token);
    }

    public boolean hasPrimary(String token){
        return getPrimary_symbols().contains(token);
    }

    public boolean hasShadow(String token){
        return getShadow_symbols().contains(token);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        View view = (View) o;
        return Objects.equals(name, view.name) &&
                Objects.equals(primary_symbols, view.primary_symbols) &&
                Objects.equals(shadow_symbols, view.shadow_symbols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, primary_symbols, shadow_symbols);
    }

    @Override
    public String toString() {
        return "View{" +
                "name='" + name + '\'' +
                ", primary_symbols=" + primary_symbols +
                ", shadow_symbols=" + shadow_symbols +
                '}';
    }
}
