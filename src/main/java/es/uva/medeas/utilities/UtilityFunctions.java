package es.uva.medeas.utilities;

import es.uva.medeas.VensimPlugin;
import es.uva.medeas.parser.Symbol;
import es.uva.medeas.parser.SymbolTable;
import org.apache.commons.lang.StringUtils;

public class UtilityFunctions {

    /**
     * Like Integer.parseInt but allows parsing strings with multiple signs (-----5)
     * @throws NumberFormatException if the argument doesnt match the regex (-|+)*[0-9]+
     * @return
     */
    public static int stringToInt(String number){
        number = number.trim();

        if(!number.matches("(-|\\+)*[0-9]+"))
            throw new NumberFormatException("The string '"+number+"' isn't a integer");
        boolean isPositive = StringUtils.countMatches(number, "-") % 2 ==0;

        String numberWithoutSigns = number.replaceAll("(-|\\+)+","");
        int intWithoutSign = Integer.parseInt(numberWithoutSigns);

        if(isPositive)
            return intWithoutSign;
        else
            return intWithoutSign * -1;

    }


    /**
     * Like Float.parseFloat but allows parsing strings with multiple signs (-----5.3)
     * @throws NumberFormatException if the argument does not contain a parsable float.
     * @return
     */
    public static float stringToFloat(String number){
        number = number.trim();
        if(!number.matches("(-|\\+)*[^-\\+]+((e|E)(-|\\+)*[^-\\+]+)?"))
            throw new NumberFormatException("The string '"+number+"' isn't a float");


        int baseNegatives = 0;
        for(char character: number.toCharArray()){
            if(character=='-')
                baseNegatives+=1;
            if(character!='-' && character!='+'){
                String reduction = baseNegatives%2==0 ? "": "-";
                if(baseNegatives>0)
                    number = number.replaceFirst("(-|\\+)+",reduction);
                else
                    number = number.replaceFirst("(\\+)+","");
                break;
            }
        }
        boolean found = false;
        int exponentialNegatives=0;
        for(int i=1;i<number.length();i++){ //Checks for sign in exponential
            char character = number.charAt(i);
            if(character=='-')
                exponentialNegatives+=1;
            if(character=='-'|| character=='+')
                found= true;

            if(found && character!='-' && character!='+'){
                String reduction = exponentialNegatives%2==0 ? "e": "e-";
                number = number.replaceFirst("(e|E)(-|\\+)+",reduction);
                break;
            }
        }


       return Float.parseFloat(number);

    }

    public static Symbol getSymbolOrCreate(SymbolTable table, String token){
        if(table.hasSymbol(token))
            return table.getSymbol(token);

        else{
            return table.addSymbol(new Symbol(token));
        }
    }

    public static String formatLogMessage(String message){
        return message + " ["+ VensimPlugin.PLUGIN_KEY +"]";
    }
}
