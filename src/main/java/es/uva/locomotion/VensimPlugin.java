package es.uva.locomotion;

import es.uva.locomotion.plugin.VensimLanguage;
import es.uva.locomotion.plugin.VensimQualityProfile;
import es.uva.locomotion.plugin.VensimRuleRepository;
import org.sonar.api.Plugin;


public class VensimPlugin implements Plugin {


    public static final String PLUGIN_KEY = "vensim"; //Must be the same key as in the pom.xml

    @Override
    public void define(Context context) {

        context.addExtensions(VensimLanguage.class,
                VensimQualityProfile.class,
                VensimSquidSensor.class,
                VensimRuleRepository.class
        );

    }

}
