package es.uva.medeas.utilities;



import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class Utilities {
    public static ServiceConnectionHandler getMockDbServiceHandlerThatReturns(String returnValue){
        ServiceConnectionHandler handler = mock(ServiceConnectionHandler.class);

        doReturn(returnValue).when(handler).sendRequestToDictionaryService(anyString(),anyList());

        return handler;
    }

    public static void setDbFacadeHandler(ServiceConnectionHandler handler){
      DBFacade.handler =  handler;
    }


}
