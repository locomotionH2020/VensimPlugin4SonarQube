package es.uva.locomotion.rules;

import es.uva.locomotion.model.AcronymsList;
import es.uva.locomotion.model.DataBaseRepresentation;
import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.model.symbol.DelayedType;
import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.plugin.VensimScanner;
import org.junit.Test;

import static es.uva.locomotion.testutilities.GeneralTestUtilities.addSymbolInLines;
import static es.uva.locomotion.testutilities.RuleTestUtilities.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestDelayedNameCheck {

    @Test
    public void testCorrectName() {

        String program = "delayed_expected_consumption_2020 = SMOOTH(1,1,1) ~~|\n" +
                "delayed_TS_expected_consumption_2020 = SMOOTH(1,TIME_STEP,1) ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        assertDoesntHaveIssueOfType(visitorContext,DelayedNameCheck.class);

    }

    @Test
    public void testNameCanContainAnyNumber(){
        String program = "delayed_numbers0_1_2_3_4_5_6_7_8_9 = SMOOTH(1,1,1) ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);

        assertDoesntHaveIssueOfType(visitorContext,DelayedNameCheck.class);

    }

    @Test
    public void testUppercaseAnyWord(){
        String program = "delayed_EXPECTED_consumption_2020  = SMOOTH(1,1,1) ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,DelayedNameCheck.class,1);
    }

    @Test
    public void testSeveralUnderscore(){
        String program = "\ndelayed_expected__consumption_2020 = SMOOTH(1,1,1) ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();

        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext,DelayedNameCheck.class,2);
    }



    @Test
    public void testMultipleDefinitionCreateDifferentIssues() {
        String program = "delayed_VAR[aSubscript] = SMOOTH(1,1,1) ~~|\n" +
                "         delayed_VAR[anotherSubscript]  = SMOOTH(1,1,1) ~~|";

        VensimVisitorContext visitorContext = getVisitorContextFromString(program);
        VensimScanner scanner = getScanner();


        scanner.checkIssues(visitorContext);
        assertHasIssueInLines(visitorContext, DelayedNameCheck.class, 1,2);
    }



    @Test
    public void testAcronymInVariableMiddle(){
        SymbolTable parsedTable = new SymbolTable();
        addSymbolInLines(parsedTable,"delayed_var_ACR_var",SymbolType.VARIABLE,1);
        parsedTable.getSymbol("delayed_var_ACR_var").setDelayed(DelayedType.DELAYED);
        for(Symbol s:parsedTable.getSymbols())
            s.setComment("Parsed Comment");

        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setAcronyms(new AcronymsList());

        AcronymsList dbDataAcronyms = dbData.getAcronyms();
        dbDataAcronyms.addAcronym("ACR");

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);
        DelayedNameCheck check = new DelayedNameCheck();
        check.scan(context);
        assertDoesntHaveIssueInLines(context,DelayedNameCheck.class,1);
    }

    @Test
    public void testAcronymInVariableOnly(){
        SymbolTable parsedTable = new SymbolTable();
        addSymbolInLines(parsedTable,"delayed_TS_ACR",SymbolType.VARIABLE,1);
        parsedTable.getSymbol("delayed_TS_ACR").setDelayed(DelayedType.TIME_STEP_DELAYED);

        for(Symbol s:parsedTable.getSymbols())
            s.setComment("Parsed Comment");

        DataBaseRepresentation dbData = new DataBaseRepresentation();
        dbData.setAcronyms(new AcronymsList());
        AcronymsList dbDataAcronyms = dbData.getAcronyms();
        dbDataAcronyms.addAcronym("ACR");

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable, new ViewTable(), null, dbData);
        DelayedNameCheck check = new DelayedNameCheck();
        check.scan(context);
        assertDoesntHaveIssueInLines(context,DelayedNameCheck.class,1);
    }
}
