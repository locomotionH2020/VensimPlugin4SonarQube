package es.uva.medeas.parser.visitors;

import es.uva.medeas.parser.*;
import es.uva.medeas.plugin.VensimVisitorContext;

import java.util.Arrays;
import java.util.List;

public class MagicNumberTableVisitor  extends ModelBaseVisitor {


    private SymbolTable numberTable;




        private static final List<String> IGNORED_FUNCTIONS_IF_ALONE_AND_CONSTANT = Arrays.asList("SQRT","TAN","TANH","SIN","SINH","COS",
                "COSH","ARCTAN", "ARCSIN","ARCCOS","ABS","LN","GAMMA LN","INTEGER","GAME","EXP",
                "POWER","LOG","MIN","MODULO","XIDZ","QUANTUM");

    public MagicNumberTableVisitor(){
        numberTable = new SymbolTable();
    }


    private VensimVisitorContext visitorContext;


    public  SymbolTable getSymbolTable(VensimVisitorContext context){
        numberTable = new SymbolTable();
        visitorContext = context;
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
       return IGNORED_FUNCTIONS_IF_ALONE_AND_CONSTANT.contains(callNode.call().Id().getText());
    }

    @Override
    public Object visitEquation(ModelParser.EquationContext ctx) {
        boolean isConstant = visitorContext.getSymbolTable().getSymbol(ctx.lhs().Id().getText()).getType() == SymbolType.CONSTANT;


        if(ctx.expr()==null || exprIsAConstant(ctx.expr()) || (exprIsAFunctionAndInIgnoreList(ctx.expr()) && isConstant))
            return null;
        ;

        return super.visit(ctx.expr());

    }

    @Override
    public Object visitDataEquation(ModelParser.DataEquationContext ctx) {
        if(ctx.expr()!=null && !exprIsAConstant(ctx.expr()) && !exprIsAFunctionAndInIgnoreList(ctx.expr()))
            return super.visit(ctx.expr());

        return null;
    }

    @Override
    public Object visitUnchangeableConstant(ModelParser.UnchangeableConstantContext ctx) {
        if(ctx.expr()!=null && !exprIsAConstant(ctx.expr()))
            return super.visit(ctx.expr());

        return null;
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
