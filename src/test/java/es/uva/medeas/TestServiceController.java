package es.uva.medeas;

import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolType;
import es.uva.medeas.utilities.DBFacade;
import es.uva.medeas.utilities.DbServiceHandler;
import es.uva.medeas.utilities.TestDBFacade;
import es.uva.medeas.utilities.Utilities;
import org.junit.After;
import org.junit.Test;
import static  org.mockito.ArgumentMatchers.*;
import org.mockito.Mockito;

import java.util.*;
import java.util.stream.Collectors;


public class TestServiceController {

    @After
    public void  resetDbFacade(){
        Utilities.setDbFacadeHandler(new DbServiceHandler());
    }

    @Test
    public void testControllerIgnoresFunctions(){
        ServiceController controller = new ServiceController("https://localhost");

        DbServiceHandler mockHandler = Utilities.getMockDbServiceHandlerThatReturns("[]");

        Utilities.setDbFacadeHandler(mockHandler);

        List<Symbol> symbols = Arrays.asList(new Symbol("func1", SymbolType.FUNCTION),
                new Symbol("var", SymbolType.VARIABLE),
                new Symbol("func2", SymbolType.FUNCTION),
                new Symbol("const", SymbolType.CONSTANT),
                new Symbol("lookup", SymbolType.LOOKUP),
                new Symbol("subscript", SymbolType.SUBSCRIPT_NAME),
                new Symbol("sValue", SymbolType.SUBSCRIPT_VALUE),
                new Symbol("realityCheck", SymbolType.REALITY_CHECK),
                new Symbol("func3", SymbolType.FUNCTION)
        );
        controller.getSymbolsFromDb(symbols);

        Mockito.verify(mockHandler,Mockito.times(1)).sendRequestToService(any(), argThat(arg->{
            Set<String> actualSet = new HashSet<>(arg);
            Set<String> expectedSet =  new HashSet<>(Arrays.asList("var", "const", "lookup","subscript","sValue","realityCheck"));
            return actualSet.equals(expectedSet);

        }));
    }

    @Test
    public void testControllerIgnoresDefaultSymbols(){
        ServiceController controller = new ServiceController("http://localhost");

        List<Symbol> symbols = Arrays.asList("FINAL TIME", "INITIAL TIME", "SAVEPER", "TIME STEP").stream().map(Symbol::new).collect(Collectors.toList());
        DbServiceHandler mockHandler = Utilities.getMockDbServiceHandlerThatReturns("[]");
        Utilities.setDbFacadeHandler(mockHandler);

        controller.getSymbolsFromDb(symbols);

        Mockito.verify(mockHandler,Mockito.times(1)).sendRequestToService(any(), argThat(List::isEmpty));

    }
}
