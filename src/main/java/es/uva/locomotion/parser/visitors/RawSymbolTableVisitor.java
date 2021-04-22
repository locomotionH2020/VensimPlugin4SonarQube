package es.uva.locomotion.parser.visitors;

import es.uva.locomotion.model.ExcelRef;
import es.uva.locomotion.model.symbol.*;
import es.uva.locomotion.parser.ModelParser;
import es.uva.locomotion.parser.ModelParserBaseVisitor;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static es.uva.locomotion.utilities.Constants.*;


public class RawSymbolTableVisitor extends ModelParserBaseVisitor<Object> {

    public static final String REMOVE_QUOTES = "^'|'$";
    private SymbolTable table;
    protected static final VensimLogger logger = VensimLogger.getInstance();
    private static final Pattern sequencePattern = Pattern.compile("(.*?)(\\d+)");
    private static final List<String> TIME_STEP_TOKEN = List.of("TIME_STEP", "TIME STEP");
    private String actualGroup;
    private Symbol actualSymbol;
    private List<String> actualIndex;

    public SymbolTable getSymbolTable(ModelParser.FileContext context) {
        table = new SymbolTable();
        actualGroup = null; //Default group is marked as null.
        actualSymbol = null;
        actualIndex = null;
        visit(context);

        return table;
    }


    private int getStartLine(ParserRuleContext context) {
        return context.start.getLine();
    }

    private Symbol getSymbolOrCreate(SymbolTable table, String token) {
        if (table.hasSymbol(token))
            return table.getSymbol(token);

        else {
            Symbol symbol = new Symbol(token);
            return table.addSymbol(symbol);
        }
    }

    private Subscript getSubscriptOrCreate(SymbolTable table, String token, boolean isCopy) {

        if (table.hasSymbol(token)) {
            Symbol s = table.getSymbol(token);
            return (Subscript) s;
        } else {
            Subscript symbol = new Subscript(token, isCopy);
            return (Subscript) table.addSymbol(symbol);
        }
    }

    @Override
    public String visitGroup(ModelParser.GroupContext ctx) {
        actualGroup = ctx.name.getText();
        return actualGroup;
    }

    @Override
    public Subscript visitSubscriptRange(ModelParser.SubscriptRangeContext ctx) {
        Subscript subscript = getSubscriptOrCreate(table, ctx.Id().getSymbol().getText(), false);
        subscript.addLine(getStartLine(ctx));
        subscript.setType(SymbolType.SUBSCRIPT);


        if (ctx.subscriptValueList() != null) {
            List<Symbol> values = visitSubscriptValueList(ctx.subscriptValueList());
            subscript.addDependencies(values);
        }

        if (ctx.subscriptSequence() != null)
            subscript.addDependencies(visitSubscriptSequence(ctx.subscriptSequence()));


        if (ctx.call() != null)
            subscript.addDependencies(visitCall(ctx.call()));

        return subscript;
    }


    @Override
    public Symbol visitEquation(ModelParser.EquationContext ctx) {
        Symbol symbol = visitLhs(ctx.lhs());
        symbol.addLine(getStartLine(ctx));


        if (ctx.expr() != null)
            symbol.addDependencies((List<Symbol>) visit(ctx.expr()));

        return symbol;
    }


    @Override
    public Symbol visitConstraint(ModelParser.ConstraintContext ctx) {
        Symbol symbol = visitLhs(ctx.lhs());
        symbol.setType(SymbolType.REALITY_CHECK);
        symbol.addLine(getStartLine(ctx));

        return symbol;
    }

    @Override
    public Object visitMacroDefinition(ModelParser.MacroDefinitionContext ctx) {
        Symbol symbol = getSymbolOrCreate(table, ctx.macroHeader().Id().getSymbol().getText());
        symbol.setType(SymbolType.FUNCTION);
        symbol.addLine(getStartLine(ctx));

        return super.visitMacroDefinition(ctx);
    }

    @Override
    public Symbol visitUnchangeableConstant(ModelParser.UnchangeableConstantContext ctx) {
        Symbol symbol = visitLhs(ctx.lhs());
        symbol.setType(SymbolType.CONSTANT);
        symbol.addLine(getStartLine(ctx));
        return symbol;
    }

