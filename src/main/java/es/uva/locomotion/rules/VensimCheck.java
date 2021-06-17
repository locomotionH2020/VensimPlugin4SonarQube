package es.uva.locomotion.rules;

import es.uva.locomotion.model.VensimVisitorContext;
import es.uva.locomotion.plugin.Issue;

public abstract class VensimCheck {

    public abstract void scan(VensimVisitorContext context);

    public void addIssue(VensimVisitorContext context, Issue issue, boolean isFiltered){
        if(!isFiltered){
            context.addIssue(issue);
        }

    }
}
