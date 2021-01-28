package es.uva.locomotion.service;

import es.uva.locomotion.model.*;
import es.uva.locomotion.utilities.Constants;
import es.uva.locomotion.utilities.exceptions.*;
import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;


import java.util.*;
import java.util.stream.Collectors;


/**
 * Controller that handles the requests to services.
 * Has the responsibility of filtering the data that goes in the service and the data that the scanner receives.
 * Also handles and logs errors with the service.
 */
public class ServiceController {

    protected static VensimLogger LOG = VensimLogger.getInstance();
    private String token;
    private final String dictionaryService;

    // I used to put this message in a separate log call, but there were another logs in between the two. So I decided to join them.
    private final String RULES_DISABLED_MESSAGE = "The rules that require the data from the dictionary service will be skipped.";
    private final String ACRONYMS_DISABLED_MESSAGE = "Variable name check may cause false positives without acronyms.";
    private final String MODULES_DISABLED_MESSAGE = "Injection of new modules can't be done without the modules from the dictionary.";
    private final String CATEGORIES_DISABLED_MESSAGE = "Injection of new categories can't be done without the categories from the dictionary.";
    private final String INVALID_URL_MESSAGE = "The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n";
    private final String MISSING_DICTIONARY_SERVICE_MESSAGE = "Missing dictionary service parameter.\n";
    private final String SERVICE_UNREACHABLE_MESSAGE = "The dictionary service was unreachable.\n";

    public ServiceController(String dictionaryService) {
        this.dictionaryService = dictionaryService;
        token = null;
    }


