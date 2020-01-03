package es.uva.medeas.parser.visitors;

import es.uva.medeas.parser.ModelBaseVisitor;
import es.uva.medeas.parser.ModelParser;
import es.uva.medeas.utilities.Constants;


public class CompoundMagicNumberVisitor extends ModelBaseVisitor {


    /**
     * Returns True if the call is a compound number
     *
     * To be considered a compound number, the call must meet the following conditions:
     *        - The function called must be included in Constants.FUNCTIONS_THAT_FORM_COMPOUND_MAGIC_NUMBERS
     *        - It doesn't contain constants or variables
     *        - If there are nested calls, every function called must be included in Constants.FUNCTIONS_THAT_FORM_COMPOUND_MAGIC_NUMBERS
     */
    public boolean callIsACompoundNumber(ModelParser.CallContext call){
        return visitCall(call);
    }


    @Override
    public Boolean visitExprList(ModelParser.ExprListContext ctx) {
        for(ModelParser.ExprContext expr: ctx.expr()){
            if(! (boolean) this.visit(expr))
                return false;
        }

        return true;
    }


    @Override
    public Boolean visitExprOperation(ModelParser.ExprOperationContext ctx) {
        return (boolean) visit(ctx.expr(0)) && (boolean) visit(ctx.expr(1));
    }

    @Override
    public Object visitConstVensim(ModelParser.ConstVensimContext ctx) {
        return ctx.StringConst()==null;
    }

    @Override
    public Object visitKeyword(ModelParser.KeywordContext ctx) {
        if(ctx.expr()==null)
            return true;

        return visit(ctx.expr());
    }

    @Override
    public Object visitLookup(ModelParser.LookupContext ctx) {
        return false;
    }



    @Override
    public Boolean visitCall(ModelParser.CallContext ctx) {
        String funcName = ctx.Id().getText();
        return Constants.FUNCTIONS_THAT_FORM_COMPOUND_MAGIC_NUMBERS.contains(funcName) && visitExprList(ctx.exprList());
    }



    @Override
    public Object visitWildCard(ModelParser.WildCardContext ctx) {
        return true;
    }

    @Override
    public Object visitSignExpr(ModelParser.SignExprContext ctx) {
        if(ctx.exprAllowSign() instanceof ModelParser.ParensContext) {
            ModelParser.ParensContext parenth = (ModelParser.ParensContext) ctx.exprAllowSign();
            return visit(parenth.expr());

        }else if(ctx.exprAllowSign() instanceof ModelParser.CallExprContext)
            return visit(ctx.exprAllowSign());

        return false;
    }

}
