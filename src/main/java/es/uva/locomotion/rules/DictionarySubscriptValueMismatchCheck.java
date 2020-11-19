package es.uva.locomotion.rules;

import es.uva.locomotion.model.Symbol;
import es.uva.locomotion.model.SymbolTable;
import es.uva.locomotion.model.SymbolType;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import org.sonar.check.Rule;

import java.util.Set;
import java.util.stream.Collectors;

@Rule(key = DictionarySubscriptValueMismatchCheck.CHECK_KEY, name = DictionarySubscriptValueMismatchCheck.NAME, description = DictionarySubscriptValueMismatchCheck.HTML_DESCRIPTION)
public class DictionarySubscriptValueMismatchCheck extends AbstractVensimCheck{

    public static final String CHECK_KEY = "symbol-subscript-value-mismatch-db" ;
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that all the subscripts in the file have the same values or a subset of values as the subscripts of the database.";
    public static final String NAME = "DictionarySubscriptValueMismatch" ;


    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable parsedTable = context.getParsedSymbolTable();
        SymbolTable dbTable = context.getDbSymbolTable();

        if(dbTable!=null)
            checkSubscriptValueMismatch(context, parsedTable, dbTable);
    }

    private void checkSubscriptValueMismatch(VensimVisitorContext context, SymbolTable parsedTable, SymbolTable dbTable) {
        for(Symbol foundSymbol: parsedTable.getSymbols()){
            if(raisesIssue(foundSymbol,dbTable)){
                foundSymbol.setAsInvalid();

                for(int line: foundSymbol.getDefinitionLines()) {
                    Issue issue = new Issue(this, line,"The subscript '"+ foundSymbol.getToken() + "' has values that aren't defined in the database. Unexpected values: '["+ getUnexpectedSymbolsString(foundSymbol,dbTable)+"]'.");
                    addIssue(context,issue,foundSymbol);

                }
            }
        }
    }

    private String getUnexpectedSymbolsString(Symbol foundSymbol, SymbolTable dbTable){
        Set<String> dbValues = dbTable.getSymbol(foundSymbol.getToken().trim()).getDependencies().stream().map(Symbol::getToken).collect(Collectors.toSet());
        Set<String> foundValues = foundSymbol.getDependencies().stream().map(Symbol::getToken).collect(Collectors.toSet());

        foundValues.removeAll(dbValues);

        return foundValues.stream().sorted().collect(Collectors.joining(", "));

    }

    private boolean raisesIssue(Symbol foundSymbol, SymbolTable dbTable) {
        if(foundSymbol.getType() != SymbolType.Subscript)
            return false;

        if(!dbTable.hasSymbol(foundSymbol.getToken()))
            return false;

        Symbol dbSymbol = dbTable.getSymbol(foundSymbol.getToken());

        if( dbSymbol.getType()!= SymbolType.Subscript)
            return false;

        Set<Symbol> dbValues = dbSymbol.getDependencies();
        Set<Symbol> foundValues = foundSymbol.getDependencies();

        return !dbValues.containsAll(foundValues);


    }

}
