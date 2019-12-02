package es.uva.medeas.testutilities;


import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;




public class UtilitiesAPI {

    public static void runSonarScanner(File folder,String token) throws IOException {
        Process process2=Runtime.getRuntime().exec("sonar-scanner -Dsonar.login="+token,
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


            return object.getJsonArray("issues");
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

    }
}
