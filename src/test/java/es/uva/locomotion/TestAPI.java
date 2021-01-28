package es.uva.locomotion;

import static org.junit.Assert.*;

import es.uva.locomotion.rules.*;
import es.uva.locomotion.testutilities.GeneralTestUtilities;
import es.uva.locomotion.testutilities.APIUtilities;
import es.uva.locomotion.utilities.JsonSymbolTableBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.api.batch.rule.Severity;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.json.*;

import static es.uva.locomotion.testutilities.APIUtilities.*;

public class TestAPI {


    private static final String SONAR_TOKEN = System.getenv("SONAR_TOKEN");
    private static final String SERVER_FLAG_FILENAME= "flagInjection";
    private static File integrationTestsFolder;
    private static File mocksFolder;

    @BeforeClass
    public static void runSonarScanner() throws IOException {
        File file = new File(
                GeneralTestUtilities.class.getClassLoader().getResource(".").getFile()
        );

       integrationTestsFolder = new File(file.getParentFile().getParentFile(),"integrationTests");
       mocksFolder = new File(file.getParentFile().getParentFile(), "mocksServicios");

       File injectionFlag = new File(mocksFolder,SERVER_FLAG_FILENAME);
       if(injectionFlag.exists())
           injectionFlag.delete();

      APIUtilities.runSonarScanner(integrationTestsFolder,SONAR_TOKEN);


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
      JsonObject issue = issueList.get(0);

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

        Map<Boolean, List<JsonObject>> partitionedIssues = issueList.stream().collect(Collectors.partitioningBy(issue-> getIssueSeverity(issue)==Severity.MAJOR));
        List<JsonObject> majorIssues = partitionedIssues.get(true);
        List<JsonObject> otherIsues = partitionedIssues.get(false);

        assertEquals(5,majorIssues.size());
        assertEquals(1,otherIsues.size());

        for(JsonObject majorIssue: majorIssues) {
            assertIssueLine(majorIssue, 1);
            assertEquals(Severity.MAJOR,getIssueSeverity(majorIssue));
        }

        for(JsonObject otherIssue: otherIsues) {
            assertIssueLine(otherIssue, 3);
            assertEquals(Severity.INFO,getIssueSeverity(otherIssue));
        }
    }

    @Test
    public void testSymbolNotDefinedInDictionaryCheck() throws IOException{
        JsonArray issues =  getIssues("testSymbolNotDefinedInDictionary.mdl",SONAR_TOKEN);
        List<JsonObject> issueList =  filterIssuesOfType(issues, SymbolNotDefinedInDictionaryCheck.CHECK_KEY);

        assertEquals(1,issueList.size());

        assertIssueLine(issueList.get(0),3);
    }

    @Test
    public void testDictionaryCommentMismatchCheck() throws IOException {
        JsonArray issues =  getIssues("testDictionaryCommentMismatch.mdl",SONAR_TOKEN);
        List<JsonObject> issueList =  filterIssuesOfType(issues, DictionaryCommentMismatchCheck.CHECK_KEY);

        assertEquals(1,issueList.size());

        assertIssueLine(issueList.get(0),2);
    }

    @Test
    public void testDictionaryUnitsMismatchCheck() throws IOException {
        JsonArray issues =  getIssues("testDictionaryUnitsMismatch.mdl",SONAR_TOKEN);
        List<JsonObject> issueList =  filterIssuesOfType(issues, DictionaryUnitsMismatchCheck.CHECK_KEY);

        assertEquals(1,issueList.size());

        assertIssueLine(issueList.get(0),2);
    }

    @Test
    public void testDictionaryTypeMismatchCheck() throws IOException {
        JsonArray issues =  getIssues("testDictionaryTypeMismatch.mdl",SONAR_TOKEN);

        List<JsonObject> issueList =  filterIssuesOfType(issues, DictionaryTypeMismatchCheck.CHECK_KEY);

        assertEquals(1,issueList.size());

        assertIssueLine(issueList.get(0),2);
    }

    @Test
    public void testDictionarySubscriptValueMismatchCheck() throws IOException{
        JsonArray issues =  getIssues("testDictionarySubscriptValueMismatch.mdl",SONAR_TOKEN);

        List<JsonObject> issueList =  filterIssuesOfType(issues, DictionarySubscriptValueMismatchCheck.CHECK_KEY);

        assertEquals(1,issueList.size());

        assertIssueLine(issueList.get(0),3);
    }

