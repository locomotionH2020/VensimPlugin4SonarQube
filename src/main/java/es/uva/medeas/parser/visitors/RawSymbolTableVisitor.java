package es.uva.medeas.parser.visitors;

import es.uva.medeas.plugin.VensimVisitorContext;
import es.uva.medeas.parser.*;

import org.antlr.v4.runtime.ParserRuleContext;



import java.util.*;


public class RawSymbolTableVisitor extends ModelBaseVisitor {
    //TODO add javadocs for RawSymbolTableVisitor y SymbolTableGenerator

    private SymbolTable table;

    public SymbolTable getSymbolTable(VensimVisitorContext context){
        table = new SymbolTable();
        visit(context.getRootNode());

        return table;
    }


    private int getStartLine(ParserRuleContext context){
        return context.start.getLine();
    }

    @Override
    public Object visitSubscriptRange(ModelParser.SubscriptRangeContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.Id().getSymbol());
        symbol.addDefinitionLine(getStartLine(ctx));
        symbol.setType(SymbolType.SUBSCRIPT_NAME);


        if(ctx.subscriptIdList()!=null) {
            for(ModelParser.SubscriptIdContext value:ctx.subscriptIdList().subscriptId()){
                Symbol subscriptValue = visitSubscriptId(value);
                subscriptValue.setType(SymbolType.SUBSCRIPT_VALUE);
                subscriptValue.addDefinitionLine(getStartLine(value));

            }
            for(ModelParser.SubscriptSequenceContext sequence:ctx.subscriptIdList().subscriptSequence())
                visitSubscriptSequence(sequence);
        }
        if(ctx.call()!=null)
            symbol.addDependencies(visitCall(ctx.call()));

