package es.uva.locomotion.rules;


import com.ibm.icu.impl.Pair;
import es.uva.locomotion.model.Symbol;
import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.parser.visitors.EmbeddedLookupVisitor;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.plugin.Issue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.batch.rule.Severity;

import java.util.ArrayList;
import java.util.List;

import static es.uva.locomotion.testutilities.RuleTestUtilities.assertHasIssueInLines;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class TestEmbeddedLookupCheck {

    public static final int DEFAULT_MINIMUM_SIZE = Integer.parseInt(EmbeddedLookupCheck.DEFAULT_SIZE);



    public EmbeddedLookupCheck getEmbeddedTableCheckWithTable(VensimVisitorContext context, List<Pair<Symbol,Integer>> pairs){
        EmbeddedLookupCheck check = spy(new EmbeddedLookupCheck());
        check.repetitions = String.valueOf(DEFAULT_MINIMUM_SIZE);
        EmbeddedLookupVisitor visitor = Mockito.mock(EmbeddedLookupVisitor.class);
        when(visitor.getSymbolTable(context.getRootNode())).thenReturn(pairs);

        doCallRealMethod().when(check).scan(any());
        when(check.getVisitor()).thenReturn(visitor);

        return check;
    }

    @BeforeClass
    public static void preTest(){
        assertTrue(DEFAULT_MINIMUM_SIZE >0);
    }

    @Test
    public void testOneLookup(){

        VensimVisitorContext context = new VensimVisitorContext(null,null, new ViewTable(), null, null);

        List<Pair<Symbol,Integer>> pairs = new ArrayList<>();

        Symbol lookup = new Symbol("name");
        lookup.addLine(1);
        pairs.add(Pair.of(lookup,1));


        EmbeddedLookupCheck check = getEmbeddedTableCheckWithTable(context,pairs);
        check.scan(context);

        for(Issue issue:context.getIssues()) {
            assertEquals("This lookup have 1 embedded data points. Consider replacing it by an external excel.", issue.getMessage());
            assertEquals(Severity.INFO,issue.getSeverity());
        }

        assertHasIssueInLines(context,EmbeddedLookupCheck.class,1);

    }
}
