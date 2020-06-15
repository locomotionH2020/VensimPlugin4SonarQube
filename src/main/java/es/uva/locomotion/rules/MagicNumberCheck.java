package es.uva.locomotion.rules;

import es.uva.locomotion.VensimPlugin;
import es.uva.locomotion.parser.visitors.MagicNumberTableVisitor;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.SymbolTable;
import es.uva.locomotion.utilities.Constants;
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
            "The number must appear several times to be considered a magic number. The repetitions can be configured as a parameter of the rule.<br><br>" +
            "If the number appears less times than then parameter, it will raise an issue of severity INFO. If it appears more times or exactly as the parameter," +
            " it will raise an issue of severity MAJOR "+
            "<h2>Exceptions</h2>\n"+
            "<ul>"+
            "   <li> Assigning a literal to a variable. Example <code>A = 3~~|</code>   </li>"+
            "   <li> Literals in tabbed arrays, lookups, one and two dimensional array of values. It's not possible to replace them with constants. </li>"+
            "   <li> Literals in reality checks </li>"+
            "   <li> Literals that are compared to switches. Switch variables are variables that begin with 'SWITCH_'. Example: <code>value = IF THEN ELSE(SWITCH_POLICy=1,VALUE1,VALUE2)~~|</code>"+
            "</ul>"+
            "<h2> Compound numbers </h2>"+
            "<p> There are functions that form a 'compound literal' if they are called with literals as arguments. For example SQRT(2) and LOG(3,4) are considered literals, but LOG(3,VALUE) isn't.<br><br>" +
            "Only the following functions create compound literals: SQRT, TAN, TANH, SIN, SINH, COS, COSH, ARCTAN, ARCCOS, ABS, LN, GAMMA LN, INTEGER, GAME, EXP, POWER, LOG, MODULO, QUANTUM.</p>" +
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>" +
            "VALUE =  IF THEN ELSE(SWITCH_POLICY = 1, 135, 41 )~~|\n" +
            "</pre>\n" +
            "<h2>Compliant Code Examples</h2>\n" +
            "<pre>\n" +
            "VALUE_POLICY_ONE = 135~~|\n"+
            "VALUE_POLICY_TWO = 135~~|\n"+
            "VALUE = IF THEN ELSE(SWITCH_POLICY = 1, VALUE_POLICY_ONE, VALUE_POLICY_TWO )~~|\n"+
            "NUMBER = SQRT(2)~~|"+
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
