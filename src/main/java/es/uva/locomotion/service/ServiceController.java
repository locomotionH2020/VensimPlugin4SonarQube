package es.uva.locomotion.service;

import es.uva.locomotion.model.AcronymsList;
import es.uva.locomotion.model.Module;
import es.uva.locomotion.model.category.Category;
import es.uva.locomotion.model.category.CategoryMap;
import es.uva.locomotion.model.symbol.Symbol;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.model.symbol.SymbolType;
import es.uva.locomotion.utilities.Constants;
import es.uva.locomotion.utilities.exceptions.*;
import es.uva.locomotion.utilities.logs.LoggingLevel;
import es.uva.locomotion.utilities.logs.VensimLogger;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Controller that handles the requests to services.
 * Has the responsibility of filtering the data that goes in the service and the data that the scanner receives.
 * Also handles and logs errors with the service.
 */
public class ServiceController {

    public static final String INVALID_RESPONSE = "The response of the dictionary service wasn't valid. ";
    public static final String GENERATE_LOGS = "To see the response use the analysis parameter: -Dvensim.logServerMessages=true \n";
    public static final String DICTIONARY_RESPONSE = " Dictionary response: ";
    public static final String INJECTION_WAS_NOT_SUCCESFUL = "Injection was not succesful.";
    protected static VensimLogger logger = VensimLogger.getInstance();
    private String token;
    private final String dictionaryService;

    // I used to put this message in a separate log call, but there were another logs in between the two. So I decided to join them.
    private static final String RULES_DISABLED_MESSAGE = "The rules that require the data from the dictionary service will be skipped.";
    private static final String ACRONYMS_DISABLED_MESSAGE = "Variable name check may cause false positives without acronyms.";
    private static final String MODULES_DISABLED_MESSAGE = "Injection of new modules can't be done without the modules from the dictionary.";
    private static final String CATEGORIES_DISABLED_MESSAGE = "Injection of new categories can't be done without the categories from the dictionary.";
    private static final String UNITS_DISABLED_MESSAGE = "Units check can't be done without the units from the dictionary.";
    private static final String INVALID_URL_MESSAGE = "The url of the dictionary service is invalid (Missing protocol http:// or https://, invalid format or invalid protocol)\n";
    private static final String MISSING_DICTIONARY_SERVICE_MESSAGE = "Missing dictionary service parameter.\n";
    private static final String SERVICE_UNREACHABLE_MESSAGE = "The dictionary service was unreachable.\n";

    public ServiceController(String dictionaryService) {
        this.dictionaryService = dictionaryService;
        token = null;
    }


