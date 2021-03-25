package es.uva.locomotion.utilities;

import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import es.uva.locomotion.parser.ModelParser;
import es.uva.locomotion.parser.visitors.RawSymbolTableVisitor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static es.uva.locomotion.utilities.Constants.IMPURE_FUNCTIONS;

public class SymbolTableGenerator {

    private static  final List<String> symbolVariables = Collections.singletonList("Time");
    private static final List<String> lookupGeneratorFunctions  = Arrays.asList("GET DIRECT LOOKUPS", "GET XLS LOOKUPS");


    public static SymbolTable getSymbolTable(ModelParser.FileContext context){
        RawSymbolTableVisitor generator = new RawSymbolTableVisitor();
        SymbolTable rawTable = generator.getSymbolTable(context);



        resolveSymbolTable(rawTable);

        return rawTable;
    }

    public static void resolveSymbolTable(SymbolTable table){

        Set<Symbol> remainingSymbols = table.getUndeterminedSymbols();
        addDefaultSymbols(table);

        while(!remainingSymbols.isEmpty()) {


            for (Symbol symbol : remainingSymbols) {
                if(symbol.getType()==SymbolType.UNDETERMINED_FUNCTION)
                    resolveFunctionType(symbol);
                else {
                   tryToDetermineType(symbol);
                }
            }

            Set<Symbol> remainingSymbolsAfter = table.getUndeterminedSymbols();

            if(remainingSymbolsAfter.equals(remainingSymbols)) {
                String unresolvableSymbol = remainingSymbols.stream().map(Symbol::getToken).collect(Collectors.joining(", "));
                throw new IllegalStateException("Can't resolve symbols: " + unresolvableSymbol);
            }


            remainingSymbols = remainingSymbolsAfter;

        }


    }

    public static void addDefaultSymbols(SymbolTable table){
        for(String variable: symbolVariables) {
            if (table.hasSymbol(variable))
                table.getSymbol(variable).setType(SymbolType.Variable);
            else {
                Symbol s = new Symbol(variable);
                s.setType(SymbolType.Variable);
                table.addSymbol(s);
            }
        }

    }

    private static void resolveFunctionType(Symbol symbol){
        if(symbol.getType()!=SymbolType.UNDETERMINED_FUNCTION)
            throw new IllegalArgumentException("You can't resolve the function type of a symbol that isn't UNDETERMINED_FUNCTION");

        for (Symbol dependency : symbol.getDependencies()) {
            if (isFunction(dependency)) {
                if (lookupGeneratorFunctions.contains(dependency.getToken())) {
                    symbol.setType(SymbolType.Lookup_Table);
                    return;
                }
            }


        }
        symbol.setType(SymbolType.Function);
    }

    private static void tryToDetermineType(Symbol symbol) {

        boolean undeterminedDependency = false;
        for (Symbol dependency : symbol.getDependencies()) {


            if (dependency.getType() == SymbolType.Function) {
                if (IMPURE_FUNCTIONS.contains(dependency.getToken())) {
                    symbol.setType(SymbolType.Variable);
                    break;
                }else if(lookupGeneratorFunctions.contains(dependency.getToken())){
                    symbol.setType(SymbolType.Lookup_Table);
                }

            }else if (dependency.getType() == SymbolType.Variable) {
                symbol.setType(SymbolType.Variable);

                break;
            }else if(dependency.getType() == SymbolType.UNDETERMINED || dependency.getType() == SymbolType.UNDETERMINED_FUNCTION) {
                undeterminedDependency = true;
            }


        }
        if(!undeterminedDependency && !symbol.hasType())
            symbol.setType(SymbolType.Constant);


    }

    private static boolean isFunction(Symbol symbol){
        return symbol.getType() == SymbolType.Function || symbol.getType() == SymbolType.UNDETERMINED_FUNCTION;
    }


}
