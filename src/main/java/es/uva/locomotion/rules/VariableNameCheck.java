package es.uva.locomotion.rules;


import es.uva.locomotion.model.AcronymsList;
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


@Rule(key = VariableNameCheck.CHECK_KEY, name = VariableNameCheck.NAME, description = VariableNameCheck.HTML_DESCRIPTION)
public class VariableNameCheck extends AbstractVensimCheck {
    protected static final VensimLogger logger = VensimLogger.getInstance();

    public static final String CHECK_KEY = "variable-name-convention";
    public static final String NAME = "VariableNameCheck";
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
            logger.unique("The rule " + NAME + " has an invalid configuration: The selected regexp is invalid. Error: " + exception.getDescription(),
                    LoggingLevel.ERROR);
            return DEFAULT_REGEXP;
        }
    }

    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable table = context.getParsedSymbolTable();
        AcronymsList acronymsList = context.getDbAcronyms();
        for (Symbol symbol : table.getSymbols()) {
            if (symbol.getType() == SymbolType.VARIABLE && !"Time".equals(symbol.getToken()) && !checkVariableFollowsConvention(symbol.getToken())) {

                boolean isOnlyAnAcronym = false;
                String aronymsListMisingWarning = "";
                if (acronymsList != null) {
                    isOnlyAnAcronym = checkIfVariableHaveAnAcronym(symbol.getToken(), acronymsList.getAcronyms());
                }else{
                    aronymsListMisingWarning = "WARNING: Could not connect with the acronyms database. This variable could be well written.";
                }

                if (!isOnlyAnAcronym) {
                    symbol.setAsInvalid(this.getClass().getSimpleName());

                    for (int line : symbol.getLines()) {
                        Issue issue = new Issue(this, line, "The variable '" + symbol.getToken() + "' doesn't follow the naming convention." + aronymsListMisingWarning);
                        addIssue(context, issue, symbol.isFiltered());
                    }
                }
            }
        }
    }

    private boolean checkVariableFollowsConvention(String name) {
        return name.matches(getRegexp());
    }

    private  boolean checkIfVariableHaveAnAcronym(String name, List<String> acronyms){
        String trimmedName = name;
        for(String acr : acronyms){
            if(trimmedName.matches(".*(^|_|\")"+acr+"($|_|\").*"))
                trimmedName = trimmedName.replace(acr, acr.toLowerCase());
        }
        return  checkVariableFollowsConvention(trimmedName);
    }
}
