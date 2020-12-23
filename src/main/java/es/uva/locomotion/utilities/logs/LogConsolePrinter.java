package es.uva.locomotion.utilities.logs;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public class LogConsolePrinter implements LogOutputMethod {
    protected static Logger LOG = Loggers.get(LogConsolePrinter.class.getSimpleName());

    @Override
    public void log(LoggingLevel level, String message) {
        switch (level){
            case INFO:
                LOG.info(message);
                break;
            case WARNING:
                LOG.warn(message);
                break;
            case ERROR:
                LOG.error(message);
                break;
            default:
                throw new IllegalArgumentException("LoggingLevel" + level + " not implemented in LogConsolePrinter");


        }
    }
}
