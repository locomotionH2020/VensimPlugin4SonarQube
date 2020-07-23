package es.uva.locomotion;

import es.uva.locomotion.plugin.VensimLanguage;
import es.uva.locomotion.plugin.VensimRuleRepository;
import es.uva.locomotion.plugin.VensimScanner;
import es.uva.locomotion.rules.VensimCheck;
import es.uva.locomotion.utilities.JsonSymbolTableBuilder;
import es.uva.locomotion.service.ServiceController;
import es.uva.locomotion.utilities.LogConsolePrinter;
import es.uva.locomotion.utilities.LogOutputMethod;
import es.uva.locomotion.utilities.VensimLogger;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class VensimSquidSensor implements Sensor {


    private static final String NAME = "Vensim Squid Sensor";
    private static final String DICTIONARY_SERVICE_PARAMETER = "vensim.dictionaryService";
    private static final String DICTIONARY_USERNAME_PARAMETER =  "vensim.dictionaryUsername";
    private static final String DICTIONARY_PASSWORD_PARAMETER = "vensim.dictionaryPassword";
    private static final String DICTIONARY_LOG_SERVER_COMMUNICATIONS = "vensim.logServerMessages";

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
        String dictionaryPassword = sensorContext.config().get(DICTIONARY_PASSWORD_PARAMETER).orElse("").trim();
        String strLogServerComms = sensorContext.config().get(DICTIONARY_LOG_SERVER_COMMUNICATIONS).orElse("false");
        boolean logServerComms = !"false".equals(strLogServerComms);

        VensimLogger.setOutputMethod(new LogConsolePrinter());
        VensimLogger.logAllServerCommunications(logServerComms);

        Iterable<InputFile> files = sensorContext.fileSystem().inputFiles(p.hasLanguage(VensimLanguage.KEY));
        List<InputFile> list = new ArrayList<>();
        files.forEach(list::add);
        List<InputFile> inputFiles = Collections.unmodifiableList(list);

        VensimScanner scanner = new VensimScanner(sensorContext, checks, new JsonSymbolTableBuilder(),
                new ServiceController(dictionaryService, dictionaryUsername, dictionaryPassword));
        scanner.scanFiles(inputFiles);
    }
}
