package es.uva.locomotion.model.symbol;


import es.uva.locomotion.model.ExcelRef;
import es.uva.locomotion.model.IssuableAbs;
import es.uva.locomotion.model.Module;
import es.uva.locomotion.model.category.Category;

import java.util.*;
import java.util.stream.Collectors;

public class Symbol extends IssuableAbs {


    private final String token;
    private final List<List<Symbol>> indexes;
    private String units;
    private String comment;
    private Set<Symbol> dependencies;
    private SymbolType type;
    private Category category;
    private Module primaryModule;
    private final List<Module> shadowModule;
    private String group;
    private List<ExcelRef> excel;
    private DelayedType isDelayed;

    private boolean isFiltered;


    public Symbol(String token) {
        super();
        this.token = token.strip();
        dependencies = new HashSet<>();
        type = SymbolType.UNDETERMINED;
        units = "";
        comment = "";
        category = null;
        indexes = new ArrayList<>();
        primaryModule = null;
        shadowModule = new ArrayList<>();
        group = "";
        excel = new ArrayList<>();
        isDelayed = DelayedType.NOT_DELAYED;

    }


    public Symbol(String token, SymbolType type) {
        this(token);
        this.type = type;
    }

    public void setType(SymbolType type) {
        this.type = type;
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

    public void addDependency(Symbol symbol) {
        dependencies.add(symbol);

    }

    public void addDependencies(Collection<Symbol> symbols) {
        for (Symbol symb : symbols) {
            addDependency(symb);
        }
    }


    public String getToken() {
        return token;
    }


    public boolean hasType() {
        return getType() != SymbolType.UNDETERMINED;
    }

    public void addIndexLine(List<Symbol> indexLine) {

        int sizeDiff = indexLine.size() - indexes.size();
        if (sizeDiff > 0)
            for (int i = 0; i < sizeDiff; i++)
                indexes.add(new ArrayList<>());


        for (int i = 0; i < indexLine.size(); i++) {
            Symbol index = indexLine.get(i);
            indexes.get(i).add(index);
        }


    }


    public List<List<Symbol>> getIndexes() {
        return indexes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return token.equals(symbol.token) && Objects.equals(indexes, symbol.indexes) && Objects.equals(units, symbol.units) && Objects.equals(comment, symbol.comment) && Objects.equals(dependencies, symbol.dependencies) && type == symbol.type && Objects.equals(category, symbol.category) && Objects.equals(primaryModule, symbol.primaryModule) && Objects.equals(shadowModule, symbol.shadowModule) && Objects.equals(group, symbol.group) && Objects.equals(excel, symbol.excel);
    }


    public boolean dbEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return token.equals(symbol.token) && Objects.equals(indexes, symbol.indexes) && Objects.equals(units, symbol.units) && Objects.equals(comment, symbol.comment) && type == symbol.type && Objects.equals(category, symbol.category) && Objects.equals(primaryModule, symbol.primaryModule) && Objects.equals(shadowModule, symbol.shadowModule);
    }



    @Override
    public String toString() {
        return "Symbol{" +
                "token='" + token + '\'' +
                ", indexes=" + indexes +
                ", units='" + units + '\'' +
                ", comment='" + comment + '\'' +
                ", dependencies=" + dependencies.stream().map(Symbol::getToken).collect(Collectors.toList()) +
                ", type=" + type +
                ", category='" + category + '\'' +
                ", primary_module='" + getPrimaryModule() + '\'' +
                ", shadow_module=" + getShadowModule() +
                ", group='" + group + '\'' +
                ", excel=" + excel +
                ", isValid=" + isValid +
                ", invalidReason=" + invalidReason +
                ", isFiltered=" + isFiltered +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(getToken(), getIndexes(), getUnits(), getComment(), getType(), getCategory(), getPrimaryModule(), getShadowModule());
    }

    public void setUnits(String units) {
        this.units = units.strip();
    }

    public void setComment(String comment) {
        this.comment = comment.strip();
    }

    public String getUnits() {
        return units;
    }

    public String getComment() {
        return comment;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    /**
     * Overrides the dependencies
     *
     * @param dependencies new dependencies
     */
    public void setDependencies(Set<Symbol> dependencies) {
        this.dependencies = dependencies;
    }


    public Module getPrimaryModule() {
        return primaryModule;
    }

    public void setPrimaryModule(Module primaryModule) {
        this.primaryModule = primaryModule;
    }

    public List<Module> getShadowModule() {
        return shadowModule;
    }

    public void addShadowModule(Module module) {
        shadowModule.add(module);
    }

    public List<Module> getViews() {
        List<Module> list = new ArrayList<>(getShadowModule());
        if (getPrimaryModule() != null)
            list.add(getPrimaryModule());
        return list;
    }

    public boolean isFiltered() {
        return isFiltered;
    }

    public void setFiltered(boolean filtered) {
        isFiltered = filtered;
    }

    public List<ExcelRef> getExcel() {
        return excel;
    }


    public void setExcel(List<ExcelRef> excel) {
        this.excel = excel;
    }

    public ExcelRef getExcelOrCreate(String filename, String sheet) {
        ExcelRef excelTmp = new ExcelRef(filename, sheet);
        if (excel.contains(excelTmp))
            return excel.get(excel.indexOf(excelTmp));

        else {
            excel.add(excelTmp);
            return excelTmp;
        }
    }

    public DelayedType isDelayed() {
        return isDelayed;
    }

    public void setDelayed(DelayedType delayed) {
        isDelayed = delayed;
    }

    @Override
    public boolean isValid() {
        if (category != null && !category.isValid()) {
            this.setAsInvalid(category.getInvalidReason());
        }
        if (primaryModule != null && !primaryModule.isValid()) {
            this.setAsInvalid(primaryModule.getInvalidReason());
        }
        return super.isValid();
    }

}
