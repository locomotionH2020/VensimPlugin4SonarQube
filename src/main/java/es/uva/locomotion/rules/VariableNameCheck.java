package es.uva.locomotion.rules;


import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.SymbolTable;
import es.uva.locomotion.parser.SymbolType;
import org.sonar.check.Rule;

import java.util.ArrayList;
import java.util.List;


@Rule(key = VariableNameCheck.CHECK_KEY, name = VariableNameCheck.NAME, description = VariableNameCheck.HTML_DESCRIPTION)
public class VariableNameCheck extends AbstractVensimCheck {
    public static final String CHECK_KEY = "variable-name-convention";
    public static final String NAME = "VariableNameCheck";
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that variables follow the name convention and match the regular expression \"([a-z0-9]+_)*[a-z0-9]+\"</p>\n" +
            "<ul>" +
            "   <li>The name must be in lower case.</li>\n" +
            "   <li>Each word must be separated by ONE underscore.</li>\n" +
            "   <li>The name shouldn't contain non-english characters.</li>\n" +
            "   <li>The name shouldn't start with a number</li>" +
            "</ul>" +
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>\n" +
            "ENERGY_REQUIRED\n" +
            "2019fuel_emissions\n" +
            "</pre>\n" +
            "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "energy_required\n" +
            "fuel_emissions_2019\n" +
            "</pre>\n";


    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable table = context.getParsedSymbolTable();
        SymbolTable dbTable = context.getDbSymbolTable();
        for (Symbol symbol : table.getSymbols()) {
            if (symbol.getType() == SymbolType.Variable && !"Time".equals(symbol.getToken()) && !checkVariableFollowsConvention(symbol.getToken())) {

                boolean isOnlyAnAcronym = false;
                if (dbTable != null) {
                    isOnlyAnAcronym = checkIfVariableHaveAnAcronym(symbol.getToken(), new ArrayList<>()); //TODO see real way of exporting acronyms
                }

                if (!isOnlyAnAcronym) {
                    symbol.setAsInvalid();

                    for (int line : symbol.getDefinitionLines()) {
                        Issue issue = new Issue(this, line, "The variable '" + symbol.getToken() + "' doesn't follow the naming convention.");
                        addIssue(context, issue, symbol);
                    }
                }
            }
        }
    }

    private boolean checkVariableFollowsConvention(String name) {
        return name.matches("([a-z0-9]+_)*[a-z0-9]+");
    }

    private  boolean checkIfVariableHaveAnAcronym(String name, List<String> acronyms){
        String trimmed_name = name;
        for(String acr : acronyms){
            if(trimmed_name.contains(acr))
                trimmed_name = trimmed_name.replace(acr, "");
        }

        return  checkVariableFollowsConvention(trimmed_name);
    }
}
