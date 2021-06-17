package es.uva.locomotion.model.symbol;


import es.uva.locomotion.model.IssuableAbs;
import es.uva.locomotion.model.Module;
import es.uva.locomotion.model.category.Category;
import es.uva.locomotion.utilities.logs.VensimLogger;

import java.util.*;
import java.util.stream.Collectors;

public class Symbol extends IssuableAbs implements Comparable<Symbol> {


    private final String token;
    private final List<List<Symbol>> indexes;
    private String units;
    private String comment;
    private Set<Symbol> dependencies;
    private SymbolType type;
    private Category category;
    private Module primaryModule;
    private final Set<Module> shadowModule;
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
        shadowModule = new HashSet<>();
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
        if (o == null || !Symbol.class.isAssignableFrom(o.getClass())) return false;
        Symbol symbol = (Symbol) o;
        return token.equals(symbol.token) && Objects.equals(indexes, symbol.indexes) && Objects.equals(units, symbol.units) && Objects.equals(comment, symbol.comment) && Objects.equals(dependencies, symbol.dependencies) && type == symbol.type && Objects.equals(category, symbol.category) && Objects.equals(primaryModule, symbol.primaryModule) && Objects.equals(shadowModule, symbol.shadowModule) && Objects.equals(group, symbol.group) && Objects.equals(excel, symbol.excel);
    }

    protected static VensimLogger logger = VensimLogger.getInstance();

    public boolean dbEquals(Object o) {
        if (this == o) return true;

        if (o == null ||!Symbol.class.isAssignableFrom(o.getClass())) return false;

        Symbol symbol = (Symbol) o;
        if(type == symbol.type && type == SymbolType.SUBSCRIPT_VALUE){
            return token.equals(symbol.token);
        }else if(type == symbol.type && type == SymbolType.SUBSCRIPT){
            List<Symbol> localDependencies = getDependencies().stream().sorted().collect(Collectors.toList());
            List<Symbol> otherDependencies = symbol.getDependencies().stream().sorted().collect(Collectors.toList());
            int i = 0;
            while (i < localDependencies.size()) {
                if (!localDependencies.get(i).dbEquals(otherDependencies.get(i))) {
                   return false;
                }
                i++;
            }

            return token.equals(symbol.token)  && Objects.equals(comment, symbol.comment);
        }

        return token.equals(symbol.token) && Objects.equals(indexes, symbol.indexes) && Objects.equals(units, symbol.units) && Objects.equals(comment, symbol.comment) && type == symbol.type && Objects.equals(category, symbol.category) && Objects.equals(primaryModule, symbol.primaryModule) && Objects.equals(shadowModule, symbol.shadowModule) && Objects.equals(excel, symbol.excel);
    }


    @Override
    public String toString() {
        return "Symbol{" +
                "isValid=" + isValid +
                ", invalidReason='" + invalidReason + '\'' +
                ", lines=" + lines +
                ", token='" + token + '\'' +
                ", indexes=" + indexes +
                ", units='" + units + '\'' +
                ", comment='" + comment + '\'' +
                ", dependencies=" + dependencies.stream().map(Symbol::getToken).collect(Collectors.toList()) +
                ", type=" + type +
                ", category=" + category +
                ", primaryModule=" + primaryModule +
                ", shadowModule=" + shadowModule +
                ", group='" + group + '\'' +
                ", excel=" + excel +
                ", isDelayed=" + isDelayed +
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

    public Set<Module> getShadowModule() {
        return shadowModule;
    }

    public void addShadowModule(Module module) {
        shadowModule.add(module);
    }

    public List<Module> getModules() {
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

    @Override
    public int compareTo(Symbol o) {
        return getToken().compareTo(o.getToken());
    }
}
