package es.uva.medeas.utilities;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final List<String> IGNORED_FUNCTIONS_IF_ALONE = Arrays.asList("SQRT","TAN","TANH","SIN","SINH","COS",
        "COSH","ARCTAN", "ARCSIN","ARCCOS","ABS","LN","GAMMA LN","INTEGER","GAME","EXP","POWER","LOG","MODULO","QUANTUM");


    public static List<String> FUNCTION_IS_COMPOUND_MAGIC_NUMBER = Arrays.asList("POWER","LOG","MODULO","QUANTUM"); //TODO Unificar comportamientos de ambas listas
}
