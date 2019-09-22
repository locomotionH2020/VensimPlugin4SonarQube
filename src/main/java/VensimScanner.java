import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.SensorContext;

import org.sonar.api.issue.NoSonarFilter;
import org.sonar.api.measures.FileLinesContextFactory;

import rules.NotEmptyCheck;
import rules.VensimCheck;

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
        ArrayList<Issue> issues = new ArrayList<>();
        try {
            if(!inputFile.contents().isEmpty()){
                Issue emptyIssue = new Issue(new NotEmptyCheck(),0,"The file is empty.");
                issues.add(emptyIssue);
            }

            saveIssues(inputFile,issues);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void saveIssues(InputFile file, List<Issue> issues){
        throw new UnsupportedOperationException("Not implemented yet!");
        //TODO Not implemented
    }


}
