package es.uva.locomotion.rules;


import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Rule(key = LookupNameCheck.CHECK_KEY,name=LookupNameCheck.NAME,description = LookupNameCheck.HTML_DESCRIPTION)
public class LookupNameCheck extends AbstractVensimCheck{
    public static final String CHECK_KEY = "lookup-name-convention" ;

    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that lookups follow the naming convention and match the regular expression: \"([a-z0-9]+_)*[a-z0-9]+_lt\"</p>\n" +
            "<ul>" +
            "   <li>Name must be in lower case (lookups are functions).</li>\n" +
            "   <li>Each word must be separated by ONE underscore.</li>\n" +
            "   <li>The name must have the suffix _lt.</li>"+
            "   <li>The name shouldn't start with a number</li>"+
            "   <li>The name shouldn't contain non-english characters. </li>"+
            "</ul>"+
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>\n" +
            "historic_demand(  GET XLS LOOKUPS('inputs.xlsx', 'ssData' , 'a', 'b3' )) ~~|\n" +
            "HISTORICAL_EXTRACTION_LT(  GET XLS LOOKUPS('inputs.xlsx', 'ssData' , 'a', 'b3' )) ~~|\n" +
            "coal demand( GET XLS LOOKUPS('inputs.xlsx', 'ssData' , 'a', 'b3' )) ~~|\n" +
            "electricity__demand_lt(  GET XLS LOOKUPS('inputs.xlsx', 'ssData' , 'a', 'b3' )) ~~|\n" +
            "</pre>\n" +
            "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "historic_demand_lt( GET XLS LOOKUPS('inputs.xlsx', 'ssData' , 'a', 'b3' ))~~|" +
            "historic_oil_extraction_lt([(0,0)-(10,10)],(0,0),(1,1),(2,0.5),(3,1),(4,0)"+
            "</pre>\n";


    public static final String NAME = "LookupNameCheck" ;

    protected static final VensimLogger LOG = VensimLogger.getInstance();
    public static final String DEFAULT_REGEXP = "([a-z0-9]+_)*[a-z0-9]+_lt";
    @RuleProperty(
            key = "lookup-name-regexp",
            defaultValue = DEFAULT_REGEXP,
            description = "The regexp definition of a lookup name.")
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

        for(Symbol symbol:table.getSymbols()){
            if(symbol.getType()== SymbolType.Lookup_Table && !checkLookupFollowsConvention(symbol.getToken())){
                symbol.setAsInvalid(this.getClass().getSimpleName());

                for(int line: symbol.getLines()) {
                    Issue issue = new Issue(this, line, "The lookup '" + symbol.getToken() + "' doesn't follow the naming convention");
                    addIssue(context,issue,symbol.isFiltered());

                }

            }

        }
    }

    private boolean checkLookupFollowsConvention(String name) {
        return name.matches(regexp);
    }
}
