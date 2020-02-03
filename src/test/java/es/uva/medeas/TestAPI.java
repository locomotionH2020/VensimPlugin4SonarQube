package es.uva.medeas;

import static org.junit.Assert.*;

import es.uva.medeas.rules.*;
import es.uva.medeas.testutilities.TestUtilities;
import es.uva.medeas.testutilities.UtilitiesAPI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.json.*;

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

    @AfterClass
    public static void deleteJsonTableOutputFile(){
        File file = new File(integrationTestsFolder,"symbolTable.json");
        if(file.exists())
            file.delete();
    }



    @Test
    public void testSonarSubscriptName() throws IOException {
      JsonArray issues =   getIssues("testSubscriptName.mdl",SONAR_TOKEN);
      List<JsonObject> issueList =  filterIssuesOfType(issues, SubscriptNameCheck.CHECK_KEY);

      assertEquals(1,issueList.size());
      JsonObject issue = issueList.get(0);;

      assertIssueLine(issue,3);
      assertIssueType(issue,SubscriptNameCheck.CHECK_KEY);


    }

    @Test
    public void testSonarSubscriptValue() throws IOException {
        JsonArray issues =   getIssues("testSubscriptValues.mdl",SONAR_TOKEN);
        List<JsonObject> issueList =  filterIssuesOfType(issues, SubscriptValueNameCheck.CHECK_KEY);

        assertEquals(1,issueList.size());
        JsonObject issue = issueList.get(0);

        assertIssueLine(issue,4);
        assertIssueType(issue, SubscriptValueNameCheck.CHECK_KEY);


    }

    @Test
    public void testSonarLookupName() throws IOException{
        JsonArray issues =   getIssues("testLookupName.mdl",SONAR_TOKEN);
        List<JsonObject> issueList =  filterIssuesOfType(issues, LookupNameCheck.CHECK_KEY);

        assertEquals(1,issueList.size());
        JsonObject issue = issueList.get(0);

        assertIssueLine(issue,3);
    }

    @Test
    public void testSonarVariableName() throws IOException{
        JsonArray issues =   getIssues("testVariableName.mdl",SONAR_TOKEN);
        List<JsonObject> issueList =  filterIssuesOfType(issues, VariableNameCheck.CHECK_KEY);


        assertEquals(1,issueList.size());
        JsonObject issue = issueList.get(0);

        assertIssueLine(issue,2);
    }

    @Test
    public void testSonarConstantName() throws IOException{
        JsonArray issues =   getIssues("testConstantName.mdl",SONAR_TOKEN);
        List<JsonObject> issueList =  filterIssuesOfType(issues, ConstantNameCheck.CHECK_KEY);


        assertEquals(1,issueList.size());
        JsonObject issue = issueList.get(0);

        assertIssueLine(issue,5);
    }

    @Test
    public void testRealityCheckName() throws IOException{
        JsonArray issues =   getIssues("testRealityCheckName.mdl",SONAR_TOKEN);
        List<JsonObject> issueList = filterIssuesOfType(issues,RealityCheckNameRule.CHECK_KEY);


        assertEquals(1,issueList.size());
        JsonObject issue = issueList.get(0);

        assertIssueLine(issue,1);
    }

    @Test
    public void testMagicNumberCheck() throws IOException{
        JsonArray issues =  getIssues("testMagicNumber.mdl",SONAR_TOKEN);
        List<JsonObject> issueList =  filterIssuesOfType(issues, MagicNumberCheck.CHECK_KEY);

        assertEquals(5,issueList.size());


        for(JsonObject issue: issueList) {
            assertIssueLine(issue, 1);
        }
    }

    @Test
    public void testSymbolNotDefinedInDictionaryCheck() throws IOException{
        JsonArray issues =  getIssues("testSymbolNotDefinedInDictionary.mdl",SONAR_TOKEN);
        List<JsonObject> issueList =  filterIssuesOfType(issues, SymbolNotFoundInDBCheck.CHECK_KEY);

        assertEquals(1,issueList.size());

        assertIssueLine(issueList.get(0),3);
    }


    @Test
    public void testSymbolTableOutput() throws IOException{
        File file = new File(integrationTestsFolder,"symbolTable.json");
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
