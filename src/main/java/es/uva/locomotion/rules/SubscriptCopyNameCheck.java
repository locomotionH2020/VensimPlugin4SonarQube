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


@Rule(key = SubscriptCopyNameCheck.CHECK_KEY, name = SubscriptCopyNameCheck.NAME, description = SubscriptCopyNameCheck.HTML_DESCRIPTION)
public class SubscriptCopyNameCheck extends AbstractVensimCheck {
    public static final String CHECK_KEY = "subscript-copy-convention";
    public static final String HTML_DESCRIPTION = "" +
            "<p>TODO</p>\n";

    public static final String NAME = "SubscriptCopyNameCheck";

    protected static final VensimLogger logger = VensimLogger.getInstance();
    public static final String DEFAULT_REGEXP = "([A-Z0-9]+_)+MAP_I";
    @RuleProperty(
            key = "subscript-name-regexp",
            defaultValue = DEFAULT_REGEXP,
            description = "The regexp definition of a subscript name.")
    public static final String REGEXP = DEFAULT_REGEXP;

    private String getRegexp() {
        try {
            Pattern.compile(REGEXP);
            return REGEXP;
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
                if (subscript.isCopy() && !checkSubscriptNameFollowsConvention(subscript.getToken())) {
                    subscript.setAsInvalid(this.getClass().getSimpleName());

                    for (int line : subscript.getLines()) {
                        Issue issue = new Issue(this, line, "The subscript copy '" + subscript.getToken() + "' doesn't follow the naming convention");
                        addIssue(context, issue, subscript.isFiltered());

                    }
                }
            }

        }
    }


    private boolean checkSubscriptNameFollowsConvention(String name) {
        return name.matches(getRegexp());


    }

}