    @Override
    public Symbol visitDataEquation(ModelParser.DataEquationContext ctx) {
        Symbol symbol = visitLhs(ctx.lhs());
        symbol.addLine(getStartLine(ctx));

        if (ctx.expr() != null)
            symbol.addDependencies((List<Symbol>) visit(ctx.expr()));

        return symbol;
    }

    @Override
    public Symbol visitLookupDefinition(ModelParser.LookupDefinitionContext ctx) {
        Symbol symbol = visitLhs(ctx.lhs());
        symbol.addLine(getStartLine(ctx));
        symbol.setType(SymbolType.LOOKUP_TABLE);

        if (ctx.lookup() != null)
            symbol.addDependencies(visitLookup(ctx.lookup()));
        else if (ctx.call() != null)
            symbol.addDependencies(visitCall(ctx.call()));


        return symbol;
    }

    @Override
    public Symbol visitStringAssign(ModelParser.StringAssignContext ctx) {
        Symbol symbol = visitLhs(ctx.lhs());
        symbol.setType(SymbolType.CONSTANT);
        symbol.addLine(getStartLine(ctx));

        return symbol;
    }


    @Override
    public Symbol visitSubscriptCopy(ModelParser.SubscriptCopyContext ctx) {

        Subscript copy = getSubscriptOrCreate(table, ctx.copy.getText(), true);
        copy.setGroup(actualGroup);
        copy.setType(SymbolType.SUBSCRIPT);
        copy.addLine(getStartLine(ctx));

        Symbol original = getSubscriptOrCreate(table, ctx.original.getText(), false);
        copy.setDependencies(original.getDependencies()); // Must be setDependencies instead of addDependencies so it works even if 'original' hasn't been defined yet.

        return copy;
    }

    @Override
    public Symbol visitRealityCheck(ModelParser.RealityCheckContext ctx) {
        Symbol symbol = visitLhs(ctx.lhs());
        symbol.setType(SymbolType.REALITY_CHECK);
        symbol.addLine(getStartLine(ctx));

        return symbol;

    }


    @Override
    public Subscript visitSubscriptId(ModelParser.SubscriptIdContext ctx) {
        return getSubscriptOrCreate(table, ctx.Id().getSymbol().getText(), false);
    }

    @Override
    public List<Symbol> visitSubscriptSequence(ModelParser.SubscriptSequenceContext ctx) {


        try {
            return parseSubscriptSequence(ctx);
        } catch (IllegalArgumentException ex) {
            logger.info(ex.getMessage() + "\nThe in-between values of the range will be ignored.");
            Symbol firstSymbol = getSubscriptOrCreate(table, ctx.Id(0).getSymbol().getText(), false);
            Symbol secondSymbol = getSubscriptOrCreate(table, ctx.Id(1).getSymbol().getText(), false);

            firstSymbol.setType(SymbolType.SUBSCRIPT_VALUE);
            firstSymbol.addLine(getStartLine(ctx));
            secondSymbol.setType(SymbolType.SUBSCRIPT_VALUE);
            secondSymbol.addLine(getStartLine(ctx));

            return Arrays.asList(firstSymbol, secondSymbol);
        }


    }

    private List<Symbol> parseSubscriptSequence(ModelParser.SubscriptSequenceContext ctx) {
        Matcher startMatcher = sequencePattern.matcher(ctx.start.getText().trim());
        Matcher endMatcher = sequencePattern.matcher(ctx.end.getText().trim());

        if (startMatcher.matches() && endMatcher.matches()) {


            List<Symbol> symbolSequence = new ArrayList<>();
            String startText = startMatcher.group(1).trim();
            String endText = endMatcher.group(1).trim();

            if (!startText.equals(endText)) {
                throw new IllegalArgumentException("The subscript sequence: '" + ctx.getText() + "doesn't match. The text '" + startText + "' and '" + endText + "' should be the same.");
            }

            int startNumber = Integer.parseInt(startMatcher.group(2));
            int endNumber = Integer.parseInt(endMatcher.group(2));

            if (startNumber >= endNumber) {
                throw new IllegalArgumentException("The end number of a sequence must be greater than the start number.  Found: '" + ctx.getText() + "'.");
            }

            String text = ctx.start.getText().trim();
            for (int i = startNumber; i < endNumber + 1; i++) {
                Symbol value = getSubscriptOrCreate(table, text.replace(startMatcher.group(2), String.valueOf(i)), false);
                value.setType(SymbolType.SUBSCRIPT_VALUE);
                value.addLine(getStartLine(ctx));
                symbolSequence.add(value);
            }

            return symbolSequence;
        } else {
            throw new IllegalArgumentException("The subscript sequence " + ctx.getText() + " is invalid.");
        }


    }


