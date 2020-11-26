package es.uva.locomotion.model;


import java.util.*;
import java.util.stream.Collectors;

public class Symbol {


    private String token;
    private List<Integer> linesDefined;
    private List<List<Symbol>> indexes;
    private String units;
    private String comment;
    private Set<Symbol> dependencies;
    private SymbolType type;
    private String category;
    private String primary_view;
    private List<String> shadow_views;

    private boolean isValid;


    private boolean isFiltered;


    public Symbol(String token){
        this.token = token.strip();
        dependencies = new HashSet<>();
        type = SymbolType.UNDETERMINED;
        linesDefined = new ArrayList<>();
        units = "";
        comment ="";
        category = "";
        indexes = new ArrayList<>();
        isValid = true;
        primary_view = "";
        shadow_views = new ArrayList<>();
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

    /*
     * @return Dependencies of the symbol. For example in "a = b*func(c)" it would return 'b', 'c' and 'func'.
     * Subscripts also contain subscript values as dependencies.
     */
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
        return getToken().equals(symbol.getToken()) &&
                linesDefined.equals(symbol.linesDefined) &&
                getIndexes().equals(symbol.getIndexes()) &&
                getUnits().equals(symbol.getUnits()) &&
                getComment().equals(symbol.getComment()) &&
                getDependencies().equals(symbol.getDependencies()) &&
                getType() == symbol.getType() &&
                getCategory().equals(symbol.getCategory()) &&
                getPrimary_view().equals(symbol.getPrimary_view()) &&
                getShadow_views().equals(symbol.getShadow_views());
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "token='" + token + '\'' +
                ", linesDefined=" + linesDefined +
                ", indexes=" + indexes+
                ", units='" + units + '\'' +
                ", comment='" + comment + '\'' +
                ", dependencies=" + dependencies.stream().map(Symbol::getToken).collect(Collectors.toList()) +
                ", type=" + type +
                ", category='" + category + '\'' +
                ", primary module='" + getPrimary_view() + '\'' +
                ", shadow modules='" + getShadow_views() +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(getToken(), linesDefined, getIndexes(), getUnits(), getComment(), getType(), getCategory(), getPrimary_view(), getShadow_views());
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

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory(){
        return category;
    }

    /**
     * Overrides the dependencies
     * @param dependencies new dependencies
     */
    public void setDependencies(Set<Symbol> dependencies) {
        this.dependencies = dependencies;
    }

    public void setAsInvalid(){
        isValid = false;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getPrimary_view() { return primary_view; }

    public void setPrimary_view(String primary_view) { this.primary_view = primary_view; }

    public List<String> getShadow_views() {
        return shadow_views;
    }

    public void addShadow_view(String module){
        shadow_views.add(module.trim());
    }

    public List<String> get_views(){
        List<String> list = new ArrayList<>(getShadow_views());
        list.add(getPrimary_view());
        return list;
    }

    public boolean isFiltered() {
        return isFiltered;
    }

    public void setFiltered(boolean filtered) {
        isFiltered = filtered;
    }
}