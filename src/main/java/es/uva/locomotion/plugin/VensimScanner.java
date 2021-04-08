package es.uva.locomotion.plugin;

import es.uva.locomotion.model.DataBaseRepresentation;
import es.uva.locomotion.model.ViewTable;
import es.uva.locomotion.model.symbol.SymbolTable;
import es.uva.locomotion.parser.ModelLexer;
import es.uva.locomotion.parser.ModelParser;
import es.uva.locomotion.parser.MultiChannelTokenStream;
import es.uva.locomotion.parser.VensimErrorListener;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.rules.VensimCheck;
import es.uva.locomotion.service.ServiceController;
import es.uva.locomotion.utilities.OutputFilesGenerator;
import es.uva.locomotion.utilities.SymbolTableGenerator;
import es.uva.locomotion.utilities.ViewTableUtility;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.rule.RuleKey;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static es.uva.locomotion.utilities.Constants.*;

public class VensimScanner {

    protected static VensimLogger logger = VensimLogger.getInstance();

    private final SensorContext context;
    private final Checks<VensimCheck> checks;
    private final OutputFilesGenerator outputFilesGenerator;

    private final ServiceController serviceController;


    public VensimScanner(SensorContext context, Checks<VensimCheck> checks, OutputFilesGenerator builder, ServiceController serviceController) {
        this.context = context;
        this.checks = checks;
        this.outputFilesGenerator = builder;
        this.serviceController = serviceController;


    }

    public void scanFiles(List<InputFile> inputFiles) {
        for (InputFile vensimFile : inputFiles) {
            if (context.isCancelled()) {
                return;
            }
            try {
                scanFile(vensimFile);
            } catch (Exception e) {
                logger.error("Unable to analyze file '" + vensimFile.toString() + "' Error: " + e.toString());
                for (StackTraceElement ele : e.getStackTrace()) {
                    logger.error(ele.toString());
                }
            }
        }

        String auxiliaryFilesDirName = context.config().get(AUXILIARY_FILES_DIR_NAME).orElse("auxiliary_files");

        outputFilesGenerator.generateFiles(Paths.get(auxiliaryFilesDirName));

    }


    protected ModelParser.FileContext getParseTree(String fileContent) {
        ModelLexer lexer = new ModelLexer(CharStreams.fromString(fileContent));
        MultiChannelTokenStream tokens = new MultiChannelTokenStream(lexer);

        ModelParser parser = new ModelParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new VensimErrorListener());

