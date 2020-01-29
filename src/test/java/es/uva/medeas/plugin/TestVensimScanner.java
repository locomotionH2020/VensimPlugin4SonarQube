package es.uva.medeas.plugin;


import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.testutilities.RuleTestUtilities;
import es.uva.medeas.rules.VensimCheck;
import es.uva.medeas.utilities.JsonSymbolTableBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.measure.NewMeasure;
import org.sonar.api.utils.log.Logger;


import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static es.uva.medeas.testutilities.TestUtilities.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TestVensimScanner {

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testScannerLogMessageParseException() throws Exception {

        Logger logger = Mockito.mock(Logger.class);
        VensimScanner.LOG = logger;
        InputFile inputFile = Mockito.mock(InputFile.class);
        when(inputFile.contents()).thenReturn("This isn't a vensim model");
        when(inputFile.toString()).thenReturn("notAVensimModel.mdl");



        VensimScanner scanner = RuleTestUtilities.getScanner();


        List<InputFile> files = new ArrayList<>();
        files.add(inputFile);
        scanner.scanFiles(files);

        Mockito.verify(logger).error("Unable to parse the file '{}'. Message {}","notAVensimModel.mdl", "no viable alternative at input 'This isn't a vensim model'");

    }


    @Test
    public void testIfAfileFailsTheRestExecutes() throws IOException, NoSuchFieldException, IllegalAccessException { //TODO Refactor


        Logger logger = Mockito.mock(Logger.class);
     ;

        JsonSymbolTableBuilder builder = Mockito.mock(JsonSymbolTableBuilder.class);
        VensimScanner.LOG = logger;
        InputFile fileBefore = Mockito.mock(InputFile.class);
        InputFile wrongFile = Mockito.mock(InputFile.class);
        InputFile fileAfter = Mockito.mock(InputFile.class);
        when(wrongFile.contents()).thenReturn("This isn't a vensim model");
        when(fileBefore.contents()).thenReturn(loadFile("invertedDependencies.mdl"));
        when(fileAfter.contents()).thenReturn(loadFile("testCyclicDependencies.mdl"));


        List<InputFile> files = new ArrayList<>();
        files.add(fileBefore);
        files.add(wrongFile);
        files.add(fileAfter);


        SensorContext context = Mockito.mock(SensorContext.class);
        when(context.isCancelled()).thenReturn(false);
        NewMeasure measure = Mockito.mock(NewMeasure.class);
        when(measure.forMetric(Mockito.any())).thenReturn(measure);
        when(measure.on(Mockito.any())).thenReturn(measure);
        when(measure.withValue(Mockito.any())).thenReturn(measure);

        when(context.newMeasure()).thenReturn(measure);

        CheckFactory factory = new CheckFactory(RuleTestUtilities.getAllActiveRules());
        Checks<VensimCheck> checks =  factory.<VensimCheck>create(VensimRuleRepository.REPOSITORY_KEY)
                .addAnnotatedChecks(VensimRuleRepository.getChecks());

        VensimScanner scanner = spy(new VensimScanner(context,checks,builder));
        doReturn(null).when(scanner).getSymbolTableFromDB(any());

        Mockito.doCallRealMethod().when(scanner).scanFiles(files);
        Mockito.doCallRealMethod().when(scanner).scanFile(Mockito.any());
        Mockito.doCallRealMethod().when(scanner).getParseTree(Mockito.any());
        Mockito.doCallRealMethod().when(scanner).checkIssues(Mockito.any());



        Mockito.doNothing().when(scanner).generateJsonOutput();


        scanner.scanFiles(files);


        Mockito.verify(scanner,Mockito.times(2)).checkIssues(Mockito.any());
        Mockito.verify(logger,Mockito.times(1)).error(Mockito.any(),Mockito.any(),Mockito.any());
        Mockito.verify(logger,Mockito.times(0)).warn(Mockito.any());

    }



}
