package es.uva.locomotion.rules;

import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.plugin.Issue;

public interface VensimCheck {
     void scan(VensimVisitorContext context);
     void addIssue(VensimVisitorContext context, Issue issue, boolean isFiltered);
}
