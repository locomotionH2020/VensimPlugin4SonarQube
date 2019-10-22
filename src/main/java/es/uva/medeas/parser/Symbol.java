package es.uva.medeas.parser;


import java.util.*;

public class Symbol {

    private String token;
    private int line;
    private Set<Symbol> dependencies;
    private SymbolType type;


    public Symbol(String token){
        this.token = token;
        dependencies = new HashSet<>();
        type = SymbolType.UNDETERMINED;
        line = -1;
    }

    public void setLine(int line){ //TODO dejar mas claro que esta es la linea en la que se define, no en la que aparece
        this.line = line;
    }

    public void setType(SymbolType type) {
        this.type = type;
    }


    public int getLine(){
        return line;
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
