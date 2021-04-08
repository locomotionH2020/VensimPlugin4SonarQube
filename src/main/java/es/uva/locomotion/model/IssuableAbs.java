package es.uva.locomotion.model;

import java.util.HashSet;
import java.util.Set;

public abstract class IssuableAbs implements Issuable{

    protected boolean isValid;
    protected String invalidReason;
    protected final Set<Integer> lines;

    public IssuableAbs(){
        isValid = true;
        lines = new HashSet<>();
    }

    public boolean isValid() {
        return isValid;
    }
    public void setAsInvalid(String invalidReason) {
        isValid = false;
        this.invalidReason = invalidReason;
    }
    public String getInvalidReason() {
        return invalidReason;
    }

    public Set<Integer> getLines() {
        return lines;
    }

    public void addLine(int line) {
        this.lines.add(line);
    }
}
