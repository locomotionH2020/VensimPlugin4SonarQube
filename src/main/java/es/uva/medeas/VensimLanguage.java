package es.uva.medeas;

import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

public class VensimLanguage extends AbstractLanguage {

    public static final String KEY = "mdl";
    public static final String NAME = "Vensim";

    private static final String[] DEFAULT_FILE_SUFFIXES = { ".mdl" };

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
