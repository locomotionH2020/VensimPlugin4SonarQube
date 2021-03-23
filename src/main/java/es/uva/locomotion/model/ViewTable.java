package es.uva.locomotion.model;

import es.uva.locomotion.model.category.Category;
import es.uva.locomotion.model.category.CategoryMap;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ViewTable {
    private final Map<String, View> table;
    private final Set<Module> modulesList;
    private final CategoryMap categoriesList;


    public ViewTable() {
        this.table = new HashMap<>();
        this.modulesList = new HashSet<>();
        this.categoriesList = new CategoryMap();
    }

    public Set<Module> getModules() {
        return modulesList;
    }


    public View getView(String name) {
        return table.getOrDefault(name, null);
    }


    public List<View> getViews() {
        List<View> view = new ArrayList<>(table.values());
        view.sort(Comparator.comparing(View::getIdentifier));
        return view;
    }

    public boolean hasView(String name) {
        return table.containsKey(name);
    }


    protected void addView(View view) {
        String name = view.getIdentifier().trim();

        if (hasView(name))
            throw new IllegalArgumentException("The view:  " + name + " already exists.");

        table.put(name, view);
    }

    public View createOrSelectView(String moduleName, String categoryName, String subcategoryName) {
        String identifier = View.generateIdentifier(moduleName, categoryName, subcategoryName);
        if (hasView(identifier)) {
            return getView(identifier);

        } else {
            Module module = createOrSelectModule(moduleName);
            Category category = null;
            Category subcategory = null;
            if (categoryName != null) {
                category = categoriesList.createOrSelectCategory(categoryName);
                if (subcategoryName != null) {
                    subcategory = categoriesList.addSubcategoryTo(category, subcategoryName);

                }
            }
            View newView = new View(module, category, subcategory);
            addModule(module);
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


    private Module createOrSelectModule(String moduleName) {
        Module module = modulesList.stream().filter(mod -> moduleName.equals(mod.getName())).findFirst().orElse(null);

        if (module == null) {
            Module newModule = new Module(moduleName);
            modulesList.add(newModule);
            return newModule;
        }
        return module;
    }

    public void addModule(Module module) {
        modulesList.add(module);
    }

    public Category getCategory(String category) {
        return categoriesList.getCategory(category);
    }

    public List<Category> getCategories() {
        return categoriesList.getCategories();
    }

    public List<Category> getSubcategories() {
        return categoriesList.getCategories().stream().flatMap((cat) -> cat.getSubcategories().stream()).collect(Collectors.toList());
    }

    public List<Category> getCategoriesAndSubcategories() {
        return Stream.concat(getCategories().stream(), getSubcategories().stream())
                .collect(Collectors.toList());
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
