package Initializer;

public class Debugger {
    private static boolean verbose = true;


    public static void __(String message, Object object){
        if(verbose){
            System.out.println("\n----------------\nOutput from "+ object.getClass().getSimpleName()+ ":\n"+ message+"\n----------------");
        }
    }


}
