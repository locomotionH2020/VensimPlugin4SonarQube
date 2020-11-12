package es.uva.locomotion.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public class VensimErrorListener extends BaseErrorListener {


    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        throw new ParseCancellationException("l:"+ line + " c:" + charPositionInLine + " " +msg);
    }


}
