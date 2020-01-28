package es.uva.medeas.parser.visitors;

import es.uva.medeas.parser.*;
import es.uva.medeas.plugin.VensimVisitorContext;


import static es.uva.medeas.utilities.UtilityFunctions.stringToFloat;
import static es.uva.medeas.utilities.UtilityFunctions.stringToInt;

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

    private boolean exprIsACompoundNumber(ModelParser.ExprContext ctx){
        if(ctx.getClass()!= ModelParser.SignExprContext.class)
            return false;


        ModelParser.SignExprContext node = (ModelParser.SignExprContext) ctx;
        if( node.exprAllowSign().getClass() != ModelParser.CallExprContext.class)
            return false;


       ModelParser.CallExprContext callNode = (ModelParser.CallExprContext) node.exprAllowSign();
        
       return isCompoundNumber(callNode.call());
    }

    private boolean exprIsANumber(ModelParser.ExprContext ctx){
        return exprIsAConstant(ctx) || exprIsACompoundNumber(ctx);
    }

    private Symbol getSymbolOrCreate(SymbolTable table,String token){
        if(table.hasSymbol(token))
            return table.getSymbol(token);

        else{
            return table.addSymbol(new Symbol(token));
        }
    }
    @Override
    public Object visitEquation(ModelParser.EquationContext ctx) {

        if(ctx.expr()==null || exprIsANumber(ctx.expr()))
            return null;


        return super.visit(ctx.expr());

    }

    @Override
    public Object visitDataEquation(ModelParser.DataEquationContext ctx) {

        if(ctx.expr()==null || exprIsANumber(ctx.expr()))
            return null;

            return super.visit(ctx.expr());


    }

    @Override
    public Object visitUnchangeableConstant(ModelParser.UnchangeableConstantContext ctx) {
        return null;
    }

    @Override
    public Object visitLookup(ModelParser.LookupContext ctx) {
        return null;
    }





    @Override
    public Object visitIntegerConst(ModelParser.IntegerConstContext ctx) {
        String value = String.valueOf(stringToInt(ctx.getText()));
        Symbol integer = getSymbolOrCreate(numberTable,value);
        integer.addDefinitionLine(ctx.start.getLine());
        return null;
    }


    @Override
    public Object visitFloatingConst(ModelParser.FloatingConstContext ctx) {
        String value = String.valueOf(stringToFloat(ctx.getText()));
        Symbol integer = getSymbolOrCreate(numberTable,value);
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


    private boolean isCompoundNumber(ModelParser.CallContext ctx){
        CompoundMagicNumberVisitor visitor = new CompoundMagicNumberVisitor();
        return visitor.visitCall(ctx);


    }



    @Override
    public Object visitCall(ModelParser.CallContext ctx) {
        String functionName = getFunctionName(ctx);

        if ("WITH LOOKUP".equals(functionName)) {
            return visit(ctx.exprList().expr(0));
        }


        if(isCompoundNumber(ctx)){
            Symbol integer = getSymbolOrCreate(numberTable,ctx.getText().trim());
            integer.addDefinitionLine(ctx.start.getLine());
            return null;
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
