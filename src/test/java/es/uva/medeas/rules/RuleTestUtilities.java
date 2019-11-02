package es.uva.medeas.rules;

import es.uva.medeas.Issue;
import es.uva.medeas.VensimRuleRepository;
import es.uva.medeas.VensimScanner;
import es.uva.medeas.VensimVisitorContext;
import es.uva.medeas.rules.VensimCheck;
import org.antlr.v4.runtime.tree.ParseTree;
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

import static es.uva.medeas.rules.TestUtilities.getParseTreeFromString;
import static es.uva.medeas.rules.TestUtilities.getSymbolTableFromString;

public class RuleTestUtilities {


    public static ActiveRules getAllActiveRules() throws NoSuchFieldException, IllegalAccessException {
        ActiveRulesBuilder builder = new ActiveRulesBuilder();


        for(Class check:VensimRuleRepository.getChecks()) {
            String check_key = (String) check.getField("CHECK_KEY").get(null);
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
        return  new VensimScanner(context,checks);
    }

    public static void assertHasIssue(VensimVisitorContext context,Class type, int line){
        boolean found = false;
        List<Integer> foundInOtherLines = new ArrayList<>();

        for(Issue issue:context.getIssues()){
            if(issue.getCheck().getClass().equals(type)) {
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


    public static VensimVisitorContext getVisitorContextFromString(String program){
        ParseTree root =  getParseTreeFromString(program);
        VensimVisitorContext visitorContext = new VensimVisitorContext(root);
        visitorContext.setSymbolTable(getSymbolTableFromString(program));
        return visitorContext;
    }



}


