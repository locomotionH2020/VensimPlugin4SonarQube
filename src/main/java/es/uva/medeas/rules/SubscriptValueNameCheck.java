package es.uva.medeas.rules;


import es.uva.medeas.Issue;
import es.uva.medeas.VensimVisitorContext;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import org.sonar.check.Rule;



@Rule(key = SubscriptValueNameCheck.CHECK_KEY)
public class SubscriptValueNameCheck implements VensimCheck {
    public static final String CHECK_KEY = "subscription-value-name-convention" ;
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that all the subscript values follow the name convention and match the regular expression \"([A-Z0-9]+_)*[A-Z0-9]+\"</p>\n" +
            "<ul>" +
            "   <li>The Name must be in upper case (subscript values are constants).</li>\n" +
            "   <li>Each word must be separated by ONE underscore.</li>\n" +
            "   <li>The name shouldn't contain any non-english character.</li>"+
            "</ul>"+
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>\n" +
            "MY_COUNTRIES_ENUM: first_country ~~|\n" +
            "MY_COUNTRIES_ENUM: FIRST__COUNTRY ~~|\n" +
            "</pre>\n" +
            "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "MY_COUNTRIES_ENUM: FIRST_COUNTRY  ~~|\n"+
            "</pre>\n";
    public static final String NAME = "SubscriptValueNameCheck" ;


    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable table = context.getSymbolTable();




        for(Symbol symbol:table.getSymbols()){
            if(symbol.getType()== SymbolType.SUBSCRIPT_VALUE && !checkSubscriptValueFollowsConvention(symbol.getToken())){
                Issue issue = new Issue(this,symbol.getDefinitionLine(),"The value of the subscript doesn't follow the naming conventions");

                context.addIssue(issue);
            }
        }
    }

    private boolean checkSubscriptValueFollowsConvention(String name) {
        return name.matches("([A-Z0-9]+_)*[A-Z0-9]+");
    }


}
