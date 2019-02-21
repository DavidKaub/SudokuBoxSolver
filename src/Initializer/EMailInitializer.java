package Initializer;

import solver.EmailBox;

import java.util.StringTokenizer;

public class EMailInitializer {

    private static String managerEmail;


    public static void main(String[] args) {
        if (args.length == 1) {
            managerEmail = args[0];
        }else
            throw new IllegalArgumentException("Wrong Input program terminated");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        EmailBox sudokuBox = new EmailBox(managerEmail);


    }



}
