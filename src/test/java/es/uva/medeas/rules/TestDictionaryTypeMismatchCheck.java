package es.uva.medeas.rules;


import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import es.uva.medeas.plugin.Issue;
import es.uva.medeas.plugin.VensimVisitorContext;
import es.uva.medeas.testutilities.TestUtilities;
import es.uva.medeas.utilities.Constants;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static es.uva.medeas.testutilities.RuleTestUtilities.*;
import static es.uva.medeas.testutilities.TestUtilities.addSymbolInLines;
import static org.junit.Assert.*;

public class TestDictionaryTypeMismatchCheck {


    @Test
    public void testIssue(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        TestUtilities.addSymbolInLines(dbTable,"var", SymbolType.SUBSCRIPT_NAME);
        TestUtilities.addSymbolInLines(parsedTable,"var", SymbolType.VARIABLE,1,2,3);


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);


        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertEquals(3,context.getIssues().size());
        assertHasIssueInLines(context,DictionaryTypeMismatchCheck.class,1,2,3);


        for(Issue issue:context.getIssues())
            assertEquals("The symbol 'var' has type 'VARIABLE' but the dictionary has 'SUBSCRIPT_NAME'.",issue.getMessage());
    }

    @Test
    public void testIssueInDifferentSymbols(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();

        TestUtilities.addSymbolInLines(dbTable,"var", SymbolType.VARIABLE);
        TestUtilities.addSymbolInLines(parsedTable,"var", SymbolType.FUNCTION,1);

        TestUtilities.addSymbolInLines(dbTable,"valid", SymbolType.CONSTANT);
        TestUtilities.addSymbolInLines(parsedTable,"valid", SymbolType.CONSTANT,2);

        TestUtilities.addSymbolInLines(dbTable,"var2", SymbolType.REALITY_CHECK);
        TestUtilities.addSymbolInLines(parsedTable,"var2", SymbolType.SUBSCRIPT_VALUE,3);

        TestUtilities.addSymbolInLines(dbTable,"valid2", SymbolType.SUBSCRIPT_VALUE);
        TestUtilities.addSymbolInLines(parsedTable,"valid2", SymbolType.SUBSCRIPT_VALUE,4);

        TestUtilities.addSymbolInLines(dbTable,"var3", SymbolType.LOOKUP);
        TestUtilities.addSymbolInLines(parsedTable,"var3", SymbolType.FUNCTION,5);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);

        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertHasIssueInLines(context,DictionaryTypeMismatchCheck.class,1,3,5);
        assertDoesntHaveIssueInLines(context,DictionaryTypeMismatchCheck.class,2,4);


    }

    @Test
    public void testIgnoresSymbolsThatArentInTheDictionary(){
        SymbolTable parsedTable = new SymbolTable();

        TestUtilities.addSymbolInLines(parsedTable,"var", SymbolType.VARIABLE,1,2,3);


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,new SymbolTable());
        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context,DictionaryTypeMismatchCheck.class);

    }

    @Test
    public void testRuleIsIgnoredIfDbTableIsNull(){
        SymbolTable parsedTable = new SymbolTable();

        TestUtilities.addSymbolInLines(parsedTable,"var", SymbolType.VARIABLE,1,2,3);


        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,null);
        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context,DictionaryTypeMismatchCheck.class);


    }

    @Test
    public void testRuleIsIgnoredIfSymbolDoenstHaveDefinitionLine(){
        SymbolTable parsedTable = new SymbolTable();
        parsedTable.addSymbol( new Symbol("var",SymbolType.SUBSCRIPT_NAME)) ;

        SymbolTable dbTable = new SymbolTable();
        dbTable.addSymbol(new Symbol("var", SymbolType.REALITY_CHECK));

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);


        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context,DictionaryTypeMismatchCheck.class);

    }

    @Test
    public void testRuleIgnoresFunctions(){

        SymbolTable parsedTable = new SymbolTable();
        addSymbolInLines(parsedTable,"function",SymbolType.FUNCTION,1);
        addSymbolInLines(parsedTable,"constant",SymbolType.CONSTANT,2);
        addSymbolInLines(parsedTable,"var",SymbolType.VARIABLE,3);
        addSymbolInLines(parsedTable, "subscript",SymbolType.SUBSCRIPT_NAME,4);
        addSymbolInLines(parsedTable,"subscriptValue",SymbolType.SUBSCRIPT_VALUE,5);
        addSymbolInLines(parsedTable,"lookup",SymbolType.LOOKUP,6);
        addSymbolInLines(parsedTable,"realityCheck",SymbolType.REALITY_CHECK,7);

        SymbolTable dbTable = new SymbolTable();

        addSymbolInLines(dbTable,"function",SymbolType.VARIABLE);
        addSymbolInLines(dbTable,"constant",SymbolType.VARIABLE);
        addSymbolInLines(dbTable,"var",SymbolType.CONSTANT);
        addSymbolInLines(dbTable, "subscript",SymbolType.VARIABLE);
        addSymbolInLines(dbTable,"subscriptValue",SymbolType.VARIABLE);
        addSymbolInLines(dbTable,"lookup",SymbolType.VARIABLE);
        addSymbolInLines(dbTable,"realityCheck",SymbolType.VARIABLE);

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertHasIssueInLines(context,DictionaryTypeMismatchCheck.class,2,3,4,5,6,7);
        assertDoesntHaveIssueInLines(context,DictionaryTypeMismatchCheck.class,1);


    }

    @Test
    public void testRuleIgnoresDefaultSymbols(){
        SymbolTable dbTable = new SymbolTable();
        SymbolTable parsedTable = new SymbolTable();


        List<Symbol> parsedSymbols = Constants.DEFAULT_VENSIM_SYMBOLS.stream().map(Symbol::new).collect(Collectors.toList());
        parsedSymbols.forEach(symbol -> {symbol.addDefinitionLine(1);
                                        symbol.setType(SymbolType.VARIABLE);
                                        parsedTable.addSymbol(symbol);
        });

        List<Symbol> dbSymbols = Constants.DEFAULT_VENSIM_SYMBOLS.stream().map(Symbol::new).collect(Collectors.toList());
        dbSymbols.forEach(symbol -> {
            symbol.setType(SymbolType.CONSTANT);
           dbTable.addSymbol(symbol);
        });

        VensimVisitorContext context = new VensimVisitorContext(null,parsedTable,dbTable);
        DictionaryTypeMismatchCheck check = new DictionaryTypeMismatchCheck();
        check.scan(context);

        assertDoesntHaveIssueOfType(context,DictionaryTypeMismatchCheck.class);


    }

}
