package es.uva.medeas.rules;


import es.uva.medeas.plugin.Issue;
import es.uva.medeas.plugin.VensimVisitorContext;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import org.sonar.check.Rule;


@Rule(key = VariableNameCheck.CHECK_KEY,name=VariableNameCheck.NAME, description= VariableNameCheck.HTML_DESCRIPTION)
public class VariableNameCheck implements VensimCheck{
    public static final String CHECK_KEY = "variable-name-convention";
    public static final String NAME = "VariableNameCheck" ;
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that variables follow the name convention and match the regular expression \"([a-z0-9]+_)*[a-z0-9]+\"</p>\n" +
            "<ul>" +
            "   <li>The name must be in lower case.</li>\n" +
            "   <li>Each word must be separated by ONE underscore.</li>\n" +
            "   <li>The name shouldn't contain non-english characters.</li>\n"+
            "   <li>The name shouldn't start with a number</li>"+
            "</ul>"+
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>\n" +
            "ENERGY_REQUIRED\n" +
            "2019fuel_emissions\n"+
            "</pre>\n" +
            "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "energy_required\n"+
            "fuel_emissions_2019\n"+
            "</pre>\n";


    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable table = context.getParsedSymbolTable();

        for(Symbol symbol:table.getSymbols()){
            if(symbol.getType()== SymbolType.Variable && !"Time".equals(symbol.getToken()) && !checkVariableFollowsConvention(symbol.getToken())){
                symbol.setAsInvalid();

                for(int line: symbol.getDefinitionLines()) {
                    Issue issue = new Issue(this,line,"The name of the variable doesn't follow the naming convention.");
                    context.addIssue(issue);
                }
            }
        }
    }

    private boolean checkVariableFollowsConvention(String name) {
        return name.matches("([a-z0-9]+_)*[a-z0-9]+");
    }


}
