package es.uva.medeas.parser;
import org.antlr.v4.runtime.Token;

import java.util.*;

public class SymbolTable {


    private Map<String, Symbol> table;

    public SymbolTable() {
        this.table = new HashMap<>();
    }


    public Symbol getSymbol(String token){
        return table.get(token);
    }


    public Symbol getSymbolOrCreate(Token token){
        String tokenText = token.getText();

        if(table.containsKey(tokenText))
            return table.get(tokenText);

        else{
            Symbol symbol = new Symbol(tokenText);
            table.put(tokenText,symbol);
            return symbol;
        }

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

}
