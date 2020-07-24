package es.uva.locomotion.utilities;

import es.uva.locomotion.VensimPlugin;

public class VensimLogger {

    private static VensimLogger instance;
    private static LogOutputMethod outputMethod = new LogConsolePrinter();
    private static boolean logServerCommunication = false;

    private VensimLogger(){

    }

    public static VensimLogger getInstance(){
        if(instance==null)
            instance = new VensimLogger();

        return instance;
    }

    public void info(String file, String message){
        outputMethod.log(LoggingLevel.INFO, formatMessage(file,message));

    }

    public void error(String file, String message){
        outputMethod.log(LoggingLevel.ERROR, formatMessage(file,message));
    }

    public void server(String message){
        if(logServerCommunication)
            outputMethod.log(LoggingLevel.INFO, formatMessage("",message));

    }

    private String formatMessage(String file, String message){
        return message + "[" + VensimPlugin.PLUGIN_KEY + "- " + file + "]";
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
