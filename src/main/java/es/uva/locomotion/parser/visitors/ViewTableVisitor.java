package es.uva.locomotion.parser.visitors;

import es.uva.locomotion.parser.*;

import es.uva.locomotion.utilities.logs.VensimLogger;


public class ViewTableVisitor extends ModelBaseVisitor<Object> {
    private ViewTable table;
    private View actualView;
    protected static VensimLogger LOG = VensimLogger.getInstance();

    private static int SYMBOL_PRIVATE_ID = 10;

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

        actualView = table.createOrSelectView(viewName);

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
