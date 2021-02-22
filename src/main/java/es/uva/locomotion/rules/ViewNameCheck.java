package es.uva.locomotion.rules;


import es.uva.locomotion.model.*;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static es.uva.locomotion.utilities.Constants.*;


@Rule(key = ViewNameCheck.CHECK_KEY, name = ViewNameCheck.NAME, description = ViewNameCheck.HTML_DESCRIPTION)
public class ViewNameCheck extends AbstractVensimCheck {
    protected static final VensimLogger LOG = VensimLogger.getInstance();

    public static final String CHECK_KEY = "view-name-convention";
    public static final String NAME = "ViewNameCheck";
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that variables follow the name convention and match the regular expression \"([a-z0-9]+_)*[a-z0-9]+\"</p>\n" +
            "<ul>" +
            "   <li>The name must be in lower case.</li>\n" +
            "   <li>Each word must be separated by ONE underscore.</li>\n" +
            "   <li>The name shouldn't contain non-english characters.</li>\n" +
            "   <li>The name shouldn't start with a number</li>" +
            "</ul>" +
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>\n" +
            "ENERGY_REQUIRED\n" +
            "2019fuel_emissions\n" +
            "</pre>\n" +
            "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "energy_required\n" +
            "fuel_emissions_2019\n" +
            "</pre>\n";

    public static final String DEFAULT_REGEXP = "([a-z0-9]+_)*[a-z0-9]+";
    @RuleProperty(
            key = "variable-name-regexp",
            defaultValue = DEFAULT_REGEXP,
            description = "The regexp definition of a variable symbol name.")
    public final String regexp = DEFAULT_REGEXP;

    private String getRegexp() {
        try {
            Pattern.compile(regexp);
            return regexp;
        } catch (PatternSyntaxException exception) {
            LOG.unique("The rule " + NAME + " has an invalid configuration: The selected regexp is invalid. Error: " + exception.getDescription(),
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
            if (!view.isValid()) {
                int line = view.getLine();
                Issue issue = new Issue(this, line, "The view '" + view.getIdentifier() + "' deos not follow the naming convention, it should use the Module separator: \"" + moduleSeparator + "\" and the Category separator \"" + categorySeparator + "\".");

                boolean generateIssue = moduleName.equals("") || view.getModule().contains(moduleName);
                addIssue(context, issue, !generateIssue);
            }

        }
    }


}
