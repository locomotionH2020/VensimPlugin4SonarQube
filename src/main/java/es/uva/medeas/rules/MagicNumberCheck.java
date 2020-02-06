package es.uva.medeas.rules;

import es.uva.medeas.VensimPlugin;
import es.uva.medeas.parser.visitors.MagicNumberTableVisitor;
import es.uva.medeas.plugin.Issue;
import es.uva.medeas.plugin.VensimVisitorContext;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.utilities.Constants;
import org.sonar.api.batch.rule.Severity;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
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
            "CHOOSE_POLICY = 5~~|\n"+
            "Year policy change energy[scenarios,sectors,final sources]=\n IF THEN ELSE(Choose policies of intensities global or by sector[scenarios]= CHOOSE_POLICY, value1, value2)~~|\n"+
            "</pre>";

    public static final String DEFAULT_REPETITIONS = "3";

    protected static Logger LOG = Loggers.get(MagicNumberCheck.class);

    private boolean alreadyLoggedPropertyError;

    public MagicNumberCheck(){
       alreadyLoggedPropertyError = false;

    }


    @RuleProperty(
            key = "minimum-repetitions",
            defaultValue = DEFAULT_REPETITIONS,
    description = "Minimum times a number must appear to be considered a magic number. Must be greater than 0.")
    public String repetitions = DEFAULT_REPETITIONS ;


    @Override
    public void scan(VensimVisitorContext context) {

        SymbolTable numberTable = getVisitor().getSymbolTable(context.getRootNode());

        int minimumRepetitions = getMinimumRepetitions();



        for(Symbol symbol: numberTable.getSymbols()){
            if(!numberIsIgnored(symbol.getToken())) {
                int foundRepetitions = symbol.getDefinitionLines().size();

                if(foundRepetitions>=1){

                    Severity issueSeverity;
                    if(foundRepetitions<minimumRepetitions)
                        issueSeverity = Severity.INFO;
                    else
                        issueSeverity = Severity.MAJOR;


                    for (int line : symbol.getDefinitionLines()) {
                        Issue issue = new Issue(this, line, "The number " + symbol.getToken() + " is repeated " +
                                symbol.getDefinitionLines().size() + " times. Consider replacing it by a constant");
                        issue.setSeverity(issueSeverity);
                        context.addIssue(issue);
                    }

                }
            }
        }



    }

    private int getMinimumRepetitions(){
        try{
            int selectedRepetitions = Integer.parseInt(repetitions);
            if(selectedRepetitions>=1)
                return selectedRepetitions;
            else{
               if(!alreadyLoggedPropertyError){
                   LOG.warn( "The rule " + NAME + " has an invalid configuration: The selected minimum repetitions must be a number greater than 0."+"["+ VensimPlugin.PLUGIN_KEY +"]");
                   alreadyLoggedPropertyError = true;
               }
                return Integer.parseInt(DEFAULT_REPETITIONS);
            }
        }catch (NumberFormatException ex){
            if(!alreadyLoggedPropertyError){
                LOG.warn("The rule " + NAME + " has an invalid configuration: The selected minimum repetitions must be a number greater than 0."+"["+ VensimPlugin.PLUGIN_KEY +"]");
                alreadyLoggedPropertyError = true;
            }
            return Integer.parseInt(DEFAULT_REPETITIONS);
        }
    }

    protected MagicNumberTableVisitor getVisitor(){
        return new MagicNumberTableVisitor();
    }

    private boolean numberIsIgnored(String number){
        return Constants.IGNORED_MAGIC_NUMBERS.contains(number);
    }

}
