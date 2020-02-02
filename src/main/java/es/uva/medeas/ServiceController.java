package es.uva.medeas;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import es.uva.medeas.utilities.Constants;
import es.uva.medeas.utilities.DBFacade;

import java.util.List;
import java.util.stream.Collectors;

public class ServiceController {

    private String dictionaryService;

    public ServiceController(String dictionaryService){
        this.dictionaryService = dictionaryService;
    }

    public SymbolTable getSymbolsFromDb(List<Symbol> symbols){
        List<String> symbolsFound = symbols.stream().filter(s -> !symbolIsIgnored(s)).map(Symbol::getToken).collect(Collectors.toList());
        return DBFacade.getExistingSymbolsFromDB(dictionaryService,symbolsFound);
    }


    private boolean symbolIsIgnored(Symbol symbol){

        return symbol.getType()==SymbolType.FUNCTION || Constants.DEFAULT_VENSIM_SYMBOLS.contains(symbol.getToken().trim());

    }
}
