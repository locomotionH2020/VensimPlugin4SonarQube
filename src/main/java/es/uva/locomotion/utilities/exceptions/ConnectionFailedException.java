package es.uva.locomotion.utilities.exceptions;

public class ConnectionFailedException extends RuntimeException {

    public ConnectionFailedException(Throwable ex){
        super(ex);
    }
}
