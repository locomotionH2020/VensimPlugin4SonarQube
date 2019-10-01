package es.uva.medeas.rules;


import es.uva.medeas.Issue;
import org.sonar.check.Rule;

import java.util.ArrayList;
import java.util.List;

@Rule(key = NotEmptyCheck.CHECK_KEY)
public class NotEmptyCheck extends VensimCheck{
    public static final String CHECK_KEY = "not-empty";

    @Override
    public List<Issue> check(String content) {
        ArrayList<Issue> issues = new ArrayList<>();

        if(content.isEmpty()){
            Issue emptyIssue = new Issue(this,1,"The file is empty.");
            issues.add(emptyIssue);
        }

        return  issues;
    }
}
