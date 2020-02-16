package es.uva.medeas.plugin;

import es.uva.medeas.rules.*;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class VensimRuleRepository implements RulesDefinition{

    private static final String REPOSITORY_NAME = "VensimRules";
    public static final String REPOSITORY_KEY = "vensim";


    @Override
    public void define(RulesDefinition.Context context) {

        RulesDefinition.NewRepository repository = context
                .createRepository(REPOSITORY_KEY, VensimLanguage.KEY)
                .setName(REPOSITORY_NAME);


        RulesDefinitionAnnotationLoader loader = new RulesDefinitionAnnotationLoader();
        List<Class> checks = new ArrayList<>();
        getChecks().forEach(checks::add);

        loader.load(repository, checks.toArray(new Class[0]));




        repository.done();
    }



    public static Iterable<Class> getChecks(){
        HashSet<Class> set = new HashSet<>();
        set.add(SubscriptNameCheck.class);
        set.add(SubscriptValueNameCheck.class);
        set.add(LookupNameCheck.class);
        set.add(VariableNameCheck.class);
        set.add(ConstantNameCheck.class);
        set.add(RealityCheckNameRule.class);
        set.add(MagicNumberCheck.class);
        set.add(SymbolNotDefinedInDictionaryCheck.class);
        set.add(SymbolWithoutCommentCheck.class);

        return set;
    }

}
