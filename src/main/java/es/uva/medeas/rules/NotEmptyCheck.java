package es.uva.medeas.rules;


import es.uva.medeas.Issue;
import es.uva.medeas.parser.ModelParser;
import org.sonar.check.Rule;

@Rule(key = NotEmptyCheck.CHECK_KEY)
public class NotEmptyCheck extends VensimVisitorCheck {
    public static final String CHECK_KEY = "not-empty";

    @Override
    public Object visitFile(ModelParser.FileContext ctx) {
        if (ctx.model() == null){
            Issue emptyIssue = new Issue(this,1,"The file is empty.");
            addIssue(emptyIssue);
        }
        return super.visitFile(ctx);
    }

}
