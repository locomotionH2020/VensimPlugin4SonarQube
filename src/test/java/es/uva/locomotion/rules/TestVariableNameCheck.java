package es.uva.locomotion.rules;

import es.uva.locomotion.model.*;
import es.uva.locomotion.plugin.VensimScanner;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import org.junit.Test;

import static es.uva.locomotion.testutilities.GeneralTestUtilities.addSymbolInLines;
import static es.uva.locomotion.testutilities.RuleTestUtilities.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestVariableNameCheck {

    @Test
    public void testCorrectName() {

        String program = "expected_consumption_2020 = Time ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        assertDoesntHaveIssueOfType(visitorContext,VariableNameCheck.class);

    }

    @Test
    public void testNameCanContainAnyNumber(){
        String program = "numbers0_1_2_3_4_5_6_7_8_9 = Time ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        assertDoesntHaveIssueOfType(visitorContext,VariableNameCheck.class);

    }

    @Test
    public void testUppercaseAnyWord(){
        String program = "EXPECTED_consumption_2020 = Time ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,VariableNameCheck.class,1);
    }

    @Test
    public void testUppercaseLastWord(){
        String program = "expected_CONSUMPTION = Time ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,VariableNameCheck.class,1);
    }

    @Test
    public void testSeveralUnderscore(){
        String program = "\nexpected__consumption_2020 = Time ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,VariableNameCheck.class,2);
    }

    @Test
    public void testNameBeginningWithUnderscore(){
        String program = "_expected_consumption_2020 = Time ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,VariableNameCheck.class,1);
    }

    @Test
    public void testNameEndingWithUnderscore(){
        String program = "expected_consumption_2020_ = Time ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,VariableNameCheck.class,1);
    }

    @Test
    public void testWeirdCharacters(){
        String program = "wèird= Time~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,VariableNameCheck.class,1);
    }

    @Test
    public void testNameBeginsWithNumber(){
        String program = "\"2020_expected_consumption\" = Time ~~|\n"+
                "\"2020expected_consumption\" = Time ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,VariableNameCheck.class,1,2);

    }

    @Test
    public void testMultipleDefinitionCreateDifferentIssues() {
        String program = "VAR[aSubscript] = Time~~|\n" +
                "         VAR[anotherSubscript] = Time~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext, VariableNameCheck.class, 1,2);
    }

    @Test
    public void testFailingRuleMakesSymbolInvalid(){
        VariableNameCheck check = new VariableNameCheck();

        SymbolTable table = new SymbolTable();
        Symbol invalid = new Symbol("INVALID", SymbolType.Variable);
        invalid.addDefinitionLine(1);
        Symbol valid = new Symbol("valid", SymbolType.Variable);
        valid.addDefinitionLine(2);
        table.addSymbol(invalid);
        table.addSymbol(valid);

        VensimVisitorContext context = new VensimVisitorContext(null,table, new ViewTable(), null, null);
        check.scan(context);

        assertTrue(valid.isValid());
        assertFalse(invalid.isValid());
    }

    @Test
    public void testAcronymInVariableStart(){
        SymbolTable parsedTable = new SymbolTable();
        addSymbolInLines(parsedTable,"ACR_var",SymbolType.Variable,1);

        for(Symbol s:parsedTable.getSymbols())
            s.setComment("Parsed Comment");

        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setAcronyms(new AcronymsList());

        AcronymsList dbDataAcronyms = dbData.getAcronyms();
        dbDataAcronyms.addAcronym("ACR");

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);
        VariableNameCheck check = new VariableNameCheck();
        check.scan(context);
        assertDoesntHaveIssueInLines(context,VariableNameCheck.class,1);
    }

    @Test
    public void testAcronymInVariableMiddle(){
        SymbolTable parsedTable = new SymbolTable();
        addSymbolInLines(parsedTable,"var_ACR_var",SymbolType.Variable,1);

        for(Symbol s:parsedTable.getSymbols())
            s.setComment("Parsed Comment");

        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setAcronyms(new AcronymsList());

        AcronymsList dbDataAcronyms = dbData.getAcronyms();
        dbDataAcronyms.addAcronym("ACR");

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);
        VariableNameCheck check = new VariableNameCheck();
        check.scan(context);
        assertDoesntHaveIssueInLines(context,VariableNameCheck.class,1);
    }
    @Test
    public void testAcronymInVariableEnd(){
        SymbolTable parsedTable = new SymbolTable();
        addSymbolInLines(parsedTable,"var_ACR",SymbolType.Variable,1);

        for(Symbol s:parsedTable.getSymbols())
            s.setComment("Parsed Comment");

        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setAcronyms(new AcronymsList());

        AcronymsList dbDataAcronyms = dbData.getAcronyms();
        dbDataAcronyms.addAcronym("ACR");

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);
        VariableNameCheck check = new VariableNameCheck();
        check.scan(context);
        assertDoesntHaveIssueInLines(context,VariableNameCheck.class,1);
    }

    @Test
    public void testAcronymInVariableOnly(){
        SymbolTable parsedTable = new SymbolTable();
        addSymbolInLines(parsedTable,"ACR",SymbolType.Variable,1);

        for(Symbol s:parsedTable.getSymbols())
            s.setComment("Parsed Comment");

        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setAcronyms(new AcronymsList());
        AcronymsList dbDataAcronyms = dbData.getAcronyms();
        dbDataAcronyms.addAcronym("ACR");

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);
        VariableNameCheck check = new VariableNameCheck();
        check.scan(context);
        assertDoesntHaveIssueInLines(context,VariableNameCheck.class,1);
    }

    @Test
    public void testAcronymInVariableMultiple(){
        SymbolTable parsedTable = new SymbolTable();
        addSymbolInLines(parsedTable,"ACR_H2O_var_CO2",SymbolType.Variable,1);

        for(Symbol s:parsedTable.getSymbols())
            s.setComment("Parsed Comment");

        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setAcronyms(new AcronymsList());

        AcronymsList dbDataAcronyms = dbData.getAcronyms();
        dbDataAcronyms.addAcronym("ACR");
        dbDataAcronyms.addAcronym("H2O");
        dbDataAcronyms.addAcronym("CO2");

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);
        VariableNameCheck check = new VariableNameCheck();
        check.scan(context);
        assertDoesntHaveIssueInLines(context,VariableNameCheck.class,1);
    }

    @Test
    public void testAcronymInVariableWithBadName(){
        SymbolTable parsedTable = new SymbolTable();
        addSymbolInLines(parsedTable,"ACRT_var",SymbolType.Variable,1);
        addSymbolInLines(parsedTable,"TACR_var",SymbolType.Variable,2);

        for(Symbol s:parsedTable.getSymbols())
            s.setComment("Parsed Comment");

        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setAcronyms(new AcronymsList());

        AcronymsList dbDataAcronyms = dbData.getAcronyms();
        dbDataAcronyms.addAcronym("ACR");

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);
        VariableNameCheck check = new VariableNameCheck();
        check.scan(context);
        assertHasIssueInLines(context,VariableNameCheck.class,1,2);
    }
}
