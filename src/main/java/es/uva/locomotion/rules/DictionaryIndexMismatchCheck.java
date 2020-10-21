package es.uva.locomotion.rules;

import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.SymbolTable;
import es.uva.locomotion.parser.SymbolType;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.utilities.UtilityFunctions;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.check.Rule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Rule(key = DictionaryIndexMismatchCheck.CHECK_KEY, name = DictionaryIndexMismatchCheck.NAME, description = DictionaryIndexMismatchCheck.HTML_DESCRIPTION)
public class DictionaryIndexMismatchCheck extends AbstractVensimCheck{
    public static final String CHECK_KEY = "symbol-index-mismatch-db" ;
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that all the symbols in the file have at least a subset of the indexes stored in the database.\n " +
            "It checks if the indexes of the symbol have the same SUBSCRIPT VALUES as the ones in the db.\n" +
            "If the symbol doesn't exist in the database, the rule is ignored,\n " +
            "If a variable is indexed by both a subscript and a subscript value in the same index (column), the rule is" +
            "ignored."+
            "</p>";
    public static final String NAME = "DictionaryIndextMismatch" ;

    protected static VensimLogger LOG = VensimLogger.getInstance();

    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable parsedTable = context.getParsedSymbolTable();
        SymbolTable dbTable = context.getDbSymbolTable();

        if(dbTable!=null)
            checkSymbolsIndexes(context, parsedTable, dbTable);
    }

    private void checkSymbolsIndexes(VensimVisitorContext context, SymbolTable parsedTable, SymbolTable dbTable) {
        for(Symbol foundSymbol: parsedTable.getSymbols()){
            if(raisesIssue(foundSymbol,dbTable)){
                foundSymbol.setAsInvalid();

                for(int line: foundSymbol.getDefinitionLines()) {
                    List<List<String>> foundTxt = foundSymbol.getIndexes().stream().map(list -> list.stream().map(Symbol::getToken).collect(Collectors.toList())).collect(Collectors.toList());
                    Symbol dbSymbol = dbTable.getSymbol(foundSymbol.getToken().trim());
                    List<String> dbTxt = dbSymbol.getIndexes().stream().map(list -> list.get(0).getToken()).collect(Collectors.toList());
                    Issue issue = new Issue(this, line,"The symbol '" +  foundSymbol.getToken() +"' is indexed by values that aren't stored in the database.'\nFound:" + foundTxt + ".\nExpected a subset of:" + dbTxt );
                    addIssue(context,issue,foundSymbol);

                }
            }
        }

    }

    private boolean raisesIssue(Symbol foundSymbol, SymbolTable dbTable) {

        if(!dbTable.hasSymbol(foundSymbol.getToken().trim()))
            return false;

        Symbol dbSymbol = dbTable.getSymbol(foundSymbol.getToken().trim());

        List<List<Symbol>> foundIndexes = foundSymbol.getIndexes();
        List<List<Symbol>> dbIndexes =  dbSymbol.getIndexes();

        try{
            return !tryToMatchIndexes(foundIndexes, dbIndexes);
        }catch (IllegalStateException ex){
            LOG.info("The symbol '" + foundSymbol.getToken() + "' is indexed by a subscript and a subscript value in the same column.");
            return false;
        }

    }

    private boolean tryToMatchIndexes(List<List<Symbol>> foundIndexes, List<List<Symbol>> dbIndexes) {
        if(foundIndexes.isEmpty())
            return true;

        if(dbIndexes.isEmpty())
            return false;
        for(List<Symbol> index:foundIndexes){

            for(List<Symbol> dbIndex:dbIndexes){
                if(matchIndex(index,dbIndex.get(0))){
                    ArrayList<List<Symbol>> newFound = new ArrayList<>(foundIndexes);

                    newFound.remove(index);
                    ArrayList<List<Symbol>> newDb = new ArrayList<>(dbIndexes);
                    newDb.remove(dbIndex);

                    if(tryToMatchIndexes(newFound,newDb))
                        return true;
                }
            }
        }
        return false;
    }


    private boolean matchIndex(List<Symbol> foundIndex, Symbol dbIndex){
        Symbol firstFoundValue = foundIndex.get(0);
        Set<Symbol> uniqueFoundIndexes = new HashSet<>(foundIndex);

        if(firstFoundValue.getType()== SymbolType.Subscript){
            if(uniqueFoundIndexes.size()==1) {
                Set<String> dbValues = dbIndex.getDependencies().stream().map(Symbol::getToken).map(String::trim).collect(Collectors.toSet());
                Set<String> foundValues = firstFoundValue.getDependencies().stream().map(Symbol::getToken).map(String::trim).collect(Collectors.toSet());
                return dbValues.containsAll(foundValues);
            }

            throw new IllegalStateException("A symbol can have either a subscript or several subscript values, not both");
        }

        List<String> dbIndexTokens = dbIndex.getDependencies().stream().map(Symbol::getToken).map(String::trim).collect(Collectors.toList());
        for(Symbol index:foundIndex){
            if(!dbIndexTokens.contains(index.getToken().trim())) {
                return false;
            }
        }
        return true;
    }
}
