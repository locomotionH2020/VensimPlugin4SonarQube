package es.uva.medeas;

import es.uva.medeas.rules.SubscriptNameCheck;
import es.uva.medeas.rules.SubscriptValueNameCheck;
import org.sonar.api.server.rule.RulesDefinition;

import java.util.HashSet;


public class VensimRuleRepository implements RulesDefinition{

    private static final String REPOSITORY_NAME = "VensimRules";
    public static final String REPOSITORY_KEY = "vensim";



    @Override
    public void define(RulesDefinition.Context context) {

        RulesDefinition.NewRepository repository = context
                .createRepository(REPOSITORY_KEY, VensimLanguage.KEY)
                .setName(REPOSITORY_NAME);


        repository.createRule(SubscriptNameCheck.CHECK_KEY).setName(SubscriptNameCheck.NAME).setHtmlDescription(SubscriptNameCheck.HTML_DESCRIPTION);
        
        repository.createRule(SubscriptValueNameCheck.CHECK_KEY).setName(SubscriptValueNameCheck.NAME).setHtmlDescription(SubscriptValueNameCheck.HTML_DESCRIPTION);



        repository.done();
    }



    public static Iterable<Class> getChecks(){
        HashSet<Class> set = new HashSet<>();
        set.add(SubscriptNameCheck.class);
        set.add(SubscriptValueNameCheck.class);
        return set;
    }

}
