package es.uva.medeas;

import es.uva.medeas.parser.VensimErrorListener;
import es.uva.medeas.parser.ModelLexer;
import es.uva.medeas.parser.ModelParser;
import es.uva.medeas.rules.VensimCheck;


import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VensimScanner {

    private static final Logger LOG = Loggers.get(VensimScanner.class);

    private final SensorContext context;
    private final Checks<VensimCheck> checks;

    public VensimScanner(SensorContext context, Checks<VensimCheck> checks) {
        this.context = context;
        this.checks = checks;

    }

    public void scanFiles(List<InputFile> inputFiles) {
        for (InputFile vensimFile : inputFiles) {
            if (context.isCancelled()) {
                return;
            }
            try {
                scanFile(vensimFile);
            } catch (Exception e) {
                LOG.warn("Unable to analyze file '{}'. Error: {}", vensimFile.toString(), e);
            }
        }

    }

    private void scanFile(InputFile inputFile) {

        try {
            String content = inputFile.contents();

            List<Issue> issues = new ArrayList<>();
            ModelLexer lexer = new ModelLexer(CharStreams.fromString(content));

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ModelParser parser = new ModelParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new VensimErrorListener());

            ParseTree root = parser.file();
            VensimVisitorContext visitorContext = new VensimVisitorContext(root);

            for (VensimCheck check : checks.all()) {
                check.scan(visitorContext);

                issues.addAll(visitorContext.getIssues());
            }
            saveIssues(inputFile,issues);

            int lines = content.split("[\r\n]+").length;
            context.<Integer>newMeasure().forMetric(CoreMetrics.NCLOC).on(inputFile).withValue(lines).save();

        } catch (IOException e) {
            LOG.error("Unable to analyze file '{}'. Error: {}", inputFile.toString(), e);
        }catch (ParseCancellationException e){
            LOG.error("Unable to parse the file '{}'. Message {}",inputFile.toString(),e.getLocalizedMessage());
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
