package es.uva.locomotion.plugin;

import es.uva.locomotion.model.*;
import es.uva.locomotion.parser.visitors.VensimVisitorContext;
import es.uva.locomotion.service.ServiceController;
import es.uva.locomotion.parser.*;
import es.uva.locomotion.rules.VensimCheck;


import es.uva.locomotion.utilities.JsonSymbolTableBuilder;
import es.uva.locomotion.utilities.SymbolTableGenerator;
import es.uva.locomotion.utilities.ViewTableUtility;
import es.uva.locomotion.utilities.logs.VensimLogger;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.rule.Checks;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.rule.RuleKey;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static es.uva.locomotion.utilities.Constants.*;

public class VensimScanner {

    protected static VensimLogger LOG = VensimLogger.getInstance();

    private final SensorContext context;
    private final Checks<VensimCheck> checks;
    private final JsonSymbolTableBuilder jsonBuilder;

    private final ServiceController serviceController;



    public VensimScanner(SensorContext context, Checks<VensimCheck> checks, JsonSymbolTableBuilder builder, ServiceController serviceController) {
        this.context = context;
        this.checks = checks;
        this.jsonBuilder = builder;
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
                LOG.error("Unable to analyze file '" + vensimFile.toString() + "' Error: " + e.toString());
                for (StackTraceElement ele : e.getStackTrace()) {
                    LOG.error(ele.toString());
                }
            }
        }


        generateJsonOutput();

    }

    protected void generateJsonOutput() {
        JsonArray symbolTable = jsonBuilder.build();
        try {

            File file = new File("symbolTable.json");
            JsonWriter writer = Json.createWriter(new FileOutputStream(file));
            writer.writeArray(symbolTable);
            writer.close();
        } catch (FileNotFoundException e) {
            LOG.error("Unable to create symbolTable.json. Error:" + e.getMessage());
        }

    }

    protected ModelParser.FileContext getParseTree(String file_content) {
        ModelLexer lexer = new ModelLexer(CharStreams.fromString(file_content));
        MultiChannelTokenStream tokens = new MultiChannelTokenStream(lexer);

        //CommonTokenStream tokens = new CommonTokenStream(lexer);
        ModelParser parser = new ModelParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new VensimErrorListener());

        return parser.file();
    }

    public void scanFile(InputFile inputFile) {

        String viewPrefix = context.config().get(VIEW_PREFIX).orElse("");
        String moduleName = context.config().get(MODULE_NAME).orElse("");
        String moduleSeparator = context.config().get(MODULE_SEPARATOR).orElse("");
        String categorySeparator = context.config().get(CATEGORY_SEPARATOR).orElse("");

        try {
            String content = inputFile.contents();
            String module = getModuleNameFromFileName(inputFile.filename());

            ModelParser.FileContext root = getParseTree(content);
            SymbolTable table = SymbolTableGenerator.getSymbolTable(root);

            ViewTable viewTable;
            if (!viewPrefix.isEmpty()) { //Support for viewPrefix
                viewTable = ViewTableUtility.getViewTable(root);
                LOG.warn("vensim.view.prefix is deprecated, please use: vensim.view.module.name and vensim.view.module.separator");
            } else if (!moduleSeparator.isEmpty()) {
                if (!categorySeparator.isEmpty()) {
                    viewTable = ViewTableUtility.getViewTable(root, moduleSeparator, categorySeparator);
                } else {
                    viewTable = ViewTableUtility.getViewTable(root, moduleSeparator);
                }
            } else {
                if (!categorySeparator.isEmpty()) {
                    LOG.warn("vensim.view.category.separator is set, but not vensim.view.module.separator, ignoring category separator");
                }
                viewTable = ViewTableUtility.getViewTable(root);
            }
            ViewTableUtility.addViews(table, viewTable);

            jsonBuilder.addTables(inputFile.filename(), table, viewTable);


            DataBaseRepresentation dbData = new DataBaseRepresentation();
            if (serviceController.isAuthenticated()) {
                dbData.setDataBaseSymbols(serviceController.getSymbolsFromDb(new ArrayList<>(table.getSymbols())));
                dbData.setAcronyms(serviceController.getAcronymsFromDb());
                dbData.setModules(serviceController.getModulesFromDb());
                dbData.setCategories(serviceController.getCategoriesFromDb());
            }
            //mark the symbols tha need to be filtered.

            if (!viewPrefix.isEmpty()) {
                ViewTableUtility.filterPrefix(table, viewPrefix);
            } else if (!moduleName.isEmpty()) {
                ViewTableUtility.filterPrefix(table, moduleName);

            }
            VensimVisitorContext visitorContext = new VensimVisitorContext(root, table, viewTable, context, dbData);


            checkIssues(visitorContext);
            saveIssues(inputFile, visitorContext.getIssues());

            int lines = content.split("[\r\n]+").length;

            context.<Integer>newMeasure().forMetric(CoreMetrics.NCLOC).on(inputFile).withValue(lines).save();

            if (!moduleSeparator.isEmpty() && dbData.getModules() != null) {
                serviceController.injectNewModules(viewTable.getModules(), dbData.getModules());
                if (!categorySeparator.isEmpty() && dbData.getCategories() != null)
                    serviceController.injectNewCategories(viewTable.getCategories().getCategoriesAndSubcategories(), dbData.getCategories().getCategoriesAndSubcategories());
            }
            if (serviceController.isAuthenticated() && dbData.getDataBaseSymbols() != null)
                serviceController.injectNewSymbols(new ArrayList<>(table.getSymbols()), viewTable.getModules(), dbData.getDataBaseSymbols());

        } catch (IOException e) {
            LOG.error("Unable to analyze file '" + inputFile.filename() + "'. Error: " + e.getMessage());
        } catch (ParseCancellationException e) {
            LOG.error("Unable to parse the file '" + inputFile.filename() + "'. Error: " + e.getLocalizedMessage());
        }

    }

    protected String getModuleNameFromFileName(String fileName) {
        String suffix = VensimLanguage.VENSIM_PLAIN_TEXT_SUFIX;
        if (fileName.endsWith(suffix))
            return fileName.substring(0, fileName.length() - suffix.length());
        else
            return fileName;


    }


    public void checkIssues(VensimVisitorContext fileContext) {
        //System.out.println("module");

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
