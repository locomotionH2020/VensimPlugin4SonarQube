package es.uva.locomotion.rules;


import es.uva.locomotion.model.category.Category;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.sonar.check.Rule;

import java.util.ArrayList;
import java.util.List;


@Rule(key = CategoryDuplicatedCheck.CHECK_KEY, name = CategoryDuplicatedCheck.NAME, description = CategoryDuplicatedCheck.HTML_DESCRIPTION)
public class CategoryDuplicatedCheck extends AbstractVensimCheck {
    protected static final VensimLogger logger = VensimLogger.getInstance();

    public static final String CHECK_KEY = "category_duplicated";
    public static final String NAME = "CategoryDuplicatedCheck";
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that all categories and subcategories are unique, there can not be two subcategories with the same" +
            " name even though they belong to diferent categories." +
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>\n" +
            "*energy.consumption-land\n\n" +
            "*energy.generation-land\n\n" +
            "</pre>\n" +
            "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "*energy.consumption-land\n\n" +
            "*energy.generation-terra\n\n" +
            "</pre>\n";


    @Override
    public void scan(VensimVisitorContext context) {
        List<Category> subcategoryList = new ArrayList<>(context.getViewTable().getSubcategories());
        List<Category> categoryList = new ArrayList<>(context.getViewTable().getCategories());
        List<Category> dbSubcategoryList;
        List<Category> dbCategoryList;
        if (context.getDbdata() != null && context.getDbdata().getCategoriesMap() != null) {
            dbSubcategoryList = new ArrayList<>(context.getDbdata().getCategoriesMap().getSubcategories());
            dbCategoryList = new ArrayList<>(context.getDbdata().getCategoriesMap().getCategories());
        } else {
            dbSubcategoryList = new ArrayList<>();
            dbCategoryList = new ArrayList<>();
        }
        List<Category> alreadyInDB = new ArrayList<>();
        while (!subcategoryList.isEmpty()) {
            Category actual = subcategoryList.remove(0);
            if (dbSubcategoryList.contains(actual)) {
                alreadyInDB.add(actual);
            } else {
                if (subcategoryList.stream().anyMatch(subcat -> subcat.getName().equalsIgnoreCase(actual.getName())) ||
                        dbCategoryList.stream().anyMatch(dbCategory -> dbCategory.getName().equalsIgnoreCase(actual.getName())) ||
                        categoryList.stream().anyMatch(category -> category.getName().equalsIgnoreCase(actual.getName())) ||
                        alreadyInDB.stream().anyMatch(alreadyInDBItem -> alreadyInDBItem.getName().equalsIgnoreCase(actual.getName()))) {
                    actual.setAsInvalid(this.getClass().getSimpleName());
                    for (int line : actual.getLines()) {
                        Issue issue = new Issue(this, line, "The subcategory '" + actual.getName() + "' already exists in the model as a category or a subcategory.");
                        addIssue(context, issue, false);
                    }
                }
            }
        }
        while (!categoryList.isEmpty()){
            Category actual = categoryList.remove(0);
            if(dbSubcategoryList.stream().anyMatch(catOrSubcat -> catOrSubcat.getName().equalsIgnoreCase(actual.getName()))){
                actual.setAsInvalid(this.getClass().getSimpleName());
                for (int line : actual.getLines()) {
                    Issue issue = new Issue(this, line, "The category '" + actual.getName() + "' already exists in the model as a category or a subcategory.");
                    addIssue(context, issue, false);
                }
            }
        }
    }


}
