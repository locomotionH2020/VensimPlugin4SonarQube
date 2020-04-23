package es.uva.locomotion.plugin;


import es.uva.locomotion.parser.ModelParser;
import es.uva.locomotion.parser.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class VensimVisitorContext {

    private ModelParser.FileContext rootNode;
    private List<Issue> issues;
    private SymbolTable parsedTable;
    private SymbolTable dbTable;

    public ModelParser.FileContext getRootNode() {
        return rootNode;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public VensimVisitorContext(ModelParser.FileContext rootNode, SymbolTable parsedTable, SymbolTable dbTable){
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
