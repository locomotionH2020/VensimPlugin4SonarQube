package es.uva.locomotion.service;




import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ServiceTestUtilities {
    public static ServiceConnectionHandler getMockDbServiceHandlerThatReturns(String returnValue ){
        ServiceConnectionHandler handler = mock(ServiceConnectionHandler.class);

        doReturn(returnValue).when(handler).sendRequestToDictionaryService(anyString(),anyList(),anyString());
        doReturn("").when(handler).authenticateForInjection(any(),any(),any());

        return handler;
    }

    public static void setDbFacadeHandler(ServiceConnectionHandler handler){
      DBFacade.handler =  handler;
    }


}
