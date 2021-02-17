package es.uva.locomotion.utilities;

import es.uva.locomotion.model.Symbol;
import es.uva.locomotion.model.SymbolTable;
import es.uva.locomotion.model.View;
import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.parser.*;
import es.uva.locomotion.parser.visitors.ViewTableVisitor;
import es.uva.locomotion.utilities.logs.VensimLogger;


public class ViewTableUtility {

    protected static VensimLogger LOG = VensimLogger.getInstance();

    public static ViewTable getViewTable(ModelParser.FileContext context, String moduleSeparator, String categorySeparator) {
        ViewTableVisitor generator = ViewTableVisitor.createViewTableVisitor(moduleSeparator, categorySeparator);
        return generator.getViewTable(context);
    }

    public static ViewTable getViewTable(ModelParser.FileContext context, String moduleSeparator) {
        ViewTableVisitor generator = ViewTableVisitor.createViewTableVisitor(moduleSeparator);
        return generator.getViewTable(context);
    }

    public static ViewTable getViewTable(ModelParser.FileContext context) {
        ViewTableVisitor generator = ViewTableVisitor.createViewTableVisitor();
        return generator.getViewTable(context);
    }

    public static void addViews(SymbolTable table, ViewTable viewTable) {
        for (Symbol symbol : table.getSymbols()) {
            String token = symbol.getToken();
            for (View view : viewTable.getViews()) {
                if (view.hasPrimary(token)) {
                    symbol.setPrimary_module(view.getModule());
                    symbol.setCategory(view.getCategoryOrSubcategory());
                }
                if (view.hasShadow(token)) symbol.addShadow_view(view.getModule());
            }
        }
    }

    public static void filterPrefix(SymbolTable table, String viewPrefix) {
        for (Symbol symbol : table.getSymbols()) {
            boolean filtered = true;
            for (String viewName : symbol.get_views()) {
                if (viewName.startsWith(viewPrefix)) {
                    filtered = false;
                    break;
                }
            }
            symbol.setFiltered(filtered);
        }
    }
}
