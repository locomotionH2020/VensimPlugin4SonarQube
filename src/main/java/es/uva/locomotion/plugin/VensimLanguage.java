package es.uva.locomotion.plugin;

import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

public class VensimLanguage extends AbstractLanguage {

    public static final String KEY = "mdl";
    public static final String NAME = "Vensim";

    public static final String VENSIM_PLAIN_TEXT_SUFIX = ".mdl";
    private static final String[] DEFAULT_FILE_SUFFIXES = { VENSIM_PLAIN_TEXT_SUFIX };

    private Configuration configuration;

    public VensimLanguage(Configuration configuration) {
        super(KEY, NAME);
        this.configuration = configuration;
    }

    @Override
    public String[] getFileSuffixes() {

        return VensimLanguage.DEFAULT_FILE_SUFFIXES;
    }


}
