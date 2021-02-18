package es.uva.locomotion.parser.visitors;

import com.ibm.icu.impl.Pair;
import es.uva.locomotion.model.Symbol;
import es.uva.locomotion.model.SymbolTable;
import es.uva.locomotion.parser.ModelParser;
import es.uva.locomotion.parser.ModelParserBaseVisitor;
import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;

import java.util.*;

public class EmbeddedLookupVisitor extends ModelParserBaseVisitor<Void> {

    protected static VensimLogger LOG = VensimLogger.getInstance();

    private List<Pair<Symbol, Integer>> lookupsTable;

    private SymbolTable symbols;
    private boolean isSymbolFiltered;


    public EmbeddedLookupVisitor() {
        lookupsTable = new ArrayList<>();
        isSymbolFiltered = false;
    }

    public void setSymbols(SymbolTable symbols) {
        this.symbols = symbols;
    }

    @Override
    public Void visitLhs(ModelParser.LhsContext ctx) {

        if (symbols == null) {
            LOG.unique("Symbol table unassigned in EmbeddedLookupVisitor", LoggingLevel.INFO);
            return null;
        }
        if (!symbols.hasSymbol(ctx.Id().getText())) {
            LOG.error("Found symbol \"" + ctx.Id().getText() + "\" that is not in the symbol table");
            return null;
        }
        Symbol symbol = symbols.getSymbol(ctx.Id().getText());
        isSymbolFiltered = symbol.isFiltered();

        return null;

    }

    public List<Pair<Symbol, Integer>> getSymbolTable(ModelParser.FileContext context) {
        lookupsTable = new ArrayList<>();
        visit(context);
        return lookupsTable;
    }


    @Override
    public Void visitLookupPointList(ModelParser.LookupPointListContext ctx) {
        int size = ctx.lookupPoint().size();
        Symbol lookup = new Symbol("not used");
        lookup.addDefinitionLine(ctx.start.getLine());
        lookup.setFiltered(isSymbolFiltered);
        lookupsTable.add(Pair.of(lookup, size));

        return null;
    }

    @Override
    public Void visitNumberList(ModelParser.NumberListContext ctx) {

        int size = ctx.integerConst().size() + ctx.floatingConst().size();
        Symbol lookup = new Symbol("not used");
        lookup.addDefinitionLine(ctx.start.getLine());
        lookup.setFiltered(isSymbolFiltered);
        lookupsTable.add(Pair.of(lookup, size));

        return null;

    }

}
