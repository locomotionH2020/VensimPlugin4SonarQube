package es.uva.medeas.rules;


import es.uva.medeas.Issue;
import es.uva.medeas.VensimVisitorContext;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import org.sonar.check.Rule;

@Rule(key = LookupNameCheck.CHECK_KEY,name=LookupNameCheck.CHECK_KEY,description = LookupNameCheck.HTML_DESCRIPTION)
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
        SymbolTable table = context.getSymbolTable();

        for(Symbol symbol:table.getSymbols()){
            if(symbol.getType()== SymbolType.LOOKUP && !checkLookupFollowsConvention(symbol.getToken())){
                Issue issue = new Issue(this,symbol.getDefinitionLine(),"The name of the lookup doesn't follow the naming conventions");
                context.addIssue(issue);
            }

        }
    }

    private boolean checkLookupFollowsConvention(String name) {
        return name.matches("([a-z0-9]+_)*[a-z0-9]+_lt");
    }
}
