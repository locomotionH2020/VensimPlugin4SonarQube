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

@Rule(key = RealityCheckNameRule.CHECK_KEY,name= RealityCheckNameRule.NAME, description= RealityCheckNameRule.HTML_DESCRIPTION)
public class RealityCheckNameRule extends AbstractVensimCheck {

    public static final String CHECK_KEY = "reality-check-name-convention";
    public static final String NAME = "RealityCheckNameRule" ;
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that reality checks follow the name convention.The default regular expression is \"([a-z0-9]+_)*[a-z0-9]+_check\"</p>\n" +
            "but it can be changed using custom quality profiles. \n The rest of this descriptions assumes the default regular expression is being used. \n" +
            "<ul>" +
            "   <li>The name must be in upper case.</li>\n" +
            "   <li>The name must have the suffix _check</li>\n"+
            "   <li>Each word must be separated by ONE underscore.</li>\n" +
            "   <li>The name shouldn't contain non-english characters.</li>\n"+
            "   <li>The name shouldn't start with a number</li>"+
            "</ul>"+
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>\n" +
            "productivity_is_positive\n" +
            "PRODUCTIVITY_IS_POSITIVE"+
            "</pre>\n" +
            "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "productivity_is_positive_check\n"+
            "temperature_not_negative_check\n"+
            "</pre>\n";

    protected static final VensimLogger logger = VensimLogger.getInstance();
    public static final String DEFAULT_REGEXP = "([a-z0-9]+_)*[a-z0-9]+_check";
    @RuleProperty(
            key = "reality-check-name-regexp",
            defaultValue = DEFAULT_REGEXP,
            description = "The regexp definition of a reality check name.")
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


        for(Symbol symbol:table.getSymbols()){
            if(symbol.getType()== SymbolType.REALITY_CHECK && !checkRealityCheckFollowsConvention(symbol.getToken())){
                symbol.setAsInvalid(this.getClass().getSimpleName());

                for(int line: symbol.getLines()) {
                    Issue issue = new Issue(this,line,"The reality check '" + symbol.getToken() + "' doesn't follow the naming convention. Regular expression: " + getRegexp());
                    addIssue(context,issue,symbol.isFiltered());

                }

            }
        }
    }


    private boolean checkRealityCheckFollowsConvention(String name) {
        return name.matches(getRegexp());
    }





}
