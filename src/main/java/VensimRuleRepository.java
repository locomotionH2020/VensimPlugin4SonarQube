import org.sonar.api.server.rule.RulesDefinition;
import rules.NotEmptyCheck;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class VensimRuleRepository implements RulesDefinition{

    private static final String REPOSITORY_NAME = "VensimRules";
    public static final String REPOSITORY_KEY = "vensim";




    @Override
    public void define(RulesDefinition.Context context) {

        RulesDefinition.NewRepository repository = context
                .createRepository(REPOSITORY_KEY, VensimLanguage.KEY)
                .setName(REPOSITORY_NAME);

        repository.createRule(NotEmptyCheck.CHECK_KEY).setName("NotEmptyCheck").setMarkdownDescription("The file shouldn't be empty");
        repository.done();
    }

    private static List<Class> getCheckClasses() {
        return StreamSupport.stream(getChecks().spliterator(), false)
                .collect(Collectors.toList());
    }

    public static Iterable<Class> getChecks(){
        HashSet<Class> set = new HashSet<>();
        set.add(NotEmptyCheck.class);
        return set;
    }

}
