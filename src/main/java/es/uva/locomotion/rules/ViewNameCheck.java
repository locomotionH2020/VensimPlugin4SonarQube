package es.uva.locomotion.rules;


import es.uva.locomotion.model.View;
import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.model.VensimVisitorContext;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static es.uva.locomotion.utilities.Constants.*;


@Rule(key = ViewNameCheck.CHECK_KEY, name = ViewNameCheck.NAME, description = ViewNameCheck.HTML_DESCRIPTION)
public class ViewNameCheck extends VensimCheck {
    protected static final VensimLogger logger = VensimLogger.getInstance();

    public static final String CHECK_KEY = "view-name-convention";
    public static final String NAME = "ViewNameCheck";
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that views follow the name convention. The default regular expression is \"([a-z0-9]+)*[a-z0-9]+\"</p>\n" +
            "but it can be changed using custom quality profiles.\n The default separators are: \"-\" for module/category and \".\" for category/subcategory. \n" +
            "but it can be changed in sonarscanner properties file.\n The rest of this descriptions assumes the default regular expression is being used. \n" +
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>\n" +
            "*energy|share_RES_vs_TFEC\n\n" +
            "*energy.share.RES-vs-TFEC\n\n" +
            "</pre>\n" +
            "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "*energy.share_RES_vs_TFEC\n\n" +
            "*energy.share_RES_vs_TFEC-general\n\n" +
            "</pre>\n";

    public static final String DEFAULT_REGEXP = "([a-zA-Z0-9_]+)*[a-zA-Z0-9]+";
    @RuleProperty(
            key = "view-name-regexp",
            defaultValue = DEFAULT_REGEXP,
            description = "The regexp definition of a module/category of a view name (without the separators)")
    public final String regexp = DEFAULT_REGEXP;

    private String getRegexp() {
        try {
            Pattern.compile(regexp);
            return regexp;
        } catch (PatternSyntaxException exception) {
            logger.unique("The rule " + NAME + " has an invalid configuration: The selected regexp is invalid. Error: " + exception.getDescription(),
                    LoggingLevel.ERROR);
            return DEFAULT_REGEXP;
        }
    }

    @Override
    public void scan(VensimVisitorContext context) {
        ViewTable table = context.getViewTable();
        SensorContext sensorContext = context.getContext();


        String moduleName = sensorContext.config().get(MODULE_NAME).orElse("");
        String moduleSeparator = sensorContext.config().get(MODULE_SEPARATOR).orElse("");
        String categorySeparator = sensorContext.config().get(CATEGORY_SEPARATOR).orElse("");
        for (View view : table.getViews()) {
            if (generateIssue(view)) {
                invalidateView(view);
                for (int line : view.getLines()) { //TODO modificar el mensaje para que sea más claro
                    Issue issue = new Issue(this, line, "This view does not follow the naming convention, Detected module: " + view.getModule()+". Detected category: " + view.getCategory() + (view.getSubcategory() != null ? ". Detected subcategory: " + view.getSubcategory() : "")  +". Regular expresion that the modules and the (sub)category should follow: " + getRegexp());

                    boolean generateIssue = moduleName.equals("") || view.getModule().getName().contains(moduleName);
                    addIssue(context, issue, !generateIssue);
                }
            }

        }
    }

    private boolean generateIssue(View view) {
        if (view.getModule() == null || !view.getModule().getName().matches(getRegexp()))
            return true;

        if (view.getCategory() == null || !view.getCategory().getName().matches(getRegexp()))
            return true;
        return view.getSubcategory() != null && !view.getSubcategory().getName().matches(getRegexp());
    }
    private void invalidateView(View view){
        view.setAsInvalid(this.getClass().getSimpleName());

        if (view.getModule() != null && !view.getModule().getName().matches(getRegexp())){
            view.getModule().setAsInvalid(this.getClass().getSimpleName());
        }
        if (view.getCategory() != null && !view.getCategory().getName().matches(getRegexp())){
            view.getCategory().setAsInvalid(this.getClass().getSimpleName());
        }
        if (view.getSubcategory() != null && !view.getSubcategory().getName().matches(getRegexp())){
            view.getSubcategory().setAsInvalid(this.getClass().getSimpleName());
        }
    }

}
