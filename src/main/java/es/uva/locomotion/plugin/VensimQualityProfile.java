package es.uva.locomotion.plugin;

import es.uva.locomotion.rules.*;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;


public final class VensimQualityProfile implements BuiltInQualityProfilesDefinition {


    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("Vensim Rules", VensimLanguage.KEY);
        profile.setDefault(true);

        String REPO_KEY = VensimRuleRepository.REPOSITORY_KEY;

        profile.activateRule(REPO_KEY, SubscriptNameCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, SubscriptCopyNameCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, SubscriptValueNameCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, LookupNameCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, VariableNameCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, ViewNameCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, SubcategoryDuplicatedCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, EmbeddedLookupCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, SymbolGroupCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, DictionarySymbolExcelRefCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, ConstantNameCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, RealityCheckNameRule.CHECK_KEY);
        profile.activateRule(REPO_KEY, MagicNumberCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, SymbolNotDefinedInDictionaryCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, SymbolWithoutCommentCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, SymbolWithoutUnitsCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, DictionaryTypeMismatchCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, DictionaryCommentMismatchCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, DictionaryUnitsMismatchCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, DictionarySubscriptValueMismatchCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, DictionaryIndexMismatchCheck.CHECK_KEY);
        profile.activateRule(REPO_KEY, DictionaryUnitSymbolCheck.CHECK_KEY);


        profile.done();
    }

}
