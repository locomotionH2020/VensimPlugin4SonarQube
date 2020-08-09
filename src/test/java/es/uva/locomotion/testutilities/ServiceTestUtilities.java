package es.uva.locomotion.testutilities;



import es.uva.locomotion.service.ServiceConnectionHandler;

import static org.mockito.Mockito.*;

public class ServiceTestUtilities {
    public static ServiceConnectionHandler getMockDbServiceHandlerThatReturns(String returnValue ){
        ServiceConnectionHandler handler = mock(ServiceConnectionHandler.class);
        doReturn(returnValue).when(handler).sendRequestToDictionaryService(any(),any(),any());

        doReturn("").when(handler).authenticate(any(),any(),any());

        return handler;
    }



}