        return parser.file();
    }

    public void scanFile(InputFile inputFile) {

        String viewPrefix = context.config().get(VIEW_PREFIX).orElse("");
        String moduleName = context.config().get(MODULE_NAME).orElse("");
        String inject = context.config().get(INJECT).orElse("false");

        boolean needToInject = !inject.equals("false");
        try {
            String content = inputFile.contents();

            ModelParser.FileContext root = getParseTree(content);
            SymbolTable table = SymbolTableGenerator.getSymbolTable(root);

            ViewTable viewTable = getViewTable(root, table);

            DataBaseRepresentation dbData = getDataBaseRepresentation(table);

            filterSymbols(viewPrefix, moduleName, table);

            VensimVisitorContext visitorContext = new VensimVisitorContext(root, table, viewTable, context, dbData);
            checkIssues(visitorContext);
            saveIssues(inputFile, visitorContext.getIssues());

            outputFilesGenerator.addTables(inputFile.filename(), table, viewTable, dbData);

            int lines = content.split("[\r\n]+").length;
            context.<Integer>newMeasure().forMetric(CoreMetrics.NCLOC).on(inputFile).withValue(lines).save();

            if (needToInject && serviceController.isAuthenticated()) {
                inyectToDictionary(table, viewTable, dbData);
            }
        } catch (IOException e) {
            logger.error("Unable to analyze file '" + inputFile.filename() + "'. Error: " + e.getMessage());
        } catch (ParseCancellationException e) {
            logger.error("Unable to parse the file '" + inputFile.filename() + "'. Error: " + e.getLocalizedMessage());
        }

    }

    private void inyectToDictionary(SymbolTable table, ViewTable viewTable, DataBaseRepresentation dbData) {
        String moduleSeparator = context.config().get(MODULE_SEPARATOR).orElse("");
        String categorySeparator = context.config().get(CATEGORY_SEPARATOR).orElse("");

        if (!moduleSeparator.isEmpty() && !dbData.getModules().isEmpty()) {
            serviceController.injectNewModules(new HashSet<>(viewTable.getModules()), dbData.getModules());
            if (!categorySeparator.isEmpty() && dbData.getCategories() != null)
                serviceController.injectNewCategories(viewTable.getCategoriesAndSubcategories(), dbData.getCategories().getCategoriesAndSubcategories());
        }
        if (!dbData.getDataBaseSymbolTable().isEmpty())
            serviceController.injectNewSymbols(new ArrayList<>(table.getSymbols()), new ArrayList<>(viewTable.getModules()), dbData.getDataBaseSymbolTable());
    }

    private void filterSymbols(String viewPrefix, String moduleName, SymbolTable table) {
        if (!viewPrefix.isBlank()) {
            ViewTableUtility.filterPrefix(table, viewPrefix);
        } else if (!moduleName.isEmpty()) {
            ViewTableUtility.filterPrefix(table, moduleName);
        }
    }

    private ViewTable getViewTable(ModelParser.FileContext root, SymbolTable table) {
        String viewPrefix = context.config().get(VIEW_PREFIX).orElse("");
        String moduleSeparator = context.config().get(MODULE_SEPARATOR).orElse("");
        String categorySeparator = context.config().get(CATEGORY_SEPARATOR).orElse("");

        ViewTable viewTable;
        if (!viewPrefix.isEmpty()) { //Support for viewPrefix
            viewTable = ViewTableUtility.getViewTable(table, root);
            logger.warn("vensim.view.prefix is deprecated, please use: vensim.view.module.name and vensim.view.module.separator");
        } else if (!moduleSeparator.isEmpty()) {
            if (!categorySeparator.isEmpty()) {
                viewTable = ViewTableUtility.getViewTable(table, root, moduleSeparator, categorySeparator);
            } else {
                viewTable = ViewTableUtility.getViewTable(table, root, moduleSeparator);
            }
        } else {
            if (!categorySeparator.isEmpty()) {
                logger.warn("vensim.view.category.separator is set, but not vensim.view.module.separator, ignoring category separator");
            }
            viewTable = ViewTableUtility.getViewTable(table, root);
        }
        return viewTable;
    }

    private DataBaseRepresentation getDataBaseRepresentation(SymbolTable table) {
        DataBaseRepresentation dbData = new DataBaseRepresentation();
        if (serviceController.isAuthenticated()) {
            dbData.setDataBaseSymbols(serviceController.getSymbolsFromDb(new ArrayList<>(table.getSymbols())));
            dbData.setAcronyms(serviceController.getAcronymsFromDb());
            dbData.setModules(serviceController.getModulesFromDb());
            dbData.setCategories(serviceController.getCategoriesFromDb());
            dbData.setUnits(serviceController.getUnitsFromDb());
        }
        return dbData;
    }

    protected String getModuleNameFromFileName(String fileName) {
        String suffix = VensimLanguage.VENSIM_PLAIN_TEXT_SUFIX;
        if (fileName.endsWith(suffix))
            return fileName.substring(0, fileName.length() - suffix.length());
        else
            return fileName;


    }


    public void checkIssues(VensimVisitorContext fileContext) {

        for (VensimCheck check : checks.all()) {
            check.scan(fileContext);
        }
    }


    protected void saveIssues(InputFile file, List<Issue> issues) {
        for (Issue preciseIssue : issues) {
            RuleKey ruleKey = checks.ruleKey(preciseIssue.getCheck());

            NewIssue newIssue = context
                    .newIssue()
                    .forRule(ruleKey);


            TextRange range = file.selectLine(preciseIssue.getLine());
            NewIssueLocation newLocation = newIssue.newLocation()
                    .on(file);
            newLocation.message(preciseIssue.getMessage());
            newLocation.at(range);

            newIssue.at(newLocation);
            newIssue.overrideSeverity(preciseIssue.getSeverity());
            newIssue.save();
        }

    }


}
