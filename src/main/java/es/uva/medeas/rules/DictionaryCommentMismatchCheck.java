package es.uva.medeas.rules;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import es.uva.medeas.plugin.Issue;
import es.uva.medeas.plugin.VensimVisitorContext;
import es.uva.medeas.utilities.Constants;
import org.sonar.check.Rule;

@Rule(key = DictionaryCommentMismatchCheck.CHECK_KEY, name = DictionaryCommentMismatchCheck.NAME, description = DictionaryCommentMismatchCheck.HTML_DESCRIPTION)
public class DictionaryCommentMismatchCheck implements VensimCheck {
    public static final String CHECK_KEY = "symbol-comment-mismatch-db" ;
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that all the symbols in the file have the same comment as the symbols stored in the database. " +
            "If the symbol found in the file doesn't have a comment the rule is ignored.<br> " +
            "The symbols predefined by Vensim (FINAL TIME, TIME STEP, etc) and functions are ignored (except lookups)</p>";
    public static final String NAME = "DictionaryCommentMismatch" ;

    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable parsedTable = context.getParsedSymbolTable();
        SymbolTable dbTable = context.getDbSymbolTable();

        if(dbTable!=null)
            checkSymbolsComment(context, parsedTable, dbTable);
    }

    private void checkSymbolsComment(VensimVisitorContext context, SymbolTable parsedTable, SymbolTable dbTable) {
        for(Symbol foundSymbol: parsedTable.getSymbols()){
            if(raisesIssue(foundSymbol,dbTable)){
                String expectedComment = dbTable.getSymbol(foundSymbol.getToken()).getComment().trim();
                for(int line: foundSymbol.getDefinitionLines()) {
                    Issue issue = new Issue(this, line,"The symbol '"+ foundSymbol.getToken() + "' has a comment '"+foundSymbol.getComment().trim() + "' but the dictionary has '"+ expectedComment + "'." );
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

        String dbComment = dbTable.getSymbol(foundSymbol.getToken()).getComment();
        String fileComment = foundSymbol.getComment();
        return !fileComment.isBlank()  && !fileComment.trim().equals(dbComment.trim());

    }

    private boolean symbolIsIgnored(Symbol symbol){
        return symbol.getType()== SymbolType.Function || Constants.DEFAULT_VENSIM_SYMBOLS.contains(symbol.getToken().trim());
    }
}
