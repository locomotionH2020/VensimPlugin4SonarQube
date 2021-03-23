package es.uva.locomotion.model;

import java.util.*;
import java.util.stream.Collectors;

public class SymbolTable {


    protected Map<String, Symbol> table;

    public SymbolTable() {
        this.table = new HashMap<>();
    }


    public Symbol getSymbol(String token){
        return table.getOrDefault(token, null);
    }

    public Collection<Symbol> getSymbols(){
        List<Symbol> symbols =  new ArrayList<>(table.values());
        symbols.sort(Comparator.comparing(Symbol::getToken));
        return symbols;
    }


    public boolean hasSymbol(String token){
        return table.containsKey(token);
    }


    /**
     * @return Symbols that have type SymbolType.UNDETERMINED or SymbolType.UNDETERMINED_FUNCTION
     */
    public Set<Symbol> getUndeterminedSymbols(){

        return getSymbols().stream().filter(symbol -> symbol.getType()==SymbolType.UNDETERMINED || symbol.getType()==SymbolType.UNDETERMINED_FUNCTION).collect(Collectors.toSet());
    }

    public Symbol addSymbol(Symbol symbol) {
        String token = symbol.getToken().trim();

        if(hasSymbol(token))
            throw new IllegalArgumentException("The symbol:  "+ token+ " already exists.");

        table.put(token,symbol);
        return symbol;
    }

    public Symbol removeSymbol(String symbol) {

        if(!hasSymbol(symbol))
            throw new IllegalArgumentException("The symbol:  "+ symbol+ " is not in the Table.");

        return table.remove(symbol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SymbolTable)) return false;
        SymbolTable table1 = (SymbolTable) o;
        return table.equals(table1.table);
    }

    @Override
    public String toString() {
        return "SymbolTable{" +
                "table=" + table +
                '}';
    }

}
