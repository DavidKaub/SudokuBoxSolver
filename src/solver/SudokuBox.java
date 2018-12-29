package solver;

import Initializer.Debugger;

import java.util.*;

public class SudokuBox {


    private String boxName;
    private String boxUri;
    private char column;
    private int row;
    private List<String> neighborNames;

    private List<Integer> unusedValues;
    private List<Integer> usedValues;

    private NetworkHandler networkHandler;
    private SudokuCell[][] boxCells;
    private boolean isSolved;


    //TODO potential solving optimization with map?
    private Map<Integer, List<SudokuCell>> potentialNumberPositions;


    public SudokuBox(String boxName, String uri, String boxManagerUri, int boxManagerPort, String initialValues) {
        this.boxName = boxName;
        this.boxUri = uri;
        this.isSolved = false;

        StringTokenizer stringTokenizer = new StringTokenizer(boxName, "_");
        stringTokenizer.nextToken();
        String boxColRow = stringTokenizer.nextToken();
        column = boxColRow.charAt(0);
        column = Character.toUpperCase(column);
        row = Integer.parseInt("" + boxColRow.charAt(1));

        neighborNames = new ArrayList<>();
        unusedValues = new ArrayList<>();
        unusedValues.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        usedValues = new ArrayList<>();

        initializeCells();

        networkHandler = new NetworkHandler(this, boxManagerUri, boxManagerPort);
        networkHandler.start();

        setInitialValues(initialValues);
        Debugger.__("initialized!", this);
        Debugger.__(this.toString(),this);
        fireLocalUpdate();
        sendInitialState();
    }


    public void fireLocalUpdate() {
        if (!isSolved) {
            /**
             * integrated internal solving:
             * count the amount of all possible fits of each cell.
             * If a number is only counted once -> the cell can be solved
             */


            for (int i = 0; i < boxCells.length; i++) {
                for (int j = 0; j < boxCells[i].length; j++) {
                    SudokuCell cell = boxCells[i][j];
                    if (cell.isSolved() && unusedValues.contains(cell.getValue())) {
                        removeAvailableValueFromBox(cell.getValue());
                    }
                }
            }
            if (unusedValues.size() == 0) {
                Debugger.__(getBoxName() + " is solved!",this);
                this.isSolved = true;
            }
        }
    }


    public void receiveCellUpdate(SudokuCell cell) {
        if (!isSolved) {
            Debugger.__("local update received",this);
            if (cell.isSolved() && unusedValues.contains(cell.getValue())) {
                Debugger.__(boxName + " found new value at cell " + cell.getGlobalCellName() + " value = " + cell.getValue(),this);
                removeAvailableValueFromBox(cell.getValue());
                /**
                 * informiere alle zellen die noch nicht geloesst sind darüber, dass der wert nicht mehr verfügbar ist
                 */
                fireLocalUpdate();
                sendNewKnowledgeToNeighbors(cell);
                Debugger.__(this.toString(),this);
            }
        }
    }

    private void sendNewKnowledgeToNeighbors(SudokuCell cell) {
        String message = cell.getGlobalCellName() + ":" + cell.getValue();
        sendNewKnowledgeToNeighbors(message);
    }

    private void sendNewKnowledgeToNeighbors(String message) {
        Debugger.__("Sending new value to all neighbors: " + message, this);
        synchronized (networkHandler) {
            networkHandler.addOutgoingMessage(message);
        }
    }

    private void removeAvailableValueFromBox(int value) {
        if (!isSolved && unusedValues.contains(value)) {
            unusedValues.remove((Integer) value);
            usedValues.add(value);
            addConstraintToAllCells(value);
        }
    }

    private void addConstraintToAllCells(int value) {
        Debugger.__("adding constraint " + value + " to all cells", this);
        for (int i = 0; i < boxCells.length; i++) {
            for (int j = 0; j < boxCells[i].length; j++) {
                boxCells[i][j].addNewConstraint(value);
            }
        }
    }


    public void sendInitialState() {
        /**
         * Propagate current state to all neighbors
         * 1. Laufe über alle Zellen
         * Wenn solved = true sende Wissen
         */
        fireLocalUpdate();
        for (int i = 0; i < boxCells.length; i++) {
            for (int j = 0; j < boxCells[i].length; j++) {
                if (boxCells[i][j].isSolved()) {
                    sendNewKnowledgeToNeighbors(boxCells[i][j]);
                }
            }
        }
    }


    public void receiveKnowledge(String message) {
        if(isSolved){
            return;
        }
        if (message.contains("BOX_")) {
            //then the message has relative information and has to be converted
            if (CellChecker.checkRelativeKnwoledge(message)) {
                message = convertRelativeToAbsoluteKnowledge(message);
                receiveAbsoluteKnowledge(message);
            } else {
                throw new IllegalArgumentException("Invalid Message received");
            }
        } else {
            receiveAbsoluteKnowledge(message);
        }
    }

