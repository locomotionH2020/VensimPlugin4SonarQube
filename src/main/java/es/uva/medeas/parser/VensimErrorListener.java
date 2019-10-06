package es.uva.medeas.parser;

import es.uva.medeas.VensimScanner;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.util.BitSet;

public class VensimErrorListener extends BaseErrorListener {

    private static final Logger LOG = Loggers.get(VensimErrorListener.class);

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        throw new ParseCancellationException(msg);
    }


}
