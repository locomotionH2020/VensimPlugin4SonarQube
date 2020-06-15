package es.uva.locomotion.service;



import es.uva.locomotion.service.DBFacade;
import es.uva.locomotion.service.ServiceConnectionHandler;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ServiceTestUtilities {
    public static ServiceConnectionHandler getMockDbServiceHandlerThatReturns(String returnValue){
        ServiceConnectionHandler handler = mock(ServiceConnectionHandler.class);

        doReturn(returnValue).when(handler).sendRequestToDictionaryService(anyString(),anyList());

        return handler;
    }

    public static void setDbFacadeHandler(ServiceConnectionHandler handler){
      DBFacade.handler =  handler;
    }


}
