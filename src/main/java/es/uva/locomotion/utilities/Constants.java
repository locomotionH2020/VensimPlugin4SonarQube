package es.uva.locomotion.utilities;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final List<String> FUNCTIONS_THAT_FORM_COMPOUND_MAGIC_NUMBERS = Arrays.asList("SQRT","TAN","TANH","SIN","SINH","COS",
            "COSH","ARCTAN", "ARCSIN","ARCCOS","ABS","LN","GAMMA LN","INTEGER","GAME","EXP","POWER","LOG","MODULO","QUANTUM");


    // This symbols are defined by default in every Vensim file
    public static final List<String> DEFAULT_VENSIM_SYMBOLS= Arrays.asList("FINAL TIME", "INITIAL TIME", "SAVEPER", "TIME STEP");

    public static  final List<String> IGNORED_MAGIC_NUMBERS = Arrays.asList("0","1","100");
}
