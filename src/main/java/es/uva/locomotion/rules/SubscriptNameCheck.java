package es.uva.locomotion.rules;


import es.uva.locomotion.model.symbol.Subscript;
import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


@Rule(key = SubscriptNameCheck.CHECK_KEY, name = SubscriptNameCheck.NAME, description = SubscriptNameCheck.HTML_DESCRIPTION)
public class SubscriptNameCheck extends AbstractVensimCheck {
    public static final String CHECK_KEY = "subscript-convention";
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that subscripts follow the name convention.The default regular expression is \"([A-Z0-9]+_)+I\"</p>\n" +
            "but it can be changed using custom quality profiles. \n The rest of this descriptions assumes the default regular expression is being used. \n" +
            "<ul>" +
            "   <li>Name must be in upper case (subscripts are constants).</li>\n" +
            "   <li>Each word must be separated by ONE underscore.</li>\n" +
            "   <li>The name must have the suffix '_I'.</li>" +
            "   <li>The name shouldn't start with a number</li>" +
            "   <li>The name shouldn't contain non-english characters. </li>" +
            "</ul>" +
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>\n" +
            "MY_COUNTRIES: COUNTRY1, COUNTRY2  ~~|\n" +
            "my_countries_i: COUNTRY1, COUNTRY2 ~~|\n" +
            "</pre>\n" +
            "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "MY_COUNTRIES_I: COUNTRY1, COUNTRY2~|\n" +
            "</pre>\n";

    public static final String NAME = "SubscriptNameCheck";

    protected static final VensimLogger logger = VensimLogger.getInstance();
    public static final String DEFAULT_REGEXP = "([A-Z0-9]+_)+I";
    @RuleProperty(
            key = "subscript-name-regexp",
            defaultValue = DEFAULT_REGEXP,
            description = "The regexp definition of a subscript name.")
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
        SymbolTable table = context.getParsedSymbolTable();

        for (Symbol symbol : table.getSymbols()) {

            if (symbol.getType() == SymbolType.SUBSCRIPT) {
                Subscript subscript = (Subscript) symbol;
                if (!subscript.isCopy() && !checkSubscriptNameFollowsConvention(subscript.getToken())) {
                    symbol.setAsInvalid(this.getClass().getSimpleName());

                    for (int line : symbol.getLines()) {
                        Issue issue = new Issue(this, line, "The subscript '" + symbol.getToken() + "' doesn't follow the naming convention. Regular expression: "+ getRegexp());
                        addIssue(context, issue, symbol.isFiltered());

                    }
                }
            }

        }
    }


    private boolean checkSubscriptNameFollowsConvention(String name) {
        return name.matches(getRegexp());


    }

}
