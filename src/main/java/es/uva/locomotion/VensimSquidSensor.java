package es.uva.locomotion;

import es.uva.locomotion.plugin.VensimLanguage;
import es.uva.locomotion.plugin.VensimRuleRepository;
import es.uva.locomotion.plugin.VensimScanner;
import es.uva.locomotion.rules.VensimCheck;
import es.uva.locomotion.service.ServiceController;
import es.uva.locomotion.utilities.OutputFilesGenerator;
import es.uva.locomotion.utilities.logs.LogConsolePrinter;
import es.uva.locomotion.utilities.logs.LogFileWriter;
import es.uva.locomotion.utilities.logs.LogOutputMethod;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static es.uva.locomotion.utilities.Constants.*;


public class VensimSquidSensor implements Sensor {


    private static final String NAME = "Vensim Squid Sensor";
    public static final String FALSE = "false";


    private final Checks<VensimCheck> checks;

    public VensimSquidSensor(CheckFactory checkFactory) {
        this.checks = checkFactory
                .<VensimCheck>create(VensimRuleRepository.REPOSITORY_KEY)
                .addAnnotatedChecks(VensimRuleRepository.getChecks());

    }


    @Override
    public void describe(SensorDescriptor sensorDescriptor) {
        sensorDescriptor.onlyOnLanguage(VensimLanguage.KEY)
                .name(NAME)
                .onlyOnFileType(InputFile.Type.MAIN);

    }

    @Override
    public void execute(SensorContext sensorContext) {
        FilePredicates p = sensorContext.fileSystem().predicates();


        String dictionaryService = sensorContext.config().get(DICTIONARY_SERVICE_PARAMETER).orElse("").trim();
        String dictionaryUsername = sensorContext.config().get(DICTIONARY_USERNAME_PARAMETER).orElse("").trim();
        String dictionaryPassword = sensorContext.config().get(DICTIONARY_CREDENTIAL_PARAMETER).orElse("").trim();
        String strLogServerComms = sensorContext.config().get(DICTIONARY_LOG_SERVER_COMMUNICATIONS).orElse(FALSE);
        String strcreateGetDiffFile = sensorContext.config().get(CREATE_GET_DIFF_FILE).orElse(FALSE);
        String logFile = sensorContext.config().get(LOG_IN_FILE).orElse("");
        String auxiliaryFilesDirName = sensorContext.config().get(AUXILIARY_FILES_DIR_NAME).orElse("auxiliary_files");

        boolean logServerComms = !FALSE.equals(strLogServerComms);
        boolean createGetDiffFile = !FALSE.equals(strcreateGetDiffFile);

        LogOutputMethod logMethod;
        if(logFile.isEmpty())
            logMethod = new LogConsolePrinter();
        else {
            try {
                logMethod = new LogFileWriter( Paths.get(auxiliaryFilesDirName + "/" + logFile.trim()).toString());
            } catch (IOException e) {
                logMethod = new LogConsolePrinter();
                VensimLogger logger = VensimLogger.getInstance();
                logger.error("I/O Exception while trying to access the log output file. Message: " + e.getMessage());
            }
        }

        VensimLogger.setOutputMethod(logMethod);
        VensimLogger.logAllServerCommunications(logServerComms);

        Iterable<InputFile> files = sensorContext.fileSystem().inputFiles(p.hasLanguage(VensimLanguage.KEY));
        List<InputFile> list = new ArrayList<>();
        files.forEach(list::add);
        List<InputFile> inputFiles = Collections.unmodifiableList(list);

        ServiceController controller = new ServiceController(dictionaryService);
        controller.authenticate(dictionaryUsername, dictionaryPassword);

        VensimScanner scanner = new VensimScanner(sensorContext, checks, new OutputFilesGenerator(createGetDiffFile), controller);
        scanner.scanFiles(inputFiles);
    }
}
