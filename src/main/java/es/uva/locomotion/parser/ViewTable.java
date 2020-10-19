package es.uva.locomotion.parser;

import java.util.*;
import java.util.stream.Collectors;

public class ViewTable {
    private Map<String, View> table;

    public ViewTable() {
        this.table = new HashMap<>();
    }


    public View getView(String name){
        if(table.containsKey(name)) {
            return table.get(name);
        }else
            throw new IllegalArgumentException("Name " + name + " not found");
    }



    public Collection<View> getViews(){
        List<View> view =  new ArrayList<>(table.values());
        view.sort(Comparator.comparing(View::getName));
        return view;
    }


    public boolean hasView(String name){
        return table.containsKey(name);
    }


    public View addView(View view) {
        String name = view.getName().trim();

        if(hasView(name))
            throw new IllegalArgumentException("The view:  "+ name+ " already exists.");

        table.put(name,view);
        return view;
    }

    public View removeView(View view) {
        String name = view.getName().trim();

        if(!hasView(name))
            throw new IllegalArgumentException("The view:  "+ name+ " is not in the Table.");

        table.remove(name);
        return view;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ViewTable)) return false;
        ViewTable table1 = (ViewTable) o;
        return table.equals(table1.table);
    }

    @Override
    public String toString() {
        return "ViewTable{" +
                "table=" + table +
                '}';
    }
}
