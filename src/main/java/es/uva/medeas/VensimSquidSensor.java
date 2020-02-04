package es.uva.medeas;

import es.uva.medeas.plugin.VensimLanguage;
import es.uva.medeas.plugin.VensimRuleRepository;
import es.uva.medeas.plugin.VensimScanner;
import es.uva.medeas.rules.VensimCheck;
import es.uva.medeas.utilities.JsonSymbolTableBuilder;
import es.uva.medeas.utilities.ServiceController;
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
    private static final String symbolDbService = "http://localhost:9999/symbols/integrationTest";

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
     

        Iterable<InputFile> files = sensorContext.fileSystem().inputFiles(p.hasLanguage(VensimLanguage.KEY));
        List<InputFile> list = new ArrayList<>();
        files.forEach(list::add);
        List<InputFile> inputFiles = Collections.unmodifiableList(list);
        VensimScanner scanner = new VensimScanner(sensorContext, checks, new JsonSymbolTableBuilder(), new ServiceController(symbolDbService));
        scanner.scanFiles(inputFiles);
    }
}
