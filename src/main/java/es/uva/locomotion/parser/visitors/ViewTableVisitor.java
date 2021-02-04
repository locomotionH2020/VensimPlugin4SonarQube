package es.uva.locomotion.parser.visitors;

import es.uva.locomotion.model.View;
import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.parser.*;

import es.uva.locomotion.utilities.logs.VensimLogger;


public class ViewTableVisitor extends ModelParserBaseVisitor<Object> {
    private ViewTable table;
    private View actualView;

    private String moduleSeparator;
    private String categorySeparator;

    protected static VensimLogger LOG = VensimLogger.getInstance();

    private static final int SYMBOL_PRIVATE_ID = 10;

    private ViewTableVisitor() {
    }

    public static ViewTableVisitor createViewTableVisitor(String moduleSeparator, String categorySeparator) {
        ViewTableVisitor vtv = new ViewTableVisitor();
        vtv.moduleSeparator = moduleSeparator;
        vtv.categorySeparator = categorySeparator;
        return vtv;
    }
    public static ViewTableVisitor createViewTableVisitor(String moduleSeparator) {
        ViewTableVisitor vtv = new ViewTableVisitor();
        vtv.moduleSeparator = moduleSeparator;
        vtv.categorySeparator = null;
        return vtv;
    }
    public static ViewTableVisitor createViewTableVisitor() {
        ViewTableVisitor vtv = new ViewTableVisitor();
        vtv.moduleSeparator = null;
        vtv.categorySeparator = null;
        return vtv;    }

    public ViewTable getViewTable(ModelParser.FileContext context){
        table = new ViewTable();
        visit(context);
        return table;
    }

    private boolean isEven(int num){
        return num % 2 == 0;

    }
    @Override
    public Object visitViewName(ModelParser.ViewNameContext ctx) {
        String viewName = ctx.getText().trim().substring(1);
        String module = viewName;
        String category = null;
        String subcategory = null;
        if(moduleSeparator != null){
            String[] aux = viewName.split("[" + moduleSeparator + "]");

            if( aux.length != 2){
                module = viewName;
            }else{
                module = aux[0];
                category =  aux[1];

                if(categorySeparator != null){
                    aux = category.split(categorySeparator);
                    subcategory = aux.length > 1 ? aux[1] : null;
                    category = aux[0];
                }
            }

        }
        actualView = table.createOrSelectView(module,category,subcategory);

        return super.visitViewName(ctx);
    }

    @Override
    public Object visitViewVariable(ModelParser.ViewVariableContext ctx) {
        int internalId = Integer.parseInt(ctx.internalId.getText());
        if(internalId == SYMBOL_PRIVATE_ID) {
            int objectType = Integer.parseInt(ctx.bits.getText());
            String token = ctx.name.getText();
            String underScoreToken = token.replace(" ", "_");
            if (isEven(objectType)){
                actualView.addShadow(token);
                actualView.addShadow(underScoreToken);
            }else{
                actualView.addPrimary(token);
                actualView.addPrimary(underScoreToken);
            }
        }
        return super.visitViewVariable(ctx);
    }
}
