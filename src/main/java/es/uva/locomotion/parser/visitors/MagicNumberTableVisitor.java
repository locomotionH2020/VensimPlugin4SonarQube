package es.uva.locomotion.parser.visitors;

import es.uva.locomotion.model.Symbol;
import es.uva.locomotion.model.SymbolTable;
import es.uva.locomotion.parser.*;


import static es.uva.locomotion.utilities.UtilityFunctions.stringToFloat;
import static es.uva.locomotion.utilities.UtilityFunctions.stringToInt;

public class MagicNumberTableVisitor  extends ModelBaseVisitor<Void> {


    private SymbolTable numberTable;



    public MagicNumberTableVisitor(){
        numberTable = new SymbolTable();
    }




    public  SymbolTable getSymbolTable(Model.FileContext context){
        numberTable = new SymbolTable();
        visit(context);
        return numberTable;
    }


    private boolean exprIsAConstant(Model.ExprContext ctx){
        return  ctx.getClass() == Model.ConstContext.class;
    }

    private boolean exprIsACompoundNumber(Model.ExprContext ctx){
        if(ctx.getClass()!= Model.SignExprContext.class)
            return false;


        Model.SignExprContext node = (Model.SignExprContext) ctx;
        if( node.exprAllowSign().getClass() != Model.CallExprContext.class)
            return false;


       Model.CallExprContext callNode = (Model.CallExprContext) node.exprAllowSign();
        
       return isCompoundNumber(callNode.call());
    }

    private boolean exprIsANumber(Model.ExprContext ctx){
        return exprIsAConstant(ctx) || exprIsACompoundNumber(ctx);
    }

    private Symbol getSymbolOrCreate(SymbolTable table, String token){
        if(table.hasSymbol(token))
            return table.getSymbol(token);

        else{
            return table.addSymbol(new Symbol(token));
        }
    }
    @Override
    public Void visitEquation(Model.EquationContext ctx) {

        if(ctx.expr()==null || exprIsANumber(ctx.expr()))
            return null;


        return super.visit(ctx.expr());

    }

    @Override
    public Void visitDataEquation(Model.DataEquationContext ctx) {

        if(ctx.expr()==null || exprIsANumber(ctx.expr()))
            return null;

            return super.visit(ctx.expr());


    }

    @Override
    public Void visitUnchangeableConstant(Model.UnchangeableConstantContext ctx) {
        return null;
    }

    @Override
    public Void visitLookup(Model.LookupContext ctx) {
        return null;
    }





    @Override
    public Void visitIntegerConst(Model.IntegerConstContext ctx) {
        String value = String.valueOf(stringToInt(ctx.getText()));
        Symbol integer = getSymbolOrCreate(numberTable,value);
        integer.addDefinitionLine(ctx.start.getLine());
        return null;
    }


    @Override
    public Void visitFloatingConst(Model.FloatingConstContext ctx) {
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
    public Void visitTabbedArray(Model.TabbedArrayContext ctx) {
        return null;
    }


    private String getFunctionName(Model.CallContext ctx){
        return ctx.Id().getText();
    }


    private boolean isCompoundNumber(Model.CallContext ctx){
        CompoundMagicNumberVisitor visitor = new CompoundMagicNumberVisitor();
        return visitor.visitCall(ctx);


    }



    @Override
    public Void visitCall(Model.CallContext ctx) {
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
    public Void visitRealityCheck(Model.RealityCheckContext ctx) {
        return null;
    }

    @Override
    public Void visitConstraint(Model.ConstraintContext ctx) {
        return null;
    }

    @Override
    public Void visitSketchesGraphsAndMetadata(Model.SketchesGraphsAndMetadataContext ctx) {
        return null;
    }


    @Override
    public Void visitLookupDefinition(Model.LookupDefinitionContext ctx) {
        if(ctx.call()!=null)
            super.visitCall(ctx.call());
        return null;
    }

    @Override
    public Void visitConstList(Model.ConstListContext ctx) {
        return null;
    }


    private boolean isASwitch(String symbol){
        return symbol.matches("SWITCH_.+");
    }

    @Override
    public Void visitExprOperation(Model.ExprOperationContext ctx) {
        if(ctx.op.getType() == Tokens.Equal)
            if(isASwitch(ctx.expr(0).getText()) || isASwitch(ctx.expr(1).getText()) )
              return null;

        return super.visitExprOperation(ctx);

    }
}
