package es.uva.medeas.parser;


import java.util.*;

public class Symbol {


    private String token;
    private List<Integer> linesDefined;
    private List<List<Symbol>> indexes;
    private String units;
    private String comment;
    private Set<Symbol> dependencies;
    private SymbolType type;


    public Symbol(String token){
        this.token = token;
        dependencies = new HashSet<>();
        type = SymbolType.UNDETERMINED;
        linesDefined = new ArrayList<>();
        units = "";
        comment ="";
        indexes = new ArrayList<>();
    }


    public Symbol(String token, SymbolType type){
      this(token);
      this.type = type;
    }

    public void addDefinitionLine(int line){
        linesDefined.add(line);
    }

    public void setType(SymbolType type) {
        this.type = type;
    }


    public List<Integer> getDefinitionLines(){
        return linesDefined;
    }

    public Set<Symbol> getDependencies() {
        return dependencies;
    }

    public SymbolType getType() {
        return type;
    }

    public void addDependency(Symbol symbol){
        dependencies.add(symbol);

    }

    public void addDependencies(Collection<Symbol> symbols){
        for(Symbol symb: symbols){
            addDependency(symb);
        }
    }

    public String getToken() {
        return token;
    }


    public boolean hasType(){
        return getType()!=SymbolType.UNDETERMINED;
    }

    public void addIndexLine(List<Symbol> indexLine){

        int sizeDiff = indexLine.size() - indexes.size();
        if(sizeDiff>0)
            for(int i=0;i<sizeDiff;i++)
                indexes.add(new ArrayList<>());


        for(int i = 0; i<indexLine.size();i++){
            Symbol index = indexLine.get(i);
            indexes.get(i).add(index);
        }


    }



    public List<List<Symbol>> getIndexes(){
        return indexes;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Symbol)) return false;
        Symbol symbol = (Symbol) o;
        return Objects.equals(getToken(), symbol.getToken()) &&
                Objects.equals(linesDefined, symbol.linesDefined) &&
                Objects.equals(getIndexes(), symbol.getIndexes()) &&
                Objects.equals(getDependencies(), symbol.getDependencies()) &&
                getType() == symbol.getType();
    }


    public void setUnits(String units) {
        this.units = units.strip();
    }

    public void setComment(String comment){
        this.comment = comment.strip();
    }

    public String getUnits() {
        return units;
    }

    public String getComment() {
        return comment;
    }

}
