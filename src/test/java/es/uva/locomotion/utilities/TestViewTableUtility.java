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
    public static final String CATEGORY = "category";

    @Test
    public void testAddView() {
        String program =
                VARIABLE_1_EQ + " = SMOOTH3(3, 4)~~| \n" +
                        VARIABLE_2_EQ + " = SMOOTH3(3, 4)~~| \n" +
                        VARIABLE_3_EQ + " = SMOOTH3(3, 4)~~| \n" +
                        "\\\\\\---/// Sketch information - do not modify anything except names\n" +
                        "V300  Do not put anything below this section - it will be ignored\n" +
                        "*" + VIEW_NAME + "|" + CATEGORY + "\n" +
                        "$192-192-192,0,Times New Roman|12||0-0-0|0-0-0|0-0-255|-1--1--1|-1--1--1|96,96,5,0\n" +
                        "10,2," + VARIABLE_1_VIEW + ",1586,885,40,20,3,3,0,0,0,0,0,0\n" +
                        "10,2," + VARIABLE_2_VIEW + ",1586,885,40,20,3,2,0,0,0,0,0,0\n" +
                        "///---\\\\\\\n";

        SymbolTable symbolTable = getSymbolTableFromString(program);
        ViewTable viewTable = getViewTableFromString(program, symbolTable, "|", ".");

        Symbol s = symbolTable.getSymbol(VARIABLE_1_EQ);
        assertEquals(VIEW_NAME, s.getPrimary_module().getName());

        assertEquals(0, s.getShadow_module().size());

        s = symbolTable.getSymbol(VARIABLE_2_EQ);

        assertNull(s.getPrimary_module());
        assertEquals(VIEW_NAME, s.getShadow_module().get(0).getName());
        assertEquals(1, s.getShadow_module().size());

        s = symbolTable.getSymbol(VARIABLE_3_EQ);

        assertNull(s.getPrimary_module());
        assertEquals(0, s.getShadow_module().size());
    }

    @Test
    public void testfilterPrefix() {
        String program =
                VARIABLE_1_EQ + " = SMOOTH3(3, 4)~~| \n" +
                        VARIABLE_2_EQ + " = SMOOTH3(3, 4)~~| \n" +
                        VARIABLE_3_EQ + " = SMOOTH3(3, 4)~~| \n" +
                        "\\\\\\---/// Sketch information - do not modify anything except names\n" +
                        "V300  Do not put anything below this section - it will be ignored\n" +
                        "*" + VIEW_NAME + "|" + CATEGORY + "\n" +
                        "$192-192-192,0,Times New Roman|12||0-0-0|0-0-0|0-0-255|-1--1--1|-1--1--1|96,96,5,0\n" +
                        "10,2," + VARIABLE_1_VIEW + ",1586,885,40,20,3,3,0,0,0,0,0,0\n" +
                        "10,2," + VARIABLE_3_VIEW + ",1586,885,40,20,3,2,0,0,0,0,0,0\n" +
                        "\\\\\\---/// Sketch information - do not modify anything except names\n" +
                        "V300  Do not put anything below this section - it will be ignored\n" +
                        "*" + VIEW_NAME_2 + "|" + CATEGORY + "\n" +
                        "$192-192-192,0,Times New Roman|12||0-0-0|0-0-0|0-0-255|-1--1--1|-1--1--1|96,96,5,0\n" +
                        "10,2," + VARIABLE_1_VIEW + ",1586,885,40,20,3,2,0,0,0,0,0,0\n" +
                        "10,2," + VARIABLE_2_VIEW + ",1586,885,40,20,3,3,0,0,0,0,0,0\n" +
                        "10,2," + VARIABLE_3_VIEW + ",1586,885,40,20,3,2,0,0,0,0,0,0\n" +
                        "\\\\\\---/// Sketch information - do not modify anything except names\n" +
                        "V300  Do not put anything below this section - it will be ignored\n" +
                        "*" + VIEW_NAME_3 + "|" + CATEGORY + "\n" +
                        "$192-192-192,0,Times New Roman|12||0-0-0|0-0-0|0-0-255|-1--1--1|-1--1--1|96,96,5,0\n" +
                        "10,2," + VARIABLE_2_VIEW + ",1586,885,40,20,3,2,0,0,0,0,0,0\n" +
                        "\\\\\\---/// Sketch information - do not modify anything except names\n" +
                        "V300  Do not put anything below this section - it will be ignored\n" +
                        "*" + VIEW_NAME_4 + "|" + CATEGORY + "\n" +
                        "$192-192-192,0,Times New Roman|12||0-0-0|0-0-0|0-0-255|-1--1--1|-1--1--1|96,96,5,0\n" +
                        "///---\\\\\\\n";
        //Filter first view
        SymbolTable symbolTable = getSymbolTableFromString(program);
        ViewTable viewTable = getViewTableFromString(program, symbolTable,"|", ".");
        ViewTableUtility.filterPrefix(symbolTable, VIEW_NAME);
        Symbol s = symbolTable.getSymbol(VARIABLE_1_EQ);
        assertFalse(s.isFiltered());
        s = symbolTable.getSymbol(VARIABLE_2_EQ);
        assertTrue(s.isFiltered());
        s = symbolTable.getSymbol(VARIABLE_3_EQ);
        assertFalse(s.isFiltered());

        //Filter "Filter"
        symbolTable = getSymbolTableFromString(program);

        ViewTableUtility.filterPrefix(symbolTable, "Filter");
        s = symbolTable.getSymbol(VARIABLE_1_EQ);
        assertFalse(s.isFiltered());
        s = symbolTable.getSymbol(VARIABLE_2_EQ);
        assertFalse(s.isFiltered());
        s = symbolTable.getSymbol(VARIABLE_3_EQ);
        assertFalse(s.isFiltered());

        //Filter last view
        symbolTable = getSymbolTableFromString(program);

        ViewTableUtility.filterPrefix(symbolTable, "None");
        s = symbolTable.getSymbol(VARIABLE_1_EQ);
        assertTrue(s.isFiltered());
        s = symbolTable.getSymbol(VARIABLE_2_EQ);
        assertTrue(s.isFiltered());
        s = symbolTable.getSymbol(VARIABLE_3_EQ);
        assertTrue(s.isFiltered());

        //Filter Second view
        symbolTable = getSymbolTableFromString(program);

        ViewTableUtility.filterPrefix(symbolTable, "All");
        s = symbolTable.getSymbol(VARIABLE_1_EQ);
        assertFalse(s.isFiltered());
        s = symbolTable.getSymbol(VARIABLE_2_EQ);
        assertFalse(s.isFiltered());
        s = symbolTable.getSymbol(VARIABLE_3_EQ);
        assertFalse(s.isFiltered());

    }
}