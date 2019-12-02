package es.uva.medeas.rules;

import es.uva.medeas.plugin.Issue;
import es.uva.medeas.plugin.VensimVisitorContext;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = MagicNumberCheck.CHECK_KEY,name=MagicNumberCheck.NAME, description= MagicNumberCheck.HTML_DESCRIPTION)
public class MagicNumberCheck implements VensimCheck {
    public static final String CHECK_KEY = "magic-number-check";
    public static final String NAME = "MagicNumberCheck" ;
    public static final String HTML_DESCRIPTION = "A magic number is a number that comes out of nowhere, and is directly used in an expression." +
            "The number must appear several times to be considered a magic number. The repetitions can be configured as a parameter of the rule." +
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>" +
            "Year policy change energy[scenarios,sectors,final sources]=\n IF THEN ELSE(Choose policies of intensities global or by sector[scenarios]=1, value1, value2)~~|\n" +
            "</pre>\n" +
            "<h2>Compliant Code Examples</h2>\n" +
            "<pre>\n" +
            "CHOOSE_POLICY = 1~~|\n"+
            "Year policy change energy[scenarios,sectors,final sources]=\n IF THEN ELSE(Choose policies of intensities global or by sector[scenarios]= CHOOSE_POLICY, value1, value2)~~|\n"+
            "</pre>";

    public static final String DEFAULT_REPETITIONS = "3";





    @RuleProperty(
            key = "minimum-repetitions",
            defaultValue = DEFAULT_REPETITIONS,
    description = "Minimum times a number must appear to be considered a magic number.")
    public String repetitions = DEFAULT_REPETITIONS ;



    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable numberTable = new MagicNumberTableVisitor().getSymbolTable(context);

        for(Symbol symbol: numberTable.getSymbols()){
            if(isMagicNumber(symbol))
                for(int line: symbol.getDefinitionLines()) {
                    Issue issue = new Issue(this,line,"The number " + symbol.getToken()  +" is repeated " +
                            symbol.getDefinitionLines().size() + "  times. Consider replacing it by a constant");
                    context.addIssue(issue);
                }
        }

    }

    private boolean isMagicNumber(Symbol symbol){
        return symbol.getDefinitionLines().size()>=Integer.parseInt(repetitions);
    }


}
