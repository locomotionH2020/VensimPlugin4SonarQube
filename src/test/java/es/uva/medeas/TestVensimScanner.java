package es.uva.medeas;


import es.uva.medeas.rules.RuleTestUtilities;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.utils.log.Logger;


import java.util.ArrayList;
import java.util.List;

import static com.ibm.icu.impl.Assert.fail;

public class TestVensimScanner
{

    @Ignore
    @Test
    public void test() throws Exception {
        fail("rename test");
        Logger logger = Mockito.mock(Logger.class);
        VensimScanner.LOG = logger;
        InputFile inputFile = Mockito.mock(InputFile.class);
        Mockito.when(inputFile.contents()).thenReturn("This isn't a vensim model");
        Mockito.when(inputFile.toString()).thenReturn("notAVensimModel.mdl");



        VensimScanner scanner = RuleTestUtilities.getScanner();


        List<InputFile> files = new ArrayList<>();
        files.add(inputFile);
        scanner.scanFiles(files);

        Mockito.verify(logger).error("Unable to parse the file '{}'. Message {}","notAVensimModel.mdl", "no viable alternative at input 'This isn't a vensim model'");

    }

    @Ignore
    @Test
    public void testIfAfileFailsTherestExecutes(){
        fail("");
    }


}