    public void authenticate(String dictionaryUser, String dictionaryPassword) {
        try {
            token = DBFacade.getAuthenticationToken(dictionaryService, dictionaryUser, dictionaryPassword);
        } catch (InvalidServiceUrlException ex) {
            logger.unique(INVALID_URL_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
        } catch (EmptyServiceException ex) {
            logger.unique(MISSING_DICTIONARY_SERVICE_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.INFO);
        } catch (ConnectionFailedException ex) {
            logger.unique(SERVICE_UNREACHABLE_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
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
            return DBFacade.getExistingSymbolsAndIndexesFromDB(dictionaryService, symbolsFound, token);
        } catch (InvalidServiceUrlException ex) {
            logger.unique(INVALID_URL_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            return null;
        } catch (EmptyServiceException ex) {
            logger.unique(MISSING_DICTIONARY_SERVICE_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.INFO);
            return null;
        } catch (ConnectionFailedException ex) {
            logger.unique(SERVICE_UNREACHABLE_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            return null;
        } catch (ServiceResponseFormatNotValid ex) {
            logMessage = INVALID_RESPONSE + ex.getMessage() + "\n" +
                    GENERATE_LOGS +
                    RULES_DISABLED_MESSAGE;

            logger.error(logMessage);
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
            logger.unique(INVALID_URL_MESSAGE + ACRONYMS_DISABLED_MESSAGE, LoggingLevel.ERROR);
            return null;
        } catch (EmptyServiceException ex) {
            logger.unique(MISSING_DICTIONARY_SERVICE_MESSAGE + ACRONYMS_DISABLED_MESSAGE, LoggingLevel.INFO);
            return null;
        } catch (ConnectionFailedException ex) {
            logger.unique(SERVICE_UNREACHABLE_MESSAGE + ACRONYMS_DISABLED_MESSAGE, LoggingLevel.ERROR);
            return null;
        } catch (ServiceResponseFormatNotValid ex) {
            logMessage = INVALID_RESPONSE + ex.getMessage() + "\n" +
                    GENERATE_LOGS +
                    ACRONYMS_DISABLED_MESSAGE;

            logger.error(logMessage);
            return null;
        }

    }

    public void injectNewSymbols(List<Symbol> foundSymbols, List<Module> validModules, SymbolTable dbSymbolTable) {
        if (dbSymbolTable == null)
            return;

        if (!isAuthenticated())
            throw new NotAuthenticatedException();

        List<Symbol> newSymbols = foundSymbols.stream().filter(symbol -> !dbSymbolTable.hasSymbol(symbol.getToken().trim()) && hasToFetchSymbolFromDB(symbol))
                .collect(Collectors.toList());

        List<Symbol> validSymbols = newSymbols.stream().filter(Symbol::isValid).collect(Collectors.toList());
        List<Symbol> filteredSymbols = validSymbols.stream().filter(Predicate.not(Symbol::isFiltered)).collect(Collectors.toList());

        validModules = validModules.stream().filter(Module::isValid).collect(Collectors.toList());
        if (!filteredSymbols.isEmpty()) {
            inyectSymbols(validModules, filteredSymbols);
        } else {
            logger.info("No new symbols to inject");
        }

        inyectNewIndexes(foundSymbols, dbSymbolTable);

    }

    private void inyectNewIndexes(List<Symbol> foundSymbols, SymbolTable dbSymbolTable) {
        //Index does not have module.
        List<Symbol> indexes = foundSymbols.stream().filter(symbol -> symbol.getType() == SymbolType.SUBSCRIPT).collect(Collectors.toList());
        List<Symbol> validIndexes = indexes.stream().filter(Symbol::isValid).collect(Collectors.toList());
        List<Symbol> filteredindexes = validIndexes.stream().filter(Predicate.not(Symbol::isFiltered)).collect(Collectors.toList());
        List<Symbol> indexesToSend = new ArrayList<>();

        for (Symbol index : filteredindexes) {
            if (dbSymbolTable.hasSymbol(index.getToken().trim())) {
                Symbol dbIndex = dbSymbolTable.getSymbol(index.getToken());

                List<Symbol> localDependencies = index.getDependencies().stream().sorted().collect(Collectors.toList());
                List<Symbol> dbDependencies = dbIndex.getDependencies().stream().sorted().collect(Collectors.toList());
                Boolean toSend = false;
                int i = 0;
                while (i < localDependencies.size() && !toSend) {
                    if (!localDependencies.get(i).dbEquals(dbDependencies.get(i))) {
                        indexesToSend.add(index);
                        toSend = true;
                    }
                    i++;
                }

            } else {
                indexesToSend.add(index);
            }
        }
        if (!indexesToSend.isEmpty()) {
            inyectIndexes(indexesToSend);

        } else {
            logger.info("No new indexes to inject");
        }
    }

    private void inyectIndexes(List<Symbol> indexesToSend) {
        try {
            List<String> tokensInjected = indexesToSend.stream().sorted(Comparator.comparing(Symbol::getToken)).map(Symbol::getToken).collect(Collectors.toList());
            logger.info("Injected indexes: " + tokensInjected);

            DBFacade.injectIndexes(dictionaryService, indexesToSend, token);


        } catch (InvalidServiceUrlException ex) {
            logger.unique(INVALID_URL_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
        } catch (EmptyServiceException ex) {
            logger.unique(MISSING_DICTIONARY_SERVICE_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.INFO);
        } catch (ConnectionFailedException ex) {
            logger.unique(SERVICE_UNREACHABLE_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
        }
    }

    private void inyectSymbols(List<Module> validModules, List<Symbol> filteredSymbols) {
        try {
            for (Module module : validModules) {
                List<Symbol> symbolsToInject = filteredSymbols.stream().filter(symbol -> symbol.getPrimaryModule() != null && symbol.getPrimaryModule().equals(module)).collect(Collectors.toList());
                if (!symbolsToInject.isEmpty()) {

                    List<String> tokensInjected = symbolsToInject.stream().map(Symbol::getToken).sorted(String::compareTo).collect(Collectors.toList());
                    logger.info("Injected symbols in module \"" + module.getName() + "\": " + tokensInjected);

                    DBFacade.injectSymbols(dictionaryService, symbolsToInject, module.getName(), token);

                }
            }

        } catch (InvalidServiceUrlException ex) {
            logger.unique(INVALID_URL_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
        } catch (EmptyServiceException ex) {
            logger.unique(MISSING_DICTIONARY_SERVICE_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.INFO);
        } catch (ConnectionFailedException ex) {
            logger.unique(SERVICE_UNREACHABLE_MESSAGE + RULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
        }
    }

    public boolean isAuthenticated() {
        return token != null;
    }


    private boolean hasToFetchSymbolFromDB(Symbol symbol) {
        return !List.of(SymbolType.FUNCTION, SymbolType.SUBSCRIPT_VALUE, SymbolType.UNDETERMINED, SymbolType.UNDETERMINED_FUNCTION).
                contains(symbol.getType()) && !Constants.DEFAULT_VENSIM_SYMBOLS.contains(symbol.getToken().trim()) &&
                !symbol.getLines().isEmpty();

    }


    public Set<Module> getModulesFromDb() {
        if (!isAuthenticated())
            throw new NotAuthenticatedException();

        String logMessage;
        try {
            return DBFacade.getExistingModulesFromDB(dictionaryService, token).stream().map(Module::new).collect(Collectors.toSet());
        } catch (InvalidServiceUrlException ex) {
            logger.unique(INVALID_URL_MESSAGE + MODULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            return null;
        } catch (EmptyServiceException ex) {
            logger.unique(MISSING_DICTIONARY_SERVICE_MESSAGE + MODULES_DISABLED_MESSAGE, LoggingLevel.INFO);
            return null;
        } catch (ConnectionFailedException ex) {
            logger.unique(SERVICE_UNREACHABLE_MESSAGE + MODULES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            return null;
        } catch (ServiceResponseFormatNotValid ex) {
            logMessage = INVALID_RESPONSE + ex.getMessage() + DICTIONARY_RESPONSE + ex.getServiceResponse() + ".\n" +
                    GENERATE_LOGS +
                    MODULES_DISABLED_MESSAGE;

            logger.error(logMessage);
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
            logger.unique(INVALID_URL_MESSAGE + CATEGORIES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            return null;
        } catch (EmptyServiceException ex) {
            logger.unique(MISSING_DICTIONARY_SERVICE_MESSAGE + CATEGORIES_DISABLED_MESSAGE, LoggingLevel.INFO);
            return null;
        } catch (ConnectionFailedException ex) {
            logger.unique(SERVICE_UNREACHABLE_MESSAGE + CATEGORIES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            return null;
        } catch (ServiceResponseFormatNotValid ex) {
            logMessage = INVALID_RESPONSE + ex.getMessage() + DICTIONARY_RESPONSE + ex.getServiceResponse() + ".\n" +
                    GENERATE_LOGS +
                    CATEGORIES_DISABLED_MESSAGE;

            logger.error(logMessage);
            return null;
        }
    }

    public void injectNewCategories(List<Category> categoriesFound, List<Category> dbCategories) {
        if (dbCategories == null)
            return;

        if (!isAuthenticated())
            throw new NotAuthenticatedException();

        List<Category> categories = new ArrayList<>(categoriesFound);

        categories = categories.stream().filter(Category::isValid)
                .collect(Collectors.toList());

        List<Category> newCategories = categories.stream().filter(category -> !dbCategories.contains(category))
                .collect(Collectors.toList());

        if (!newCategories.isEmpty()) {
            try {

                List<String> tokensInjected = newCategories.stream().map(Category::getWholeName).sorted(String::compareTo).collect(Collectors.toList());
                logger.info("Injected categories: " + tokensInjected);

                DBFacade.injectCategories(dictionaryService, newCategories, token);


            } catch (InvalidServiceUrlException ex) {
                logger.unique(INVALID_URL_MESSAGE + CATEGORIES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            } catch (EmptyServiceException ex) {
                logger.unique(MISSING_DICTIONARY_SERVICE_MESSAGE + CATEGORIES_DISABLED_MESSAGE, LoggingLevel.INFO);
            } catch (ConnectionFailedException ex) {
                logger.unique(SERVICE_UNREACHABLE_MESSAGE + CATEGORIES_DISABLED_MESSAGE, LoggingLevel.ERROR);
            }

        } else {
            logger.info("No new categories to inject");
        }


    }

    public void injectNewModules(Set<Module> modulesList, Set<Module> dbModules) {
        if (dbModules == null)
            return;

        if (!isAuthenticated())
            throw new NotAuthenticatedException();


        List<String> newModules = modulesList.stream()
                .filter(module -> !dbModules.contains(module))
                .filter(Module::isValid)
                .map(Module::getName)
                .collect(Collectors.toList());

        if (!newModules.isEmpty()) {
            try {

                List<String> tokensInjected = newModules.stream().sorted(String::compareTo).collect(Collectors.toList());
                logger.info("Injected modules: " + tokensInjected);


                DBFacade.injectModules(dictionaryService, newModules, token);

            } catch (InvalidServiceUrlException ex) {
                logger.unique(INVALID_URL_MESSAGE + INJECTION_WAS_NOT_SUCCESFUL, LoggingLevel.ERROR);
            } catch (EmptyServiceException ex) {
                logger.unique(MISSING_DICTIONARY_SERVICE_MESSAGE + INJECTION_WAS_NOT_SUCCESFUL, LoggingLevel.INFO);
            } catch (ConnectionFailedException ex) {
                logger.unique(SERVICE_UNREACHABLE_MESSAGE + INJECTION_WAS_NOT_SUCCESFUL, LoggingLevel.ERROR);
            }

        } else {
            logger.info("No new modules to inject");
        }

    }

    public Set<String> getUnitsFromDb() {
        if (!isAuthenticated())
            throw new NotAuthenticatedException();

        String logMessage;
        try {
            return DBFacade.getExistingUnitsFromDB(dictionaryService, token);
        } catch (InvalidServiceUrlException ex) {
            logger.unique(INVALID_URL_MESSAGE + UNITS_DISABLED_MESSAGE, LoggingLevel.ERROR);
            return new HashSet<>();
        } catch (EmptyServiceException ex) {
            logger.unique(MISSING_DICTIONARY_SERVICE_MESSAGE + UNITS_DISABLED_MESSAGE, LoggingLevel.INFO);
            return new HashSet<>();
        } catch (ConnectionFailedException ex) {
            logger.unique(SERVICE_UNREACHABLE_MESSAGE + UNITS_DISABLED_MESSAGE, LoggingLevel.ERROR);
            return new HashSet<>();
        } catch (ServiceResponseFormatNotValid ex) {
            logMessage = INVALID_RESPONSE + ex.getMessage() + DICTIONARY_RESPONSE + ex.getServiceResponse() + ".\n" +
                    GENERATE_LOGS +
                    UNITS_DISABLED_MESSAGE;

            logger.error(logMessage);
            return new HashSet<>();
        }

    }

}
