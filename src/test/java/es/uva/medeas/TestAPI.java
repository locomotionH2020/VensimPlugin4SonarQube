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
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class TestAPI {


    private static final String SONAR_TOKEN = System.getenv("SONAR_TOKEN");

    @BeforeClass
    public static void runSonarScanner() throws IOException {
        File file = new File(
                TestUtilities.class.getClassLoader().getResource(".").getFile()
        );



       File integrationTestsFolder = new File(file.getParentFile().getParentFile(),"integrationTests");

        Process process2=Runtime.getRuntime().exec("sonar-scanner " +
                        "  -Dsonar.projectKey=integrationTests " +
                        "  -Dsonar.sources=." +
                        "  -Dsonar.host.url=http://localhost:9000 " +
                        "  -Dsonar.login="+SONAR_TOKEN,
                null, integrationTestsFolder);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(process2.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(process2.getErrorStream()));

        String s;

        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public JsonArray getIssues(String file_name) throws IOException {

        URL url = new URL("http://localhost:9000/api/issues/search?statuses=OPEN&componentKeys=integrationTests:"+file_name);
        // Statuses=OPEN is required because sometimes there are some issues that shouldn't appear but are still stored.
        // For example if you change the key of a rule, all the issues with the previous key will remain as 'zombie' issues.
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        String auth = Base64.getEncoder().encodeToString((SONAR_TOKEN+":").getBytes());
        con.setRequestProperty  ("Authorization", "Basic " + auth);

        con.setRequestProperty("Content-Type", "application/json");
        //TODO Refactor

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        JsonReader jsonReader = Json.createReader(new StringReader(content.toString()));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();


        return object.getJsonArray("issues");

    }


    private void assertIssueLine(JsonObject issue, int line){
        assertEquals(line,issue.getInt("line"));
    }

    private void assertIssueType(JsonObject issue, String rule_key){
        assertEquals("vensim:"+rule_key,issue.getString("rule"));
    }

    @Test
    public void testSonarSubscriptName() throws IOException {
      JsonArray issues =   getIssues("testSubscriptName.mdl");


      assertEquals(1,issues.size());
      JsonObject issue = issues.getJsonObject(0);

      assertIssueLine(issue,3);
      assertIssueType(issue,SubscriptNameCheck.CHECK_KEY);


    }

    @Test
    public void testSonarSubscriptValue() throws IOException {
        JsonArray issues =   getIssues("testSubscriptValues.mdl");


        assertEquals(1,issues.size());
        JsonObject issue = issues.getJsonObject(0);

        assertIssueLine(issue,4);
        assertIssueType(issue, SubscriptValueNameCheck.CHECK_KEY);


    }
}
