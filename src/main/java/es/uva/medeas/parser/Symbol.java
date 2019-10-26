package es.uva.medeas.parser;


import java.util.*;

public class Symbol {

    public static int LINE_NOT_DEFINED = -1;
    private String token;
    private int lineDefined;
    private Set<Symbol> dependencies;
    private SymbolType type;


    public Symbol(String token){
        this.token = token;
        dependencies = new HashSet<>();
        type = SymbolType.UNDETERMINED;
        lineDefined = LINE_NOT_DEFINED;
    }

    public void setDefinitionLine(int line){ 
        this.lineDefined = line;
    }

    public void setType(SymbolType type) {
        this.type = type;
    }


    public int getDefinitionLine(){
        return lineDefined;
    }

    public Set<Symbol> getDependencies() {
        return dependencies;
    }

    public SymbolType getType() {
        return type;
    }

    public void addDependency(Symbol symbol){
        dependencies.add(symbol);

    }

    public void addDependencies(Collection<Symbol> symbols){
        for(Symbol symb: symbols){
            addDependency(symb);
        }
    }

    public String getToken() {
        return token;
    }


    public boolean hasType(){
        return getType()!=SymbolType.UNDETERMINED;
    }
}
