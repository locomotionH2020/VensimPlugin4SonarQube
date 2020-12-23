package es.uva.locomotion.model;

import java.util.*;

public class ViewTable {
    private Map<String, View> table;
    private Set<String> modules;
    private SortedMap<String, Set<String>> categories;


    public ViewTable() {
        this.table = new HashMap<>();
        this.modules = new HashSet<>();
        this.categories = new TreeMap<>();
    }


    public View getView(String name) {
        if (table.containsKey(name)) {
            return table.get(name);
        } else
            throw new IllegalArgumentException("Name " + name + " not found");
    }


    public List<View> getViews() {
        List<View> view = new ArrayList<>(table.values());
        view.sort(Comparator.comparing(View::getModule));
        return view;
    }


    public boolean hasView(String name) {
        return table.containsKey(name);
    }


    public void addView(View view) {
        String name = view.getIdentifier().trim();

        if (hasView(name))
            throw new IllegalArgumentException("The view:  " + name + " already exists.");

        table.put(name, view);
    }

    public View createOrSelectView(String module, String category, String subcategory) {
        String identifier = View.generateIdentifier(module, category, subcategory);
        if (hasView(identifier)) {
            return getView(identifier);
        } else {
            View newView = new View(module, category, subcategory);
            addModule(module);
            if (category != null) {
                addCategory(category);
                if(subcategory != null){
                    addSubcategory(category, subcategory);
                }
            }
            addView(newView);
            return newView;
        }
    }

    public View createOrSelectView(String module, String category) {
        return createOrSelectView(module, category, null);
    }

    public View createOrSelectView(String module) {
        return createOrSelectView(module, null, null);
    }

    public View removeView(View view) {
        String name = view.getModule().trim();

        if (!hasView(name))
            throw new IllegalArgumentException("The view:  " + name + " is not in the Table.");

        table.remove(name);
        return view;
    }


    public List<String> getModules() {
        List<String> mod = new ArrayList<>(modules);
        mod.sort(Comparator.comparing(String::toString));
        return mod;
    }


    public boolean hasModule(String name) {
        return modules.contains(name);
    }

    public void addModule(String module) {
        modules.add(module);
    }

    public List<String> getCategoriesName() {
        List<String> cat = new ArrayList<>(categories.keySet());
        cat.sort(Comparator.comparing(String::toString));
        return cat;
    }

    public SortedMap<String, Set<String>> getCategories() {
        return new TreeMap<>(categories);
    }

    public List<String> getSubcategoriesFromCategory(String categoryName) {
        if (table.containsKey(categoryName)) {
            return new ArrayList<>(categories.get(categoryName));
        } else
            throw new IllegalArgumentException("Category " + categoryName + " not found");
    }

    public boolean hasCategory(String name) {
        return categories.containsKey(name);
    }


    public void addCategory(String category) {

        if (hasView(category))
            return;
        categories.put(category, new HashSet<>());
    }


    public boolean hasSubcategory(String category, String subcategory) {
        Set<String> subcategories = categories.get(category);
        if (subcategories == null)
            throw new IllegalArgumentException("The category:  " + category + " does not exists.");
        return subcategories.contains(subcategory);
    }

    public void addSubcategory(String category, String subcategory) {
        categories.get(category).add(subcategory);
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
