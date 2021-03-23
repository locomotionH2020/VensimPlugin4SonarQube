package es.uva.locomotion.model;


import java.util.Set;

public interface Issuable {

    boolean isValid();

    void setAsInvalid(String invalidReason);

    String getInvalidReason();

    Set<Integer> getLines();

    void addLine(int line);
}
