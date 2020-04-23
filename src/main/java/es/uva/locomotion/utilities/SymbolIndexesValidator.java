package es.uva.locomotion.utilities;

import es.uva.locomotion.parser.Symbol;
import es.uva.locomotion.utilities.exceptions.InconsistentIndexesException;
import es.uva.locomotion.utilities.exceptions.MixedIndexTypeException;
import es.uva.locomotion.utilities.exceptions.MultipleSubscriptIndexesException;
import es.uva.locomotion.utilities.exceptions.UnexpectedIndexTypeException;

import java.util.List;

public class SymbolIndexesValidator {

    public static void validate(Symbol symbol){

        List<List<Symbol>> indexes = symbol.getIndexes();


        checkInconsistentNumberOfIndexes(indexes);
        checkIndexesContainBothValuesAndSubscripts(indexes);

    }

    private static void checkIndexesContainBothValuesAndSubscripts(List<List<Symbol>> indexes) {
        for(List<Symbol> indexColumn: indexes){
            boolean subscriptFound = false;
            boolean valueFound = false;

            for(Symbol indexFound: indexColumn)
                switch (indexFound.getType()){
                    case Subscript:
                        if(subscriptFound)
                            throw new MultipleSubscriptIndexesException();
                        if(valueFound)
                            throw new MixedIndexTypeException();

                        subscriptFound = true;
                        break;

                    case Subscript_Value:
                        if(subscriptFound)
                            throw new MixedIndexTypeException();

                        valueFound = true;
                        break;

                    default:
                        throw new UnexpectedIndexTypeException();
                }
        }
    }

    private static void checkInconsistentNumberOfIndexes(List<List<Symbol>> indexes) {
        if(indexes.size()==0)
            return;

        int indexLines= indexes.get(0).size();
        for(List<Symbol> line: indexes){
            if(line.size()!=indexLines)
                throw new InconsistentIndexesException();
        }

    }


}
