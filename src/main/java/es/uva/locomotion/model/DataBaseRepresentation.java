package es.uva.locomotion.model;

import java.util.ArrayList;
import java.util.List;

public class DataBaseRepresentation {

    private SymbolTable dataBaseSymbols;
    private AcronymsList acronyms;
    private List<String> modules;
    private CategoryMap categories;
    private List<String> units;

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

    public List<String> getModules() {
        return new ArrayList<>(modules);
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    public List<String> getUnits() {
        return new ArrayList<>(units);
    }

    public void setUnits(List<String> units) {
        this.units = units;
    }
}
