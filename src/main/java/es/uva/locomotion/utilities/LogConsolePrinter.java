package es.uva.locomotion.utilities;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.IOException;

public class LogConsolePrinter implements LogOutputMethod {
    protected static Logger LOG = Loggers.get(LogConsolePrinter.class.getSimpleName());

    @Override
    public void log(LoggingLevel level, String message) {
        switch (level){
            case INFO:
                LOG.info(message);
                break;
            case ERROR:
                LOG.error(message);
                break;
            default:
                throw new IllegalArgumentException("LoggingLevel" + level + " not implemented in LogConsolePrinter");


        }
    }
}
