package es.uva.locomotion.model.symbol;

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
}
