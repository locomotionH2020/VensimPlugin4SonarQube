package es.uva.medeas.rules;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import es.uva.medeas.plugin.Issue;
import es.uva.medeas.plugin.VensimVisitorContext;
import org.sonar.check.Rule;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Rule(key = DictionarySubscriptValueMismatchCheck.CHECK_KEY, name = DictionarySubscriptValueMismatchCheck.NAME, description = DictionarySubscriptValueMismatchCheck.HTML_DESCRIPTION)
public class DictionarySubscriptValueMismatchCheck implements VensimCheck{

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
                    context.addIssue(issue);
                }
            }
        }
    }

    private String getUnexpectedSymbolsString(Symbol foundSymbol, SymbolTable dbTable){
        Set<Symbol> dbValues = dbTable.getSymbol(foundSymbol.getToken().trim()).getDependencies();
        Set<Symbol> foundValues = new HashSet<>(foundSymbol.getDependencies());

        foundValues.removeAll(dbValues);

        return foundValues.stream().map(Symbol::getToken).sorted().collect(Collectors.joining(", "));

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
