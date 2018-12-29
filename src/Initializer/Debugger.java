package Initializer;

public class Debugger {
    private static boolean verbose = true;
    private static boolean verboseBox = false;
    private static boolean verboseCell = false;


    public static void __(String message, Object object) {
        if (verbose) {
            String className = object.getClass().getSimpleName();
            boolean print = false;

            switch (className) {
                case "SudokuCell":
                    print = verboseCell;
                    break;

                case "SudokuBox":
                    print = verboseBox;
                    break;

                default:
                    print = true;
                    break;
            }
            if (print)
                System.out.println("\n----------------\nOutput from " + object.getClass().getSimpleName() + ":\n" + message + "\n----------------");
        }
    }


}
