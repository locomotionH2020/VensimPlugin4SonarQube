package es.uva.medeas;


import es.uva.medeas.rules.RuleTestUtilities;
import es.uva.medeas.rules.VensimCheck;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.measure.NewMeasure;
import org.sonar.api.utils.log.Logger;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static es.uva.medeas.rules.TestUtilities.*;

public class TestVensimScanner
{


    @Test
    public void testScannerLogMessageParseException() throws Exception {

        Logger logger = Mockito.mock(Logger.class);
        VensimScanner.LOG = logger;
        InputFile inputFile = Mockito.mock(InputFile.class);
        Mockito.when(inputFile.contents()).thenReturn("This isn't a vensim model");
        Mockito.when(inputFile.toString()).thenReturn("notAVensimModel.mdl");



        VensimScanner scanner = RuleTestUtilities.getScanner();


        List<InputFile> files = new ArrayList<>();
        files.add(inputFile);
        scanner.scanFiles(files);

        Mockito.verify(logger).error("Unable to parse the file '{}'. Message {}","notAVensimModel.mdl", "no viable alternative at input 'This isn't a vensim model'");

    }


    @Test
    public void testIfAfileFailsTheRestExecutes() throws IOException, NoSuchFieldException, IllegalAccessException {


        Logger logger = Mockito.mock(Logger.class);

        JsonSymbolTableBuilder builder = Mockito.mock(JsonSymbolTableBuilder.class);
        VensimScanner.LOG = logger;
        InputFile fileBefore = Mockito.mock(InputFile.class);
        InputFile wrongFile = Mockito.mock(InputFile.class);
        InputFile fileAfter = Mockito.mock(InputFile.class);
        Mockito.when(wrongFile.contents()).thenReturn("This isn't a vensim model");
        Mockito.when(fileBefore.contents()).thenReturn(loadFile("invertedDependencies.mdl"));
        Mockito.when(fileAfter.contents()).thenReturn(loadFile("testCyclicDependencies.mdl"));

      VensimScanner scanner = Mockito.mock(VensimScanner.class);




        List<InputFile> files = new ArrayList<>();
        files.add(fileBefore);
        files.add(wrongFile);
        files.add(fileAfter);

        Mockito.doCallRealMethod().when(scanner).scanFiles(files);
        Mockito.doCallRealMethod().when(scanner).scanFile(Mockito.any());
        Mockito.doCallRealMethod().when(scanner).getParseTree(Mockito.any());
        Mockito.doCallRealMethod().when(scanner).checkIssues(Mockito.any());
        Mockito.doNothing().when(scanner).generateJsonOutput();

        SensorContext context = Mockito.mock(SensorContext.class);
        Mockito.when(context.isCancelled()).thenReturn(false);
        NewMeasure measure = Mockito.mock(NewMeasure.class);
        Mockito.when(measure.forMetric(Mockito.any())).thenReturn(measure);
        Mockito.when(measure.on(Mockito.any())).thenReturn(measure);
        Mockito.when(measure.withValue(Mockito.any())).thenReturn(measure);

        Mockito.when(context.newMeasure()).thenReturn(measure);
        Whitebox.setInternalState(scanner, "context", context);
        Whitebox.setInternalState(scanner, "jsonBuilder", builder);
        CheckFactory factory = new CheckFactory(RuleTestUtilities.getAllActiveRules());
        Checks<VensimCheck> checks =  factory.<VensimCheck>create(VensimRuleRepository.REPOSITORY_KEY)
                .addAnnotatedChecks(VensimRuleRepository.getChecks());


        Whitebox.setInternalState(scanner,"checks", checks );
        scanner.scanFiles(files);


        Mockito.verify(scanner,Mockito.times(2)).checkIssues(Mockito.any());
        Mockito.verify(logger,Mockito.times(1)).error(Mockito.any(),Mockito.any(),Mockito.any());
        Mockito.verify(logger,Mockito.times(0)).warn(Mockito.any());



    }


}
