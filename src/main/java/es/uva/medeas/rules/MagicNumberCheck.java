package es.uva.medeas.rules;

import es.uva.medeas.Issue;
import es.uva.medeas.VensimVisitorContext;
import es.uva.medeas.parser.ModelBaseVisitor;
import es.uva.medeas.parser.ModelParser;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = MagicNumberCheck.CHECK_KEY,name=MagicNumberCheck.NAME, description= MagicNumberCheck.HTML_DESCRIPTION)
public class MagicNumberCheck extends ModelBaseVisitor implements VensimCheck {
    public static final String CHECK_KEY = "magic-number-check";
    public static final String NAME = "MagicNumberCheck" ;
    public static final String HTML_DESCRIPTION = "Description Missing :("; //TODO

    public static final String DEFAULT_REPETITIONS = "3";


    private SymbolTable numberTable;


    @RuleProperty(
            key = "minimum-repetitions",
            defaultValue = DEFAULT_REPETITIONS,
    description = "Minimum times a number must appear to be considered a magic number.")
    public String repetitions = DEFAULT_REPETITIONS ;



    @Override
    public void scan(VensimVisitorContext context) {

        int min_repetitions = Integer.parseInt(repetitions);
        numberTable = new SymbolTable();

        visit(context.getRootNode());

        numberTable.print();


        for(Symbol symbol: numberTable.getSymbols()){
            if(symbol.getDefinitionLines().size()>=min_repetitions)
                for(int line: symbol.getDefinitionLines()) {
                    Issue issue = new Issue(this,line,"The number " + symbol.getToken()  +" is repeated " +
                            symbol.getDefinitionLines().size() + "  times. Consider replacing it by a constant");
                    context.addIssue(issue);
                }
        }

    }


    private boolean exprIsAConstant(ModelParser.ExprContext ctx){
        return  ctx.getClass() == ModelParser.ConstContext.class;
    }
    @Override
    public Object visitEquation(ModelParser.EquationContext ctx) {

        if(!exprIsAConstant(ctx.expr()))
            return super.visit(ctx.expr());


        return null;

    }


    @Override
    public Object visitLookup(ModelParser.LookupContext ctx) {
        return null;
    }

    @Override
    public Object visitIntegerConst(ModelParser.IntegerConstContext ctx) {
       Symbol integer = numberTable.getSymbolOrCreate(ctx.getText());
       integer.addDefinitionLine(ctx.start.getLine());
       return null;
    }


    @Override
    public Object visitFloatingConst(ModelParser.FloatingConstContext ctx) {
        Symbol integer = numberTable.getSymbolOrCreate(ctx.getText());
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
}
