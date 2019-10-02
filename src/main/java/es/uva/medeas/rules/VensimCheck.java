package es.uva.medeas.rules;
import es.uva.medeas.Issue;
import es.uva.medeas.VensimVisitorContext;
import es.uva.medeas.parser.ModelBaseVisitor;



public abstract class VensimCheck extends ModelBaseVisitor {

    private VensimVisitorContext context;

    public void scan(VensimVisitorContext context) {
        this.context = context;
        visit(context.getRootNode());
    }


    public void addIssue(Issue issue){
        context.addIssue(issue);
    }


}
