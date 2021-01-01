package es.uva.locomotion.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AcronymsList {

    private final List<String> acronyms;

    public AcronymsList() {
        acronyms = new ArrayList<>();
    }

    public List<String> getAcronyms() {
        return acronyms;
    }


    public void addAcronym(String acronym) {
        acronyms.add(acronym);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcronymsList that = (AcronymsList) o;
        return Objects.equals(acronyms, that.acronyms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(acronyms);
    }
}
