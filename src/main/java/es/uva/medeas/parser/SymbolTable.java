package es.uva.medeas.parser;
import org.antlr.v4.runtime.Token;

import java.util.*;
import java.util.stream.Collectors;

public class SymbolTable {


    private Map<String, Symbol> table;

    public SymbolTable() {
        this.table = new HashMap<>();
    }


    public Symbol getSymbol(String token){
        return table.get(token);
    }


    public Symbol getSymbolOrCreate(String token){

        if(table.containsKey(token))
            return table.get(token);

        else{
            return createSymbol(token);
        }

    }


    public Symbol createSymbol(String token){
        Symbol symbol = new Symbol(token);
        table.put(token,symbol);
        return symbol;
    }

    public Symbol getSymbolOrCreate(Token token){
       return getSymbolOrCreate(token.getText());

    }
    public void resolveDependencies(){
        //TODO método no implementado
        throw new IllegalStateException("Método no implementado");
    }

    public void print(){
        //TODO Temporal para comprobar visualmente si funciona:
        for(Symbol symbol: table.values()){
            System.out.println(symbol.getToken() + ": " + symbol.getType());
            if (symbol.getContext()!=null)
                System.out.println("Línea: " + symbol.getContext().getStart().getLine());
            System.out.print("Dependenencias: ");
            for(Symbol dep: symbol.getDependencies()){
                System.out.print(dep.getToken() + "  ");
            }
            System.out.print("\n");
        }
    }

    public Collection<Symbol> getSymbols(){
        return table.values();
    }


    public boolean hasSymbol(String token){
        return table.containsKey(token);
    }


    public Set<Symbol> getUndeterminedSymbols(){
        return getSymbols().stream().filter(symbol -> symbol.getType()==SymbolType.UNDETERMINED).collect(Collectors.toSet());
    }

}
