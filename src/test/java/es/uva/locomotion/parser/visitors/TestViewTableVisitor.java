package es.uva.locomotion.parser.visitors;

import es.uva.locomotion.model.View;
import es.uva.locomotion.model.ViewTable;
import org.junit.Test;

import static es.uva.locomotion.testutilities.GeneralTestUtilities.getViewTableFromString;
import static org.junit.Assert.*;

public class TestViewTableVisitor {

    public static final String VARIABLE_2 = "VARIABLE2";
    public static final String VARIABLE_1 = "VARIABLE1";

    @Test
    public void getViewTable() {
        String program = "\\\\\\---/// Sketch information - do not modify anything except names\n" +
                "V300  Do not put anything below this section - it will be ignored\n" +
                "*Intro\n" +
                "$192-192-192,0,Times New Roman|12||0-0-0|0-0-0|0-0-255|-1--1--1|-1--1--1|96,96,5,0\n" +
                "12,1,0,722,147,182,44,3,135,0,8,-1,0,0,0,-1--1--1,0-0-0,|20||0-0-0\n" +
                "GRAPH\n" +
                "10,2,"+VARIABLE_1+",1586,885,40,20,3,3,0,0,0,0,0,0\n"+
                "10,2,"+VARIABLE_2+",1586,885,40,20,3,2,0,0,0,0,0,0\n"+
                "\\\\\\---/// Sketch information - do not modify anything except names\n" +
                "V300  Do not put anything below this section - it will be ignored\n" +
                "*Intro2\n" +
                "$192-192-192,0,Times New Roman|12||0-0-0|0-0-0|0-0-255|-1--1--1|-1--1--1|96,96,5,0\n" +
                "10,2,"+VARIABLE_2+",1586,885,40,20,3,3,0,0,0,0,0,0\n" +
                "10,2,Demand by sector FD,1586,885,40,20,3,3,0,0,0,0,0,0\n" +
                "///---\\\\\\\n";
        ViewTable table = getViewTableFromString(program);
        View intro = table.getView("Intro");

        assertTrue( intro.getPrimary_symbols().contains(VARIABLE_1));
        assertFalse( intro.getPrimary_symbols().contains(VARIABLE_2));

        assertTrue( intro.getShadow_symbols().contains(VARIABLE_2));

        View intro2 = table.getView("Intro2");

        assertTrue( intro2.getPrimary_symbols().contains(VARIABLE_2));
        assertFalse( intro2.getSymbols().contains(VARIABLE_1));






    }
    @Test
    public void getViewTableNameDuplication() {
        String program = "\\\\\\---/// Sketch information - do not modify anything except names\n" +
                "V300  Do not put anything below this section - it will be ignored\n" +
                "*Intro\n" +
                "$192-192-192,0,Times New Roman|12||0-0-0|0-0-0|0-0-255|-1--1--1|-1--1--1|96,96,5,0\n" +
                "12,1,0,722,147,182,44,3,135,0,8,-1,0,0,0,-1--1--1,0-0-0,|20||0-0-0\n" +
                "GRAPH\n" +
                "10,2,"+VARIABLE_1+",1586,885,40,20,3,3,0,0,0,0,0,0\n"+
                "10,2,"+VARIABLE_2+",1586,885,40,20,3,2,0,0,0,0,0,0\n"+
                "10,2,"+VARIABLE_2+",1586,885,40,20,3,2,0,0,0,0,0,0\n"+
                "\\\\\\---/// Sketch information - do not modify anything except names\n" +
                "V300  Do not put anything below this section - it will be ignored\n" +
                "*Intro\n" +
                "$192-192-192,0,Times New Roman|12||0-0-0|0-0-0|0-0-255|-1--1--1|-1--1--1|96,96,5,0\n" +
                "10,2,"+VARIABLE_2+",1586,885,40,20,3,3,0,0,0,0,0,0\n" +
                "10,2,"+VARIABLE_2+",1586,885,40,20,3,2,0,0,0,0,0,0\n"+
                "10,2,Demand by sector FD,1586,885,40,20,3,3,0,0,0,0,0,0\n" +
                "///---\\\\\\\n";
        ViewTable table = getViewTableFromString(program);
        View intro = table.getView("Intro");

        assertTrue( intro.getPrimary_symbols().contains(VARIABLE_1));
        assertTrue( intro.getPrimary_symbols().contains(VARIABLE_2));
        assertFalse( intro.getShadow_symbols().contains(VARIABLE_1));
        assertTrue( intro.getShadow_symbols().contains(VARIABLE_2));

        assertEquals(4,intro.getSymbols().size());

    }
    @Test
    public void getViewNameWithAllASCII() {
        String program = "\\\\\\---/// Sketch information - do not modify anything except names\n" +
                "V300  Do not put anything below this section - it will be ignored\n" +
                "* View1\n" +
                "$192-192-192,0,Times New Roman|12||0-0-0|0-0-0|0-0-255|-1--1--1|-1--1--1|96,96,5,0\n" +
                "///---\\\\\\\n";
        ViewTable table = getViewTableFromString(program);
    }
    @Test
    public void getViewNameModuleAndCategoryOutput() {
        String program = "\\\\\\---/// Sketch information - do not modify anything except names\n" +
                "V300  Do not put anything below this section - it will be ignored\n" +
                "* ModuleName.CategoryName\n" +
                "$192-192-192,0,Times New Roman|12||0-0-0|0-0-0|0-0-255|-1--1--1|-1--1--1|96,96,5,0\n"
                +"\\\\\\---/// Sketch information - do not modify anything except names\n" +
                "V300  Do not put anything below this section - it will be ignored\n" +
                "* ModuleName2.CategoryName2-SubCategoryName\n" +
                "$192-192-192,0,Times New Roman|12||0-0-0|0-0-0|0-0-255|-1--1--1|-1--1--1|96,96,5,0\n" +

                "///---\\\\\\\n";
        ViewTable table = getViewTableFromString(program, ".","-");

        assertTrue(table.hasModule("ModuleName"));
        assertTrue(table.hasModule("ModuleName2"));

        assertTrue(table.hasCategory("CategoryName"));
        assertTrue(table.hasCategory("CategoryName2"));

        assertTrue(table.hasSubcategory("CategoryName2", "SubCategoryName"));
        assertFalse(table.hasSubcategory("CategoryName", "SubCategoryName"));
    }
    @Test
    public void getViewNameModulOutput() {
        String program = "\\\\\\---/// Sketch information - do not modify anything except names\n" +
                "V300  Do not put anything below this section - it will be ignored\n" +
                "* ModuleName.CategoryName\n" +
                "$192-192-192,0,Times New Roman|12||0-0-0|0-0-0|0-0-255|-1--1--1|-1--1--1|96,96,5,0\n"
                +"\\\\\\---/// Sketch information - do not modify anything except names\n" +
                "V300  Do not put anything below this section - it will be ignored\n" +
                "* ModuleName2.CategoryName2-SubCategoryName\n" +
                "$192-192-192,0,Times New Roman|12||0-0-0|0-0-0|0-0-255|-1--1--1|-1--1--1|96,96,5,0\n" +

                "///---\\\\\\\n";
        ViewTable table = getViewTableFromString(program, ".");

        assertTrue(table.hasModule("ModuleName"));
        assertTrue(table.hasModule("ModuleName2"));

        assertTrue(table.hasCategory("CategoryName"));
        assertTrue(table.hasCategory("CategoryName2-SubCategoryName"));

        assertFalse(table.hasSubcategory("CategoryName2-SubCategoryName", "SubCategoryName"));
        assertFalse(table.hasSubcategory("CategoryName", "SubCategoryName"));
    }
}