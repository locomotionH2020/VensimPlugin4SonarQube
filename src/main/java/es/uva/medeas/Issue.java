package es.uva.medeas;

import es.uva.medeas.rules.VensimCheck;


public class Issue {

    private final VensimCheck check;
    private final int line;
    private String message;


    public Issue(VensimCheck v, int line, String message){
        check = v;
        this.message = message;
        this.line = line;

    }

    public String getMessage(){
        return message;
    }

    public int getLine(){
        return  line;
    }


    public VensimCheck getCheck(){
        return  check;
    }



}
