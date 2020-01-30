package es.uva.medeas.utilities.exceptions;

public class ConnectionFailedException extends RuntimeException {

    public ConnectionFailedException(Throwable ex){
        super(ex);
    }
}
