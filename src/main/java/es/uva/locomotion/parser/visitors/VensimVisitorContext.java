package es.uva.locomotion.parser.visitors;


import es.uva.locomotion.model.AcronymsList;
import es.uva.locomotion.model.DataBaseRepresentation;
import es.uva.locomotion.model.SymbolTable;
import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.parser.ModelParser;
import es.uva.locomotion.plugin.Issue;
import org.sonar.api.batch.sensor.SensorContext;

import java.util.ArrayList;
import java.util.List;

public class VensimVisitorContext {

    private final ModelParser.FileContext rootNode;
    private final List<Issue> issues;
    private final SymbolTable parsedTable;
    private final ViewTable viewTable;
    private final SensorContext context;

    private final DataBaseRepresentation dbData;

    public ModelParser.FileContext getRootNode() {
        return rootNode;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public VensimVisitorContext(ModelParser.FileContext rootNode, SymbolTable parsedTable, ViewTable viewTable, SensorContext context, DataBaseRepresentation dbTable){
        this.rootNode = rootNode;
        this.viewTable = viewTable;
        this.context = context;
        issues = new ArrayList<>();
        this.parsedTable = parsedTable;
        this.dbData = dbTable;
    }

    public void addIssue(Issue issue){
        issues.add(issue);
    }



    public SymbolTable getParsedSymbolTable(){
        return parsedTable;
    }

    public SymbolTable getDbSymbolTable(){

        return dbData != null ? dbData.getDataBaseSymbols() : null;
    }
    public AcronymsList getDbAcronyms(){
        return dbData != null ? dbData.getAcronyms() : null;
    }

    public DataBaseRepresentation getDbdata(){
        return dbData;
    }

    public ViewTable getViewTable() {
        return viewTable;
    }

    public SensorContext getContext() {
        return context;
    }
}
