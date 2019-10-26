package es.uva.medeas.rules;


import es.uva.medeas.Issue;
import es.uva.medeas.VensimVisitorContext;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import org.sonar.check.Rule;



@Rule(key = SubscriptValueNameCheck.CHECK_KEY)
public class SubscriptValueNameCheck implements VensimCheck {
    public static final String CHECK_KEY = "subscription-value-name-convention" ;


    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable table = context.getSymbolTable();




        for(Symbol symbol:table.getSymbols()){
            if(symbol.getType()== SymbolType.SUBSCRIPT_VALUE && !checkSubscriptValueFollowsConvention(symbol.getToken())){
                Issue issue = new Issue(this,symbol.getDefinitionLine(),"The value of the subscript doesn't follow the naming conventions");

                context.addIssue(issue);
            }
        }
    }

    private boolean checkSubscriptValueFollowsConvention(String name) {
        return name.matches("([A-Z0-9]+_)*[A-Z0-9]+");
    }


}
