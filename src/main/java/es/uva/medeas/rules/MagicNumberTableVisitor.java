package es.uva.medeas.rules;

import es.uva.medeas.VensimVisitorContext;
import es.uva.medeas.parser.ModelBaseVisitor;
import es.uva.medeas.parser.ModelParser;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;

public class MagicNumberTableVisitor  extends ModelBaseVisitor {


    private SymbolTable numberTable;


    public MagicNumberTableVisitor(){
        numberTable = new SymbolTable();
    }


    public  SymbolTable getSymbolTable(VensimVisitorContext context){
        numberTable = new SymbolTable();
        visit(context.getRootNode());
        return numberTable;
    }


    private boolean exprIsAConstant(ModelParser.ExprContext ctx){
        return  ctx.getClass() == ModelParser.ConstContext.class;
    }
    @Override
    public Object visitEquation(ModelParser.EquationContext ctx) {

        if(!exprIsAConstant(ctx.expr()))
            return super.visit(ctx.expr());

        return null;

    }


    @Override
    public Object visitLookup(ModelParser.LookupContext ctx) {
        return null;
    }

    @Override
    public Object visitIntegerConst(ModelParser.IntegerConstContext ctx) {
        Symbol integer = numberTable.getSymbolOrCreate(ctx.getText());
        integer.addDefinitionLine(ctx.start.getLine());
        return null;
    }


    @Override
    public Object visitFloatingConst(ModelParser.FloatingConstContext ctx) {
        Symbol integer = numberTable.getSymbolOrCreate(ctx.getText());
        integer.addDefinitionLine(ctx.start.getLine());
        return null;
    }

    @Override
    public Object visitTabbedArray(ModelParser.TabbedArrayContext ctx) {
        return null;
    }


    private String getFunctionName(ModelParser.CallContext ctx){
        return ctx.Id().getText();
    }

    @Override
    public Object visitCall(ModelParser.CallContext ctx) {
        if ("WITH LOOKUP".equals(getFunctionName(ctx))) {
            return visit(ctx.exprList().expr(0));
        }

        return super.visitCall(ctx);
    }

    @Override
    public Object visitRealityCheck(ModelParser.RealityCheckContext ctx) {
        return null;
    }

    @Override
    public Object visitConstraint(ModelParser.ConstraintContext ctx) {
        return null;
    }



}
