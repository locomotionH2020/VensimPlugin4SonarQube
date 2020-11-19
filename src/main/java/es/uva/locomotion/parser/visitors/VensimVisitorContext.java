package es.uva.locomotion.parser.visitors;


import es.uva.locomotion.model.AcronymsList;
import es.uva.locomotion.model.DataBaseRepresentation;
import es.uva.locomotion.parser.Model;
import es.uva.locomotion.model.SymbolTable;
import es.uva.locomotion.plugin.Issue;

import java.util.ArrayList;
import java.util.List;

public class VensimVisitorContext {

    private Model.FileContext rootNode;
    private List<Issue> issues;
    private SymbolTable parsedTable;



    private DataBaseRepresentation dbData;

    public Model.FileContext getRootNode() {
        return rootNode;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public VensimVisitorContext(Model.FileContext rootNode, SymbolTable parsedTable, DataBaseRepresentation dbTable){
        this.rootNode = rootNode;
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

}
