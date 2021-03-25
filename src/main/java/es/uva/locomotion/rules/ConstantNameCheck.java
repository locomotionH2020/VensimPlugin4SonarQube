package es.uva.locomotion.rules;

import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import es.uva.locomotion.utilities.Constants;
import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Rule(key = ConstantNameCheck.CHECK_KEY, name = ConstantNameCheck.NAME, description = ConstantNameCheck.HTML_DESCRIPTION)
public class ConstantNameCheck extends AbstractVensimCheck {
    public static final String CHECK_KEY = "constant-name-convention";
    public static final String NAME = "ConstantNameCheck";
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that constants follow the name convention and match the regular expression \"([A-Z0-9]+_)*[A-Z0-9]+\"</p>\n" +
            "<ul>" +
            "   <li>The name must be in upper case.</li>\n" +
            "   <li>Each word must be separated by ONE underscore.</li>\n" +
            "   <li>The name shouldn't contain non-english characters.</li>\n" +
            "   <li>The name shouldn't start with a number</li>" +
            "</ul>" +
            "Symbols defined by default in Vensim are excluded from this rule (TIME STEP, INITIAL TIME, FINAL TIME, SAVEPER)" +
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>\n" +
            "productivity\n" +
            "STARTING__PRODUCT\n" +
            "</pre>\n" +
            "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "PRODUCTIVITY\n" +
            "STARTING_PRODUCT\n" +
            "</pre>\n";

    protected static final VensimLogger LOG = VensimLogger.getInstance();
    public static final String DEFAULT_REGEXP = "([A-Z0-9]+_)*[A-Z0-9]+";
    @RuleProperty(
            key = "constant-name-regexp",
            defaultValue = DEFAULT_REGEXP,
            description = "The regexp definition of a constant name.")
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
        SymbolTable table = context.getParsedSymbolTable();


        for (Symbol symbol : table.getSymbols()) {

            if (mustGenerateIssue(symbol)) {

                symbol.setAsInvalid(this.getClass().getSimpleName());

                for (int line : symbol.getLines()) {
                    Issue issue = new Issue(this, line, "The constant '" + symbol.getToken() + "' doesn't follow the naming convention.");
                    addIssue(context, issue, symbol.isFiltered());

                }

            }
        }
    }

    private boolean mustGenerateIssue(Symbol symbol) {
        return symbol.getType() == SymbolType.Constant
                && !symbol.getLines().isEmpty()
                && !isDefinedByDefaultInVensim(symbol)
                && !checkConstantFollowsConvention(symbol.getToken());
    }


    private boolean isDefinedByDefaultInVensim(Symbol symbol) {
        return Constants.DEFAULT_VENSIM_SYMBOLS.contains(symbol.getToken().trim());
    }

    private boolean checkConstantFollowsConvention(String name) {
        return name.matches(getRegexp());
    }
}
