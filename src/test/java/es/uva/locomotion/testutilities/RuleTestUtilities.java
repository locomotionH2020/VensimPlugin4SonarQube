package es.uva.locomotion.testutilities;

import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.parser.ModelParser;
import es.uva.locomotion.service.ServiceController;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.plugin.VensimRuleRepository;
import es.uva.locomotion.plugin.VensimScanner;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.rules.VensimCheck;
import es.uva.locomotion.utilities.JsonSymbolTableBuilder;
import org.mockito.Mockito;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.rule.CheckFactory;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.batch.rule.internal.NewActiveRule;

import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.rule.RuleKey;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

import static es.uva.locomotion.testutilities.GeneralTestUtilities.getParseTreeFromString;
import static es.uva.locomotion.testutilities.GeneralTestUtilities.getSymbolTableFromString;

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
        ActiveRules rules;
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

        return  new VensimScanner(context,checks, new JsonSymbolTableBuilder(), Mockito.mock(ServiceController.class));
    }

    public static void assertHasIssueInLines(VensimVisitorContext context, Class type, Integer... lines){
        assertTrue("You must specify the lines where you expect the issue",lines.length>0);

        List<Integer> foundIn = new ArrayList<>();

        for(Issue issue:context.getIssues()){
            if(isSameRule(issue.getCheck().getClass(),type)) {
                    foundIn.add(issue.getLine());
            }
        }
        List<Integer> expected = Arrays.asList(lines);

        if(foundIn.isEmpty() && !expected.isEmpty())
            throw new AssertionError("The context doesn't have any issue of type '"+ type.getSimpleName() + ".");
        if(!foundIn.containsAll(expected))
            throw new AssertionError("Issue of type: '" + type.getSimpleName() + "' not found in lines "+ expected + ".But was found in lines: " + foundIn );


    }

    private static boolean isSameRule(Class rule1, Class rule2){ //Compares check-keys instead of classes to allow the use of mocks
        try {
            return rule1.getField(CHECK_KEY_FIELD).equals(rule2.getField(CHECK_KEY_FIELD));
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void assertDoesntHaveIssueInLines(VensimVisitorContext context, Class type, Integer... lines){
        assertTrue("You must specify the lines where an issue isn't expected. To test that the context doesnt have any issue you can use 'assertDoesntHaveIssueOfType'",lines.length>0);

        List<Integer> lineList = Arrays.asList(lines);
        for(Issue issue:context.getIssues()){
            assertFalse(issue.getCheck().getClass().equals(type) && lineList.contains(issue.getLine()));
        }


    }
    public static void assertDoesntHaveIssueOfType(VensimVisitorContext context, Class type){
        for(Issue issue:context.getIssues()){
            assertNotEquals("Found unexpected issue in line: " + issue.getLine() ,issue.getCheck().getClass(), type);
        }
    }


    public static VensimVisitorContext getVisitorContextFromString(String program){
        ModelParser.FileContext root =  getParseTreeFromString(program);
        File file = new File(
                GeneralTestUtilities.class.getClassLoader().getResource(".").getFile()
        );
        SensorContextTester contextTester = SensorContextTester.create(file);
        return new VensimVisitorContext(root,getSymbolTableFromString(program), new ViewTable(), contextTester, null);
    }



}


