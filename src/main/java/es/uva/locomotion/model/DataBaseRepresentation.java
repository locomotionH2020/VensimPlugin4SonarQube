package es.uva.locomotion.model;

public class DataBaseRepresentation {

    private SymbolTable dataBaseSymbols;
    private AcronymsList acronyms;
    private ModulesList modules;
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

    public ModulesList getModules() {
        return modules;
    }

    public void setModules(ModulesList modules) {
        this.modules = modules;
    }
}