    public void authenticate(String dictionaryUser, String dictionaryPassword) {
        try {
            token = DBFacade.getAuthenticationToken(dictionaryService, dictionaryUser, dictionaryPassword);
        } catch (InvalidServiceUrlException ex) {
            LOG.unique(INVALID_URL_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
        } catch (EmptyServiceException ex) {
            LOG.unique(MISSING_DICTIONARY_SERVICE_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.INFO);
        } catch (ConnectionFailedException ex) {
            LOG.unique(SERVICE_UNREACHABLE_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
        }
    }

    /**
     * @param symbols List of symbols to be searched in the dictionary service. Functions and symbols defined by Vensim are ignored
     *                ({@link Constants#DEFAULT_VENSIM_SYMBOLS})
     * @return Symbol table containing the symbols found with the data (type, comments, indexes, etc) found in the dictionary.<br>
     * Returns null and logs a error if:
     * <ul>
     *     <li>{@link ServiceController#dictionaryService} is empty or null,</li>
     *     <li>The dictionary service was unreachable (invalid domain, no connection, etc)</li>
     *     <li>The response from the service wasn't valid</li>
     * </ul>
     */
    public SymbolTable getSymbolsFromDb(List<Symbol> symbols) {
        if (!isAuthenticated())
            throw new NotAuthenticatedException();

        List<String> symbolsFound = symbols.stream().filter(this::hasToFetchSymbolFromDB).map(Symbol::getToken).collect(Collectors.toList());
        String logMessage;
        try {
            return DBFacade.getExistingSymbolsFromDB(dictionaryService, symbolsFound, token);
        } catch (InvalidServiceUrlException ex) {
            LOG.unique(INVALID_URL_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            return null;
        } catch (EmptyServiceException ex) {
            LOG.unique(MISSING_DICTIONARY_SERVICE_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.INFO);
            return null;
        } catch (ConnectionFailedException ex) {
            LOG.unique(SERVICE_UNREACHABLE_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            return null;
        } catch (ServiceResponseFormatNotValid ex) {
            logMessage = "The response of the dictionary service wasn't valid. " + ex.getMessage() + "\n" +
                    "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                    RULES_DISABLED_MESSAGE;

            LOG.error(logMessage);
            return null;
        }
    }

    public AcronymsList getAcronymsFromDb() {
        if (!isAuthenticated())
            throw new NotAuthenticatedException();

        String logMessage;
        try {
            return DBFacade.getExistingAcronymsFromDB(dictionaryService, token);
        } catch (InvalidServiceUrlException ex) {
            LOG.unique(INVALID_URL_MESSAGE + ACRONYMS_DISABLED_MESSAGE, LoggingLevel.ERROR);
            return null;
        } catch (EmptyServiceException ex) {
            LOG.unique(MISSING_DICTIONARY_SERVICE_MESSAGE + ACRONYMS_DISABLED_MESSAGE, LoggingLevel.INFO);
            return null;
        } catch (ConnectionFailedException ex) {
            LOG.unique(SERVICE_UNREACHABLE_MESSAGE + ACRONYMS_DISABLED_MESSAGE, LoggingLevel.ERROR);
            return null;
        } catch (ServiceResponseFormatNotValid ex) {
            logMessage = "The response of the dictionary service wasn't valid. " + ex.getMessage() + "\n" +
                    "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                    ACRONYMS_DISABLED_MESSAGE;

            LOG.error(logMessage);
            return null;
        }

    }

    public void injectNewSymbols(List<Symbol> foundSymbols, SymbolTable dbSymbolTable) {
        if (dbSymbolTable == null)
            return;

        if (!isAuthenticated())
            throw new NotAuthenticatedException();

        List<Symbol> newSymbols = foundSymbols.stream().filter(symbol -> !dbSymbolTable.hasSymbol(symbol.getToken().trim()) && hasToFetchSymbolFromDB(symbol))
                .collect(Collectors.toList());

        List<Symbol> validSymbols = newSymbols.stream().filter(Symbol::isValid).collect(Collectors.toList());

        if (validSymbols.size() >= 1) {
            try {
                DBFacade.injectSymbols(dictionaryService, validSymbols, token);
                List<String> tokensInjected = validSymbols.stream().map(Symbol::getToken).sorted(String::compareTo).collect(Collectors.toList());
                LOG.info("Injected symbols: " + tokensInjected);
            } catch (InvalidServiceUrlException ex) {
                LOG.unique(INVALID_URL_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            } catch (EmptyServiceException ex) {
                LOG.unique(MISSING_DICTIONARY_SERVICE_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.INFO);
            } catch (ConnectionFailedException ex) {
                LOG.unique(SERVICE_UNREACHABLE_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            }

        }

    }

    public boolean isAuthenticated() {
        return token != null;
    }


    private boolean hasToFetchSymbolFromDB(Symbol symbol) {
        return !List.of(SymbolType.Function, SymbolType.Subscript_Value, SymbolType.UNDETERMINED, SymbolType.UNDETERMINED_FUNCTION).
                contains(symbol.getType()) && !Constants.DEFAULT_VENSIM_SYMBOLS.contains(symbol.getToken().trim()) &&
                !symbol.getDefinitionLines().isEmpty();

    }


    public List<String> getModulesFromDb() {
        if (!isAuthenticated())
            throw new NotAuthenticatedException();

        String logMessage;
        try {
            return DBFacade.getExistingModulesFromDB(dictionaryService, token);
        } catch (InvalidServiceUrlException ex) {
            LOG.unique(INVALID_URL_MESSAGE + MODULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            return null;
        } catch (EmptyServiceException ex) {
            LOG.unique(MISSING_DICTIONARY_SERVICE_MESSAGE + MODULES_DISABLED_MESSAGE, LoggingLevel.INFO);
            return null;
        } catch (ConnectionFailedException ex) {
            LOG.unique(SERVICE_UNREACHABLE_MESSAGE + MODULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            return null;
        } catch (ServiceResponseFormatNotValid ex) {
            logMessage = "The response of the dictionary service wasn't valid. " + ex.getMessage() + "\n" +
                    "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                    MODULES_DISABLED_MESSAGE;

            LOG.error(logMessage);
            return null;
        }

    }

    public CategoryMap getCategoriesFromDb() {
        if (!isAuthenticated())
            throw new NotAuthenticatedException();

        String logMessage;
        try {
            return DBFacade.getExistingCategoriesFromDB(dictionaryService, token);
        } catch (InvalidServiceUrlException ex) {
            LOG.unique(INVALID_URL_MESSAGE + CATEGORIES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            return null;
        } catch (EmptyServiceException ex) {
            LOG.unique(MISSING_DICTIONARY_SERVICE_MESSAGE + CATEGORIES_DISABLED_MESSAGE, LoggingLevel.INFO);
            return null;
        } catch (ConnectionFailedException ex) {
            LOG.unique(SERVICE_UNREACHABLE_MESSAGE + CATEGORIES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            return null;
        } catch (ServiceResponseFormatNotValid ex) {
            logMessage = "The response of the dictionary service wasn't valid. " + ex.getMessage() + "\n" +
                    "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n" +
                    CATEGORIES_DISABLED_MESSAGE;

            LOG.error(logMessage);
            return null;
        }
    }

    public void injectNewCategories(List<Category> categoriesFound, List<Category> dbCategories) {
        if (dbCategories == null)
            return;

        if (!isAuthenticated())
            throw new NotAuthenticatedException();

        List<Category> categories = new ArrayList<>(categoriesFound);

        categories = categories.stream().filter(category -> {
            if(category.getSuperCategory() != null){
                return moduleNameIsValid(category.getName()) && moduleNameIsValid(category.getSuperCategory().getName());
            }
            return moduleNameIsValid(category.getName());
        })
                .collect(Collectors.toList());

        List<Category> newCategories = categories.stream().filter(category -> !dbCategories.contains(category))
                .collect(Collectors.toList());


        categories.removeAll(newCategories);
        //TODO try

        if (newCategories.size() >= 1) {
            try {
                DBFacade.injectCategories(dictionaryService, newCategories, token);
                List<String> tokensInjected = newCategories.stream().map(Category::getName).sorted(String::compareTo).collect(Collectors.toList());
                LOG.info("Injected categories: " + tokensInjected);
            } catch (InvalidServiceUrlException ex) {
                LOG.unique(INVALID_URL_MESSAGE + CATEGORIES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            } catch (EmptyServiceException ex) {
                LOG.unique(MISSING_DICTIONARY_SERVICE_MESSAGE + CATEGORIES_DISABLED_MESSAGE, LoggingLevel.INFO);
            } catch (ConnectionFailedException ex) {
                LOG.unique(SERVICE_UNREACHABLE_MESSAGE + CATEGORIES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            }

        }


    }

    public void injectNewModules(List<String> modulesList, List<String> dbModules) {
        if (dbModules == null)
            return;

        if (!isAuthenticated())
            throw new NotAuthenticatedException();

        List<String> newModules = modulesList.stream().filter(module -> !dbModules.contains(module) && moduleNameIsValid(module))
                .collect(Collectors.toList());

        if (newModules.size() >= 1) {
            try {
                DBFacade.injectModules(dictionaryService, newModules, token);
                List<String> tokensInjected = newModules.stream().sorted(String::compareTo).collect(Collectors.toList());
                LOG.info("Injected modules: " + tokensInjected);

            } catch (InvalidServiceUrlException ex) {
                LOG.unique(INVALID_URL_MESSAGE + MODULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            } catch (EmptyServiceException ex) {
                LOG.unique(MISSING_DICTIONARY_SERVICE_MESSAGE + MODULES_DISABLED_MESSAGE, LoggingLevel.INFO);
            } catch (ConnectionFailedException ex) {
                LOG.unique(SERVICE_UNREACHABLE_MESSAGE + MODULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            }

        }

    }

    private boolean moduleNameIsValid(String module) {
        return module.matches("^([a-zA-Z0-9]+_)*[a-zA-Z0-9]+$");
    }
}
