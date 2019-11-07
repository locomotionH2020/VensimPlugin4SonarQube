package es.uva.medeas.rules;


import es.uva.medeas.Issue;
import es.uva.medeas.VensimVisitorContext;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import org.sonar.check.Rule;



@Rule(key = SubscriptNameCheck.CHECK_KEY, name = SubscriptNameCheck.NAME, description = SubscriptNameCheck.HTML_DESCRIPTION)
public class SubscriptNameCheck implements VensimCheck {
    public static final String CHECK_KEY = "subscript-convention" ;
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that all subscript names match the regular expression: \"([A-Z0-9]+_)*[0-9]*[A-Z]+S_ENUM]\"</p>\n" +
            "<ul>" +
            "   <li>Name must be in upper case (subscripts are constants).</li>\n" +
            "   <li>Each word must be separated by ONE underscore.</li>\n" +
            "   <li>The last word must be plural.</li>" +
            "   <li>The name must have the suffix _ENUM.</li>"+
            "   <li>The name shouldn't start with a number</li>"+
            "   <li>The name shouldn't contain non-english characters. </li>"+
            "</ul>"+
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>\n" +
                "MY_COUNTRIES: COUNTRY1, COUNTRY2  ~~|\n" +
                "MY_COUNTRY_ENUM: COUNTRY1, COUNTRY2 ~~|\n" +
                "my_countries_enum: COUNTRY1, COUNTRY2 ~~|\n" +
            "</pre>\n" +
            "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "MY_COUNTRIES_ENUM: COUNTRY1, COUNTRY2~|\n"+
            "</pre>\n";

    public static final String NAME = "SubscriptNameCheck";


    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable table = context.getSymbolTable();

        for(Symbol symbol:table.getSymbols()){

            if(symbol.getType()== SymbolType.SUBSCRIPT_NAME && !checkSubscriptNameFollowsConvention(symbol.getToken())){
                Issue issue = new Issue(this,symbol.getDefinitionLine(),"The name of the subscript doesn't follow the naming conventions");
                context.addIssue(issue);
            }

        }
    }



    private boolean checkSubscriptNameFollowsConvention(String name){
        return name.matches("([A-Z0-9]+_)*[0-9]*[A-Z]+S_ENUM");


    }

}
