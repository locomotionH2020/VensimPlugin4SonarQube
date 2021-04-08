package es.uva.locomotion.model.symbol;

import java.util.Objects;

public class Subscript extends Symbol {

    private final boolean isCopy;

    public Subscript(String token) {
        super(token);
        isCopy = false;

    }

    public Subscript(String token, SymbolType type) {
        super(token, type);
        isCopy = false;
    }

    public Subscript(String token, boolean isCopy) {
        super(token);
        this.isCopy = isCopy;

    }

    public Subscript(String token, SymbolType type, boolean isCopy) {
        super(token, type);
        this.isCopy = isCopy;
    }

    public boolean isCopy() {
        return isCopy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subscript subscript = (Subscript) o;
        return isCopy == subscript.isCopy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isCopy);
    }
}
