package es.uva.medeas;

import es.uva.medeas.rules.SubscriptConventionCheck;
import org.sonar.api.rule.Severity;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import es.uva.medeas.rules.NotEmptyCheck;

public final class VensimQualityProfile implements BuiltInQualityProfilesDefinition {



    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("Vensim Rules", VensimLanguage.KEY);
        profile.setDefault(true);

        String REPO_KEY = VensimRuleRepository.REPOSITORY_KEY;
        NewBuiltInActiveRule rule1 = profile.activateRule(REPO_KEY, NotEmptyCheck.CHECK_KEY);
        rule1.overrideSeverity(Severity.MAJOR);
        NewBuiltInActiveRule rule2 = profile.activateRule(REPO_KEY, SubscriptConventionCheck.CHECK_KEY);
        profile.done();
    }

}
