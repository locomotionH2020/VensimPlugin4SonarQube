package es.uva.medeas.rules;

import es.uva.medeas.VensimVisitorContext;
import es.uva.medeas.parser.ModelParser;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;

public class TableGeneratorVisitor extends VensimCheck {


    private SymbolTable table;
    private Symbol currentDefinition;

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
        currentDefinition = symbol;

        return super.visitSubscriptRange(ctx);
    }

    @Override
    public Object visitEquation(ModelParser.EquationContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.lhs().Id().getSymbol());
        symbol.setContext(ctx);
        currentDefinition = symbol;

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
        currentDefinition = symbol;

        return super.visitDataEquation(ctx);
    }

    @Override
    public Object visitLookupDefinition(ModelParser.LookupDefinitionContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.lhs().Id().getSymbol());
        symbol.setContext(ctx);
        symbol.setType(SymbolType.LOOKUP);
        currentDefinition = symbol;

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
    public Object visitVar(ModelParser.VarContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.Id().getSymbol());
        currentDefinition.addDependency(symbol);
        return super.visitVar(ctx);
    }

    @Override
    public Object visitSubscriptId(ModelParser.SubscriptIdContext ctx) {
        if (currentDefinition.getType()==SymbolType.SUBSCRIPT_NAME) {
            Symbol symbol = table.getSymbolOrCreate(ctx.Id().getSymbol());
            symbol.setType(SymbolType.SUBSCRIPT_VALUE);
            symbol.setContext(ctx);
        }
        return super.visitSubscriptId(ctx);
    }

    @Override
    public Object visitSubscriptSequence(ModelParser.SubscriptSequenceContext ctx) {
        Symbol firstSymbol = table.getSymbolOrCreate(ctx.Id(0).getSymbol());
        Symbol secondSymbol = table.getSymbolOrCreate(ctx.Id(1).getSymbol());

        firstSymbol.setType(SymbolType.SUBSCRIPT_VALUE);
        firstSymbol.setContext(ctx);
        secondSymbol.setType(SymbolType.SUBSCRIPT_VALUE);
        secondSymbol.setContext(ctx);
        return super.visitSubscriptSequence(ctx);
    }


    @Override
    public Object visitCall(ModelParser.CallContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.Id().getSymbol());
        symbol.setType(SymbolType.FUNCTION);
        currentDefinition.addDependency(symbol);
        return super.visitCall(ctx);
    }

    @Override
    public Object visitLookupCall(ModelParser.LookupCallContext ctx) {
        Symbol symbol = table.getSymbolOrCreate(ctx.Id().getSymbol());
        symbol.setType(SymbolType.FUNCTION);
        currentDefinition.addDependency(symbol);
        return super.visitLookupCall(ctx);
    }


}
