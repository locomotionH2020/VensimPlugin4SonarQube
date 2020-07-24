package es.uva.locomotion.utilities;

import java.io.FileWriter;
import java.io.IOException;

public class LogFileWriter implements LogOutputMethod {

    private FileWriter writer;

    public LogFileWriter(String fileName) throws IOException {
        writer = new FileWriter(fileName);

    }

    @Override
    public void log(LoggingLevel level, String message) {
        try {
            writer.write(level.toString() + ":" + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
