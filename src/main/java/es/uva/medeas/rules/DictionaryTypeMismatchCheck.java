package es.uva.medeas.rules;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import es.uva.medeas.plugin.Issue;
import es.uva.medeas.plugin.VensimVisitorContext;
import es.uva.medeas.utilities.Constants;
import org.sonar.check.Rule;

@Rule(key = DictionaryTypeMismatchCheck.CHECK_KEY, name = DictionaryTypeMismatchCheck.NAME, description = DictionaryTypeMismatchCheck.HTML_DESCRIPTION)
public class DictionaryTypeMismatchCheck implements VensimCheck {

    public static final String CHECK_KEY = "symbol-type-mismatch-db" ;
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that all the symbols in the file have the same type as the symbols stored in the database " +
            "The symbols predefined by Vensim (FINAL TIME, TIME STEP, etc) and functions are ignored (except lookups)</p>";
    public static final String NAME = "DictionaryTypeMismatch" ;


    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable parsedTable = context.getParsedSymbolTable();
        SymbolTable dbTable = context.getDbSymbolTable();

        if(dbTable!=null)
            checkSymbolsType(context, parsedTable, dbTable);
    }

    private void checkSymbolsType(VensimVisitorContext context, SymbolTable parsedTable, SymbolTable dbTable) {
        for(Symbol foundSymbol: parsedTable.getSymbols()){
            if(raisesIssue(foundSymbol,dbTable)){
                foundSymbol.setAsInvalid();

                SymbolType expectedType = dbTable.getSymbol(foundSymbol.getToken()).getType();
                for(int line: foundSymbol.getDefinitionLines()) {
                    Issue issue = new Issue(this, line,"The symbol '"+ foundSymbol.getToken() + "' has type '"+foundSymbol.getType() + "' but the dictionary has '"+ expectedType + "'." );
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

        Symbol dbSymbol = dbTable.getSymbol(foundSymbol.getToken());
        return dbSymbol.getType()!=foundSymbol.getType();


    }

    private boolean symbolIsIgnored(Symbol symbol){
        return symbol.getType()== SymbolType.Function || Constants.DEFAULT_VENSIM_SYMBOLS.contains(symbol.getToken().trim());
    }
}
