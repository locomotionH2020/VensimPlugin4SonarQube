package es.uva.medeas.parser.visitors;

import es.uva.medeas.parser.*;
import es.uva.medeas.plugin.VensimVisitorContext;
import es.uva.medeas.utilities.Constants;

public class MagicNumberTableVisitor  extends ModelBaseVisitor {


    private SymbolTable numberTable;




       ;

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

    private boolean exprIsAFunctionAndInIgnoreList(ModelParser.ExprContext ctx){
        if(ctx.getClass()!= ModelParser.SignExprContext.class)
            return false;


        ModelParser.SignExprContext node = (ModelParser.SignExprContext) ctx;
        if( node.exprAllowSign().getClass() != ModelParser.CallExprContext.class)
            return false;


       ModelParser.CallExprContext callNode = (ModelParser.CallExprContext) node.exprAllowSign();
       return Constants.IGNORED_FUNCTIONS_IF_ALONE.contains(callNode.call().Id().getText());
    }

    private boolean exprIsIgnored(ModelParser.ExprContext ctx){
        return exprIsAConstant(ctx) || exprIsAFunctionAndInIgnoreList(ctx);
    }


    @Override
    public Object visitEquation(ModelParser.EquationContext ctx) {

        if(ctx.expr()==null || exprIsIgnored(ctx.expr()))
            return null;


        return super.visit(ctx.expr());

    }

    @Override
    public Object visitDataEquation(ModelParser.DataEquationContext ctx) {

        if(ctx.expr()==null || exprIsIgnored(ctx.expr()))
            return null;

            return super.visit(ctx.expr());


    }

    @Override
    public Object visitUnchangeableConstant(ModelParser.UnchangeableConstantContext ctx) {

        if(ctx.expr()==null || exprIsIgnored(ctx.expr()))
            return null;


            return super.visit(ctx.expr());


    }

    @Override
    public Object visitLookup(ModelParser.LookupContext ctx) {
        return null;
    }

    @Override
    public Object visitIntegerConst(ModelParser.IntegerConstContext ctx) {
        String value = String.valueOf(Integer.parseInt(ctx.getText()));
        Symbol integer = numberTable.getSymbolOrCreate(value);
        integer.addDefinitionLine(ctx.start.getLine());
        return null;
    }


    @Override
    public Object visitFloatingConst(ModelParser.FloatingConstContext ctx) {
        String value = String.valueOf(Float.parseFloat(ctx.getText()));
        Symbol integer = numberTable.getSymbolOrCreate(value);
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


    private boolean isCompoundMagicNumber(ModelParser.CallContext ctx){
        CompoundMagicNumberVisitor visitor = new CompoundMagicNumberVisitor();

        return visitor.visitExprList(ctx.exprList());
    }



    @Override
    public Object visitCall(ModelParser.CallContext ctx) {
        String functionName = getFunctionName(ctx);

        if ("WITH LOOKUP".equals(functionName)) {
            return visit(ctx.exprList().expr(0));
        }
        if (Constants.FUNCTION_IS_COMPOUND_MAGIC_NUMBER.contains(functionName)){
            if(isCompoundMagicNumber(ctx)){
                Symbol integer = numberTable.getSymbolOrCreate(ctx.getText().trim());
                integer.addDefinitionLine(ctx.start.getLine());
                return null;
            }
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


    @Override
    public Object visitLookupDefinition(ModelParser.LookupDefinitionContext ctx) {
        if(ctx.call()!=null)
            super.visitCall(ctx.call());
        return null;
    }

    @Override
    public Object visitConstList(ModelParser.ConstListContext ctx) {
        return null;
    }

}
