package es.uva.locomotion.utilities;

import es.uva.locomotion.model.Symbol;
import es.uva.locomotion.model.SymbolTable;
import es.uva.locomotion.model.ViewTable;
import org.junit.Test;

import static es.uva.locomotion.testutilities.GeneralTestUtilities.getSymbolTableFromString;
import static es.uva.locomotion.testutilities.GeneralTestUtilities.getViewTableFromString;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

public class TestViewTableUtility {

    public static final String VARIABLE_1_EQ = "VARIABLE_1_name";
    public static final String VARIABLE_2_EQ = "VARIABLE_2_name";
    public static final String VARIABLE_3_EQ = "VARIABLE_3_name";
    public static final String VARIABLE_1_VIEW = "VARIABLE_1 name";
    public static final String VARIABLE_2_VIEW = "VARIABLE 2 name";
    public static final String VARIABLE_3_VIEW = "VARIABLE 3 name";
    public static final String VIEW_NAME = "Filter_Intro";
    public static final String VIEW_NAME_2 = "All_Another name";
    public static final String VIEW_NAME_3 = "Filter_A view";
    public static final String VIEW_NAME_4 = "None_Last view";

    @Test
    public void testAddView() {
        String program =
                VARIABLE_1_EQ + " = SMOOTH3(3, 4)~~| \n" +
                        VARIABLE_2_EQ + " = SMOOTH3(3, 4)~~| \n" +
                        VARIABLE_3_EQ + " = SMOOTH3(3, 4)~~| \n" +
                        "\\\\\\---/// Sketch information - do not modify anything except names\n" +
                        "V300  Do not put anything below this section - it will be ignored\n" +
                        "*" + VIEW_NAME + "\n" +
                        "$192-192-192,0,Times New Roman|12||0-0-0|0-0-0|0-0-255|-1--1--1|-1--1--1|96,96,5,0\n" +
                        "10,2," + VARIABLE_1_VIEW + ",1586,885,40,20,3,3,0,0,0,0,0,0\n" +
                        "10,2," + VARIABLE_2_VIEW + ",1586,885,40,20,3,2,0,0,0,0,0,0\n" +
                        "///---\\\\\\\n";

        SymbolTable symbolTable = getSymbolTableFromString(program);
        ViewTable viewTable = getViewTableFromString(program);
        ViewTableUtility.addViews(symbolTable, viewTable);

        Symbol s = symbolTable.getSymbol(VARIABLE_1_EQ);
        assertEquals(VIEW_NAME, s.getPrimary_view());
        assertEquals(0, s.getShadow_views().size());

        s = symbolTable.getSymbol(VARIABLE_2_EQ);
        assertTrue(s.getPrimary_view().isBlank());
        assertEquals(VIEW_NAME, s.getShadow_views().get(0));
        assertEquals(1, s.getShadow_views().size());

        s = symbolTable.getSymbol(VARIABLE_3_EQ);

        assertTrue(s.getPrimary_view().isBlank());
        assertEquals(0, s.getShadow_views().size());
    }

