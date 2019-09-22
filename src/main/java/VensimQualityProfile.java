

import org.sonar.api.rule.Severity;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import rules.NotEmptyCheck;

public final class VensimQualityProfile implements BuiltInQualityProfilesDefinition {



    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("Vensim Rules", VensimLanguage.KEY);
        profile.setDefault(true);

        String REPO_KEY = VensimRuleRepository.REPOSITORY_KEY;
        profile.activateRule(REPO_KEY, NotEmptyCheck.CHECK_KEY);



        profile.done();
    }

}
