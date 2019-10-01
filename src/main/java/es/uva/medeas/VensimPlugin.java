package es.uva.medeas;

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
