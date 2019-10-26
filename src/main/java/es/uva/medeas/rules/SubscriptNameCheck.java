package es.uva.medeas.rules;


import es.uva.medeas.Issue;
import es.uva.medeas.VensimVisitorContext;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import org.sonar.check.Rule;



@Rule(key = SubscriptNameCheck.CHECK_KEY)
public class SubscriptNameCheck implements VensimCheck {
    public static final String CHECK_KEY = "subscription-convention" ;


    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable table = context.getSymbolTable();

        for(Symbol symbol:table.getSymbols()){

            if(symbol.getType()== SymbolType.SUBSCRIPT_NAME && !checkSubscriptNameFollowsConvention(symbol.getToken())){
                Issue issue = new Issue(this,symbol.getDefinitionLine(),"The name of the subscript doesn't follow the naming conventions");
                context.addIssue(issue);
            }

        }
    }

    //TODO test that parsing a incorrect file doesn't throw an exception

    private boolean checkSubscriptNameFollowsConvention(String name){
        return name.matches("([A-Z0-9]+_)*[A-Z0-9]+S_ENUM");

    }

}
