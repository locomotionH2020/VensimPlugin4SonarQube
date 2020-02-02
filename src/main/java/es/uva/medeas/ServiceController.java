package es.uva.medeas;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
import es.uva.medeas.utilities.Constants;
import es.uva.medeas.utilities.DBFacade;
import es.uva.medeas.utilities.exceptions.ConnectionFailedException;
import es.uva.medeas.utilities.exceptions.EmptyServiceException;
import es.uva.medeas.utilities.exceptions.InvalidServiceUrlException;
import es.uva.medeas.utilities.exceptions.ServiceResponseFormatNotValid;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Controller that handles the requests to services.
 * Has the responsibility of filtering the data that goes in the service and the data that the scanner receives.
 * Also handles and logs errors with the service.
 */
public class ServiceController {

    protected static Logger LOG = Loggers.get(ServiceController.class.getSimpleName());
    private String dictionaryService;

    public ServiceController(String dictionaryService){
        this.dictionaryService = dictionaryService;
    }

    /**
     * @param symbols List of symbols to be searched in the dictionary service. Functions and symbols defined by Vensim are ignored
     *                ({@link Constants#DEFAULT_VENSIM_SYMBOLS})
     * @return Symbol table containing the symbols found with the data (type, comments, indexes, etc) found in the dictionary.<br>
     * Returns null and logs a error if:
     *    <ul>
     *        <li>{@link ServiceController#dictionaryService} is empty or null,</li>
     *        <li>The dictionary service was unreachable (invalid domain, no connection, etc)</li>
     *        <li>The response from the service wasn't valid</li>
     *    </ul>
     */
    public SymbolTable getSymbolsFromDb(List<Symbol> symbols){
        List<String> symbolsFound = symbols.stream().filter(s -> !symbolIsIgnored(s)).map(Symbol::getToken).collect(Collectors.toList());
        boolean success = false;
        try {
            SymbolTable table = DBFacade.getExistingSymbolsFromDB(dictionaryService, symbolsFound);
            success = true;
            return table;
        }catch (InvalidServiceUrlException ex){
            LOG.error("The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)"+ "["+ VensimPlugin.LOG_NAME +"]");
            return null;
        }catch (EmptyServiceException ex){
            LOG.info("Missing dictionary service parameter." + "["+ VensimPlugin.LOG_NAME +"]");
            return null;
        }catch (ConnectionFailedException ex){
            LOG.error("The dictionary service was unreachable." + "["+ VensimPlugin.LOG_NAME +"]");
            return null;
        }catch (ServiceResponseFormatNotValid ex){
            LOG.error("The response of the dictionary service wasn't vaid"+"["+ VensimPlugin.LOG_NAME +"]");
            return null;
        }finally {
            if(!success)
                LOG.info("The rules that require the data from the dictionary service will be skipped."+"["+ VensimPlugin.LOG_NAME +"]");
        }

    }


    private boolean symbolIsIgnored(Symbol symbol){

        return symbol.getType()==SymbolType.FUNCTION || Constants.DEFAULT_VENSIM_SYMBOLS.contains(symbol.getToken().trim());

    }
}
