package es.uva.locomotion.rules;


import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.utilities.Constants;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.sonar.check.Rule;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Rule(key = DictionaryUnitSymbolCheck.CHECK_KEY, name = DictionaryUnitSymbolCheck.NAME, description = DictionaryUnitSymbolCheck.HTML_DESCRIPTION)
public class DictionaryUnitSymbolCheck extends AbstractVensimCheck {
    public static final String CHECK_KEY = "unit-convention-dictionary";

    public static final String HTML_DESCRIPTION = "" +
            "<p>This rule checks that all the symbols contains a unit from the dictionary</p>";


    public static final String NAME = "DictionaryUnitSymbolCheck";

    protected static final VensimLogger LOG = VensimLogger.getInstance();

    private final List<SymbolType> IGNORED_TYPES = Arrays.asList(SymbolType.FUNCTION, SymbolType.SUBSCRIPT_VALUE);

    @Override
    public void scan(VensimVisitorContext context) {
        SymbolTable parsedTable = context.getParsedSymbolTable();

        if (context.getDbdata() != null) {
                Set<String> dbUnits = context.getDbdata().getUnits();
                dbUnits.add("Dmnl");
                checkSymbolsUnits(context, parsedTable, dbUnits);
        }
    }

    private void checkSymbolsUnits(VensimVisitorContext context, SymbolTable parsedTable, Set<String> dbUnits) {
        for (Symbol foundSymbol : parsedTable.getSymbols()) {
            if (raisesIssue(foundSymbol, dbUnits)) {
                foundSymbol.setAsInvalid(this.getClass().getSimpleName());

                for (int line : foundSymbol.getLines()) {
                    Issue issue = new Issue(this, line, "The symbol '" + foundSymbol.getToken() + "' has '" + foundSymbol.getUnits().trim() + "' as units, but they don't exists in the dictionary, permited units are: " + dbUnits + ".");
                    addIssue(context, issue, foundSymbol.isFiltered());

                }
            }
        }
    }

    private boolean raisesIssue(Symbol foundSymbol, Set<String> dbUnits) {
        if (symbolIsIgnored(foundSymbol))
            return false;

        if (dbUnits.contains(foundSymbol.getToken()))
            return false;

        String fileUnits = foundSymbol.getUnits();
        return !fileUnits.isBlank() && !dbUnits.stream().map(String::trim).collect(Collectors.toList()).contains(fileUnits.trim());
    }

    private boolean symbolIsIgnored(Symbol symbol) {
        return IGNORED_TYPES.contains(symbol.getType()) || Constants.DEFAULT_VENSIM_SYMBOLS.contains(symbol.getToken().trim());
    }
}
