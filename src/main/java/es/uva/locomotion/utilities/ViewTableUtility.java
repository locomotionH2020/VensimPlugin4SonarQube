package es.uva.locomotion.utilities;

import es.uva.locomotion.model.Module;
import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.parser.ModelParser;
import es.uva.locomotion.parser.visitors.ViewTableVisitor;
import es.uva.locomotion.utilities.logs.VensimLogger;


public class ViewTableUtility {

    private ViewTableUtility() {
        throw new IllegalStateException("Utility class");
    }

    protected static VensimLogger logger = VensimLogger.getInstance();

    public static ViewTable getViewTable(SymbolTable table, ModelParser.FileContext context, String moduleSeparator, String categorySeparator) {
        ViewTableVisitor generator = ViewTableVisitor.createViewTableVisitor(moduleSeparator, categorySeparator);
        generator.setSymbolTable(table);
        return generator.getViewTable(context);
    }

    public static ViewTable getViewTable(SymbolTable table, ModelParser.FileContext context, String moduleSeparator) {
        ViewTableVisitor generator = ViewTableVisitor.createViewTableVisitor(moduleSeparator);
        generator.setSymbolTable(table);

        return generator.getViewTable(context);
    }

    public static ViewTable getViewTable(SymbolTable table, ModelParser.FileContext context) {
        ViewTableVisitor generator = ViewTableVisitor.createViewTableVisitor();
        generator.setSymbolTable(table);

        return generator.getViewTable(context);
    }


    public static void filterModule(SymbolTable table, String moduleName) {
        for (Symbol symbol : table.getSymbols()) {
            boolean filter = symbol.getPrimaryModule() == null || !symbol.getPrimaryModule().getName().equalsIgnoreCase(moduleName);
            symbol.setFiltered(filter);
        }
    }
}
