package es.uva.medeas;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Tree;

import java.util.ArrayList;
import java.util.List;

public class VensimVisitorContext {

    private ParseTree rootNode;
    List<Issue> issues;

    public ParseTree getRootNode() {
        return rootNode;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public VensimVisitorContext(ParseTree rootNode){
        this.rootNode = rootNode;
        issues = new ArrayList<>();
    }

    public void addIssue(Issue issue){
        issues.add(issue);
    }
}
