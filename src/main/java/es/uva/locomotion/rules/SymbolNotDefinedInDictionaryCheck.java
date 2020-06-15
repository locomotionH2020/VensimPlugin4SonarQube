package es.uva.locomotion.rules;

import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.SymbolTable;
import es.uva.locomotion.parser.SymbolType;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.utilities.Constants;
import org.sonar.check.Rule;



@Rule(key = SymbolNotDefinedInDictionaryCheck.CHECK_KEY, name= SymbolNotDefinedInDictionaryCheck.NAME,description = SymbolNotDefinedInDictionaryCheck.HTML_DESCRIPTION)
public class SymbolNotDefinedInDictionaryCheck implements VensimCheck {
    public static final String CHECK_KEY = "symbol-not-found-db" ;
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that all the symbols in the file are defined in the dictionary. " +
            "The symbols predefined by Vensim (FINAL TIME, TIME STEP, etc) and functions are ignored (except lookups)</p>";
    public static final String NAME = "SymbolNotFoundInDB" ;


    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable parsedTable = context.getParsedSymbolTable();
        SymbolTable dbTable = context.getDbSymbolTable();

        if(dbTable!=null)
            checkParsedSymbols(context, parsedTable, dbTable);
    }


    private void checkParsedSymbols(VensimVisitorContext context, SymbolTable parsedTable, SymbolTable dbTable) {
        for(Symbol foundSymbol: parsedTable.getSymbols()){
            if(!symbolIsIgnored(foundSymbol) && !dbTable.hasSymbol(foundSymbol.getToken())){

                for(int line: foundSymbol.getDefinitionLines()) {
                    Issue issue = new Issue(this, line,"This symbol isn't defined in the database.");
                    context.addIssue(issue);
                }
            }
        }
    }


    private boolean symbolIsIgnored(Symbol symbol){

        return symbol.getType()== SymbolType.Function || Constants.DEFAULT_VENSIM_SYMBOLS.contains(symbol.getToken().trim());

    }
}