    @Override
    public Object visitExprOperation(ModelParser.ExprOperationContext ctx) {
        List<Symbol> symbols = (List<Symbol>) visit(ctx.expr(0));
        symbols.addAll((List<Symbol>) visit(ctx.expr(1)));

        return symbols;
    }


    @Override
    public Object visitVar(ModelParser.VarContext ctx) {
        Symbol id = getSymbolOrCreate(table, ctx.Id().getSymbol().getText());

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
        if (ctx.expr() != null)
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
        Symbol delayP = getSymbolOrCreate(table, "DELAYP");
        List<Symbol> input = (List<Symbol>) visit(ctx.expr(0));
        List<Symbol> delayTime = (List<Symbol>) visit(ctx.expr(1));
        Symbol pipeline = getSymbolOrCreate(table, ctx.Id().getSymbol().getText());
        pipeline.setType(SymbolType.VARIABLE);
        pipeline.addLine(ctx.Id().getSymbol().getLine());


        symbols.add(delayP);
        symbols.addAll(input);
        symbols.addAll(delayTime);

        pipeline.addDependencies(symbols);

        symbols.add(pipeline);
        return symbols;
    }

    @Override
    public Object visitTabbedArray(ModelParser.TabbedArrayContext ctx) {
        List<Symbol> symbols = new ArrayList<>();
        Symbol tabbedFunc = getSymbolOrCreate(table, "TABBED ARRAY");

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
        else {
            call = table.addSymbol(new Symbol(token));
            call.setType(SymbolType.UNDETERMINED_FUNCTION);
        }

        if (EXCEL_FUNCTIONS.contains(token)) {

            List<String> exprs = ctx.exprList().expr().stream().map(ModelParser.ExprContext::getText).collect(Collectors.toList());

            String filename = exprs.get(0).replaceAll(REMOVE_QUOTES, "");
            String sheet = exprs.get(1).replaceAll(REMOVE_QUOTES, "");
            ExcelRef excel = actualSymbol.getExcelOrCreate(filename, sheet);

            if (EXCEL_DATA.contains(token)) {
                String seriesCellRange = exprs.get(2).replaceAll(REMOVE_QUOTES, "");
                String cellRange = exprs.get(3).replaceAll(REMOVE_QUOTES, "");
                excel.addCellRangeInformation(actualIndex, cellRange, seriesCellRange);

            } else {
                String cellRange = exprs.get(2).replaceAll(REMOVE_QUOTES, "");
                excel.addCellRangeInformation(actualIndex, cellRange);
            }

        }

        if (VENSIM_DYNAMIC_FUNCTIONS.contains(token)) {

            actualSymbol.setDelayed(DelayedType.DELAYED);

            ModelParser.ExprContext secondArgument = ctx.exprList().expr(1);
            if (secondArgument instanceof ModelParser.SignExprContext) { //Only if the delay is TIME STEP, the delay type is TIME_STEP_DELAYED.
                ModelParser.ExprAllowSignContext signedType = ((ModelParser.SignExprContext) secondArgument).exprAllowSign();
                if (signedType instanceof ModelParser.VarContext) {
                    String tokenOfArgument = ((ModelParser.VarContext) signedType).Id().getSymbol().getText();
                    if(TIME_STEP_TOKEN.contains(tokenOfArgument)){
                        actualSymbol.setDelayed(DelayedType.TIME_STEP_DELAYED);
                    }
                }
            }
        }

        List<Symbol> symbols;
        if (ctx.exprList() != null)
            symbols = (List<Symbol>) visit(ctx.exprList());
        else
            symbols = new ArrayList<>();

        symbols.add(call);

        return symbols;
    }


