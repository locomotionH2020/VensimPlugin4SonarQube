package es.uva.locomotion.rules;

import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.plugin.Issue;

public abstract class AbstractVensimCheck implements VensimCheck {

    public void addIssue(VensimVisitorContext context, Issue issue, boolean isFiltered){
        if(!isFiltered){
            context.addIssue(issue);
        }

    }
}
