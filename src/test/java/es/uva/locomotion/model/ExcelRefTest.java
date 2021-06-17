package es.uva.locomotion.model;

import es.uva.locomotion.model.symbol.ExcelRef;
import org.antlr.v4.runtime.misc.Triple;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ExcelRefTest {

    @Test
    public void getFilename() {
        ExcelRef er = new ExcelRef("filename", "sheet");
        assertEquals("filename", er.getFilename());
    }

    @Test
    public void setFilename() {
        ExcelRef er = new ExcelRef("filename", "sheet");
        er.setFilename("anotherName");
        assertEquals("anotherName", er.getFilename());
    }

    @Test
    public void getSheet() {
        ExcelRef er = new ExcelRef("filename", "sheet");
        assertEquals("sheet", er.getSheet());
    }

    @Test
    public void setSheet() {
        ExcelRef er = new ExcelRef("filename", "sheet");
        er.setSheet("anotherName");
        assertEquals("anotherName", er.getSheet());
    }

    @Test
    public void cellRangeInformation() {
        ExcelRef er = new ExcelRef("filename", "sheet");
        er.addCellRangeInformation(List.of("suscript1", "suscript2"), "cellrange");
        er.addCellRangeInformation(List.of("suscript3", "suscript4"), "cellrange2","seriesCellrange");

        Triple<List<String>, String, String> info1 = new Triple<>(List.of("suscript1", "suscript2"), "cellrange", null);
        Triple<List<String>, String, String> info2 = new Triple<>(List.of("suscript3", "suscript4"), "cellrange2", "seriesCellrange");
        List<Triple<List<String>, String, String>> expected = List.of(info1, info2);
        List<Triple<List<String>, String, String>> actual = er.getCellRangeInformation();
        assertEquals(expected,actual);
    }


    @Test
    public void testEquals() {
        ExcelRef er = new ExcelRef("filename", "sheet");
        ExcelRef erSame = new ExcelRef("filename", "sheet");
        ExcelRef erName = new ExcelRef("diff", "sheet");
        ExcelRef erSheet = new ExcelRef("filename", "diff");

        assertEquals(er,er);
        assertEquals(er,erSame);
        assertNotEquals(er,erName);
        assertNotEquals(er,erSheet);

        er.addCellRangeInformation(List.of("suscript1", "suscript2"), "cellrange");
        erSame.addCellRangeInformation(List.of("suscript1", "suscript2"), "cellrange");
        ExcelRef erSubscripts = new ExcelRef("filename", "sheet");
        ExcelRef erCellRange = new ExcelRef("filename", "sheet");
        ExcelRef erSeries = new ExcelRef("filename", "sheet");

        assertEquals(er,er);
        assertEquals(er,erSame);
        assertNotEquals(er,erSubscripts);
        assertNotEquals(er,erCellRange);
        assertNotEquals(er,erSeries);
    }
}