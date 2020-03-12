package es.uva.medeas.rules;


import es.uva.medeas.plugin.Issue;
import es.uva.medeas.plugin.VensimVisitorContext;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import org.sonar.check.Rule;



@Rule(key = SubscriptNameCheck.CHECK_KEY, name = SubscriptNameCheck.NAME, description = SubscriptNameCheck.HTML_DESCRIPTION)
public class SubscriptNameCheck implements VensimCheck {
    public static final String CHECK_KEY = "subscript-convention" ;
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that all subscript names match the regular expression: \"([A-Z0-9]+_)+I\"</p>\n" +
            "<ul>" +
            "   <li>Name must be in upper case (subscripts are constants).</li>\n" +
            "   <li>Each word must be separated by ONE underscore.</li>\n" +
            "   <li>The name must have the suffix '_I'.</li>"+
            "   <li>The name shouldn't start with a number</li>"+
            "   <li>The name shouldn't contain non-english characters. </li>"+
            "</ul>"+
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>\n" +
                "MY_COUNTRIES: COUNTRY1, COUNTRY2  ~~|\n" +
                "my_countries_i: COUNTRY1, COUNTRY2 ~~|\n" +
            "</pre>\n" +
            "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "MY_COUNTRIES_I: COUNTRY1, COUNTRY2~|\n"+
            "</pre>\n";

    public static final String NAME = "SubscriptNameCheck";


    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable table = context.getParsedSymbolTable();

        for(Symbol symbol:table.getSymbols()){

            if(symbol.getType()== SymbolType.Subscript && !checkSubscriptNameFollowsConvention(symbol.getToken())){
                for(int line: symbol.getDefinitionLines()) {
                    Issue issue = new Issue(this,line,"The name of the subscript doesn't follow the naming conventions");
                    context.addIssue(issue);
                }
            }

        }
    }



    private boolean checkSubscriptNameFollowsConvention(String name){
        return name.matches("([A-Z0-9]+_)+I");


    }

}
