package es.uva.locomotion.model;

import es.uva.locomotion.model.category.CategoryMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataBaseRepresentation {

    private SymbolTable dataBaseSymbols;
    private AcronymsList acronyms;
    private Set<Module> modules;
    private CategoryMap categories;
    private Set<String> units;

    public DataBaseRepresentation() {
        dataBaseSymbols = null;
        acronyms = null;
        units = null;
    }

    public SymbolTable getDataBaseSymbols() {
        return dataBaseSymbols;
    }


    public void setDataBaseSymbols(SymbolTable dataBaseSymbols) {
        this.dataBaseSymbols = dataBaseSymbols;
    }

    public AcronymsList getAcronyms() {
        return acronyms;
    }

    public void setAcronyms(AcronymsList acronyms) {
        this.acronyms = acronyms;
    }

    public CategoryMap getCategories() {
        return categories;
    }

    public void setCategories(CategoryMap categories) {
        this.categories = categories;
    }

    public Set<Module> getModules() {
        return new HashSet<>(modules);//TODO si no hay diccionario peta.
    }

    public void setModules(Set<Module> modules) {
        this.modules = modules;
    }

    public Set<String> getUnits() {
        if(units == null){
            return null;
        }
        return new HashSet<>(units);
    }

    public void setUnits(Set<String> units) {
        this.units = units;
    }
}
