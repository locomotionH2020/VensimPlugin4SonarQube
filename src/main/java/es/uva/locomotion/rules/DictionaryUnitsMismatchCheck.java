package es.uva.locomotion.rules;

import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.SymbolTable;
import es.uva.locomotion.parser.SymbolType;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.utilities.Constants;
import org.sonar.check.Rule;

import java.util.Arrays;
import java.util.List;

@Rule(key = DictionaryUnitsMismatchCheck.CHECK_KEY, name = DictionaryUnitsMismatchCheck.NAME, description = DictionaryUnitsMismatchCheck.HTML_DESCRIPTION)
public class DictionaryUnitsMismatchCheck extends AbstractVensimCheck{
    public static final String CHECK_KEY = "symbol-units-mismatch-db" ;
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that all the symbols in the file have the same units as the symbols stored in the database. " +
            "If the symbol found in the file doesn't have units the rule is ignored.<br> " +
            "The symbols predefined by Vensim (FINAL TIME, TIME STEP, etc), subscript values and functions are ignored (except lookups)</p>";
    public static final String NAME = "DictionaryUnitsMismatch" ;
    private final List<SymbolType> IGNORED_TYPES = Arrays.asList(SymbolType.Function,SymbolType.Subscript_Value);

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
                foundSymbol.setAsInvalid();

                String expectedUnits = dbTable.getSymbol(foundSymbol.getToken()).getUnits().trim();
                for(int line: foundSymbol.getDefinitionLines()) {
                    Issue issue = new Issue(this, line,"The symbol '"+ foundSymbol.getToken() + "' has '"+foundSymbol.getUnits().trim() + "' as units but the dictionary has '"+ expectedUnits + "'." );
                    addIssue(context,issue,foundSymbol);

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
        return IGNORED_TYPES.contains(symbol.getType()) || Constants.DEFAULT_VENSIM_SYMBOLS.contains(symbol.getToken().trim());
    }
}
