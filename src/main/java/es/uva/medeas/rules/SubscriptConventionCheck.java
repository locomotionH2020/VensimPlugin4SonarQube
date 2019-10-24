package es.uva.medeas.rules;


import es.uva.medeas.Issue;
import es.uva.medeas.VensimVisitorContext;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import org.sonar.check.Rule;



@Rule(key = SubscriptConventionCheck.CHECK_KEY)
public class SubscriptConventionCheck implements VensimCheck {
    public static final String CHECK_KEY = "subscription-convention" ;


    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable table = context.getSymbolTable();




        for(Symbol symbol:table.getSymbols()){

            if(symbol.getType()== SymbolType.SUBSCRIPT_NAME && !checkSubscriptNameFollowsConvention(symbol.getToken())){
                Issue issue = new Issue(this,symbol.getLine(),"The subscript name doesnt match the pattern");
                context.addIssue(issue);
            }
            if(symbol.getType()== SymbolType.SUBSCRIPT_VALUE && !checkSubscriptValueFollowsConvention(symbol.getToken())){
                Issue issue = new Issue(this,symbol.getLine(),"The subscript name doesnt match the pattern");

                context.addIssue(issue);
            }
        }
    }

    private boolean checkSubscriptValueFollowsConvention(String name) {
        return name.matches("[A-Z0-9_]+_CASE");
    }


    private boolean checkSubscriptNameFollowsConvention(String name){
        return name.matches("ENUMERATE_[A-Z0-9_]+S");

    }

}
