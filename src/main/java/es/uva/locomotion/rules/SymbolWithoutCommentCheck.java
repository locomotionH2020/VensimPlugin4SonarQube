package es.uva.locomotion.rules;


import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.SymbolTable;
import es.uva.locomotion.parser.SymbolType;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.plugin.VensimVisitorContext;
import org.sonar.check.Rule;

import java.util.Arrays;
import java.util.List;

@Rule(key= SymbolWithoutCommentCheck.CHECK_KEY, name = SymbolWithoutCommentCheck.NAME, description = SymbolWithoutCommentCheck.HTML_DESCRIPTION)
public class SymbolWithoutCommentCheck implements VensimCheck{

    public static final String CHECK_KEY = "symbol-without-comment" ;
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that all the symbols have a comment. The only exceptions are macros/functions and subscript values. " +
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>\n" +
            "materials to extract for EV batteries Mt =\n" +
            "materials required for EV batteries *(1-recycling rates minerals alt tech) ~ Mt/Year ~ |"+
            "</pre>\n" +
            "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "materials to extract for EV batteries Mt =\n" +
            "materials required for EV batteries *(1-recycling rates minerals alt tech) "+
            "~ Mt/Year \n~ Annual materials to be mined for the construction of EV batteries |"+
            "</pre>\n";

    public static final String NAME = "SymbolWithoutComment" ;

    private List<SymbolType> IGNORED_TYPES = Arrays.asList(SymbolType.Function,SymbolType.Subscript_Value);

    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable table = context.getParsedSymbolTable();

        for(Symbol symbol: table.getSymbols()){
            if(!IGNORED_TYPES.contains(symbol.getType()) && symbol.getComment().isBlank()){
                symbol.setAsInvalid();

                for(int line: symbol.getDefinitionLines()) {
                    Issue issue = new Issue(this,line,"The symbol '"+ symbol.getToken() + "' should have a comment");
                    context.addIssue(issue);
                }
            }
        }
    }
}
