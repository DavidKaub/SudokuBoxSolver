package Initializer;


import solver.SudokuBox;

import java.util.StringTokenizer;

public class Initializer {

    private static String managerUri = "localhost";
    private static int managerPort = 4242;
    private static String boxName;
    private static String initValues;
    private static int localPort;


    public static void main(String[] args) {
        if (!parseArgs(args)) {
            throw new IllegalArgumentException("Wrong Input program terminated");
        }
        SudokuBox sudokuBox = new SudokuBox(boxName,"localhost",localPort,managerUri,managerPort,initValues);


    }

    private static boolean parseArgs(String[] args) {
        if (args.length == 4) {
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
            localPort = Integer.parseInt(args[3]);
            return true;
        } else {
            if (args.length == 3)
                System.out.println("This implementation needs a additional 4th parameter for the port under which the Box can be reached");
            if (args.length < 3) System.out.println("Parameters missing! Check the documentation for more details");
        }
        return false;
    }

}
