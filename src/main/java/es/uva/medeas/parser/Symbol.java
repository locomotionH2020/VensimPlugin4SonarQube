package es.uva.medeas.parser;

import org.antlr.v4.runtime.ParserRuleContext;


import java.util.*;

public class Symbol {

    private String token;
    private ParserRuleContext context;
    private Set<Symbol> dependencies;
    private SymbolType type;


    public Symbol(String token){
        this.token = token;
        dependencies = new HashSet<>();
        type = SymbolType.UNDETERMINED;
    }

    public void setContext(ParserRuleContext context) {
        this.context = context;
    }

    public void setType(SymbolType type) {
        this.type = type;
    }

    public ParserRuleContext getContext() {
        return context;
    }

    public int getLine(){
        return context.start.getLine();
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

}
