package es.uva.medeas.parser.visitors;

import es.uva.medeas.parser.*;


import static es.uva.medeas.utilities.UtilityFunctions.stringToFloat;
import static es.uva.medeas.utilities.UtilityFunctions.stringToInt;

public class MagicNumberTableVisitor  extends ModelBaseVisitor<Void> {


    private SymbolTable numberTable;



    public MagicNumberTableVisitor(){
        numberTable = new SymbolTable();
    }




    public  SymbolTable getSymbolTable(ModelParser.FileContext context){
        numberTable = new SymbolTable();
        visit(context);
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
    public Void visitEquation(ModelParser.EquationContext ctx) {

        if(ctx.expr()==null || exprIsANumber(ctx.expr()))
            return null;


        return super.visit(ctx.expr());

    }

    @Override
    public Void visitDataEquation(ModelParser.DataEquationContext ctx) {

        if(ctx.expr()==null || exprIsANumber(ctx.expr()))
            return null;

            return super.visit(ctx.expr());


    }

    @Override
    public Void visitUnchangeableConstant(ModelParser.UnchangeableConstantContext ctx) {
        return null;
    }

    @Override
    public Void visitLookup(ModelParser.LookupContext ctx) {
        return null;
    }





    @Override
    public Void visitIntegerConst(ModelParser.IntegerConstContext ctx) {
        String value = String.valueOf(stringToInt(ctx.getText()));
        Symbol integer = getSymbolOrCreate(numberTable,value);
        integer.addDefinitionLine(ctx.start.getLine());
        return null;
    }


    @Override
    public Void visitFloatingConst(ModelParser.FloatingConstContext ctx) {
        float value = stringToFloat(ctx.getText());
        String strValue;
        if(isInteger(value)){
            int intValue = (int) value;
            strValue = String.valueOf(intValue);
        }else{
            strValue = String.valueOf(value);
        }

        Symbol floatSymbol = getSymbolOrCreate(numberTable,strValue);
        floatSymbol.addDefinitionLine(ctx.start.getLine());
        return null;
    }

    private boolean isInteger(float value) {
        return (int) value == value;
    }

    @Override
    public Void visitTabbedArray(ModelParser.TabbedArrayContext ctx) {
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
    public Void visitCall(ModelParser.CallContext ctx) {
        String functionName = getFunctionName(ctx);

        if ("WITH Lookup_Table".equals(functionName)) {
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
    public Void visitRealityCheck(ModelParser.RealityCheckContext ctx) {
        return null;
    }

    @Override
    public Void visitConstraint(ModelParser.ConstraintContext ctx) {
        return null;
    }


    @Override
    public Void visitLookupDefinition(ModelParser.LookupDefinitionContext ctx) {
        if(ctx.call()!=null)
            super.visitCall(ctx.call());
        return null;
    }

    @Override
    public Void visitConstList(ModelParser.ConstListContext ctx) {
        return null;
    }


    private boolean isASwitch(String symbol){
        return symbol.matches("SWITCH_.+");
    }

    @Override
    public Void visitExprOperation(ModelParser.ExprOperationContext ctx) {
        if(ctx.op.getType() == ModelLexer.Equal){
            if(!isASwitch(ctx.expr(0).getText()))
                visit(ctx.expr(1));
            if(!isASwitch(ctx.expr(1).getText()))
                visit(ctx.expr(0));

        }else{
            super.visitExprOperation(ctx);
        }

        return null;
    }
}
