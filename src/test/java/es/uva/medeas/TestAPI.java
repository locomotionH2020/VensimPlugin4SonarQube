package es.uva.medeas;

import static org.junit.Assert.*;

import es.uva.medeas.rules.SubscriptNameCheck;
import es.uva.medeas.rules.SubscriptValueNameCheck;
import es.uva.medeas.rules.TestUtilities;
import es.uva.medeas.rules.VensimCheck;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import static es.uva.medeas.UtilitiesAPI.*;

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

        JsonReader jsonReader = Json.createReader(new StringReader("{\"COUNTRY1\":{\"type\":\"SUBSCRIPT_VALUE\",\"line\":3,\"dependencies\":[]},\"COUNTRY2\":{\"type\":\"SUBSCRIPT_VALUE\",\"line\":3,\"dependencies\":[]},\"COUNTERY_ENUM\":{\"type\":\"SUBSCRIPT_NAME\",\"line\":3,\"dependencies\":[]},\"Time\":{\"type\":\"VARIABLE\",\"line\":-1,\"dependencies\":[]},\"MY_10_FAVORITE_COUNTRIES_ENUM\":{\"type\":\"SUBSCRIPT_NAME\",\"line\":1,\"dependencies\":[]}}"));
        JsonObject expectedObjectSubscriptName = jsonReader.readObject();
        jsonReader.close();
        

        assertEquals(expectedObjectSubscriptName,filesAnalyzed.get("testSubscriptName.mdl").getJsonObject("symbols"));
        assertNotNull(filesAnalyzed.get("testSubscriptValues.mdl").getJsonObject("symbols"));
    }
}
