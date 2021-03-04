package es.uva.locomotion.utilities;

import es.uva.locomotion.model.*;
import es.uva.locomotion.utilities.logs.VensimLogger;

import javax.json.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.List;

public class OutputFilesGenerator {

    protected static VensimLogger LOG = VensimLogger.getInstance();

    private JsonSymbolTableBuilder symbolsJson;
    private JsonDictoinaryDiffBuilder diffJson;
    private boolean generateGetDiff;

    public OutputFilesGenerator(boolean generateGetDiff) {
        this.generateGetDiff = generateGetDiff;
        symbolsJson = new JsonSymbolTableBuilder();
        diffJson = new JsonDictoinaryDiffBuilder();
    }

    public void generateFiles(Path resume) {
        try {
            LOG.info("Generating output files at: '" + resume.toAbsolutePath() + "'");
            File directory =resume.toFile();
            if (! directory.exists()){
                directory.mkdir();
            }
            Path symbolFile = resume.toAbsolutePath().resolve("symbolTable.json");
            JsonWriter writer = Json.createWriter(new FileOutputStream(symbolFile.toFile()));
            writer.writeArray(symbolsJson.build());
            writer.close();

            if (generateGetDiff) {
                Path dictionaryDiffFile = resume.toAbsolutePath().resolve("dictionaryDiff.json");
                writer = Json.createWriter(new FileOutputStream(dictionaryDiffFile.toFile()));
                writer.writeArray(diffJson.build());
                writer.close();
            }
        } catch (FileNotFoundException e) {
            LOG.error("Unable to create symbolTable.json. Error: '" + e.getMessage() +"'");
        }
    }

    public void addTables(String filename, SymbolTable table, ViewTable viewTable, DataBaseRepresentation dbData) {
        symbolsJson.addTables(filename, table, viewTable);
        if (generateGetDiff)
            diffJson.addTables(filename, table, viewTable, dbData);
    }
}
