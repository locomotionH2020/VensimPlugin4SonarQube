package es.uva.locomotion.rules;

import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.SymbolTable;
import es.uva.locomotion.parser.SymbolType;
import es.uva.locomotion.plugin.Issue;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.testutilities.TestUtilities;
import es.uva.locomotion.utilities.Constants;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static es.uva.locomotion.testutilities.RuleTestUtilities.*;
import static es.uva.locomotion.testutilities.TestUtilities.addSymbolInLines;
import static org.junit.Assert.*;


public class TestDictionaryCommentMismatchCheck {
    @Test
    public void testIssue(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = new Symbol("var");
        parsedVar.setComment("A comment");
        parsedVar.addDefinitionLine(1);
        parsedVar.addDefinitionLine(2);
        parsedTable.addSymbol(parsedVar);

        Symbol dbVar = new Symbol("var");
        dbVar.setComment("Doesn't match");
        dbTable.addSymbol(dbVar);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);


        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);

        assertEquals(2,context.getIssues().size());
        assertHasIssueInLines(context,DictionaryCommentMismatchCheck.class,1,2);


        assertFalse(parsedVar.isValid());
        for(Issue issue:context.getIssues())
            assertEquals("The symbol 'var' has a comment 'A comment' but the dictionary has 'Doesn't match'.",issue.getMessage());
    }

    @Test
    public void testParsedSymbolDoesntHaveComment(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = new Symbol("var");
        parsedVar.addDefinitionLine(1);
        parsedVar.addDefinitionLine(2);
        parsedTable.addSymbol(parsedVar);

        Symbol dbVar = new Symbol("var");
        dbVar.setComment("comment");
        dbTable.addSymbol(dbVar);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);


        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);

        assertTrue(parsedVar.isValid());
        assertDoesntHaveIssueOfType(context,DictionaryCommentMismatchCheck.class);
    }

    @Test
    public void testBothCommentsAreTrimmed(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = new Symbol("var");
        parsedVar.setComment("                                                                               Some comment                                              ");
        parsedVar.addDefinitionLine(1);
        parsedTable.addSymbol(parsedVar);

        Symbol dbVar = new Symbol("var");
        dbVar.setComment("    Some comment    ");
        dbTable.addSymbol(dbVar);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);

        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);

       assertDoesntHaveIssueOfType(context,DictionaryCommentMismatchCheck.class);
    }




    @Test
    public void testIssueInDifferentSymbols(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol var = TestUtilities.addSymbolInLines(parsedTable,"var", SymbolType.Variable,1);
        Symbol valid1 = TestUtilities.addSymbolInLines(parsedTable, "valid1", SymbolType.Variable, 2);
        Symbol var2 = TestUtilities.addSymbolInLines(parsedTable, "var2", SymbolType.Variable, 3);
        Symbol valid2 = TestUtilities.addSymbolInLines(parsedTable, "valid2", SymbolType.Variable, 4);
        Symbol var3 = TestUtilities.addSymbolInLines(parsedTable, "var3", SymbolType.Variable, 5);

        var.setComment("different comment");
        var2.setComment("different comment");
        var3.setComment("different comment");
        valid1.setComment("Same comment");
        valid2.setComment("Same comment");

        Symbol dbVar = TestUtilities.addSymbolInLines(dbTable,"var", SymbolType.Variable);
        Symbol dbValid = TestUtilities.addSymbolInLines(dbTable, "valid1", SymbolType.Variable);
        Symbol dbVar2 = TestUtilities.addSymbolInLines(dbTable, "var2", SymbolType.Variable, 3);
        Symbol dbValid2 = TestUtilities.addSymbolInLines(dbTable, "valid2", SymbolType.Variable);
        Symbol dbVar3 = TestUtilities.addSymbolInLines(dbTable, "var3", SymbolType.Variable);

        dbVar.setComment("mismatch");
        dbVar2.setComment("mismatch");
        dbVar3.setComment("mismatch");
        dbValid.setComment("Same comment");
        dbValid2.setComment("Same comment");

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);

        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);


        assertHasIssueInLines(context,DictionaryCommentMismatchCheck.class,1,3,5);
        assertDoesntHaveIssueInLines(context,DictionaryCommentMismatchCheck.class,2,4);


    }



    @Test
    public void testSymbolNotFoundInDB(){
        SymbolTable parsedTable = new SymbolTable();


        Symbol var = new Symbol("var");
        var.setComment("Some comment");
        parsedTable.addSymbol(var);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,new SymbolTable());


        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context,DictionaryTypeMismatchCheck.class);
    }

    @Test
    public void testSymbolInDbButNotInFile(){
        SymbolTable dbTable = new SymbolTable();


        Symbol dbVar = new Symbol("var");
        dbVar.setComment("Some comment");
        dbTable.addSymbol(dbVar);

        VensimVisitorContext context = new VensimVisitorContext(null,new SymbolTable(),dbTable);


        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context,DictionaryTypeMismatchCheck.class);
    }


    @Test
    public void testDoesntRaiseIssueIfThereIsntDefinitionLines(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = new Symbol("var");
        parsedVar.setComment("A comment");
        parsedTable.addSymbol(parsedVar);

        Symbol dbVar = new Symbol("var");
        dbVar.setComment("Doesn't match");
        dbTable.addSymbol(dbVar);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);


        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context,DictionaryCommentMismatchCheck.class);
    }

    @Test
    public void testRuleIsIgnoredIfDbTableIsNull(){

        SymbolTable parsedTable = new SymbolTable();

        Symbol parsedVar = new Symbol("var");
        parsedVar.setComment("A comment");
        parsedTable.addSymbol(parsedVar);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,null);


        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context,DictionaryCommentMismatchCheck.class);
    }

    @Test
    public void testIgnoresDefaultSymbols(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();


        List<Symbol> parsedSymbols = Constants.DEFAULT_VENSIM_SYMBOLS.stream().map(Symbol::new).collect(Collectors.toList());
        parsedSymbols.forEach(symbol -> {symbol.addDefinitionLine(1);
           symbol.setComment("Parsed comment");
            parsedTable.addSymbol(symbol);
        });

        List<Symbol> dbSymbols = Constants.DEFAULT_VENSIM_SYMBOLS.stream().map(Symbol::new).collect(Collectors.toList());
        dbSymbols.forEach(symbol -> {
            symbol.setComment("DB comment");
            dbTable.addSymbol(symbol);
        });

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context,DictionaryCommentMismatchCheck.class);
    }

    @Test
    public void testIgnoresFunctions(){
        SymbolTable parsedTable = new SymbolTable();
        addSymbolInLines(parsedTable,"function",SymbolType.Function,1);
        addSymbolInLines(parsedTable,"constant",SymbolType.Constant,2);
        addSymbolInLines(parsedTable,"var",SymbolType.Variable,3);
        addSymbolInLines(parsedTable, "subscript",SymbolType.Subscript,4);
        addSymbolInLines(parsedTable,"subscriptValue",SymbolType.Subscript_Value,5);
        addSymbolInLines(parsedTable,"lookup",SymbolType.Lookup_Table,6);
        addSymbolInLines(parsedTable,"realityCheck",SymbolType.Reality_Check,7);

        for(Symbol s:parsedTable.getSymbols())
            s.setComment("Parsed Comment");

        SymbolTable dbTable = new SymbolTable();

        addSymbolInLines(dbTable,"function",SymbolType.Variable);
        addSymbolInLines(dbTable,"constant",SymbolType.Variable);
        addSymbolInLines(dbTable,"var",SymbolType.Variable);
        addSymbolInLines(dbTable, "subscript",SymbolType.Variable);
        addSymbolInLines(dbTable,"subscriptValue",SymbolType.Variable);
        addSymbolInLines(dbTable,"lookup",SymbolType.Variable);
        addSymbolInLines(dbTable,"realityCheck",SymbolType.Variable);

        for(Symbol s:dbTable.getSymbols())
            s.setComment("Db Comment");


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryCommentMismatchCheck check = new DictionaryCommentMismatchCheck();
        check.scan(context);
        
        assertHasIssueInLines(context,DictionaryCommentMismatchCheck.class,2,3,4,5,6,7);
        assertDoesntHaveIssueInLines(context,DictionaryCommentMismatchCheck.class,1);
    }
}
