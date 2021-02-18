package es.uva.locomotion.utilities.logs;

import es.uva.locomotion.VensimPlugin;

import java.util.HashSet;
import java.util.Set;

public class VensimLogger {

    private static VensimLogger instance;
    private static LogOutputMethod outputMethod = new LogConsolePrinter();
    private static boolean logServerCommunication = false;
    private final Set<String> uniqueLoggedMessages;

    private VensimLogger(){
        uniqueLoggedMessages = new HashSet<>();
    }

    public static VensimLogger getInstance(){
        if(instance==null)
            instance = new VensimLogger();

        return instance;
    }

    public void info(String message){
        info(message,"");
    }

    public void info(String message, String file){
        outputMethod.log(LoggingLevel.INFO, formatMessage(message,file));

    }

    public void warn(String message){
        warn(message,"");
    }

    public void warn(String message, String file){
        outputMethod.log(LoggingLevel.WARNING, formatMessage(message,file));

    }


    public void error(String message) {
        error(message,"");
    }

    public void error(String message, String file){
        outputMethod.log(LoggingLevel.ERROR, formatMessage(message,file));
    }

    public void server(String message){
        if(logServerCommunication)
            outputMethod.log(LoggingLevel.INFO, formatMessage(message,""));
    }

    /**  Guarantees that as long as the message is unique, it will only log it once, even if the method is called
     * multiple times.
     *
     * */
    public void unique(String message, LoggingLevel level){
        String trimmedMessage = message.trim();
        if(uniqueLoggedMessages.contains(trimmedMessage))
            return;

        uniqueLoggedMessages.add(trimmedMessage);
        outputMethod.log(level, formatMessage(message,""));
    }

    private String formatMessage(String message,String file){
        String fileStr = file.isEmpty() ? "" : "- " + file;
        return message + "[" + VensimPlugin.PLUGIN_KEY + fileStr + "]";
    }

    /**
     * There are two main output methods: Display in console and store in a file
     */
    public static void setOutputMethod(LogOutputMethod method){
        outputMethod = method;
    }

    public static void logAllServerCommunications(boolean doLog){
        logServerCommunication = doLog;
    }
}
