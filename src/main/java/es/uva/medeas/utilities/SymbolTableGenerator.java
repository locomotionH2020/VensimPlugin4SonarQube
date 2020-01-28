package es.uva.medeas.utilities;

import es.uva.medeas.plugin.VensimVisitorContext;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import es.uva.medeas.parser.visitors.RawSymbolTableVisitor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SymbolTableGenerator {

    private static  final List<String> symbolVariables = Arrays.asList("Time");
    private static final List<String> nonPureFunctions = Arrays.asList("INTEG","STEP","DELAY1", "DELAY1I", "DELAY3", "DELAY3I",
            "FORECAST", "SMOOTH3", "SMOOTH3I", "SMOOTHI", "SMOOTH", "TREND","RAMP","RANDOM 0 1","RANDOM BETA","RANDOM BINOMIAL",
            "RANDOM EXPONENTIAL", "RANDOM GAMMA", "RANDOM LOOKUP","RANDOM NEGATIVE BINOMIAL","RANDOM NORMAL", "RANDOM PINK NOISE",
            "RANDOM POISSON","RANDOM TRIANGULAR","RANDOM UNIFORM","RANDOM WEIBULL");
    private static final List<String> lookupGeneratorFunctions  = Arrays.asList("GET DIRECT LOOKUPS", "GET XLS LOOKUPS");


    public static SymbolTable getSymbolTable(VensimVisitorContext context){
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
                table.getSymbol(variable).setType(SymbolType.VARIABLE);
            else {
                Symbol s = new Symbol(variable);
                s.setType(SymbolType.VARIABLE);
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
                    symbol.setType(SymbolType.LOOKUP);
                    return;
                }
            }


        }
        symbol.setType(SymbolType.FUNCTION);
    }

    private static void tryToDetermineType(Symbol symbol) {

        boolean undeterminedDependency = false;
        for (Symbol dependency : symbol.getDependencies()) {


            if (dependency.getType() == SymbolType.FUNCTION) {
                if (nonPureFunctions.contains(dependency.getToken())) {
                    symbol.setType(SymbolType.VARIABLE);
                    break;
                }else if(lookupGeneratorFunctions.contains(dependency.getToken())){
                    symbol.setType(SymbolType.LOOKUP);
                }

            }else if (dependency.getType() == SymbolType.VARIABLE) {
                symbol.setType(SymbolType.VARIABLE);

                break;
            }else if(dependency.getType() == SymbolType.UNDETERMINED || dependency.getType() == SymbolType.UNDETERMINED_FUNCTION) {
                undeterminedDependency = true;
            }


        }
        if(!undeterminedDependency && !symbol.hasType())
            symbol.setType(SymbolType.CONSTANT);


    }

    private static boolean isFunction(Symbol symbol){
        return symbol.getType() == SymbolType.FUNCTION || symbol.getType() == SymbolType.UNDETERMINED_FUNCTION;
    }
}
