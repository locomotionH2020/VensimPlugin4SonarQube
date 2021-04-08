package es.uva.locomotion.testutilities;


import es.uva.locomotion.VensimPlugin;
import org.sonar.api.batch.rule.Severity;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class APIUtilities {

    public static void runSonarScanner(File folder,String token) throws IOException {

        Process process2=Runtime.getRuntime().exec("sonar-scanner.bat",
                null, folder);

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


    public static JsonArray getIssues(String file_name,String token) throws IOException {
        URI url = URI.create("http://localhost:9000/api/issues/search?statuses=OPEN&componentKeys=integrationTests:"+file_name);

        HttpClient client = HttpClient.newBuilder()
                .authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(token, "".toCharArray());
                    }
                })
                .build();


        var request = HttpRequest.newBuilder()
                .uri(url)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
           String content = response.body();
            JsonReader jsonReader = Json.createReader(new StringReader(content));
            JsonObject object = jsonReader.readObject();
            jsonReader.close();

            JsonArray issues = object.getJsonArray("issues");
            if(issues.size()==0){
                System.out.println("Warning: The file '" + file_name+  "' doesn't have issues. You might have written the wrong file name.");
            }


            return issues;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

    }



    public static void assertIssueLine(JsonObject issue, int line){
        assertEquals(line,issue.getInt("line"));
    }

    public static void assertIssueType(JsonObject issue, String rule_key){
        assertTrue(issueIsType(issue,rule_key));
    }

    public static Severity getIssueSeverity(JsonObject issue){
        return Severity.valueOf(issue.getString("severity"));
    }

    public static boolean issueIsType(JsonObject issue, String rule_key){
        String expectedKey = VensimPlugin.PLUGIN_KEY+ ":"+rule_key;
        return expectedKey.equals(issue.getString("rule"));
    }


    public static List<JsonObject> filterIssuesOfType(JsonArray issues, String rule_key){
         return issues.stream().filter(issue -> issueIsType((JsonObject) issue, rule_key)).
                map(value -> (JsonObject) value).collect(Collectors.toList());
    }
}
