package es.uva.locomotion.model;

import java.util.*;

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


    public void addView(View view) {
        String name = view.getName().trim();

        if(hasView(name))
            throw new IllegalArgumentException("The view:  "+ name+ " already exists.");

        table.put(name,view);
    }

    public View createOrSelectView(String viewName){
        if(hasView(viewName)){
            return getView(viewName);
        }else{
            View newView = new View(viewName);
            addView(newView);
            return newView;
        }
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
