package es.uva.locomotion.model;

import java.util.*;

//TODO refactor to unbind from CategoryMap
public class ViewTable {
    private final Map<String, View> table;
    private final List<String> modulesList;
    private final CategoryMap categoriesList;


    public ViewTable() {
        this.table = new HashMap<>();
        this.modulesList = new ArrayList<>();
        this.categoriesList = new CategoryMap();
    }

    public List<String> getModules() {
        return modulesList;
    }

    public CategoryMap getCategories() {
        return categoriesList;
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

    public View createOrSelectView(String module, String category, String subcategory, boolean isValid) {
        String identifier = View.generateIdentifier(module, category, subcategory);
        if (hasView(identifier)) {
            return getView(identifier);
        } else {
            View newView = new View(module, category, subcategory, isValid);
            if (!hasModule(module) && isValid) addModule(module);
            if (category != null) {
                if (!hasCategory(category) && isValid) addCategory(category);
                if (subcategory != null) {
                    if (!hasSubcategory(category, subcategory) && isValid) addSubcategory(category, subcategory);
                }
            }
            addView(newView);
            return newView;
        }
    }
    public View createOrSelectView(String module, String category, String subcategory) {
        return createOrSelectView(module, category, subcategory, true);

    }
    public View createOrSelectView(String module, String category, boolean isValid) {
        return createOrSelectView(module, category, null, isValid);
    }

    public View createOrSelectView(String module) {
        return createOrSelectView(module, null, null, false);
    }

    public View removeView(View view) {
        String name = view.getModule().trim();

        if (!hasView(name))
            throw new IllegalArgumentException("The view:  " + name + " is not in the Table.");

        table.remove(name);
        return view;
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

    public List<Category> getCategoriesList() {
        return categoriesList.getCategoriesList();
    }


    public boolean hasCategory(String name) {
        return categoriesList.contains(name);
    }


    public void addCategory(String category) {

        if (hasCategory(category))
            return;
        Category c = new Category(category);
        categoriesList.add(c);
    }

    public Set<Category> getSubcategories(String categoryName) {
        if (!categoriesList.contains(categoryName)) {
            throw new IllegalArgumentException(categoryName + " is not a category name in the map.");
        }
        Category category = categoriesList.createOrSelectCategory(categoryName);
        return category.getSubcategories();
    }

    public boolean hasSubcategory(String categoryName, String subcategory) {
        if (!categoriesList.contains(categoryName)) {
            throw new IllegalArgumentException(categoryName + " is not a category name in the map.");
        }
        Category category = categoriesList.createOrSelectCategory(categoryName);

        Set<String> subcategories = category.getSubcategoriesNames();
        if (subcategories == null) {
            return false;
        }
        return subcategories.contains(subcategory);
    }

    public void addSubcategory(String categoryName, String subcategory) {
        if (!categoriesList.contains(categoryName)) {
            throw new IllegalArgumentException(categoryName + " is not a category name in the map.");
        }
        Category category = categoriesList.createOrSelectCategory(categoryName);

        Category sub = new Category(subcategory);
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