    @Override
    public Object visitExprList(ModelParser.ExprListContext ctx) {
        List<Symbol> symbols = new ArrayList<>();
        for (ModelParser.ExprContext expr : ctx.expr()) {
            symbols.addAll((List<Symbol>) visit(expr));
        }
        return symbols;
    }


    @Override
    public List<Symbol> visitIndexList(ModelParser.IndexListContext ctx) {
        List<Symbol> symbols = new ArrayList<>();


        if (!ctx.subscriptId().isEmpty()) {
            for (ModelParser.SubscriptIdContext subscript : ctx.subscriptId()) {
                symbols.add(visitSubscriptId(subscript));
            }
        }

        return symbols;
    }


    @Override
    public List<Symbol> visitSubscriptValueList(ModelParser.SubscriptValueListContext ctx) {
        List<Symbol> symbols = new ArrayList<>();


        if (!ctx.subscriptId().isEmpty()) {
            for (ModelParser.SubscriptIdContext value : ctx.subscriptId()) {
                Symbol valueSymbol = visitSubscriptId(value);
                valueSymbol.setType(SymbolType.SUBSCRIPT_VALUE);
                valueSymbol.addLine(getStartLine(value));

                symbols.add(valueSymbol);
            }
        }

        if (!ctx.subscriptSequence().isEmpty()) {
            for (ModelParser.SubscriptSequenceContext sequence : ctx.subscriptSequence()) {
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

        return new ArrayList<>();
    }

    @Override
    public List<Symbol> visitConstList(ModelParser.ConstListContext ctx) {
        return new ArrayList<>();
    }

    @Override
    public List<Symbol> visitLookupPointList(ModelParser.LookupPointListContext ctx) {
        List<Symbol> symbols = new ArrayList<>();


        for (ModelParser.LookupPointContext point : ctx.lookupPoint()) {
            symbols.addAll(visitLookupPoint(point));
        }


        return symbols;
    }

    @Override
    public List<Symbol> visitLookupRange(ModelParser.LookupRangeContext ctx) {
        List<Symbol> symbols = new ArrayList<>();

        symbols.addAll(visitLookupPoint(ctx.lookupPoint(0)));
        symbols.addAll(visitLookupPoint(ctx.lookupPoint(1)));

        if (ctx.referenceLine() != null)
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


    @Override
    public Symbol visitSymbolWithDoc(ModelParser.SymbolWithDocContext ctx) {
        Object symbolObj = visitSymbolWithDocDefinition(ctx.symbolWithDocDefinition());
        if (!(symbolObj instanceof Symbol))
            throw new IllegalStateException("The visitor returned an object that isn't of type Symbol. Actual object: " + symbolObj);

        Symbol symbol = (Symbol) symbolObj;

        String comment = ctx.unitsDoc().comment.getText().substring(1).replace("\\", "").replace("\n", "").replace("\t", "");
        String units = ctx.unitsDoc().units.getText().substring(1);

        if (!comment.isBlank())
            symbol.setComment(comment);

        if (!units.isBlank())
            symbol.setUnits(units);


        return symbol;
    }

    @Override
    public Symbol visitLhs(ModelParser.LhsContext ctx) {
        Symbol id = getSymbolOrCreate(table, ctx.Id().getText());
        id.setGroup(actualGroup);
        List<Symbol> indexes = null;
        if (ctx.indexes != null) {
            indexes = visitSubscript(ctx.indexes);
            id.addIndexLine(indexes);
            actualIndex = indexes.stream().map(Symbol::getToken).collect(Collectors.toList());

        } else {
            actualIndex = new ArrayList<>();
        }
        actualSymbol = id;
        return id;
    }

    @Override
    public List<Symbol> visitSubscript(ModelParser.SubscriptContext ctx) {
        return visitIndexList(ctx.indexList());
    }


}
