package es.uva.medeas.testutilities;

import static org.junit.Assert.*;

import es.uva.medeas.rules.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import static es.uva.medeas.testutilities.UtilitiesAPI.*;

public class TestAPI {


    private static final String SONAR_TOKEN = System.getenv("SONAR_TOKEN");
    private static File integrationTestsFolder;

    @BeforeClass
    public static void runSonarScanner() throws IOException {
        File file = new File(
                TestUtilities.class.getClassLoader().getResource(".").getFile()
        );


       integrationTestsFolder = new File(file.getParentFile().getParentFile(),"integrationTests");

       UtilitiesAPI.runSonarScanner(integrationTestsFolder,SONAR_TOKEN);

    }






    private void assertIssueLine(JsonObject issue, int line){
        assertEquals(line,issue.getInt("line"));
    }

    private void assertIssueType(JsonObject issue, String rule_key){
        assertEquals("vensim:"+rule_key,issue.getString("rule"));
    }

    @Test
    public void testSonarSubscriptName() throws IOException {
      JsonArray issues =   getIssues("testSubscriptName.mdl",SONAR_TOKEN);


      assertEquals(1,issues.size());
      JsonObject issue = issues.getJsonObject(0);

      assertIssueLine(issue,3);
      assertIssueType(issue,SubscriptNameCheck.CHECK_KEY);


    }

    @Test
    public void testSonarSubscriptValue() throws IOException {
        JsonArray issues =   getIssues("testSubscriptValues.mdl",SONAR_TOKEN);


        assertEquals(1,issues.size());
        JsonObject issue = issues.getJsonObject(0);

        assertIssueLine(issue,4);
        assertIssueType(issue, SubscriptValueNameCheck.CHECK_KEY);


    }

    @Test
    public void testSonarLookupName() throws IOException{
        JsonArray issues =   getIssues("testLookupName.mdl",SONAR_TOKEN);


        assertEquals(1,issues.size());
        JsonObject issue = issues.getJsonObject(0);

        assertIssueLine(issue,3);
        assertIssueType(issue, LookupNameCheck.CHECK_KEY);
    }

    @Test
    public void testSonarVariableName() throws IOException{
        JsonArray issues =   getIssues("testVariableName.mdl",SONAR_TOKEN);


        assertEquals(1,issues.size());
        JsonObject issue = issues.getJsonObject(0);

        assertIssueLine(issue,2);
        assertIssueType(issue, VariableNameCheck.CHECK_KEY);
    }

    @Test
    public void testSonarConstantName() throws IOException{
        JsonArray issues =   getIssues("testConstantName.mdl",SONAR_TOKEN);


        assertEquals(1,issues.size());
        JsonObject issue = issues.getJsonObject(0);

        assertIssueLine(issue,5);
        assertIssueType(issue, ConstantNameCheck.CHECK_KEY);
    }

    @Test
    public void testRealityCheckName() throws IOException{
        JsonArray issues =   getIssues("testRealityCheckName.mdl",SONAR_TOKEN);


        assertEquals(1,issues.size());
        JsonObject issue = issues.getJsonObject(0);

        assertIssueLine(issue,1);
        assertIssueType(issue, RealityCheckNameRule.CHECK_KEY);
    }


    @Test
    public void testSymbolTableOutput() throws IOException{
        File file = new File(integrationTestsFolder,"symbolTable.json");
        //TODO delete file after test completition
        InputStream fis = new FileInputStream(file);

        JsonReader reader = Json.createReader(fis);

        JsonArray jsonFile = reader.readArray();


        Map<String,JsonObject> filesAnalyzed = new HashMap<>();
        for(int i=0;i<jsonFile.size();i++){
            JsonObject object = jsonFile.getJsonObject(i);
            filesAnalyzed.put(object.getString("file"),object);
        }

        assertTrue(filesAnalyzed.keySet().contains("testSubscriptName.mdl"));

        JsonReader jsonReader = Json.createReader(new StringReader( "{\"COUNTRY1\":{\"type\":\"SUBSCRIPT_VALUE\",\"lines\":[1,3],\"dependencies\":[]},\"COUNTRY2\":{\"type\":\"SUBSCRIPT_VALUE\",\"lines\":[1,3],\"dependencies\":[]},\"COUNTERY_ENUM\":{\"type\":\"SUBSCRIPT_NAME\",\"lines\":[3],\"dependencies\":[]},\"Time\":{\"type\":\"VARIABLE\",\"lines\":[],\"dependencies\":[]},\"MY_10_FAVORITE_COUNTRIES_ENUM\":{\"type\":\"SUBSCRIPT_NAME\",\"lines\":[1],\"dependencies\":[]}}\n"    ));
        JsonObject expectedObjectSubscriptName = jsonReader.readObject();
        jsonReader.close();


        assertEquals(expectedObjectSubscriptName,filesAnalyzed.get("testSubscriptName.mdl").getJsonObject("symbols"));
        assertNotNull(filesAnalyzed.get("testSubscriptValues.mdl").getJsonObject("symbols"));
    }
}
