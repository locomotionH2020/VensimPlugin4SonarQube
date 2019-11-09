package es.uva.medeas.rules;

import es.uva.medeas.Issue;
import es.uva.medeas.VensimVisitorContext;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import org.sonar.check.Rule;

@Rule(key = ConstantNameCheck.CHECK_KEY,name=ConstantNameCheck.NAME, description= ConstantNameCheck.HTML_DESCRIPTION)
public class ConstantNameCheck implements VensimCheck {
    public static final String CHECK_KEY = "constant-name-convention";
    public static final String NAME = "ConstantNameCheck" ;
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that constants follow the name convention and match the regular expression \"([A-Z0-9]+_)*[A-Z0-9]+\"</p>\n" +
            "<ul>" +
            "   <li>The name must be in upper case.</li>\n" +
            "   <li>Each word must be separated by ONE underscore.</li>\n" +
            "   <li>The name shouldn't contain non-english characters.</li>\n"+
            "   <li>The name shouldn't start with a number</li>"+
            "</ul>"+
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>\n" +
            "productivity\n" +
            "STARTING__PRODUCT\n"+
            "</pre>\n" +
            "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "PRODUCTIVITY\n"+
            "STARTING_PRODUCT\n"+
            "</pre>\n";

    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable table = context.getSymbolTable();


        for(Symbol symbol:table.getSymbols()){
            if(symbol.getType()== SymbolType.CONSTANT && !symbol.getDefinitionLines().isEmpty() && !checkConstantFollowsConvention(symbol.getToken())){
                for(int line: symbol.getDefinitionLines()) {
                    Issue issue = new Issue(this,line,"The name of the constant doesn't follow the naming convention.");
                    context.addIssue(issue);
                }

            }
        }
    }

    private boolean checkConstantFollowsConvention(String name) {
        return name.matches("([A-Z0-9]+_)*[A-Z0-9]+");
    }
}
