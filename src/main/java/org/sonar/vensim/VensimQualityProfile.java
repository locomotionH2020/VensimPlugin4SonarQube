package org.sonar.vensim;

import org.sonar.api.rule.Severity;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.vensim.rules.NotEmptyCheck;

public final class VensimQualityProfile implements BuiltInQualityProfilesDefinition {



    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("Vensim Rules", VensimLanguage.KEY);
        profile.setDefault(true);

        String REPO_KEY = VensimRuleRepository.REPOSITORY_KEY;
        NewBuiltInActiveRule rule1 = profile.activateRule(REPO_KEY, NotEmptyCheck.CHECK_KEY);
        rule1.overrideSeverity(Severity.MAJOR);
        profile.done();
    }

}
