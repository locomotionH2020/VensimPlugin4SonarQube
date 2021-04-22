package es.uva.locomotion.rules;


import es.uva.locomotion.model.AcronymsList;
import es.uva.locomotion.model.symbol.DelayedType;
import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;


@Rule(key = DelayedNameCheck.CHECK_KEY, name = DelayedNameCheck.NAME, description = DelayedNameCheck.HTML_DESCRIPTION)
public class DelayedNameCheck extends AbstractVensimCheck {
    protected static final VensimLogger logger = VensimLogger.getInstance();

    public static final String CHECK_KEY = "delayed-variable-name-convention";
    public static final String NAME = "DelayedVariableNameCheck";
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that delayed variables follow the name convention.There are two types od delayed variables. \n" +
            "<ul>" +
            "   <li>TIME STEP delayed: have the symbol \"TIME STEP\" as delay time, their default regular expression is \"delayed_TS_([a-z0-9]+_)*[a-z0-9]+\"</li>\n" +
            "   <li>delayed: have anything but the symbol \"TIME STEP\" as delay time, their default regular expression is \"delayed_([a-z0-9]+_)*[a-z0-9]+\"</li>\n" +
            "</ul>" +
            "</p>\n" +
            "Any of the two regexp can be changed using custom quality profiles.\n" +
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>\n" +
            "ENERGY_REQUIRED\n" +
            "2019fuel_emissions\n" +
            "</pre>\n" +
            "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "delayed_energy_required\n" +
            "delayed_TS_fuel_emissions_2019\n" +
            "</pre>\n";

    public static final String DEFAULT_DELAYED_REGEXP = "delayed_([a-z0-9]+_)*[a-z0-9]+";
    @RuleProperty(
            key = "delayed-variable-name-regexp",
            defaultValue = DEFAULT_DELAYED_REGEXP,
            description = "The regexp definition of a delayed variable symbol name.")
    public final String delayed_regexp = DEFAULT_DELAYED_REGEXP;

    private String getDelayedRegexp() {
        try {
            Pattern.compile(delayed_regexp);
            return delayed_regexp;
        } catch (PatternSyntaxException exception) {
            logger.unique("The rule " + NAME + " has an invalid configuration: The selected regexp is invalid. Error: " + exception.getDescription(),
                    LoggingLevel.ERROR);
            return DEFAULT_DELAYED_REGEXP;
        }
    }

    public static final String DEFAULT_TIME_STEP_DELAYED_REGEXP = "delayed_TS_([a-z0-9]+_)*[a-z0-9]+";
    @RuleProperty(
            key = "time-step-delayed-variable-name-regexp",
            defaultValue = DEFAULT_TIME_STEP_DELAYED_REGEXP,
            description = "The regexp definition of a time step delayed variable symbol name.")
    public final String time_step_delayed_regexp = DEFAULT_TIME_STEP_DELAYED_REGEXP;

    private String getTimeStepDelayedRegexp() {
        try {
            Pattern.compile(time_step_delayed_regexp);
            return time_step_delayed_regexp;
        } catch (PatternSyntaxException exception) {
            logger.unique("The rule " + NAME + " has an invalid configuration: The selected regexp is invalid. Error: " + exception.getDescription(),
                    LoggingLevel.ERROR);
            return DEFAULT_TIME_STEP_DELAYED_REGEXP;
        }
    }

    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable table = context.getParsedSymbolTable();
        AcronymsList acronymsList = context.getDbAcronyms();
        List<Symbol> delayedSymbols = table.getSymbols().stream().filter((symbol) -> symbol.isDelayed() != DelayedType.NOT_DELAYED).collect(Collectors.toList());
        for (Symbol symbol : delayedSymbols) {
            if (symbol.getType() == SymbolType.VARIABLE && !"Time".equals(symbol.getToken())) {
                String token = symbol.getToken();
                boolean isOnlyAnAcronym = false;
                String aronymsListMisingWarning = "";
                if (acronymsList != null) {
                    token = removeArconyms(token, acronymsList.getAcronyms());
                } else {
                    aronymsListMisingWarning = "WARNING: Could not connect with the acronyms database. This variable could be well written.";
                }

                if (symbol.isDelayed() == DelayedType.DELAYED && !token.matches(getDelayedRegexp())) {
                    symbol.setAsInvalid(this.getClass().getSimpleName());

                    for (int line : symbol.getLines()) {
                        Issue issue = new Issue(this, line, "The delayed variable '" + symbol.getToken() + "' doesn't follow the naming convention. Regular expression: " + getDelayedRegexp() + ". " + aronymsListMisingWarning);
                        addIssue(context, issue, symbol.isFiltered());
                    }
                } else if (symbol.isDelayed() == DelayedType.TIME_STEP_DELAYED && !token.matches(getTimeStepDelayedRegexp())){
                    symbol.setAsInvalid(this.getClass().getSimpleName());

                    for (int line : symbol.getLines()) {
                        Issue issue = new Issue(this, line, "The time step delayed variable '" + symbol.getToken() + "' doesn't follow the naming convention. Regular expression: " + getTimeStepDelayedRegexp() + ". " + aronymsListMisingWarning);
                        addIssue(context, issue, symbol.isFiltered());
                    }

                }
            }
        }
    }


    private String removeArconyms(String name, List<String> acronyms) {
        String trimmedName = name;
        for (String acr : acronyms) {
            if (trimmedName.matches(".*(^|_|\")" + acr + "($|_|\").*"))
                trimmedName = trimmedName.replace(acr, acr.toLowerCase());
        }
        return trimmedName;
    }
}
