package es.uva.medeas.utilities;

import es.uva.medeas.VensimPlugin;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import es.uva.medeas.parser.SymbolType;
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
    private String lastLoggedMessage; //Used to avoid sending the same message every time a file is analyzed.

    // I used to put this message in a separate log call, but there were another logs in between the two. So I decided to join them.
    private final String RULES_DISABLED_MESSAGE = "The rules that require the data from the dictionary service will be skipped. ";

    public ServiceController(String dictionaryService){
        this.dictionaryService = dictionaryService;
        lastLoggedMessage="";
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
        String logMessage="";
        try {
            return  DBFacade.getExistingSymbolsFromDB(dictionaryService, symbolsFound);
        }catch (InvalidServiceUrlException ex){
            logMessage = "The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n"+ RULES_DISABLED_MESSAGE +"["+ VensimPlugin.PLUGIN_KEY +"]";
            logError(logMessage);
            return null;
        }catch (EmptyServiceException ex){
            logMessage = "Missing dictionary service parameter.\n" + RULES_DISABLED_MESSAGE + "[" + VensimPlugin.PLUGIN_KEY + "]";
            logInfo(logMessage);
            return null;
        }catch (ConnectionFailedException ex){
            logMessage = "The dictionary service was unreachable.\n"+ RULES_DISABLED_MESSAGE + "["+ VensimPlugin.PLUGIN_KEY +"]";
            logError(logMessage);
            return null;
        }catch (ServiceResponseFormatNotValid ex){
           logMessage = "The response of the dictionary service wasn't valid. "+ ex.getMessage() + "\n"+
                    RULES_DISABLED_MESSAGE+"["+ VensimPlugin.PLUGIN_KEY +"]";

           logError(logMessage);
            return null;
        }

    }

    private void logError(String message){
        if(!message.equals(lastLoggedMessage)) {
            LOG.error(message);
            lastLoggedMessage = message;
        }
    }

    private void logInfo(String message){
        if(!message.equals(lastLoggedMessage)) {
            lastLoggedMessage = message;
            LOG.info(message);
        }
    }


    private boolean symbolIsIgnored(Symbol symbol){

        return symbol.getType()==SymbolType.Function || Constants.DEFAULT_VENSIM_SYMBOLS.contains(symbol.getToken().trim());

    }
}
