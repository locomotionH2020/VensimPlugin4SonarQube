package es.uva.locomotion.parser;

import java.util.*;
import java.util.stream.Collectors;

public class SymbolTable {


    private Map<String, Symbol> table;

    public SymbolTable() {
        this.table = new HashMap<>();
    }


    public Symbol getSymbol(String token){
        if(table.containsKey(token)) {
            return table.get(token);
        }else
            throw new IllegalArgumentException("Token " + token + " not found");
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

    public Symbol removeSymbol(Symbol symbol) {
        String token = symbol.getToken().trim();

        if(!hasSymbol(token))
            throw new IllegalArgumentException("The symbol:  "+ token+ " is not in the Table.");

        table.remove(token);
        return symbol;
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
