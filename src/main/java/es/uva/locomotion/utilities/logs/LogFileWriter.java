package es.uva.locomotion.utilities.logs;

import java.io.FileWriter;
import java.io.IOException;

public class LogFileWriter implements LogOutputMethod {

    private final FileWriter writer;

    public LogFileWriter(String fileName) throws IOException {
        writer = new FileWriter(fileName);
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
