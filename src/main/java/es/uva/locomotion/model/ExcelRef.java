package es.uva.locomotion.model;

import org.antlr.v4.runtime.misc.Triple;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExcelRef {

    private String filename;
    private String sheet;
    private List<Triple<List<String>, String,String>> cellRangeInformation; //subscript cellrange seriescellrange?
    public ExcelRef(String filename, String sheet){
        this.filename = filename;
        this.sheet = sheet;
        cellRangeInformation = new ArrayList<>();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSheet() {
        return sheet;
    }

    public void setSheet(String sheet) {
        this.sheet = sheet;
    }


    public void addCellRangeInformation(List<String> subcript, String cellrange, String seriesCellrange) {
        cellRangeInformation.add(new Triple<>(subcript, cellrange, seriesCellrange));
    }
    public void addCellRangeInformation(List<String> subcript, String cellrange) {
        cellRangeInformation.add(new Triple<>(subcript, cellrange, null));
    }

    public List<Triple<List<String>, String,String>> getCellRangeInformation() {
        return cellRangeInformation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExcelRef excelRef = (ExcelRef) o;
        return filename.equals(excelRef.filename) && sheet.equals(excelRef.sheet) && cellRangeInformation.equals(excelRef.cellRangeInformation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename, sheet);
    }

    @Override
    public String toString() {
        return "ExcelRef{" +
                "filename='" + filename + '\'' +
                ", sheet='" + sheet + '\'' +
                ", cellRangeInformation=" + cellRangeInformation +
                '}';
    }
}