        return symbol;
    }


    @Override
    public Object visitEquation(ModelParser.EquationContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.lhs().Id().getSymbol());
        symbol.addDefinitionLine(getStartLine(ctx));


        if(ctx.expr()!=null)
            symbol.addDependencies( (List<Symbol>) visit(ctx.expr()));

        return symbol;
    }


    @Override
    public Object visitConstraint(ModelParser.ConstraintContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.Id().getSymbol());
        symbol.setType(SymbolType.REALITY_CHECK);
        symbol.addDefinitionLine(getStartLine(ctx));

        return null;
    }

    @Override
    public Object visitMacroDefinition(ModelParser.MacroDefinitionContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.macroHeader().Id().getSymbol());
        symbol.setType(SymbolType.FUNCTION);
        symbol.addDefinitionLine(getStartLine(ctx));

        return super.visitMacroDefinition(ctx);
    }

    @Override
    public Object visitUnchangeableConstant(ModelParser.UnchangeableConstantContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.lhs().Id().getSymbol());
        symbol.setType(SymbolType.CONSTANT);
        symbol.addDefinitionLine(getStartLine(ctx));
        return null;
    }

    @Override
    public Object visitDataEquation(ModelParser.DataEquationContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.lhs().Id().getSymbol());
        symbol.addDefinitionLine(getStartLine(ctx));

        if(ctx.expr()!=null)
            symbol.addDependencies( (List<Symbol>) visit(ctx.expr()));

        return symbol;
    }

    @Override
    public Object visitLookupDefinition(ModelParser.LookupDefinitionContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.lhs().Id().getSymbol());
        symbol.addDefinitionLine(getStartLine(ctx));
        symbol.setType(SymbolType.LOOKUP);

        if (ctx.lookup()!=null)
            symbol.addDependencies(  visitLookup(ctx.lookup()));
        else if (ctx.call()!=null)
            symbol.addDependencies(visitCall(ctx.call()));


        return symbol;
    }

    @Override
    public Object visitStringAssign(ModelParser.StringAssignContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.lhs().Id().getSymbol());
        symbol.setType(SymbolType.CONSTANT);
        symbol.addDefinitionLine(getStartLine(ctx));

        return symbol;
    }


    @Override
    public Object visitSubscriptCopy(ModelParser.SubscriptCopyContext ctx) {

        Symbol symbol = table.getSymbolOrCreate(ctx.Id(0).getSymbol());
        symbol.setType(SymbolType.SUBSCRIPT_NAME);
        symbol.addDefinitionLine(getStartLine(ctx));

        return symbol;
    }

    @Override
    public Object visitRealityCheck(ModelParser.RealityCheckContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.Id().getSymbol());
        symbol.setType(SymbolType.REALITY_CHECK);
        symbol.addDefinitionLine(getStartLine(ctx));

        return symbol;

    }



    @Override
    public Symbol visitSubscriptId(ModelParser.SubscriptIdContext ctx) {
        return table.getSymbolOrCreate(ctx.Id().getSymbol());
    }

    @Override
    public List<Symbol> visitSubscriptSequence(ModelParser.SubscriptSequenceContext ctx) {
        Symbol firstSymbol = table.getSymbolOrCreate(ctx.Id(0).getSymbol());
        Symbol secondSymbol = table.getSymbolOrCreate(ctx.Id(1).getSymbol());

        firstSymbol.setType(SymbolType.SUBSCRIPT_VALUE);
        firstSymbol.addDefinitionLine(getStartLine(ctx));
        secondSymbol.setType(SymbolType.SUBSCRIPT_VALUE);
        secondSymbol.addDefinitionLine(getStartLine(ctx));

        return Arrays.asList(firstSymbol,secondSymbol);

    }








    @Override
    public List<Symbol> visitExprOperation(ModelParser.ExprOperationContext ctx) {
        List<Symbol> symbols = (List<Symbol>) visit(ctx.expr(0));
        symbols.addAll((List<Symbol>) visit(ctx.expr(1)));

        return  symbols;
    }



    @Override
    public Object visitVar(ModelParser.VarContext ctx) {
        Symbol id = table.getSymbolOrCreate(ctx.Id().getSymbol());

        List<Symbol> symbols = new ArrayList<>();
        symbols.add(id);

        return symbols;
    }

    @Override
    public Object visitConst(ModelParser.ConstContext ctx) {
        return new ArrayList<Symbol>();
    }

    @Override
    public List<Symbol> visitKeyword(ModelParser.KeywordContext ctx) {
        if (ctx.expr()!=null)
            return (List<Symbol>) visit(ctx.expr());
        else
            return new ArrayList<>();
    }


    @Override
    public Object visitParens(ModelParser.ParensContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Object visitWildCard(ModelParser.WildCardContext ctx) {
        return new ArrayList<Symbol>();
    }

    @Override
    public Object visitDelayPArg(ModelParser.DelayPArgContext ctx) {
        List<Symbol> symbols = new ArrayList<>();
        Symbol delayP = table.getSymbolOrCreate("DELAYP");
        List<Symbol> input = (List<Symbol>) visit(ctx.expr(0));
        List<Symbol> delayTime = (List<Symbol>) visit(ctx.expr(1));
        Symbol pipeline = table.getSymbolOrCreate(ctx.Id().getSymbol());
        pipeline.setType(SymbolType.VARIABLE);
        pipeline.addDefinitionLine(ctx.Id().getSymbol().getLine());


        symbols.add(delayP);
        symbols.addAll(input);
        symbols.addAll(delayTime);

        pipeline.addDependencies(symbols);

        symbols.add(pipeline);
        return  symbols;
    }

    @Override
    public Object visitTabbedArray(ModelParser.TabbedArrayContext ctx) {
        List<Symbol> symbols = new ArrayList<>();
        Symbol tabbedFunc = table.getSymbolOrCreate("TABBED ARRAY");

        symbols.add(tabbedFunc);
        return symbols;
    }

    @Override
    public Object visitSignExpr(ModelParser.SignExprContext ctx) {
        return visit(ctx.exprAllowSign());

    }


    @Override
    public List<Symbol> visitCall(ModelParser.CallContext ctx) {
        String token = ctx.Id().getSymbol().getText();
        Symbol call;

        if (table.hasSymbol(token))
            call = table.getSymbol(token);
        else{
            call = table.createSymbol(token);
            call.setType(SymbolType.UNDETERMINED_FUNCTION);
        }


        List<Symbol> symbols;
        if(ctx.exprList()!=null)
             symbols =  (List<Symbol>) visit(ctx.exprList());
        else
            symbols = new ArrayList<>();

        symbols.add(call);
        return symbols;
    }



    @Override
    public List<Symbol> visitSubscript(ModelParser.SubscriptContext ctx) {
        return visitSubscriptIdList(ctx.subscriptIdList());
    }


    @Override
    public Object visitExprList(ModelParser.ExprListContext ctx) {
        List<Symbol> symbols = new ArrayList<>();
        for(ModelParser.ExprContext expr: ctx.expr()){
            symbols.addAll((List<Symbol>)visit(expr));
        }
        return symbols;
    }

    @Override
    public List<Symbol> visitSubscriptIdList(ModelParser.SubscriptIdListContext ctx) {
        List<Symbol> symbols = new ArrayList<>();


        if(!ctx.subscriptId().isEmpty()){
            for(ModelParser.SubscriptIdContext subscript: ctx.subscriptId()){
                symbols.add( visitSubscriptId(subscript));
            }
        }

        if(!ctx.subscriptSequence().isEmpty()){
            for(ModelParser.SubscriptSequenceContext sequence: ctx.subscriptSequence()){
                symbols.addAll(visitSubscriptSequence(sequence));
            }
        }
        return symbols;
    }


    @Override
    public List<Symbol> visitLookup(ModelParser.LookupContext ctx) {
        return new ArrayList<>();
    }


    @Override
    public List<Symbol> visitLookupPoint(ModelParser.LookupPointContext ctx) {

        return  new ArrayList<>();
    }

    @Override
    public List<Symbol> visitConstList(ModelParser.ConstListContext ctx) {
        return new ArrayList<>();
    }

    @Override
    public List<Symbol> visitLookupPointList(ModelParser.LookupPointListContext ctx) {
        List<Symbol> symbols = new ArrayList<>();


        for(ModelParser.LookupPointContext point:ctx.lookupPoint()){
            symbols.addAll(visitLookupPoint(point));
        }



        return symbols;
    }

    @Override
    public List<Symbol> visitLookupRange(ModelParser.LookupRangeContext ctx) {
        List<Symbol> symbols = new ArrayList<>();

        symbols.addAll(visitLookupPoint(ctx.lookupPoint(0)));
        symbols.addAll(visitLookupPoint(ctx.lookupPoint(1)));

        if (ctx.referenceLine()!=null)
            symbols.addAll(visitReferenceLine(ctx.referenceLine()));

       return symbols;
    }


    @Override
    public List<Symbol> visitReferenceLine(ModelParser.ReferenceLineContext ctx) {
        return visitLookupPointList(ctx.lookupPointList());
    }

    @Override
    public List<Symbol> visitNumberList(ModelParser.NumberListContext ctx) {
        return new ArrayList<>();
    }

}
