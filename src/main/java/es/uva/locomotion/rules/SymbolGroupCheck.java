package es.uva.locomotion.rules;


import es.uva.locomotion.model.AcronymsList;
import es.uva.locomotion.model.Symbol;
import es.uva.locomotion.model.SymbolTable;
import es.uva.locomotion.model.SymbolType;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.sonar.api.batch.rule.Severity;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


@Rule(key = SymbolGroupCheck.CHECK_KEY, name = SymbolGroupCheck.NAME, description = SymbolGroupCheck.HTML_DESCRIPTION)
public class SymbolGroupCheck extends AbstractVensimCheck {
    protected static final VensimLogger LOG = VensimLogger.getInstance();

    public static final String DEFAULT_CONTROL_SYMBOLS = "TIME;TIME STEP;INITIAL TIME;FINAL TIME;SAVEPER";


    public static final String CHECK_KEY = "symbol-control-group";
    public static final String NAME = "SymbolGroupCheck";
    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that variables are declared in the correct group. Only:\n" +
            DEFAULT_CONTROL_SYMBOLS + " can be declared in .Control group\n" +
            "<h2>Noncompliant Code Examples</h2>\n" +
            "<pre>\n" +
            "TIME_STEP  = 0.03125\n" +
            "\t~\tYear [0,?]\n" +
            "\t~\tThe time step for the simulation.\n" +
            "\t|\n" +
            "********\n" +
            "\t.Control\n" +
            "********~\n" +
            "other_symbol  = 0\n" +
            "\t~~|\n" +
            "</pre>\n" +
            "<h2>Compliant Solution</h2>\n" +
            "<pre>\n" +
            "other_symbol  = 0\n" +
            "\t~~|\n" +
            "********\n" +
            "\t.Control\n" +
            "********~\n" +
            "TIME_STEP  = 0.03125\n" +
            "\t~\tYear [0,?]\n" +
            "\t~\tThe time step for the simulation.\n" +
            "\t|\n" +
            "</pre>\n";

    public static final String DEFAULT_CONTROL_GROUP_NAME = "Control";
    @RuleProperty(
            key = "control-group-symbol",
            defaultValue = DEFAULT_CONTROL_SYMBOLS,
            description = "The list of symbols that must be in control group (separated with \";\").")
    public  final String symbols = DEFAULT_CONTROL_SYMBOLS;

    private  List<String> getDefaultControlSymbols() {
        try {
            return Arrays.asList(symbols.split(";"));
        } catch (PatternSyntaxException exception) {
            LOG.unique("The rule " + NAME + " has an invalid configuration: The selected list of symbols is invalid. Error: " + exception.getDescription(),
                    LoggingLevel.ERROR);
            return Arrays.asList(DEFAULT_CONTROL_SYMBOLS.split(";"));
        }
    }

    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable table = context.getParsedSymbolTable();
        SymbolTable dbTable = context.getDbSymbolTable();
        AcronymsList acronymsList = context.getDbAcronyms();
        List<String> controlSymbols = getDefaultControlSymbols();
        for (Symbol symbol : table.getSymbols()) {
            if (checkGroupControl(symbol.getGroup())) {
                if (!controlSymbols.contains(symbol.getToken())) {
                    for (int line : symbol.getDefinitionLines()) {

                        Issue issue = new Issue(this, line, "This symbol is declared in the group \"Control\"" +
                                " but it does not belongs in it. Try repositioning its declaration to outside of this group");
                        issue.setSeverity(Severity.MAJOR);
                        addIssue(context, issue, symbol.isFiltered());
                    }
                }
            } else {
                if (controlSymbols.contains(symbol.getToken())) {
                    for (int line : symbol.getDefinitionLines()) {

                        Issue issue = new Issue(this, line, "This symbol is not declared in the group \"Control\"" +
                                " but it does belongs in it. Try repositioning its declaration to inside of this group");
                        issue.setSeverity(Severity.MAJOR);
                        addIssue(context, issue, symbol.isFiltered());
                    }
                }
            }
        }
    }

    public boolean checkGroupControl(String group) {
        if (group == null) {
            return false;
        } else {
            return group.equals(DEFAULT_CONTROL_GROUP_NAME);
        }
    }
}