package es.uva.medeas.rules;

import es.uva.medeas.plugin.Issue;
import es.uva.medeas.plugin.VensimVisitorContext;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import org.sonar.check.Rule;

@Rule(key = RealityCheckNameRule.CHECK_KEY,name= RealityCheckNameRule.NAME, description= RealityCheckNameRule.HTML_DESCRIPTION)
public class RealityCheckNameRule implements VensimCheck {

    public static final String CHECK_KEY = "reality-check-name-convention";
    public static final String NAME = "RealityCheckNameRule" ;
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that reality checks follow the name convention and match the regular expression \"([a-z0-9]+_)*[a-z0-9]+_test\"</p>\n" +
            "<ul>" +
            "   <li>The name must be in upper case.</li>\n" +
            "   <li>The name must have the suffix _test</li>\n"+
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
            "productivity_is_positive_test\n"+
            "temperature_not_negative_test\n"+
            "</pre>\n";

    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable table = context.getParsedSymbolTable();


        for(Symbol symbol:table.getSymbols()){
            if(symbol.getType()== SymbolType.Reality_Check && !checkRealityCheckFollowsConvention(symbol.getToken())){
                for(int line: symbol.getDefinitionLines()) {
                    Issue issue = new Issue(this,line,"The name of the reality check doesn't follow the naming convention.");
                    context.addIssue(issue);
                }

            }
        }
    }


    private boolean checkRealityCheckFollowsConvention(String name) {
        return name.matches("([a-z0-9]+_)*[a-z0-9]+_test");
    }





}
