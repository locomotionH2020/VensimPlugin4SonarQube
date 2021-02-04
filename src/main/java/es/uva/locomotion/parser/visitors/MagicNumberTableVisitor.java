package es.uva.locomotion.parser.visitors;

import es.uva.locomotion.model.Symbol;
import es.uva.locomotion.model.SymbolTable;
import es.uva.locomotion.parser.*;
import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;


import static es.uva.locomotion.utilities.UtilityFunctions.stringToFloat;
import static es.uva.locomotion.utilities.UtilityFunctions.stringToInt;

public class MagicNumberTableVisitor extends ModelParserBaseVisitor<Void> {

    protected static VensimLogger LOG = VensimLogger.getInstance();

    private SymbolTable numberTable;

    private SymbolTable symbols;
    private boolean isSymbolFiltered;

    public MagicNumberTableVisitor() {
        numberTable = new SymbolTable();
        isSymbolFiltered = false;
    }

    public void setSymbols(SymbolTable symbols) {
        this.symbols = symbols;
    }

    @Override
    public Void visitLhs(ModelParser.LhsContext ctx) {

        if (symbols == null) {
            LOG.unique("Symbol table unassigned in MagicNumberVisitor", LoggingLevel.INFO);
            return null;
        }

        if (!symbols.hasSymbol(ctx.Id().getText())) {
            LOG.error("Found symbol \"" + ctx.Id().getText() + "\" that is not in the symbol table");
            return null;
        }
        Symbol symbol = symbols.getSymbol(ctx.Id().getText());
        isSymbolFiltered = symbol.isFiltered();

        return null;

    }

    public SymbolTable getSymbolTable(ModelParser.FileContext context) {
        numberTable = new SymbolTable();
        visit(context);
        return numberTable;
    }


    private boolean exprIsAConstant(ModelParser.ExprContext ctx) {
        return ctx.getClass() == ModelParser.ConstContext.class;
    }

    private boolean exprIsACompoundNumber(ModelParser.ExprContext ctx) {
        if (ctx.getClass() != ModelParser.SignExprContext.class)
            return false;


        ModelParser.SignExprContext node = (ModelParser.SignExprContext) ctx;
        if (node.exprAllowSign().getClass() != ModelParser.CallExprContext.class)
            return false;


        ModelParser.CallExprContext callNode = (ModelParser.CallExprContext) node.exprAllowSign();

        return isCompoundNumber(callNode.call());
    }

    private boolean exprIsANumber(ModelParser.ExprContext ctx) {
        return exprIsAConstant(ctx) || exprIsACompoundNumber(ctx);
    }

    private Symbol getSymbolOrCreate(SymbolTable table, String token) {
        if (table.hasSymbol(token))
            return table.getSymbol(token);

        else {
            return table.addSymbol(new Symbol(token));
        }
    }

    @Override
    public Void visitEquation(ModelParser.EquationContext ctx) {
        if (ctx.expr() == null || exprIsANumber(ctx.expr()))
            return null;


        return super.visitChildren(ctx);

    }

    @Override
    public Void visitDataEquation(ModelParser.DataEquationContext ctx) {

        if (ctx.expr() == null || exprIsANumber(ctx.expr()))
            return null;

        return super.visitChildren(ctx);


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

        if (!isSymbolFiltered) {
            String value = String.valueOf(stringToInt(ctx.getText()));
            Symbol integer = getSymbolOrCreate(numberTable, value);
            integer.addDefinitionLine(ctx.start.getLine());
        }
        return null;
    }


    @Override
    public Void visitFloatingConst(ModelParser.FloatingConstContext ctx) {
        float value = stringToFloat(ctx.getText());
        String strValue;
        if (isInteger(value)) {
            int intValue = (int) value;
            strValue = String.valueOf(intValue);
        } else {
            strValue = String.valueOf(value);
        }
        if (!isSymbolFiltered) {
            Symbol floatSymbol = getSymbolOrCreate(numberTable, strValue);
            floatSymbol.addDefinitionLine(ctx.start.getLine());
        }
        return null;
    }

    private boolean isInteger(float value) {
        return (int) value == value;
    }

    @Override
    public Void visitTabbedArray(ModelParser.TabbedArrayContext ctx) {
        return null;
    }


    private String getFunctionName(ModelParser.CallContext ctx) {
        return ctx.Id().getText();
    }


    private boolean isCompoundNumber(ModelParser.CallContext ctx) {
        CompoundMagicNumberVisitor visitor = new CompoundMagicNumberVisitor();
        return visitor.visitCall(ctx);


    }


    @Override
    public Void visitCall(ModelParser.CallContext ctx) {
        String functionName = getFunctionName(ctx);

        if ("WITH LOOKUP".equals(functionName)) {
            return visit(ctx.exprList().expr(0));
        }


        if (isCompoundNumber(ctx) && !isSymbolFiltered) {
            Symbol integer = getSymbolOrCreate(numberTable, ctx.getText().trim());
            integer.addDefinitionLine(ctx.start.getLine());
            return null;
        }


        return super.visitChildren(ctx);
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
    public Void visitSketchesGraphsAndMetadata(ModelParser.SketchesGraphsAndMetadataContext ctx) {
        return null;
    }


    @Override
    public Void visitLookupDefinition(ModelParser.LookupDefinitionContext ctx) {
        if (ctx.call() != null)
            super.visitChildren(ctx);
        return null;
    }

    @Override
    public Void visitConstList(ModelParser.ConstListContext ctx) {
        return null;
    }


    private boolean isASwitch(String symbol) {
        return symbol.matches("SWITCH_.+");
    }

    @Override
    public Void visitExprOperation(ModelParser.ExprOperationContext ctx) {
        if (ctx.op.getType() == ModelLexer.Equal)
            if (isASwitch(ctx.expr(0).getText()) || isASwitch(ctx.expr(1).getText()))
                return null;

        return super.visitChildren(ctx);

    }
}
