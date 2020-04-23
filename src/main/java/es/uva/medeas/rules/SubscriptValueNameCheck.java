package es.uva.medeas.rules;


import es.uva.medeas.plugin.Issue;
import es.uva.medeas.plugin.VensimVisitorContext;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import org.sonar.check.Rule;



@Rule(key = SubscriptValueNameCheck.CHECK_KEY, name=SubscriptValueNameCheck.NAME,description = SubscriptValueNameCheck.HTML_DESCRIPTION)
public class SubscriptValueNameCheck implements VensimCheck {
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



    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable table = context.getParsedSymbolTable();

        for(Symbol symbol:table.getSymbols()){
            if(symbol.getType()== SymbolType.Subscript_Value && !checkSubscriptValueFollowsConvention(symbol.getToken())){
                symbol.setAsInvalid();

                for(int line: symbol.getDefinitionLines()) {
                    Issue issue = new Issue(this, line,"The value of the subscript doesn't follow the naming conventions");
                    context.addIssue(issue);
                }
            }
        }
    }

    private boolean checkSubscriptValueFollowsConvention(String name) {
        return name.matches("([A-Z0-9]+_)*[A-Z0-9]+");
    }


}
