package es.uva.medeas.plugin;


import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.plugin.Issue;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;

public class VensimVisitorContext {

    private ParseTree rootNode;
    private List<Issue> issues;
    private SymbolTable table;

    public ParseTree getRootNode() {
        return rootNode;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public VensimVisitorContext(ParseTree rootNode){
        this.rootNode = rootNode;
        issues = new ArrayList<>();
        table = null;
    }

    public void addIssue(Issue issue){
        issues.add(issue);
    }

    public void setSymbolTable(SymbolTable table) {
        this.table = table;
    }

    public SymbolTable getSymbolTable(){
        return table;
    }
}
