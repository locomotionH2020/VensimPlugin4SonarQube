package es.uva.medeas.rules;

import es.uva.medeas.plugin.Issue;
import es.uva.medeas.plugin.VensimVisitorContext;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import es.uva.medeas.utilities.Constants;
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
            "</ul>" +
            "Symbols defined by default in Vensim are excluded from this rule (TIME STEP, INITIAL TIME, FINAL TIME, SAVEPER)"+
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
        SymbolTable table = context.getParsedSymbolTable();


        for(Symbol symbol:table.getSymbols()){
            if(mustGenerateIssue(symbol)){
                for(int line: symbol.getDefinitionLines()) {
                    Issue issue = new Issue(this,line,"The name of the constant doesn't follow the naming convention.");
                    context.addIssue(issue);
                }

            }
        }
    }

    private boolean mustGenerateIssue(Symbol symbol){
        return  symbol.getType() == SymbolType.CONSTANT
                && !symbol.getDefinitionLines().isEmpty()
                && !isDefinedByDefaultInVensim(symbol)
                && !checkConstantFollowsConvention(symbol.getToken());
    }


    private boolean isDefinedByDefaultInVensim(Symbol symbol){
         return Constants.DEFAULT_VENSIM_SYMBOLS.contains(symbol.getToken().trim());
    }

    private boolean checkConstantFollowsConvention(String name) {
        return name.matches("([A-Z0-9]+_)*[A-Z0-9]+");
    }
}
