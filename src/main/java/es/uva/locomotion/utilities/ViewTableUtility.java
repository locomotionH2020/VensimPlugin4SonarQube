package es.uva.locomotion.utilities;

import es.uva.locomotion.model.Module;
import es.uva.locomotion.model.Symbol;
import es.uva.locomotion.model.SymbolTable;
import es.uva.locomotion.model.View;
import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.parser.*;
import es.uva.locomotion.parser.visitors.ViewTableVisitor;
import es.uva.locomotion.utilities.logs.VensimLogger;


public class ViewTableUtility {

    protected static VensimLogger LOG = VensimLogger.getInstance();

    public static ViewTable getViewTable(SymbolTable table, ModelParser.FileContext context, String moduleSeparator, String categorySeparator) {
        ViewTableVisitor generator = ViewTableVisitor.createViewTableVisitor(moduleSeparator, categorySeparator);
        generator.setSymbolTable(table);
        return generator.getViewTable(context);
    }

    public static ViewTable getViewTable(SymbolTable table,ModelParser.FileContext context, String moduleSeparator) {
        ViewTableVisitor generator = ViewTableVisitor.createViewTableVisitor(moduleSeparator);
        generator.setSymbolTable(table);

        return generator.getViewTable(context);
    }

    public static ViewTable getViewTable(SymbolTable table,ModelParser.FileContext context) {
        ViewTableVisitor generator = ViewTableVisitor.createViewTableVisitor();
        generator.setSymbolTable(table);

        return generator.getViewTable(context);
    }



    public static void filterPrefix(SymbolTable table, String viewPrefix) {
        for (Symbol symbol : table.getSymbols()) {

            boolean filtered = true;
            for (Module viewName : symbol.get_views()) {
                if (viewName.getName().startsWith(viewPrefix)) {
                    filtered = false;
                    break;
                }
            }
            symbol.setFiltered(filtered);
        }
    }
}
