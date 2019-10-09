package es.uva.medeas.rules;

import es.uva.medeas.VensimVisitorContext;
import es.uva.medeas.parser.ModelParser;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableGeneratorVisitor extends VensimCheck {


    private SymbolTable table;


    public SymbolTable getSymbolTable(VensimVisitorContext context){
        table = new SymbolTable();

        visit(context.getRootNode());

        return table;
    }

    @Override
    public Object visitSubscriptRange(ModelParser.SubscriptRangeContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.Id().getSymbol());
        symbol.setContext(ctx);
        symbol.setType(SymbolType.SUBSCRIPT_NAME);


        if(ctx.subscriptIdList()!=null)
            symbol.addDependencies(visitSubscriptIdList(ctx.subscriptIdList()));
        if(ctx.call()!=null)
            symbol.addDependencies(visitCall(ctx.call()));

        return super.visitSubscriptRange(ctx);
    }

    //TODO Mapping list

    @Override
    public Object visitEquation(ModelParser.EquationContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.lhs().Id().getSymbol());
        symbol.setContext(ctx);

        if(ctx.expr()!=null)
            symbol.addDependencies( (List<Symbol>) visit(ctx.expr()));

        return super.visitEquation(ctx);
    }


    @Override
    public Object visitConstraint(ModelParser.ConstraintContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.Id().getSymbol());
        symbol.setType(SymbolType.REALITY_CHECK);
        symbol.setContext(ctx);
        // return super.visitConstraint(ctx); TODO Creo que esto no es necesario, pero comprobarlo por si acaso.
        return null;
    }

    @Override
    public Object visitMacroDefinition(ModelParser.MacroDefinitionContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.macroHeader().Id().getSymbol());
        symbol.setType(SymbolType.FUNCTION);
        symbol.setContext(ctx);
        // return super.visitMacroDefinition(ctx); TODO Creo que esto no es necesario, pero comprobarlo por si acaso.
        return null;
    }

    @Override
    public Object visitUnchangeableConstant(ModelParser.UnchangeableConstantContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.lhs().Id().getSymbol());
        symbol.setType(SymbolType.CONSTANT);
        symbol.setContext(ctx);
        //return super.visitUnchangeableConstant(ctx); TODO Creo que esto no es necesario, pero comprobarlo por si acaso.
        // Es posible que dentro de la unchangeable constant haya funciones, pero por definicion ya se sabe que es una constante,
        // asi que no hay por que dejarlo como dependencia?
        return null;
    }

    @Override
    public Object visitDataEquation(ModelParser.DataEquationContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.lhs().Id().getSymbol());
        symbol.setContext(ctx);

        if(ctx.expr()!=null)
            symbol.addDependencies( (List<Symbol>) visit(ctx.expr()));


        return super.visitDataEquation(ctx);
    }

    @Override
    public Object visitLookupDefinition(ModelParser.LookupDefinitionContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.lhs().Id().getSymbol());
        symbol.setContext(ctx);
        symbol.setType(SymbolType.LOOKUP);

        symbol.addDependencies( (List<Symbol>) visit(ctx.lookup()));


        return super.visitLookupDefinition(ctx);
    }

    @Override
    public Object visitStringAssign(ModelParser.StringAssignContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.lhs().Id().getSymbol());
        symbol.setType(SymbolType.CONSTANT);
        symbol.setContext(ctx);
        //return super.visitStringAssign(ctx); TODO Buscar contraejemplo en el que esto sea necesario
        return null;
    }


    @Override
    public Object visitSubscriptCopy(ModelParser.SubscriptCopyContext ctx) {
        //TODO en todos los ejemplos el elemento nuevo se pone primero, pero no se si se puede hacer al revés.
        Symbol symbol = table.getSymbolOrCreate(ctx.Id(0).getSymbol());
        symbol.setType(SymbolType.SUBSCRIPT_NAME);
        symbol.setContext(ctx);
        return super.visitSubscriptCopy(ctx);
    }

    @Override
    public Object visitRealityCheck(ModelParser.RealityCheckContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.Id(0).getSymbol());
        symbol.setType(SymbolType.REALITY_CHECK);
        symbol.setContext(ctx);
        return null;
       // return super.visitRealityCheck(ctx); TODO Creo que esto no es necesario, pero comprobarlo por si acaso.
    }



    @Override
    public Symbol visitSubscriptId(ModelParser.SubscriptIdContext ctx) {

        Symbol symbol = table.getSymbolOrCreate(ctx.Id().getSymbol());
        symbol.setType(SymbolType.SUBSCRIPT_VALUE);
        symbol.setContext(ctx);

        return symbol; //TODO: ESto no es del todo correcto, debería de cambiarlo. Los subscript ids son tanto el nombre del enum como el valor
                        // Y tengo que testearlo porque eso no lo estaba comprobando
    }

    @Override
    public List<Symbol> visitSubscriptSequence(ModelParser.SubscriptSequenceContext ctx) {
        Symbol firstSymbol = table.getSymbolOrCreate(ctx.Id(0).getSymbol());
        Symbol secondSymbol = table.getSymbolOrCreate(ctx.Id(1).getSymbol());

        firstSymbol.setType(SymbolType.SUBSCRIPT_VALUE);
        firstSymbol.setContext(ctx);
        secondSymbol.setType(SymbolType.SUBSCRIPT_VALUE);
        secondSymbol.setContext(ctx);

        return Arrays.asList(firstSymbol,secondSymbol);

    }





    @Override
    public Object visitCallExpr(ModelParser.CallExprContext ctx) {
        return super.visitCallExpr(ctx); //TODO
    }


    @Override
    public Object visitPower(ModelParser.PowerContext ctx) {
        List<Symbol> symbols = (List<Symbol>) visit(ctx.expr(0));
        symbols.addAll((List<Symbol>) visit(ctx.expr(1)));

        return  symbols;
    }

    @Override
    public Object visitMulDiv(ModelParser.MulDivContext ctx) {
        List<Symbol> symbols = (List<Symbol>) visit(ctx.expr(0));
        symbols.addAll((List<Symbol>) visit(ctx.expr(1)));

        return  symbols;
    }


    @Override
    public Object visitAddSub(ModelParser.AddSubContext ctx) {
        List<Symbol> symbols = (List<Symbol>) visit(ctx.expr(0));
        symbols.addAll((List<Symbol>) visit(ctx.expr(1)));

        return  symbols;
    }

    @Override
    public Object visitRelational(ModelParser.RelationalContext ctx) {
        List<Symbol> symbols = (List<Symbol>) visit(ctx.expr(0));
        symbols.addAll((List<Symbol>) visit(ctx.expr(1)));

        return  symbols;
    }

    @Override
    public Object visitEquality(ModelParser.EqualityContext ctx) {
        List<Symbol> symbols = (List<Symbol>) visit(ctx.expr(0));
        symbols.addAll((List<Symbol>) visit(ctx.expr(1)));

        return  symbols;
    }

    @Override
    public Object visitAnd(ModelParser.AndContext ctx) {
        List<Symbol> symbols = (List<Symbol>) visit(ctx.expr(0));
        symbols.addAll((List<Symbol>) visit(ctx.expr(1)));

        return  symbols;
    }

    @Override
    public Object visitOr(ModelParser.OrContext ctx) {
        List<Symbol> symbols = (List<Symbol>) visit(ctx.expr(0));
        symbols.addAll((List<Symbol>) visit(ctx.expr(1)));

        return  symbols;
    }

    @Override
    public Object visitVar(ModelParser.VarContext ctx) {
        Symbol id = table.getSymbolOrCreate(ctx.Id().getSymbol());

        List<Symbol> symbols = null;
        if(ctx.subscript()!=null)
             symbols = (List<Symbol>)  visit(ctx.subscript());
        else
            symbols = new ArrayList<>();

        symbols.add(id);
        return symbols;
    }

    @Override
    public Object visitConst(ModelParser.ConstContext ctx) {
        return new ArrayList<Symbol>();
    }

    @Override
    public Object visitKeyword(ModelParser.KeywordContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Object visitLookupArg(ModelParser.LookupArgContext ctx) {
        return super.visitLookupArg(ctx); //TODO revisar
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
        List<Symbol> symbols = (List<Symbol>) visit(ctx.expr(0));
        symbols.addAll((List<Symbol>) visit(ctx.expr(1)));

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
        return visit(ctx.expr());
    }


    @Override
    public List<Symbol> visitCall(ModelParser.CallContext ctx) {
        Symbol call = table.getSymbolOrCreate(ctx.Id().getSymbol());
        call.setType(SymbolType.FUNCTION);

        List<Symbol> symbols =  (List<Symbol>) visit(ctx.exprList());
        symbols.add(call);
        return symbols;
    }

    @Override
    public Object visitLookupCall(ModelParser.LookupCallContext ctx) {
        Symbol call = table.getSymbolOrCreate(ctx.Id().getSymbol());
        call.setType(SymbolType.FUNCTION);


        List<Symbol> symbols;

         symbols=  (List<Symbol>) visit(ctx.expr());



        symbols.add(call);

        if(ctx.subscript()!=null)
            symbols.addAll(visitSubscript(ctx.subscript())); //TODO implement subscript.


        return symbols;
    }

    @Override
    public List<Symbol> visitSubscript(ModelParser.SubscriptContext ctx) {
        List<Symbol> symbols = new ArrayList<>();
        for(ModelParser.SubscriptIdContext subscript: ctx.subscriptId()){
            symbols.add((Symbol) visitSubscriptId(subscript));
        }
        return symbols;
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
        //TODO testear si cuando no hay subscriptID, la lista esta vacia o si es null

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
    public Object visitLookup(ModelParser.LookupContext ctx) {
        List<Symbol> symbols;
        if(ctx.lookupPointList()!=null) {
            symbols = visitLookupPointList(ctx.lookupPointList());

            if (ctx.lookupRange() != null)
                symbols.addAll(visitLookupRange(ctx.lookupRange()));

        }else{
            symbols = visitNumberList(ctx.numberList());
        }
        return symbols;
    }


    @Override
    public List<Symbol> visitLookupPoint(ModelParser.LookupPointContext ctx) {
        List<Symbol> symbols = (List<Symbol>) visit(ctx.expr(0));

        symbols.addAll((List<Symbol>) visit(ctx.expr(1)));

        return symbols;
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
