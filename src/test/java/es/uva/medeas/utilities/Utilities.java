package es.uva.medeas.utilities;



import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class Utilities {
    public static DbServiceHandler getMockDbServiceHandlerThatReturns(String returnValue){
        DbServiceHandler handler = mock(DbServiceHandler.class);

        doReturn(returnValue).when(handler).sendRequestToService(anyString(),anyList());

        return handler;
    }

    public static void setDbFacadeHandler(DbServiceHandler handler){
      DBFacade.handler =  handler;
    }


}
