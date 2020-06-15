package es.uva.locomotion.rules;

import es.uva.locomotion.parser.visitors.VensimVisitorContext;

public interface VensimCheck {
     void scan(VensimVisitorContext context);
}
