package es.uva.locomotion.model;

import java.util.*;
import java.util.stream.Collectors;

public class NumberTable {

    protected Map<String, Number> table;

    public NumberTable() {
        this.table = new HashMap<>();
    }


    public Number getNumber(String token){
        return table.getOrDefault(token, null);
    }

    public Collection<Number> getNumbers(){
        List<Number> Numbers =  new ArrayList<>(table.values());
        Numbers.sort(Comparator.comparing(Number::getToken));
        return Numbers;
    }


    public boolean hasNumber(String token){
        return table.containsKey(token);
    }


    public Number addNumber(Number Number) {
        String token = Number.getToken().trim();

        if(hasNumber(token))
            throw new IllegalArgumentException("The Number:  "+ token+ " already exists.");

        table.put(token,Number);
        return Number;
    }

    public Number removeNumber(String Number) {

        if(!hasNumber(Number))
            throw new IllegalArgumentException("The Number:  "+ Number+ " is not in the Table.");

        return table.remove(Number);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumberTable)) return false;
        NumberTable table1 = (NumberTable) o;
        return table.equals(table1.table);
    }

    @Override
    public String toString() {
        return "NumberTable{" +
                "table=" + table +
                '}';
    }
  
}
