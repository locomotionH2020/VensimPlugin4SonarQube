package es.uva.locomotion.service;

import es.uva.locomotion.VensimPlugin;
import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.parser.SymbolTable;
import es.uva.locomotion.parser.SymbolType;
import es.uva.locomotion.utilities.Constants;
import es.uva.locomotion.utilities.exceptions.ConnectionFailedException;
import es.uva.locomotion.utilities.exceptions.EmptyServiceException;
import es.uva.locomotion.utilities.exceptions.InvalidServiceUrlException;
import es.uva.locomotion.utilities.exceptions.ServiceResponseFormatNotValid;
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
    private final String token;
    private String dictionaryService;
    private String lastLoggedMessage; //Used to avoid sending the same message every time a file is analyzed.

    // I used to put this message in a separate log call, but there were another logs in between the two. So I decided to join them.
    private final String RULES_DISABLED_MESSAGE = "The rules that require the data from the dictionary service will be skipped. ";
    private final String INJECTION_DISABLED_MESSAGE = "New symbols won't be injected to the service. ";

    public ServiceController(String dictionaryService, String dictionaryUser, String dictionaryPassword){
        this.dictionaryService = dictionaryService;
        lastLoggedMessage="";
        token = authenticate(dictionaryUser, dictionaryPassword);

        if(token == null)
            logInfo("Unable to log in. The username/password were incorrect or there was an internal server error");
    }


    private String authenticate(String dictionaryUser, String dictionaryPassword) {
        String logMessage="";
        try {
            return DBFacade.getAuthenticationToken(dictionaryService, dictionaryUser, dictionaryPassword);
        } catch (InvalidServiceUrlException ex) {
            logMessage = "The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n" + RULES_DISABLED_MESSAGE + "[" + VensimPlugin.PLUGIN_KEY + "]";
            logError(logMessage);
            return null;
        } catch (EmptyServiceException ex) {
            logMessage = "Missing dictionary service parameter.\n" + RULES_DISABLED_MESSAGE + "[" + VensimPlugin.PLUGIN_KEY + "]";
            logInfo(logMessage);
            return null;
        } catch (ConnectionFailedException ex) {
            logMessage = "The dictionary service was unreachable.\n" + RULES_DISABLED_MESSAGE + "[" + VensimPlugin.PLUGIN_KEY + "]";
            logError(logMessage);
            return null;
        } catch (ServiceResponseFormatNotValid ex) {
            logMessage = "The response of the dictionary service wasn't valid. " + ex.getMessage() + "\n" +
                    "Actual response: " + ex.getServiceResponse() + "\n" +
                    RULES_DISABLED_MESSAGE + "[" + VensimPlugin.PLUGIN_KEY + "]";

            logError(logMessage);
            return null;
        }
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
        List<String> symbolsFound = symbols.stream().filter(this::hasToFetchSymbolFromDB).map(Symbol::getToken).collect(Collectors.toList());
        String logMessage="";
        try {
            return  DBFacade.getExistingSymbolsFromDB(dictionaryService, symbolsFound, token);
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
                   "Actual response: " + ex.getServiceResponse() + "\n" +
                    RULES_DISABLED_MESSAGE+"["+ VensimPlugin.PLUGIN_KEY +"]";

           logError(logMessage);
            return null;
        }
    }

    public void injectNewSymbols( String module, List<Symbol> foundSymbols, SymbolTable dbSymbolTable){
        if(dbSymbolTable==null)
            return;

        List<Symbol> newSymbols = foundSymbols.stream().filter(symbol -> !dbSymbolTable.hasSymbol(symbol.getToken().trim()) && hasToFetchSymbolFromDB(symbol))
                .collect(Collectors.toList());

        List<Symbol> validSymbols = newSymbols.stream().filter(Symbol::isValid).collect(Collectors.toList());

        if(validSymbols.size()>=1){
            String logMessage="";
            try {
                DBFacade.injectSymbols(dictionaryService, module, validSymbols, token);
                List<String> tokensInjected = validSymbols.stream().map(Symbol::getToken).sorted(String::compareTo).collect(Collectors.toList());
                logInfo("Injected  symbols:" + tokensInjected);
            }catch (InvalidServiceUrlException ex){
                logMessage = "The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n"+ INJECTION_DISABLED_MESSAGE +"["+ VensimPlugin.PLUGIN_KEY +"]";
                logError(logMessage);
            }catch (EmptyServiceException ex){
                logMessage = "Missing dictionary service parameter.\n" + INJECTION_DISABLED_MESSAGE + "[" + VensimPlugin.PLUGIN_KEY + "]";
                logInfo(logMessage);
            }catch (ConnectionFailedException ex){
                logMessage = "The dictionary service was unreachable.\n"+ INJECTION_DISABLED_MESSAGE + "["+ VensimPlugin.PLUGIN_KEY +"]";
                logError(logMessage);
            }

        }

    }

    public boolean isAuthenticated(){
        return token != null;
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


    private boolean hasToFetchSymbolFromDB(Symbol symbol){
        return !List.of(SymbolType.Function, SymbolType.UNDETERMINED, SymbolType.UNDETERMINED_FUNCTION).
                contains(symbol.getType()) && !Constants.DEFAULT_VENSIM_SYMBOLS.contains(symbol.getToken().trim()) &&
                !symbol.getDefinitionLines().isEmpty();

    }
}
