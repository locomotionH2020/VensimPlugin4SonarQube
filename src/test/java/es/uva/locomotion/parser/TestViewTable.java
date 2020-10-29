package es.uva.locomotion.parser;

import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.Assert.*;

public class TestViewTable {

    @Test
    public void viewOptions() {
        ViewTable vt = new ViewTable();
        View v = new View("name");
        vt.addView(v);

        assertEquals(v,vt.getView("name"));
        assertTrue(vt.hasView("name"));
        assertFalse(vt.hasView("no This name"));

        View v2 = new View("name2");
        vt.addView(v2);

        Collection<View> viewsReturned = vt.getViews();


        assertTrue(viewsReturned.contains(v));
        assertTrue(viewsReturned.contains(v2));
        assertEquals(2, viewsReturned.size());

        vt.removeView(v);

        assertFalse(vt.hasView("name"));
        assertTrue(vt.hasView("name2"));


    }

    @Test
    public void viewCreateOrSelectView() {
        ViewTable vt = new ViewTable();
        View v = vt.createOrSelectView("name");

        assertEquals(v,vt.getView("name"));
        assertTrue(vt.hasView("name"));
        assertFalse(vt.hasView("no This name"));

        View vIgual = vt.createOrSelectView("name");

        assertEquals(vIgual,v);

    }
}