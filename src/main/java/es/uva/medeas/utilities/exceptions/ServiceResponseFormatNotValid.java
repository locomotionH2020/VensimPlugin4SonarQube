package es.uva.medeas.utilities.exceptions;

public class ServiceResponseFormatNotValid extends RuntimeException {
    private String serviceResponse;

    public ServiceResponseFormatNotValid(String message) {
        super(message);
    }

    public void setServiceResponse(String json){
        this.serviceResponse = json;
    }

    public String getServiceResponse() {
        return serviceResponse;
    }

    public ServiceResponseFormatNotValid(String message,String serviceResponse){
        this(message);
        setServiceResponse(serviceResponse);
    }
}
