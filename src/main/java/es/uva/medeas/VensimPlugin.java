package es.uva.medeas;

import es.uva.medeas.VensimSquidSensor;
import es.uva.medeas.plugin.VensimLanguage;
import es.uva.medeas.plugin.VensimQualityProfile;
import es.uva.medeas.plugin.VensimRuleRepository;
import org.sonar.api.Plugin;

public class VensimPlugin implements Plugin {


    @Override
    public void define(Context context) {


        context.addExtensions(VensimLanguage.class,
                VensimQualityProfile.class,
                VensimSquidSensor.class,
                VensimRuleRepository.class
        );

    }

}
