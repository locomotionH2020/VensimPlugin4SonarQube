package es.uva.locomotion.utilities.logs;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class LogFileWriter implements LogOutputMethod {

    private final FileWriter writer;

    public LogFileWriter(Path fileName) throws IOException {
        writer = new FileWriter(fileName.toFile());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public void log(LoggingLevel level, String message) {
        try {
            writer.write(level.toString() + ":" + message+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

}
