package es.uva.locomotion.model.symbol;

import java.util.Objects;

public class Number extends Symbol {
    private int ocurrences;

    public Number(String token) {
        super(token);
        ocurrences = 0;
    }

    public int getOcurrences() {
        return ocurrences;
    }

    private void occurs() {
        ocurrences++;
    }

    @Override
    public void addLine(int line) {
        super.addLine(line);
        occurs();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Number number = (Number) o;
        return ocurrences == number.ocurrences;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ocurrences);
    }
}
