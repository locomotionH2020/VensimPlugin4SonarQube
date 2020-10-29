package es.uva.locomotion.rules;

import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.plugin.Issue;

public abstract class AbstractVensimCheck implements VensimCheck {

    public void addIssue(VensimVisitorContext context, Issue issue, Symbol symbol){
        if(!symbol.isFiltered()){
            context.addIssue(issue);
        }

    }
}
