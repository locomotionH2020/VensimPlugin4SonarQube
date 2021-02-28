package es.uva.locomotion.testutilities;



import es.uva.locomotion.service.ServiceConnectionHandler;

import static org.mockito.Mockito.*;

public class ServiceTestUtilities {
    public static ServiceConnectionHandler getMockDbServiceHandlerThatReturns(String returnValue ){
        ServiceConnectionHandler handler = mock(ServiceConnectionHandler.class);
        doReturn(returnValue).when(handler).sendSymbolTableRequestToDictionaryService(any(),any(),any());
        doReturn(returnValue).when(handler).sendAcronymsRequestToDictionaryService(any(),any());
        doReturn(returnValue).when(handler).sendModuleRequestToDictionaryService(any(),any());
        doReturn(returnValue).when(handler).sendCategoriesRequestToDictionaryService(any(),any());
        doReturn(returnValue).when(handler).sendUnitsRequestToDictionaryService(any(),any());

        doReturn("").when(handler).authenticate(any(),any(),any());

        return handler;
    }



}
