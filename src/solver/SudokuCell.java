package solver;
import Initializer.Debugger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class SudokuCell {


    private int columnInBox;
    private int rowInBox;
    private boolean isSolved;
    private int value;


    private SudokuBox parent;
    private List<Integer> potetialFits;


    SudokuCell(SudokuBox parent, int columnInBox, int rowInBox){
        this.parent = parent;
        List<Integer> list = new ArrayList<>();
        list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        this.potetialFits = list;
        isSolved = false;
        this.rowInBox = rowInBox;
        this.columnInBox = columnInBox;
        this.value = -1;
    }

    void setValue(int value) {
        this.value = value;
        isSolved = true;
        potetialFits = null;
        parent.receiveCellUpdate(this);
    }
    private void update(){
        if(!isSolved){
            if(potetialFits.size() == 1){
                setValue(potetialFits.get(0));
            }
        }
    }

    void addNewConstraint(int constraint){
        if(potetialFits != null && potetialFits.contains(constraint)){
            Debugger.__("Cell "+getGlobalCellName()+ " receiving new constraint: "+constraint, this);
            potetialFits.remove((Integer) constraint);
            update();
        }
    }

    String getGlobalCellName(){
        int charVal = Character.valueOf(parent.getColumn());
        charVal = charVal + getColumnInBox();
        char col = (char) charVal;
        int row = parent.getRow() + getRowInBox();
        return ""+col+row;
    }


    int getColumnInBox() {
        return columnInBox;
    }

    int getRowInBox() {
        return rowInBox;
    }

    boolean isSolved() {
        update();
        return isSolved;
    }

    int getValue() {
        return value;
    }

}
