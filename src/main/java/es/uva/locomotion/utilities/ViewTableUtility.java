package es.uva.locomotion.utilities;

import es.uva.locomotion.model.Symbol;
import es.uva.locomotion.model.SymbolTable;
import es.uva.locomotion.model.View;
import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.parser.*;
import es.uva.locomotion.parser.visitors.ViewTableVisitor;


public class ViewTableUtility {

    public static ViewTable getViewTable(Model.FileContext context) {
        ViewTableVisitor generator = new ViewTableVisitor();
        return generator.getViewTable(context);
    }

    public static void addViews(SymbolTable table, ViewTable viewTable) {
        for (Symbol symbol : table.getSymbols()) {
            String token = symbol.getToken();
            for (View view : viewTable.getViews()) {
                if (view.hasPrimary(token)) symbol.setPrimary_view(view.getName());
                if (view.hasShadow(token)) symbol.addShadow_view(view.getName());
            }
        }
    }

    public static void filterPrefix(SymbolTable table, String viewPrefix) {
        for (Symbol symbol : table.getSymbols()) {
            boolean filtered = true;
            for(String viewName : symbol.get_views()){
                if (viewName.startsWith(viewPrefix)) {
                    filtered = false;
                    break;
                }
            }
            symbol.setFiltered(filtered);
        }
    }
}
