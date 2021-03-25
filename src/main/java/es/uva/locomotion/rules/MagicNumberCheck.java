package es.uva.locomotion.rules;

import es.uva.locomotion.model.symbol.Number;
import es.uva.locomotion.model.symbol.NumberTable;
import es.uva.locomotion.parser.visitors.MagicNumberTableVisitor;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.utilities.Constants;
import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.sonar.api.batch.rule.Severity;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;


@Rule(key = MagicNumberCheck.CHECK_KEY,name=MagicNumberCheck.NAME, description= MagicNumberCheck.HTML_DESCRIPTION)
public class MagicNumberCheck extends AbstractVensimCheck {
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

    protected static VensimLogger LOG = VensimLogger.getInstance();

    public MagicNumberCheck(){
    }


    @RuleProperty(
            key = "minimum-repetitions",
            defaultValue = DEFAULT_REPETITIONS,
    description = "Minimum times a number must appear to be considered a magic number. Must be greater than 0.")
    public String repetitions = DEFAULT_REPETITIONS ;


    @Override
    public void scan(VensimVisitorContext context) {

        MagicNumberTableVisitor visitor = getVisitor();
        SymbolTable symbolTable = context.getParsedSymbolTable();
        visitor.setSymbols(symbolTable);

        NumberTable numberTable = visitor.getNumberTable(context.getRootNode());

        int minimumRepetitions = getMinimumRepetitions();

        for(Number symbol: numberTable.getNumbers()){
            if(!numberIsIgnored(symbol.getToken())) {
                int foundRepetitions = symbol.getOcurrences();
                if(foundRepetitions>=1){

                    Severity issueSeverity;
                    if(foundRepetitions<minimumRepetitions)
                        issueSeverity = Severity.INFO;
                    else
                        issueSeverity = Severity.MAJOR;


                    for (int line : symbol.getLines()) {

                        Issue issue = new Issue(this, line, "The number " + symbol.getToken() + " is repeated " +
                                symbol.getOcurrences() + " times. Consider replacing it by a constant");
                        issue.setSeverity(issueSeverity);
                        addIssue(context,issue,symbol.isFiltered());

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
        }catch (NumberFormatException ex){
            // Empty catch block so that the error is logged if 'selectedRepetitions' is < 1 or if it isn't a number.
        }
        LOG.unique( "The rule " + NAME + " has an invalid configuration: The selected minimum repetitions must be a number greater than 0.",
                LoggingLevel.ERROR);
        return Integer.parseInt(DEFAULT_REPETITIONS);

    }

    protected MagicNumberTableVisitor getVisitor(){
        return new MagicNumberTableVisitor();
    }

    private boolean numberIsIgnored(String number){
        return Constants.IGNORED_MAGIC_NUMBERS.contains(number);
    }

}
