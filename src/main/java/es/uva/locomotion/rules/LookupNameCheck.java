package es.uva.locomotion.rules;


import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.SymbolTable;
import es.uva.locomotion.parser.SymbolType;
import org.sonar.check.Rule;

@Rule(key = LookupNameCheck.CHECK_KEY,name=LookupNameCheck.NAME,description = LookupNameCheck.HTML_DESCRIPTION)
public class LookupNameCheck implements VensimCheck{
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


    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable table = context.getParsedSymbolTable();

        for(Symbol symbol:table.getSymbols()){
            if(symbol.getType()== SymbolType.Lookup_Table && !checkLookupFollowsConvention(symbol.getToken())){
                symbol.setAsInvalid();

                for(int line: symbol.getDefinitionLines()) {
                    Issue issue = new Issue(this, line, "The lookup '" + symbol.getToken() + "' doesn't follow the naming conventions");
                    context.addIssue(issue);
                }

            }

        }
    }

    private boolean checkLookupFollowsConvention(String name) {
        return name.matches("([a-z0-9]+_)*[a-z0-9]+_lt");
    }
}
