package org.sonar.vensim;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.SensorContext;

import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.issue.NoSonarFilter;
import org.sonar.api.measures.FileLinesContextFactory;

import org.sonar.api.rule.RuleKey;
import org.sonar.vensim.rules.NotEmptyCheck;
import org.sonar.vensim.rules.VensimCheck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VensimScanner {


    private final SensorContext context;
    private final List<InputFile> inputFiles;
    private final Checks<VensimCheck> checks;
    private final FileLinesContextFactory fileLinesContextFactory;
    private final NoSonarFilter noSonarFilter;

    public VensimScanner(SensorContext context, Checks<VensimCheck> checks,
                         FileLinesContextFactory fileLinesContextFactory, NoSonarFilter noSonarFilter, List<InputFile> inputFiles) {
        this.context = context;
        this.checks = checks;
        this.fileLinesContextFactory = fileLinesContextFactory;
        this.noSonarFilter = noSonarFilter;
        this.inputFiles = inputFiles;

    }

    public void scanFiles() {
        for (InputFile vensimFile : inputFiles) {
            System.out.println("Analizando fichero: " + vensimFile.path());
            if (context.isCancelled()) {
                return;
            }
            try {
                scanFile(vensimFile);
            } catch (Exception e) {
                System.out.println("Error" + e.getMessage()); //TODO Add logger
            }
        }

    }

    private void scanFile(InputFile inputFile) {

        try {
            String content = inputFile.contents();

            List<Issue> issues = new ArrayList<>();

            for (VensimCheck check : checks.all()) {
               issues.addAll(check.check(content));
            }
            saveIssues(inputFile,issues);
        } catch (IOException e) {
            e.printStackTrace(); //TODO Log
        }


    }


    private void saveIssues(InputFile file, List<Issue> issues){
        for (Issue preciseIssue : issues) {
            RuleKey ruleKey = checks.ruleKey(preciseIssue.getCheck());
 
            NewIssue newIssue = context
                    .newIssue()
                    .forRule(ruleKey);


            TextRange range = file.selectLine(preciseIssue.getLine());
            NewIssueLocation newLocation = newIssue.newLocation()
                    .on(file);
            newLocation.message(preciseIssue.getMessage());
            newLocation.at(range);

            newIssue.at(newLocation);
            newIssue.save();
        }

    }


}
