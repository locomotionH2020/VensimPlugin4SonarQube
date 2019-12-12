package es.uva.medeas.parser.visitors;

import es.uva.medeas.parser.ModelBaseVisitor;
import es.uva.medeas.parser.ModelParser;
import es.uva.medeas.utilities.Constants;


public class CompoundMagicNumberVisitor extends ModelBaseVisitor {


    /**
     * Returns True if the arguments are part of a compound number.
     * The conditions are:
     *        * It doesnt contain constants or variables
     *        * If it contains a function call, that function is included in the ignored function list (Constants.IGNORED_FUNCTIONS_IF_ALONE)
     *          or in the compound function list (Constants.FUNCTION_IS_COMPOUND_MAGIC_NUMBER)
     */
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
        return true;
    }

    @Override
    public Object visitKeyword(ModelParser.KeywordContext ctx) {
        if(ctx.expr()==null)
            return true;

        return visit(ctx.expr());
    }

    @Override
    public Object visitLookup(ModelParser.LookupContext ctx) {
        return true;
    }




    @Override
    public Object visitCallExpr(ModelParser.CallExprContext ctx) {
        String funcName = ctx.call().Id().getText();
        return Constants.FUNCTION_IS_COMPOUND_MAGIC_NUMBER.contains(funcName) || Constants.IGNORED_FUNCTIONS_IF_ALONE.contains(funcName);
    }



    @Override
    public Object visitWildCard(ModelParser.WildCardContext ctx) {
        return true;
    }

    @Override
    public Object visitSignExpr(ModelParser.SignExprContext ctx) {
        if(ctx.exprAllowSign() instanceof ModelParser.ParensContext) { //TODO Test
            ModelParser.ParensContext parenth = (ModelParser.ParensContext) ctx.exprAllowSign();
            return visit(parenth.expr());
        }else if(ctx.exprAllowSign() instanceof ModelParser.CallExprContext)
            return visit(ctx.exprAllowSign());

                    //TODO Refactor
        return false;
    }

}