    @Test
    public void testDictionaryIndexMismatchCheck() throws IOException{
        JsonArray issues =  getIssues("testDictionaryIndexMismatch.mdl",SONAR_TOKEN);

        List<JsonObject> issueList =  filterIssuesOfType(issues, DictionaryIndexMismatchCheck.CHECK_KEY);

        assertEquals(2,issueList.size());

       assertIssueLine(issueList.get(0),6);
        assertIssueLine(issueList.get(1),7);
    }


    @Test
    public void testSymbolWithoutCommentCheck() throws IOException{
        JsonArray issues =  getIssues("testSymbolWithoutCommentOrUnits.mdl",SONAR_TOKEN);
        List<JsonObject> issueList =  filterIssuesOfType(issues, SymbolWithoutCommentCheck.CHECK_KEY);

        assertEquals(1,issueList.size());

        assertIssueLine(issueList.get(0),3);
    }



    @Test
    public void testSymbolWithoutUnitsCheck() throws IOException{
        JsonArray issues =  getIssues("testSymbolWithoutCommentOrUnits.mdl",SONAR_TOKEN);
        List<JsonObject> issueList =  filterIssuesOfType(issues, SymbolWithoutUnitsCheck.CHECK_KEY);

        assertEquals(1,issueList.size());

        assertIssueLine(issueList.get(0),1);
    }

    @Test
    public void testSymbolInjection(){
        assertTrue(new File(mocksFolder,"flagInjection").exists());
    }



    @Test
    public void testJsonOutputNoCommentsAndNoUnits() throws IOException{
        Map<String, JsonObject> filesAnalyzed = getFilesAnalyzedJsonOutput();

        assertTrue(filesAnalyzed.containsKey("testJsonOutput.mdl"));

        JsonObject symbols = filesAnalyzed.get("testJsonOutput.mdl").getJsonObject("symbols");
        JsonObject symbol = symbols.getJsonObject("SYMBOL_WITHOUT_COMMENTS_OR_UNITS");

        assertEquals("",symbol.getString(JsonSymbolTableBuilder.KEY_COMMENT));
        assertEquals("", symbol.getString(JsonSymbolTableBuilder.KEY_UNITS));


    }

    @Test
    public void testJsonOutputWithCommentsAndUnits() throws IOException{
        Map<String, JsonObject> filesAnalyzed = getFilesAnalyzedJsonOutput();

        assertTrue(filesAnalyzed.containsKey("testJsonOutput.mdl"));

        JsonObject symbols = filesAnalyzed.get("testJsonOutput.mdl").getJsonObject("symbols");
        JsonObject symbol = symbols.getJsonObject("SYMBOL_WITH_COMMENTS_AND_UNITS");

        assertEquals("a comment",symbol.getString(JsonSymbolTableBuilder.KEY_COMMENT));
        assertEquals("units", symbol.getString(JsonSymbolTableBuilder.KEY_UNITS));

    }


    @Test
    public void testJsonOutputSymbolWithoutDependencies() throws IOException{
        Map<String, JsonObject> filesAnalyzed = getFilesAnalyzedJsonOutput();

        assertTrue(filesAnalyzed.containsKey("testJsonOutput.mdl"));

        JsonObject symbols = filesAnalyzed.get("testJsonOutput.mdl").getJsonObject("symbols");
        JsonObject symbol = symbols.getJsonObject("SYMBOL_WITHOUT_DEPENDENCIES");

        JsonArray dependencies = symbol.getJsonArray(JsonSymbolTableBuilder.KEY_DEPENDENCIES);

        assertTrue(dependencies.isEmpty());
    }

    @Test
    public void testJsonOutputSymbolWithOneDependency() throws IOException{
        Map<String, JsonObject> filesAnalyzed = getFilesAnalyzedJsonOutput();

        assertTrue(filesAnalyzed.containsKey("testJsonOutput.mdl"));

        JsonObject symbols = filesAnalyzed.get("testJsonOutput.mdl").getJsonObject("symbols");
        JsonObject symbol = symbols.getJsonObject("SYMBOL_WITH_ONE_DEPENDENCY");

        JsonArray dependencies = symbol.getJsonArray(JsonSymbolTableBuilder.KEY_DEPENDENCIES);

        assertEquals(1,dependencies.size());
        assertEquals("SYMBOL_WITHOUT_DEPENDENCIES",dependencies.getString(0));

    }

