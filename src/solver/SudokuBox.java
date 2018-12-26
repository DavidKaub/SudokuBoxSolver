package solver;

import java.util.*;

public class SudokuBox {


    private String boxName;
    private String boxUri;
    private int boxPort;
    private char column;
    private int row;
    private List<String> neighborNames;
    private List <Integer> unusedValues;
    private List <Integer> usedValues;
    private NetworkHandler networkHandler;
    private SudokuCell[][] boxCells;




    public SudokuBox(String boxName, String uri, int port, String boxManagerUri, int boxManagerPort,String initialValues){
        this.boxName = boxName;
        this.boxUri = uri;
        this.boxPort = port;

        StringTokenizer stringTokenizer = new StringTokenizer(boxName, "_");
        stringTokenizer.nextToken();
        String boxColRow = stringTokenizer.nextToken();
        column = boxColRow.charAt(0);
        row = Integer.parseInt(""+boxColRow.charAt(1));

        neighborNames = new ArrayList<>();
        unusedValues = new ArrayList<>();
        unusedValues.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        usedValues = new ArrayList<>();

        initializeCells();
        setInitialValues(initialValues);
        networkHandler = new NetworkHandler(this,boxManagerUri,boxManagerPort);
        networkHandler.start();
    }


    private void initializeCells(){
        //setup raw cells
        boxCells = new SudokuCell[3][3];
        List<Integer> list = new ArrayList<>();
        list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        for (int i = 0; i < boxCells.length; i++){
            for (int j = 0; j < boxCells[i].length; j++){
                boxCells[i][j] = new SudokuCell(this,i,j, list);
            }
        }
    }


    private void setInitialValues(String initialValues){
        StringTokenizer stringTokenizer = new StringTokenizer(initialValues,", :");

        while(stringTokenizer.hasMoreTokens()){
            String cell = stringTokenizer.nextToken().trim();
            int value = Integer.parseInt(stringTokenizer.nextToken().trim());
            if(cell.length() != 2){
                System.out.println("FEHLER!!!");
                throw new IllegalArgumentException("WRONG STRING SPLIT");
            }
            int x = Integer.parseInt(""+cell.charAt(0));
            int y = Integer.parseInt(""+cell.charAt(1));
            boxCells[x][y].setValue(value);
        }
    }


    public List<String> getNeighborNames(){

        if (!neighborNames.isEmpty()){
            return neighborNames;
        }
        if(this.column > 'A'){
            /**
             * hat einen linken Nachbarn
             */
            int charVal = Character.valueOf(column);
            charVal = charVal - 3;
            char c = (char) charVal;
            neighborNames.add(""+c+row);
        }
        if(this.column < 'G'){
            /**
             * hat einen rechten Nachbarn
             */
            int charVal = Character.valueOf(column);
            charVal = charVal + 3;
            char c = (char) charVal;
            neighborNames.add(""+c+row);
        }
        if(this.row > 1){
            /**
             * hat einen oberen Nachbarn
             */
            int r = row - 3;
            neighborNames.add(""+column+r);
        }

        if(this.row < 7){
            /**
             * hat einen unteren Nachbarn
             */
            int r = row + 3;
            neighborNames.add(""+column+r);
        }
        return neighborNames;
    }

    public String getBoxUri() {
        return boxUri;
    }

    public int getBoxPort() {
        return boxPort;
    }

    public char getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public String getBoxName() {
        return boxName;
    }
}
