package es.uva.locomotion.parser.visitors;


import es.uva.locomotion.parser.Model;
import es.uva.locomotion.parser.SymbolTable;
import es.uva.locomotion.plugin.Issue;

import java.util.ArrayList;
import java.util.List;

public class VensimVisitorContext {

    private Model.FileContext rootNode;
    private List<Issue> issues;
    private SymbolTable parsedTable;
    private SymbolTable dbTable;

    public Model.FileContext getRootNode() {
        return rootNode;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public VensimVisitorContext(Model.FileContext rootNode, SymbolTable parsedTable, SymbolTable dbTable){
        this.rootNode = rootNode;
        issues = new ArrayList<>();
        this.parsedTable = parsedTable;
        this.dbTable = dbTable;
    }

    public void addIssue(Issue issue){
        issues.add(issue);
    }



    public SymbolTable getParsedSymbolTable(){
        return parsedTable;
    }

    public SymbolTable getDbSymbolTable(){
        return dbTable;
    }
}
