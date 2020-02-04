package es.uva.medeas.testutilities;

import es.uva.medeas.utilities.ServiceController;
import es.uva.medeas.parser.ModelParser;
import es.uva.medeas.plugin.Issue;
import es.uva.medeas.plugin.VensimRuleRepository;
import es.uva.medeas.plugin.VensimScanner;
import es.uva.medeas.plugin.VensimVisitorContext;
import es.uva.medeas.rules.VensimCheck;
import es.uva.medeas.utilities.JsonSymbolTableBuilder;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.batch.rule.internal.NewActiveRule;

import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.rule.RuleKey;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

import static es.uva.medeas.testutilities.TestUtilities.getParseTreeFromString;
import static es.uva.medeas.testutilities.TestUtilities.getSymbolTableFromString;

public class RuleTestUtilities {

    private static final String CHECK_KEY_FIELD = "CHECK_KEY";


    public static ActiveRules getAllActiveRules() throws NoSuchFieldException, IllegalAccessException {
        ActiveRulesBuilder builder = new ActiveRulesBuilder();


        for(Class check:VensimRuleRepository.getChecks()) {
            String check_key = (String) check.getField(CHECK_KEY_FIELD).get(null);
            RuleKey checkRuleKey = RuleKey.of(VensimRuleRepository.REPOSITORY_KEY, check_key );
            NewActiveRule rule = new NewActiveRule.Builder().setRuleKey(checkRuleKey).build();

            builder.addRule(rule);
        }

       return builder.build();
    }



    public static VensimScanner getScanner(){
        ActiveRules rules = null;
        try {
            rules = getAllActiveRules();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        CheckFactory factory = new CheckFactory(rules);

        Checks<VensimCheck> checks =  factory.<VensimCheck>create(VensimRuleRepository.REPOSITORY_KEY)
                .addAnnotatedChecks(VensimRuleRepository.getChecks());

        Path path = Paths.get("");
        SensorContextTester context = SensorContextTester.create(path);
        return  new VensimScanner(context,checks, new JsonSymbolTableBuilder(), new ServiceController("")); //TODO revisar uso del metodo y si debería hacer mock de
        //service controller
    }

    public static void assertHasIssue(VensimVisitorContext context,Class type, int line){
        boolean found = false;
        List<Integer> foundInOtherLines = new ArrayList<>();

        for(Issue issue:context.getIssues()){
            if(isSameRule(issue.getCheck().getClass(),type)) {
                if(issue.getLine()==line)
                    found = true;
                else
                    foundInOtherLines.add(issue.getLine());
            }
        }

        if(!found)
            if(foundInOtherLines.isEmpty())
                throw new AssertionError("Issue of type '"+ type.getSimpleName() + "'  not found.");
            else
                throw new AssertionError("Issue of type: '" + type.getSimpleName() + "' not found in line "+ line + ".But was found in lines: " + foundInOtherLines );
    }

    private static boolean isSameRule(Class rule1, Class rule2){ //Compares check-keys instead of classes to allow the use of mocks
        try {
            return rule1.getField(CHECK_KEY_FIELD).equals(rule2.getField(CHECK_KEY_FIELD));
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void assertDoesntHaveIssue(VensimVisitorContext context,Class type, int line){
        for(Issue issue:context.getIssues()){
            assertFalse(issue.getCheck().getClass().equals(type) && issue.getLine()==line);
        }


    }


    public static VensimVisitorContext getVisitorContextFromString(String program){
        ModelParser.FileContext root =  getParseTreeFromString(program);
        return new VensimVisitorContext(root,getSymbolTableFromString(program),null);
    }



}