    private void receiveAbsoluteKnowledge(String message) {
        if (isSolved) {
            return;
        }
        //message with absolute information
        if (!CellChecker.checkAbsoluteKnowledge(message)) {
            Exception exception = new IllegalArgumentException("INVALID KNOWLEDGE RECEIVED");
        } else {
            Debugger.__("receiving new knowledge: " + message, this);

            StringTokenizer strokenizer = new StringTokenizer(message, ":");
            String cell = strokenizer.nextToken();

            char column = cell.charAt(0);
            int row = Integer.parseInt("" + cell.charAt(1));
            int value = Integer.parseInt(strokenizer.nextToken());

            boolean forCol = false;


            //TODO Pürfe diese Methoden bzw die schleifen!
            if (checkIfColumnIsWithinBounderies(column)) {
                Debugger.__("knowledge is relevant for column", this);
                forCol = true;
                int locCol = column - this.column;
                for (int i = 0; i < boxCells[locCol].length; i++) {
                    boxCells[locCol][i].addNewConstraint(value);
                }
            }
            boolean forRow = false;

            if (checkIfRowIsWithinBounderies(row)) {
                Debugger.__("knowledge is relevant for row", this);
                forRow = true;
                int locRow = row - this.row;
                //System.out.println("cell: "+cell+ " - loc row= "+ locRow);
                for (int i = 0; i < boxCells.length; i++) {
                    boxCells[i][locRow].addNewConstraint(value);
                }
            }
            if (!(forCol || forRow)) {
                Debugger.__("new knowledge not used - not relevant!",this);
            }

            fireLocalUpdate();
            /**
             * Send to all neighbors and safe as already sent!
             */
            sendNewKnowledgeToNeighbors(message);
        }
    }

    private String convertRelativeToAbsoluteKnowledge(String knowledge) {
        throw new IllegalArgumentException("not implemented yet!");
        /**
         * TODO !!!
         */
        //return knowledge;
    }


    private boolean checkIfRowIsWithinBounderies(int row) {
        if (this.row <= row && (this.row + 2) >= row) {
            //System.out.println("check for row: input = "+ row+ " local = "+ this.row+ " -> true");
            return true;
        }
        //System.out.println("check for row: input = "+ row+ " local = "+ this.row+ " -> false");
        return false;
    }

    private boolean checkIfColumnIsWithinBounderies(char column) {
        if (this.column <= column && (this.column + 2) >= column) {
            return true;
        }
        return false;
    }


    private void initializeCells() {
        //setup raw cells
        boxCells = new SudokuCell[3][3];
        for (int i = 0; i < boxCells.length; i++) {
            for (int j = 0; j < boxCells[i].length; j++) {
                boxCells[i][j] = new SudokuCell(this, i, j);
            }
        }
    }


    private void setInitialValues(String initialValues) {
        StringTokenizer stringTokenizer = new StringTokenizer(initialValues, ", :");

        while (stringTokenizer.hasMoreTokens()) {
            String cell = stringTokenizer.nextToken().trim();
            int value = Integer.parseInt(stringTokenizer.nextToken().trim());
            if (cell.length() != 2) {
                System.out.println("FEHLER!!!");
                throw new IllegalArgumentException("WRONG STRING SPLIT");
            }
            int x = Integer.parseInt("" + cell.charAt(0));
            int y = Integer.parseInt("" + cell.charAt(1));
            Debugger.__("Initial Value for cell " + boxCells[x][y].getGlobalCellName() + "  = " + value,this);
            boxCells[x][y].setValue(value);
        }
    }


    public List<String> getNeighborNames() {

        if (!neighborNames.isEmpty()) {
            return neighborNames;
        }
        if (this.column > 'A') {
            /**
             * hat einen linken Nachbarn
             */
            int charVal = Character.valueOf(column);
            charVal = charVal - 3;
            char c = (char) charVal;
            neighborNames.add("BOX_" + c + row);
        }
        if (this.column < 'G') {
            /**
             * hat einen rechten Nachbarn
             */
            int charVal = Character.valueOf(column);
            charVal = charVal + 3;
            char c = (char) charVal;
            neighborNames.add("BOX_" + c + row);
        }
        if (this.row > 1) {
            /**
             * hat einen oberen Nachbarn
             */
            int r = row - 3;
            neighborNames.add("BOX_" + column + r);
        }

        if (this.row < 7) {
            /**
             * hat einen unteren Nachbarn
             */
            int r = row + 3;
            neighborNames.add("BOX_" + column + r);
        }
        return neighborNames;
    }

    public String getBoxUri() {
        return boxUri;
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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getBoxName() + ":");

        stringBuilder.append("\n");
        for (int i = 0; i < boxCells.length; i++) {
            stringBuilder.append("\n -------------");
            stringBuilder.append("\n | ");
            for (int j = 0; j < boxCells[i].length; j++) {
                if (boxCells[j][i].getValue() != -1) {
                    stringBuilder.append(boxCells[j][i].getValue() + " | ");
                } else stringBuilder.append(" " + " | ");
            }
        }
        stringBuilder.append("\n -------------");
        return stringBuilder.toString();
    }


    public boolean isSolved(){
        return isSolved;
    }

    public String printResult(){
        if(!isSolved) return null;
        StringBuilder stringBuilder = new StringBuilder();

        for (int r = 0; r < boxCells[0].length; r++){
            for (int c = 0; c < boxCells.length; c++){
                stringBuilder.append(boxCells[c][r].getValue()+",");
            }
        }
        String result = stringBuilder.toString();
        //remove last comma
        result = result.substring(0, (result.length()-1));
        return result;
    }

}
