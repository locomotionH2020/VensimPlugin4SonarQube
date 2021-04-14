package es.uva.locomotion.rules;


import es.uva.locomotion.model.category.Category;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.sonar.check.Rule;

import java.util.ArrayList;
import java.util.List;


@Rule(key = SubcategoryDuplicatedCheck.CHECK_KEY, name = SubcategoryDuplicatedCheck.NAME, description = SubcategoryDuplicatedCheck.HTML_DESCRIPTION)
public class SubcategoryDuplicatedCheck extends AbstractVensimCheck {
    protected static final VensimLogger logger = VensimLogger.getInstance();

    public static final String CHECK_KEY = "subcategory_duplicated";
    public static final String NAME = "SubcategoryDuplicatedCheck";
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that all subcategories are unique, there can not be two subcategories with the same" +
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
        List<Category> categoryList = new ArrayList<>(context.getViewTable().getSubcategories());

        while (!categoryList.isEmpty()) {
            Category actual = categoryList.remove(0);
            if (categoryList.stream().anyMatch(subcat -> subcat.getName().equals(actual.getName()))) {
                actual.setAsInvalid(this.getClass().getSimpleName());
                for (int line : actual.getLines()) {
                    Issue issue = new Issue(this, line, "The subcategory '" + actual.getName() + "' already exists in the model in the category:'" + categoryList.stream().filter(subcat -> subcat.getName().equals(actual.getName())).findFirst().get().getName() + "', this can not happen.");
                    addIssue(context, issue, false);
                }
            }

        }
    }


}
