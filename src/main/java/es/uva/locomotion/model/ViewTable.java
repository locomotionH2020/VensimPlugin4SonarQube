package es.uva.locomotion.model;

import java.util.*;

public class ViewTable {
    private Map<String, View> table;
    private ModulesList modulesList;
    private CategoryList categoriesList;


    public ViewTable() {
        this.table = new HashMap<>();
        this.modulesList = new ModulesList();
        this.categoriesList = new CategoryList();
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


    public List<String> getModulesList() {
        return modulesList.getModules();
    }


    public boolean hasModule(String name) {
        return modulesList.contains(name);
    }

    public void addModule(String module) {
        modulesList.add(module);
    }

    public List<String> getCategoriesName() {
        return categoriesList.getCategoriesName();
    }

    public Map<String, Category> getCategoriesList() {
        return categoriesList.getCategoriesList();
    }


    public boolean hasCategory(String name) {
        return categoriesList.containsCategory(name);
    }


    public void addCategory(String category) {

        if (hasCategory(category))
            return;
        Category c = new Category(category);
        categoriesList.add(c);
    }

    public Set<Category> getSubcategories(String categoryName) {
        Category category = categoriesList.getCategory(categoryName);
        return category.getSubcategories();
    }

    public boolean hasSubcategory(String categoryName, String subcategory) {
        Category category = categoriesList.getCategory(categoryName);

        Set<String> subcategories = category.getSubcategoriesNames();

        return subcategories.contains(subcategory);
    }

    public void addSubcategory(String categoryName, String subcategory) {
        Category category = categoriesList.getCategory(categoryName);

        Category sub = new Category(category,subcategory);
        category.addSubcategory(sub);
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
