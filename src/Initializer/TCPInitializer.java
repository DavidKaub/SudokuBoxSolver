package Initializer;

import solver.TCP_BOX;

import java.util.StringTokenizer;

public class TCPInitializer {

    private static String managerUri = "localhost";
    private static int managerPort = 4242;
    private static String boxName;
    private static String initValues;


    public static void main(String[] args) {
        if (!parseArgs(args)) {
            throw new IllegalArgumentException("Wrong Input program terminated");
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TCP_BOX sudokuBox = new TCP_BOX(boxName,"localhost",managerUri,managerPort,initValues);


    }

    private static boolean parseArgs(String[] args) {
        if (args.length == 3) {
            boxName = args[0];
            initValues = args[1];
            String managerInfo = args[2];
            StringTokenizer stringTokenizer = new StringTokenizer(managerInfo, ":");

            if (stringTokenizer.countTokens() < 2 || stringTokenizer.countTokens() > 3) {
                System.out.println("Issue with stringTokenizer Wrong amount of tokens");
            } else {
                if (stringTokenizer.countTokens() == 2) {
                    managerUri = stringTokenizer.nextToken();
                    managerPort = Integer.parseInt(stringTokenizer.nextToken());
                } else if (stringTokenizer.countTokens() == 3) {
                    stringTokenizer.nextToken();
                    managerUri = stringTokenizer.nextToken().trim();
                    while(managerUri.contains("/")){
                        managerUri = managerUri.replace("/","");
                    }
                    managerUri.trim();
                    managerPort = Integer.parseInt(stringTokenizer.nextToken().trim());
                }
            }
            return true;
        } else {
            System.out.println("Wrong or missing Parameters! Check the documentation for more details");
        }
        return false;
    }

}
