package es.uva.medeas.plugin;

import es.uva.medeas.rules.*;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;


public final class VensimQualityProfile implements BuiltInQualityProfilesDefinition {



    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("Vensim Rules", VensimLanguage.KEY);
        profile.setDefault(true);

        String REPO_KEY = VensimRuleRepository.REPOSITORY_KEY;

        profile.activateRule(REPO_KEY, SubscriptNameCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, SubscriptValueNameCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, LookupNameCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, VariableNameCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, ConstantNameCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY,RealityCheckNameRule.CHECK_KEY);
        profile.activateRule(REPO_KEY,MagicNumberCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, SymbolNotDefinedInDictionaryCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, SymbolWithoutCommentCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY,SymbolWithoutUnitsCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY,DictionaryTypeMismatchCheck.CHECK_KEY);


        profile.done();
    }

}
