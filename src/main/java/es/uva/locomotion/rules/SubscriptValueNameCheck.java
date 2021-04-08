package es.uva.locomotion.rules;


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


@Rule(key = SubscriptValueNameCheck.CHECK_KEY, name=SubscriptValueNameCheck.NAME,description = SubscriptValueNameCheck.HTML_DESCRIPTION)
public class SubscriptValueNameCheck extends AbstractVensimCheck {
    public static final String CHECK_KEY = "subscript-value-name-convention" ;
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that all the subscript values follow the name convention and match the regular expression \"([A-Z0-9]+_)*[A-Z0-9]+\"</p>\n" +
            "<ul>" +
            "   <li>The name must be in upper case (subscript values are constants).</li>\n" +
            "   <li>Each word must be separated by ONE underscore.</li>\n" +
            "   <li>The name shouldn't contain non-english characters.</li>\n"+
            "   <li>The name shouldn't start with a number</li>"+
            "</ul>"+
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>\n" +
            "MY_COUNTRIES_I: first_country ~~|\n" +
            "MY_COUNTRIES_I: FIRST__COUNTRY ~~|\n" +
            "</pre>\n" +
            "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "MY_COUNTRIES_I: FIRST_COUNTRY  ~~|\n"+
            "</pre>\n";
    public static final String NAME = "SubscriptValueNameCheck" ;

    protected static final VensimLogger logger = VensimLogger.getInstance();
    public static final String DEFAULT_REGEXP = "([A-Z0-9]+_)*[A-Z0-9]+";
    @RuleProperty(
            key = "subscript-value-name-regexp",
            defaultValue = DEFAULT_REGEXP,
            description = "The regexp definition of a subscript value name.")
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

        for(Symbol symbol:table.getSymbols()){
            if(symbol.getType()== SymbolType.SUBSCRIPT_VALUE && !checkSubscriptValueFollowsConvention(symbol.getToken())){
                symbol.setAsInvalid(this.getClass().getSimpleName());

                for(int line: symbol.getLines()) {
                    Issue issue = new Issue(this, line,"The subscript value '" + symbol.getToken() + "' doesn't follow the naming convention");
                    addIssue(context,issue,symbol.isFiltered());
                }
            }
        }
    }

    private boolean checkSubscriptValueFollowsConvention(String name) {
        return name.matches(getRegexp());
    }


}
