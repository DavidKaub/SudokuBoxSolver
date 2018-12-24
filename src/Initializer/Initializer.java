package Initializer;

import solver.ManagerConnection;
import solver.SudokuBox;

import java.io.IOException;

public class Initializer {

     static String managerUri = "localhost";
     static int managerPort = 4242;
     static String boxName ="BOX_A1";

    public static void main(String [] args){
        if(!parseArgs(args)){
            throw new IllegalArgumentException("Wrong Input program terminated");
        }
        SudokuBox sudokuBox = new SudokuBox(boxName,managerUri,4444);
        try {
            ManagerConnection managerConnection = new ManagerConnection(sudokuBox,managerUri,managerPort);
            managerConnection.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private static boolean parseArgs(String [] a){
        return true;
    }

/*
    private static boolean parseArgs(String [] args){
        // Check if arguments have the specified patterns
        for(String arg : args) {
            // Box
            if (arg.matches("^BOX_[ADG][147]$")) {
                //this.boxName(arg);
                continue;
            }

            // Cells
            if (arg.matches("^([0-2][0-2]:[1-9],\\s*){0,8}([0-2][0-2]:[1-9])$")) {
                String[] singleCells = arg.split(",");
                for (String cell: singleCells) {
                    cell = cell.trim();
                    int column = Integer.parseInt(String.valueOf(cell.charAt(0)));
                    int row = Integer.parseInt(String.valueOf(cell.charAt(1)));
                    int value = Integer.parseInt(String.valueOf(cell.charAt(3)));
                    Main.box.setCellValue(column, row, value);
                }
                continue;
            }

            // URI of manager
            if (arg.matches("^(tcp://)([a-zA-Z0-9.:-]+):([0-9]+)$")) {
                Main.managerAddress = arg.substring(6, arg.lastIndexOf(":"));
                Main.managerPort = Integer.parseInt(arg.substring(arg.lastIndexOf(":") + 1));
                managerAddressIsKnown = true;
                continue;
            }

            return false;
        }
        return true;

    }
    */

}
