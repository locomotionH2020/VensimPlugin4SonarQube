package es.uva.locomotion.utilities;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final List<String> FUNCTIONS_THAT_FORM_COMPOUND_MAGIC_NUMBERS = Arrays.asList("SQRT","TAN","TANH","SIN","SINH","COS",
            "COSH","ARCTAN", "ARCSIN","ARCCOS","ABS","LN","GAMMA LN","INTEGER","GAME","EXP","POWER","LOG","MODULO","QUANTUM");


    public static final List<String> IMPURE_FUNCTIONS = Arrays.asList("INTEG","STEP","DELAY1", "DELAY1I", "DELAY3", "DELAY3I",
            "FORECAST", "SMOOTH3", "SMOOTH3I", "SMOOTHI", "SMOOTH", "TREND","RAMP","RANDOM 0 1","RANDOM BETA","RANDOM BINOMIAL",
            "RANDOM EXPONENTIAL", "RANDOM GAMMA", "RANDOM LOOKUP","RANDOM NEGATIVE BINOMIAL","RANDOM NORMAL", "RANDOM PINK NOISE",
            "RANDOM POISSON","RANDOM TRIANGULAR","RANDOM UNIFORM","RANDOM WEIBULL");

    // This symbols are defined by default in every Vensim file
    public static final List<String> DEFAULT_VENSIM_SYMBOLS= Arrays.asList("TIME", "FINAL TIME", "INITIAL TIME", "SAVEPER", "TIME STEP");

    public static  final List<String> IGNORED_MAGIC_NUMBERS = Arrays.asList("0","1","100","-1");
}
