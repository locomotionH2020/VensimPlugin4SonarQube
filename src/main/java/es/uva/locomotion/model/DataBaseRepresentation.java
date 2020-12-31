package es.uva.locomotion.model;

import java.util.List;

public class DataBaseRepresentation {

    private SymbolTable dataBaseSymbols;
    private AcronymsList acronyms;
    private List<String> modules;
    private CategoryMap categories;

    public DataBaseRepresentation() {
    dataBaseSymbols = new SymbolTable();
    acronyms = new AcronymsList();
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
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }
}
