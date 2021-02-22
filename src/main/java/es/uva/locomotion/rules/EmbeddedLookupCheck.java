package es.uva.locomotion.rules;


import com.ibm.icu.impl.Pair;
import es.uva.locomotion.model.AcronymsList;
import es.uva.locomotion.model.Symbol;
import es.uva.locomotion.model.SymbolTable;
import es.uva.locomotion.model.SymbolType;
import es.uva.locomotion.parser.visitors.EmbeddedLookupVisitor;
import es.uva.locomotion.parser.visitors.MagicNumberTableVisitor;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.sonar.api.batch.rule.Severity;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import java.util.List;
import java.util.Map;


@Rule(key = EmbeddedLookupCheck.CHECK_KEY, name = EmbeddedLookupCheck.NAME, description = EmbeddedLookupCheck.HTML_DESCRIPTION)
public class EmbeddedLookupCheck extends AbstractVensimCheck {
    protected static final VensimLogger LOG = VensimLogger.getInstance();

    public static final String CHECK_KEY = "lookup-embedded";
    public static final String NAME = "EmbeddedLookupCheck";
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that lookups are not embedded in the model</p>\n" +
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>\n" +
            "\"name_lt\"([(0,0)-(10,10)],(1985,1378.15),(1986,1422.43),(1987,1422.43),(1988,1422.43),(1989,1422.43))\n" +
            "</pre>\n" +
            "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "name_lt(GET_DIRECT_LOOKUPS('file.xlsx', 'World', 'index', 'name'))\n" +
            "</pre>\n";

    public static final String DEFAULT_SIZE = "5";

    @RuleProperty(
            key = "minimum-size",
            defaultValue = DEFAULT_SIZE,
            description = "Minimum size of a lookup to be considered embedded. Must be greater than 0.")
    public String repetitions = DEFAULT_SIZE;


    private int getMinimumRepetitions() {
        try {
            int selectedRepetitions = Integer.parseInt(repetitions);
            if (selectedRepetitions >= 1)
                return selectedRepetitions;
        } catch (NumberFormatException ex) {
            // Empty catch block so that the error is logged if 'selectedRepetitions' is < 1 or if it isn't a number.
        }
        LOG.unique("The rule " + NAME + " has an invalid configuration: The selected minimum size must be a number greater than 0.",
                LoggingLevel.ERROR);
        return Integer.parseInt(DEFAULT_SIZE);

    }


    @Override
    public void scan(VensimVisitorContext context) {

        EmbeddedLookupVisitor visitor = getVisitor();
        SymbolTable symbolTable = context.getParsedSymbolTable();
        visitor.setSymbols(symbolTable);

        List<Pair<Symbol, Integer>> lookupTable = visitor.getSymbolTable(context.getRootNode());

        int minimumRepetitions = getMinimumRepetitions();

        for (Pair<Symbol,Integer> pair : lookupTable) {

            int foundRepetitions = pair.second;
            Symbol lookup = pair.first;

            if (foundRepetitions >= 1) {

                Severity issueSeverity;
                if (foundRepetitions < minimumRepetitions)
                    issueSeverity = Severity.INFO;
                else
                    issueSeverity = Severity.MAJOR;

                for (int line : lookup.getDefinitionLines()) {

                    Issue issue = new Issue(this, line, "This lookup have " + foundRepetitions + " embedded" +
                            " data points. Consider replacing it by an external excel.");
                    issue.setSeverity(issueSeverity);
                    addIssue(context, issue, lookup.isFiltered());

                }

            }

        }
    }

    public EmbeddedLookupVisitor getVisitor() {
        return new EmbeddedLookupVisitor();
    }
}
