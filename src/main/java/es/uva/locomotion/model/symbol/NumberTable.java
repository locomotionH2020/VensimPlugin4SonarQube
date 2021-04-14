package es.uva.locomotion.model.symbol;

import java.util.*;

public class NumberTable {

    protected Map<String, Number> table;

    public NumberTable() {
        this.table = new HashMap<>();
    }


    public Number getNumber(String token){
        return table.getOrDefault(token, null);
    }

    public Collection<Number> getNumbers(){
        List<Number> numbers =  new ArrayList<>(table.values());
        numbers.sort(Comparator.comparing(Number::getToken));
        return numbers;
    }


    public boolean hasNumber(String token){
        return table.containsKey(token);
    }


    public Number addNumber(Number number) {
        String token = number.getToken().trim();

        if(hasNumber(token))
            throw new IllegalArgumentException("The number:  "+ token+ " already exists.");

        table.put(token,number);
        return number;
    }

    public Number removeNumber(String number) {

        if(!hasNumber(number))
            throw new IllegalArgumentException("The number:  "+ number+ " is not in the Table.");

        return table.remove(number);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumberTable)) return false;
        NumberTable table1 = (NumberTable) o;
        return table.equals(table1.table);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table);
    }

    @Override
    public String toString() {
        return "NumberTable{" +
                "table=" + table +
                '}';
    }
  
}
