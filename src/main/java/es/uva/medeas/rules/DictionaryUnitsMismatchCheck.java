package es.uva.medeas.rules;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import es.uva.medeas.plugin.Issue;
import es.uva.medeas.plugin.VensimVisitorContext;
import es.uva.medeas.utilities.Constants;
import org.sonar.check.Rule;

@Rule(key = DictionaryUnitsMismatchCheck.CHECK_KEY, name = DictionaryUnitsMismatchCheck.NAME, description = DictionaryUnitsMismatchCheck.HTML_DESCRIPTION)
public class DictionaryUnitsMismatchCheck implements VensimCheck {
    public static final String CHECK_KEY = "symbol-units-mismatch-db" ;
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that all the symbols in the file have the same units as the symbols stored in the database. " +
            "If the symbol found in the file doesn't have units the rule is ignored.<br> " +
            "The symbols predefined by Vensim (FINAL TIME, TIME STEP, etc) and functions are ignored (except lookups)</p>";
    public static final String NAME = "DictionaryUnitsMismatch" ;

    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable parsedTable = context.getParsedSymbolTable();
        SymbolTable dbTable = context.getDbSymbolTable();

        if(dbTable!=null)
            checkSymbolsUnits(context, parsedTable, dbTable);
    }

    private void checkSymbolsUnits(VensimVisitorContext context, SymbolTable parsedTable, SymbolTable dbTable) {
        for(Symbol foundSymbol: parsedTable.getSymbols()){
            if(raisesIssue(foundSymbol,dbTable)){
                String expectedUnits = dbTable.getSymbol(foundSymbol.getToken()).getUnits().trim();
                for(int line: foundSymbol.getDefinitionLines()) {
                    Issue issue = new Issue(this, line,"The symbol '"+ foundSymbol.getToken() + "' has '"+foundSymbol.getUnits().trim() + "' as units but the dictionary has '"+ expectedUnits + "'." );
                    context.addIssue(issue);
                }
            }
        }
    }

    private boolean raisesIssue(Symbol foundSymbol, SymbolTable dbTable) {
        if(symbolIsIgnored(foundSymbol))
            return false;

        if(!dbTable.hasSymbol(foundSymbol.getToken()))
            return false;

        String dbUnits = dbTable.getSymbol(foundSymbol.getToken()).getUnits();
        String fileUnits = foundSymbol.getUnits();
        return !fileUnits.isBlank()  && !fileUnits.trim().equals(dbUnits.trim());

    }

    private boolean symbolIsIgnored(Symbol symbol){
        return symbol.getType()== SymbolType.FUNCTION || Constants.DEFAULT_VENSIM_SYMBOLS.contains(symbol.getToken().trim());
    }
}