    @Test
    public void testfilterPrefix() {
        String program =
                VARIABLE_1_EQ + " = SMOOTH3(3, 4)~~| \n" +
                        VARIABLE_2_EQ + " = SMOOTH3(3, 4)~~| \n" +
                        VARIABLE_3_EQ + " = SMOOTH3(3, 4)~~| \n" +
                        "\\\\\\---/// Sketch information - do not modify anything except names\n" +
                        "V300  Do not put anything below this section - it will be ignored\n" +
                        "*" + VIEW_NAME + "\n" +
                        "$192-192-192,0,Times New Roman|12||0-0-0|0-0-0|0-0-255|-1--1--1|-1--1--1|96,96,5,0\n" +
                        "10,2," + VARIABLE_1_VIEW + ",1586,885,40,20,3,3,0,0,0,0,0,0\n" +
                        "10,2," + VARIABLE_3_VIEW + ",1586,885,40,20,3,2,0,0,0,0,0,0\n" +
                        "\\\\\\---/// Sketch information - do not modify anything except names\n" +
                        "V300  Do not put anything below this section - it will be ignored\n" +
                        "*" + VIEW_NAME_2 + "\n" +
                        "$192-192-192,0,Times New Roman|12||0-0-0|0-0-0|0-0-255|-1--1--1|-1--1--1|96,96,5,0\n" +
                        "10,2," + VARIABLE_1_VIEW + ",1586,885,40,20,3,2,0,0,0,0,0,0\n" +
                        "10,2," + VARIABLE_2_VIEW + ",1586,885,40,20,3,3,0,0,0,0,0,0\n" +
                        "10,2," + VARIABLE_3_VIEW + ",1586,885,40,20,3,2,0,0,0,0,0,0\n" +
                        "\\\\\\---/// Sketch information - do not modify anything except names\n" +
                        "V300  Do not put anything below this section - it will be ignored\n" +
                        "*" + VIEW_NAME_3 + "\n" +
                        "$192-192-192,0,Times New Roman|12||0-0-0|0-0-0|0-0-255|-1--1--1|-1--1--1|96,96,5,0\n" +
                        "10,2," + VARIABLE_2_VIEW + ",1586,885,40,20,3,2,0,0,0,0,0,0\n" +
                        "\\\\\\---/// Sketch information - do not modify anything except names\n" +
                        "V300  Do not put anything below this section - it will be ignored\n" +
                        "*" + VIEW_NAME_4 + "\n" +
                        "$192-192-192,0,Times New Roman|12||0-0-0|0-0-0|0-0-255|-1--1--1|-1--1--1|96,96,5,0\n" +
                        "///---\\\\\\\n";
        //Filter first view
        SymbolTable symbolTable = getSymbolTableFromString(program);
        ViewTable viewTable = getViewTableFromString(program);
        ViewTableUtility.addViews(symbolTable, viewTable);

        ViewTableUtility.filterPrefix(symbolTable, VIEW_NAME);
        Symbol s = symbolTable.getSymbol(VARIABLE_1_EQ);
        assertFalse(s.isFiltered());
        s = symbolTable.getSymbol(VARIABLE_2_EQ);
        assertTrue(s.isFiltered());
        s = symbolTable.getSymbol(VARIABLE_3_EQ);
        assertFalse(s.isFiltered());

        //Filter "Filter_"
        symbolTable = getSymbolTableFromString(program);
        viewTable = getViewTableFromString(program);
        ViewTableUtility.addViews(symbolTable, viewTable);

        ViewTableUtility.filterPrefix(symbolTable, "Filter_");
        s = symbolTable.getSymbol(VARIABLE_1_EQ);
        assertFalse(s.isFiltered());
        s = symbolTable.getSymbol(VARIABLE_2_EQ);
        assertFalse(s.isFiltered());
        s = symbolTable.getSymbol(VARIABLE_3_EQ);
        assertFalse(s.isFiltered());

        //Filter last view
        symbolTable = getSymbolTableFromString(program);
        viewTable = getViewTableFromString(program);
        ViewTableUtility.addViews(symbolTable, viewTable);

        ViewTableUtility.filterPrefix(symbolTable, "None_");
        s = symbolTable.getSymbol(VARIABLE_1_EQ);
        assertTrue(s.isFiltered());
        s = symbolTable.getSymbol(VARIABLE_2_EQ);
        assertTrue(s.isFiltered());
        s = symbolTable.getSymbol(VARIABLE_3_EQ);
        assertTrue(s.isFiltered());

        //Filter Second view
        symbolTable = getSymbolTableFromString(program);
        viewTable = getViewTableFromString(program);
        ViewTableUtility.addViews(symbolTable, viewTable);

        ViewTableUtility.filterPrefix(symbolTable, "All_");
        s = symbolTable.getSymbol(VARIABLE_1_EQ);
        assertFalse(s.isFiltered());
        s = symbolTable.getSymbol(VARIABLE_2_EQ);
        assertFalse(s.isFiltered());
        s = symbolTable.getSymbol(VARIABLE_3_EQ);
        assertFalse(s.isFiltered());

    }
}