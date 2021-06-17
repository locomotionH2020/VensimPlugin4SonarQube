package es.uva.locomotion.rules;


import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import es.uva.locomotion.model.VensimVisitorContext;
import es.uva.locomotion.plugin.Issue;
import org.sonar.check.Rule;

import java.util.Arrays;
import java.util.List;

@Rule(key= SymbolWithoutUnitsCheck.CHECK_KEY, name = SymbolWithoutUnitsCheck.NAME, description = SymbolWithoutUnitsCheck.HTML_DESCRIPTION)
public class SymbolWithoutUnitsCheck extends VensimCheck{

    public static final String CHECK_KEY = "symbol-without-units" ;
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that all the symbols have units. The only exceptions are macros/functions and subscript values. " +
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>\n" +
            "materials to extract for EV batteries Mt =\n" +
            "materials required for EV batteries *(1-recycling rates minerals alt tech) ~ ~ Annual materials to be mined\n for the construction of EV batteries  |"+
            "</pre>\n" +
            "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "materials to extract for EV batteries Mt =\n" +
            "materials required for EV batteries *(1-recycling rates minerals alt tech) "+
            "~ Mt/Year \n~ Annual materials to be mined for the construction of EV batteries |"+
            "</pre>\n";

    public static final String NAME = "SymbolWithoutUnits" ;

    private static final List<SymbolType> IGNORED_TYPES = Arrays.asList(SymbolType.FUNCTION,SymbolType.SUBSCRIPT_VALUE,SymbolType.SUBSCRIPT);

    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable table = context.getParsedSymbolTable();

        for(Symbol symbol: table.getSymbols()){
            if(!IGNORED_TYPES.contains(symbol.getType()) && symbol.getUnits().isBlank()){
                symbol.setAsInvalid(this.getClass().getSimpleName());

                for(int line: symbol.getLines()) {
                    Issue issue = new Issue(this,line,"The symbol '"+ symbol.getToken() + "' should have units.");
                    addIssue(context,issue,symbol.isFiltered());
                }
            }
        }
    }
}
