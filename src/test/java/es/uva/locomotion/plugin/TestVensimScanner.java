package es.uva.locomotion.plugin;


import es.uva.locomotion.model.SymbolTable;
import es.uva.locomotion.service.ServiceController;
import es.uva.locomotion.testutilities.RuleTestUtilities;
import es.uva.locomotion.rules.VensimCheck;
import es.uva.locomotion.utilities.JsonSymbolTableBuilder;
import es.uva.locomotion.utilities.OutputFilesGenerator;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.measure.NewMeasure;
import org.sonar.api.config.Configuration;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static es.uva.locomotion.testutilities.GeneralTestUtilities.*;
import static org.mockito.Mockito.*;

public class TestVensimScanner {

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testScannerLogMessageParseException() throws Exception {

        VensimLogger logger = mock(VensimLogger.class);
        VensimScanner.LOG = logger;
        InputFile inputFile = Mockito.mock(InputFile.class);
        when(inputFile.contents()).thenReturn("This isn't a vensim model");
        when(inputFile.filename()).thenReturn("notAVensimModelParser.mdl");


        VensimScanner scanner = RuleTestUtilities.getScanner();


        List<InputFile> files = new ArrayList<>();
        files.add(inputFile);
        scanner.scanFiles(files);

        Mockito.verify(logger).error("Unable to parse the file 'notAVensimModelParser.mdl'. Error: l:1 c:25 mismatched input '<EOF>' expecting {':=', '[', ':IGNORE:', ':EXCEPT:', INFO_UNIT, Keyword}");

    }

    @Test
    public void testScannerGetsModuleNameCorrectly() throws IOException {
        InputFile file = Mockito.mock(InputFile.class);
        when(file.contents()).thenReturn(""); // Esto se podría mejorar si se inyecta un generador de tabla de símbolos en
        // el escáner en vez de usar un método estático
        when(file.filename()).thenReturn("climate.mdl");


        SensorContext context = Mockito.mock(SensorContext.class, Mockito.RETURNS_DEEP_STUBS);
        Checks<VensimCheck> checks = (Checks<VensimCheck>) Mockito.mock(Checks.class);
        OutputFilesGenerator builder = mock(OutputFilesGenerator.class);
        ServiceController controller = mock(ServiceController.class);
        when(controller.isAuthenticated()).thenReturn(true);
        when(controller.getSymbolsFromDb(any())).thenReturn(mock(SymbolTable.class));


        VensimScanner scanner = spy(new VensimScanner(context, checks, builder, controller));

        Mockito.doNothing().when(scanner).checkIssues(any());
        Mockito.doNothing().when(scanner).saveIssues(any(), anyList());
        Mockito.doCallRealMethod().when(scanner).scanFile(file);
        scanner.scanFile(file);
        Mockito.verify(controller).injectNewSymbols(any(), any(), any());


    }

    @Test
    public void testIfAfileFailsTheRestExecutes() throws IOException, NoSuchFieldException, IllegalAccessException {


        VensimLogger logger = mock(VensimLogger.class);



        OutputFilesGenerator builder = Mockito.mock(OutputFilesGenerator.class);
        VensimScanner.LOG = logger;
        InputFile fileBefore = Mockito.mock(InputFile.class);
        InputFile wrongFile = Mockito.mock(InputFile.class);
        InputFile fileAfter = Mockito.mock(InputFile.class);
        when(wrongFile.contents()).thenReturn("This isn't a vensim model");
        when(fileBefore.contents()).thenReturn(loadFile("invertedDependencies.mdl"));
        when(fileAfter.contents()).thenReturn(loadFile("testCyclicDependencies.mdl"));
        ServiceController mockServiceController = mock(ServiceController.class);


        List<InputFile> files = new ArrayList<>();
        files.add(fileBefore);
        files.add(wrongFile);
        files.add(fileAfter);

        SensorContext context = Mockito.mock(SensorContext.class);
        Configuration configuration = Mockito.mock(Configuration.class);
        Optional<String> optional = Optional.empty();

        when(context.isCancelled()).thenReturn(false);
        when(context.config()).thenReturn(configuration);
        when(configuration.get(Mockito.anyString())).thenReturn(optional);

        NewMeasure measure = Mockito.mock(NewMeasure.class);
        when(measure.forMetric(Mockito.any())).thenReturn(measure);
        when(measure.on(Mockito.any())).thenReturn(measure);
        when(measure.withValue(Mockito.any())).thenReturn(measure);

        when(context.newMeasure()).thenReturn(measure);

        CheckFactory factory = new CheckFactory(RuleTestUtilities.getAllActiveRules());
        Checks<VensimCheck> checks = factory.<VensimCheck>create(VensimRuleRepository.REPOSITORY_KEY)
                .addAnnotatedChecks(VensimRuleRepository.getChecks());

        VensimScanner scanner = spy(new VensimScanner(context, checks, builder, mockServiceController));
        when(mockServiceController.getSymbolsFromDb(anyList())).thenReturn(null);

        Mockito.doCallRealMethod().when(scanner).scanFiles(files);
        Mockito.doCallRealMethod().when(scanner).scanFile(Mockito.any());
        Mockito.doCallRealMethod().when(scanner).getParseTree(Mockito.any());
        Mockito.doCallRealMethod().when(scanner).checkIssues(Mockito.any());
        Mockito.doReturn("").when(scanner).getModuleNameFromFileName(any());
        Mockito.doNothing().when(scanner).saveIssues(any(), any());


        scanner.scanFiles(files);


        Mockito.verify(scanner, Mockito.times(2)).checkIssues(Mockito.any());
        Mockito.verify(logger, Mockito.times(1)).error(Mockito.any());
        Mockito.verify(logger, Mockito.times(0)).info(Mockito.any());

    }


}
