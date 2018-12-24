package solver;

import java.util.List;

public class SudokuBox {


    private String boxName;
    private String boxUri;
    private int boxPort;

    private char column;
    private int row;
    private List<String> neighborNames;






    public SudokuBox(String boxName, String uri, int port){
        this.boxName = boxName;
        this.boxUri = uri;
        this.boxPort = port;
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