    @Test
    public void testJsonOutputSymbolWithMultipleDependencies() throws IOException{
        Map<String, JsonObject> filesAnalyzed = getFilesAnalyzedJsonOutput();

        assertTrue(filesAnalyzed.containsKey("testJsonOutput.mdl"));

        JsonObject symbols = filesAnalyzed.get("testJsonOutput.mdl").getJsonObject("symbols");
        JsonObject symbol = symbols.getJsonObject("SYMBOL_WITH_MULTIPLE_DEPENDENCIES");

        JsonArray dependencies = symbol.getJsonArray(JsonSymbolTableBuilder.KEY_DEPENDENCIES);

        assertEquals(2,dependencies.size());

        Set<String> actualDependencies = new HashSet<>();
        for(int i=0;i<dependencies.size();i++)
            actualDependencies.add(dependencies.getString(i));

        assertEquals(Set.of("Time","SYMBOL_WITHOUT_DEPENDENCIES"),actualDependencies);


    }

    @Test
    public void testJsonOutputWithoutDefinedLine() throws IOException{
        Map<String, JsonObject> filesAnalyzed = getFilesAnalyzedJsonOutput();

        assertTrue(filesAnalyzed.containsKey("testJsonOutput.mdl"));

        JsonObject symbols = filesAnalyzed.get("testJsonOutput.mdl").getJsonObject("symbols");
        JsonObject symbol = symbols.getJsonObject("Time");

        JsonArray lines = symbol.getJsonArray(JsonSymbolTableBuilder.KEY_LINES);

        assertEquals(0,lines.size());

    }

    @Test
    public void testJsonOutputSymbolDefinedOnce() throws IOException{
        Map<String, JsonObject> filesAnalyzed = getFilesAnalyzedJsonOutput();

        assertTrue(filesAnalyzed.containsKey("testJsonOutput.mdl"));

        JsonObject symbols = filesAnalyzed.get("testJsonOutput.mdl").getJsonObject("symbols");
        JsonObject symbol = symbols.getJsonObject("SYMBOL_WITHOUT_COMMENTS_OR_UNITS");

        JsonArray lines = symbol.getJsonArray(JsonSymbolTableBuilder.KEY_LINES);

        assertEquals(1,lines.size());
        assertEquals(1,lines.getInt(0));
    }

    @Test
    public void testJsonOutputSymbolDefinedMultipleTimes() throws IOException{
        Map<String, JsonObject> filesAnalyzed = getFilesAnalyzedJsonOutput();

        assertTrue(filesAnalyzed.containsKey("testJsonOutput.mdl"));

        JsonObject symbols = filesAnalyzed.get("testJsonOutput.mdl").getJsonObject("symbols");
        JsonObject symbol = symbols.getJsonObject("SYMBOL_DEFINED_MULTIPLE_TIMES");

        JsonArray lines = symbol.getJsonArray(JsonSymbolTableBuilder.KEY_LINES);
        Set<Integer> actualLines = new HashSet<>();
        for(int i=0;i<lines.size();i++)
            actualLines.add(lines.getInt(i));

        assertEquals(2,lines.size());
        assertEquals(actualLines,Set.of(3,4));
    }

    @Test
    public void testJsonOutputSymbolType() throws IOException{
        Map<String, JsonObject> filesAnalyzed = getFilesAnalyzedJsonOutput();

        assertTrue(filesAnalyzed.containsKey("testJsonOutput.mdl"));

        JsonObject symbols = filesAnalyzed.get("testJsonOutput.mdl").getJsonObject("symbols");
        JsonObject symbol = symbols.getJsonObject("SYMBOL_WITH_MULTIPLE_DEPENDENCIES");

        assertEquals("Variable",symbol.getString(JsonSymbolTableBuilder.KEY_TYPE));
    }




    private Map<String, JsonObject> getFilesAnalyzedJsonOutput() throws FileNotFoundException {
        File file = new File(integrationTestsFolder, "symbolTable.json");
        InputStream fis = new FileInputStream(file);

        JsonReader reader = Json.createReader(fis);

        JsonArray jsonFile = reader.readArray();


        Map<String, JsonObject> filesAnalyzed = new HashMap<>();
        for (int i = 0; i < jsonFile.size(); i++) {
            JsonObject object = jsonFile.getJsonObject(i);
            filesAnalyzed.put(object.getString("file"), object);
        }
        return filesAnalyzed;
    }



}
