package es.uva.locomotion.model;


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
    private Module primary_module;
    private final List<Module> shadow_module;
    private String group;
    private List<ExcelRef> excel;

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
        primary_module = null;
        shadow_module = new ArrayList<>();
        group = "";
        excel = new ArrayList<>();

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
        return token.equals(symbol.token) && Objects.equals(indexes, symbol.indexes) && Objects.equals(units, symbol.units) && Objects.equals(comment, symbol.comment) && Objects.equals(dependencies, symbol.dependencies) && type == symbol.type && Objects.equals(category, symbol.category) && Objects.equals(primary_module, symbol.primary_module) && Objects.equals(shadow_module, symbol.shadow_module) && Objects.equals(group, symbol.group) && Objects.equals(excel, symbol.excel);
    }


    public boolean dbEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return token.equals(symbol.token) && Objects.equals(indexes, symbol.indexes) && Objects.equals(units, symbol.units) && Objects.equals(comment, symbol.comment) && type == symbol.type && Objects.equals(category, symbol.category) && Objects.equals(primary_module, symbol.primary_module) && Objects.equals(shadow_module, symbol.shadow_module);
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
                ", primary_module='" + getPrimary_module() + '\'' +
                ", shadow_module=" + getShadow_module() +
                ", group='" + group + '\'' +
                ", excel=" + excel +
                ", isValid=" + isValid +
                ", invalidReason=" + invalidReason +
                ", isFiltered=" + isFiltered +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(getToken(), getIndexes(), getUnits(), getComment(), getType(), getCategory(), getPrimary_module(), getShadow_module());
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


    public Module getPrimary_module() {
        return primary_module;
    }

    public void setPrimary_module(Module primary_module) {
        this.primary_module = primary_module;
    }

    public List<Module> getShadow_module() {
        return shadow_module;
    }

    public void addShadow_module(Module module) {
        shadow_module.add(module);
    }

    public List<Module> get_views() {
        List<Module> list = new ArrayList<>(getShadow_module());
        if (getPrimary_module() != null)
            list.add(getPrimary_module());
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

    @Override
    public boolean isValid() {
        if (category != null && !category.isValid()) {
            this.setAsInvalid(category.getInvalidReason());
        }
        if (primary_module != null && !primary_module.isValid()) {
            this.setAsInvalid(primary_module.getInvalidReason());
        }
        return super.isValid();
    }
}
