package es.uva.medeas.rules;

import es.uva.medeas.VensimVisitorContext;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SymbolTableGenerator {

    private static  final List<String> symbolVariables = Arrays.asList("Time");
    private static final List<String> nonPureFunctions = Arrays.asList("INTEG","STEP","DELAY1", "DELAY1I", "DELAY3", "DELAY3I", "FORECAST", "SMOOTH3", "SMOOTH3I", "SMOOTHI", "SMOOTH", "TREND","RAMP");
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
                tryToDetermineType(symbol);
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
            Symbol s =  table.getSymbolOrCreate(variable);
            s.setType(SymbolType.VARIABLE);
        }

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
            }else if(dependency.getType() == SymbolType.UNDETERMINED) {
                undeterminedDependency = true;
                //TODO Normalmente pondría aqui un break, pero es posible que haya un ciclo de dependencias, y
                // La única forma de romperlo que tengo de momento es asumir que INTEG es uan funcion
                // Y para ello tengo que leer todas las dependencias, por si no es la primera
            }


        }
        if(!undeterminedDependency && !symbol.hasType())
            symbol.setType(SymbolType.CONSTANT);


    }
}
