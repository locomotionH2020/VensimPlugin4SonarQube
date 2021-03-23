package es.uva.locomotion.model;

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
}
