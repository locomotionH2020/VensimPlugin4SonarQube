package es.uva.locomotion.parser.visitors;

import es.uva.locomotion.parser.ModelBaseVisitor;
import es.uva.locomotion.parser.Model;
import es.uva.locomotion.utilities.Constants;

/**
 * From the outside you should only call 'callIsACompoundNumber'. The other methods can't be protected/private
 * due to inheritance.
 */
public class CompoundMagicNumberVisitor extends ModelBaseVisitor {


    /**
     * Returns True if the call is a compound number
     *
     * To be considered a compound number, the call must meet the following conditions:
     *        - The function called must be included in Constants.FUNCTIONS_THAT_FORM_COMPOUND_MAGIC_NUMBERS
     *        - It doesn't contain identifiers (neither constants nor variables).
     *        - If there are nested calls, every function called must be included in Constants.FUNCTIONS_THAT_FORM_COMPOUND_MAGIC_NUMBERS
     *        - It doesn't contain wildcards
     */
    public boolean callIsACompoundNumber(Model.CallContext call){
        return visitCall(call);
    }


    @Override
    public Boolean visitExprList(Model.ExprListContext ctx) {
        for(Model.ExprContext expr: ctx.expr()){
            if(! (boolean) this.visit(expr))
                return false;
        }

        return true;
    }


    @Override
    public Boolean visitExprOperation(Model.ExprOperationContext ctx) {
        return (boolean) visit(ctx.expr(0)) && (boolean) visit(ctx.expr(1));
    }

    @Override
    public Object visitConstVensim(Model.ConstVensimContext ctx) {
        return ctx.StringConst()==null;
    }

    @Override
    public Object visitKeyword(Model.KeywordContext ctx) {
        if(ctx.expr()==null)
            return true;

        return visit(ctx.expr());
    }

    @Override
    public Object visitLookup(Model.LookupContext ctx) {
        return false;
    }



    @Override
    public Boolean visitCall(Model.CallContext ctx) {
        String funcName = ctx.Id().getText();
        return Constants.FUNCTIONS_THAT_FORM_COMPOUND_MAGIC_NUMBERS.contains(funcName) && visitExprList(ctx.exprList());
    }



    @Override
    public Boolean visitWildCard(Model.WildCardContext ctx) {
        return false;
    }

    @Override
    public Object visitSignExpr(Model.SignExprContext ctx) {
        if(ctx.exprAllowSign() instanceof Model.ParensContext) {
            Model.ParensContext parenth = (Model.ParensContext) ctx.exprAllowSign();
            return visit(parenth.expr());

        }else if(ctx.exprAllowSign() instanceof Model.CallExprContext)
            return visit(ctx.exprAllowSign());

        return false;
    }

}
