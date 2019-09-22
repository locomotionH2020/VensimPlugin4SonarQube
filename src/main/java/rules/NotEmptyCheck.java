package rules;


import org.sonar.check.Rule;

@Rule(key = NotEmptyCheck.CHECK_KEY)
public class NotEmptyCheck extends VensimCheck{
    public static final String CHECK_KEY = "not-empty";

}
