package es.uva.locomotion.plugin;

import es.uva.locomotion.rules.VensimCheck;
import org.sonar.api.batch.rule.Severity;


public class Issue {

    private final VensimCheck check;
    private final int line;
    private final String message;
    private Severity severity;


    /**
     * Creates a Issue of severity 'MAJOR'.
     * @param source Rule that generted the issue
     * @param line Line of the issue
     * @param message Message that will be displayed in the issue in Sonarqube
     */
    public Issue(VensimCheck source, int line, String message){
        check = source;
        this.message = message;
        this.line = line;
        this.severity = Severity.MAJOR;

    }

    public String getMessage(){
        return message;
    }

    public int getLine(){
        return  line;
    }


    public VensimCheck getCheck(){
        return  check;
    }


    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity issueSeverity) {
        severity = issueSeverity;
    }
}
