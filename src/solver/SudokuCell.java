package solver;
import java.util.List;

public class SudokuCell {


    private int columnInBox;
    private int rowInBox;
    private boolean isSolved;
    private int value;


    private SudokuBox parent;
    private List<Integer> potetialFits;


    public SudokuCell(SudokuBox parent, int columnInBox, int rowInBox, List<Integer> potentialFits){
        this.parent = parent;
        this.potetialFits = potentialFits;
        isSolved = false;
        this.rowInBox = rowInBox;
        this.columnInBox = columnInBox;
        this.value = -1;
    }

    public void setValue(int value) {
        this.value = value;
        isSolved = true;
        potetialFits = null;
        informParent();
    }

    public void informParent(){

        //TODO uncomment!
       // parent.receiveLocalUpdate(this);
        /**
         * inform parent abount new state!
         */

    }

    private void update(){
        if(!isSolved){
            if(potetialFits.size() == 1){
                setValue(potetialFits.get(0));
            }
        }
    }

    public void addNewConstraint(int constraint){
        //System.out.println("Cell "+getGlobalCellName()+ " receiving new constraint: "+constraint);
        if(potetialFits != null && potetialFits.contains(constraint)){
            //System.out.println("WOW Dude - I'm ("+getGlobalCellName()+")amazed to know about that!");
            potetialFits.remove((Integer) constraint);
            update();
        }
    }

    public String getGlobalCellName(){
        int charVal = Character.valueOf(parent.getColumn());
        charVal = charVal + getColumnInBox();
        char col = (char) charVal;
        int row = parent.getRow() + getRowInBox();
        return ""+col+row;
    }







    public int getColumnInBox() {
        return columnInBox;
    }

    public int getRowInBox() {
        return rowInBox;
    }

    public boolean isSolved() {
        update();
        return isSolved;
    }

    public int getValue() {
        return value;
    }

    public SudokuBox getParent() {
        return parent;
    }

    public List<Integer> getPotetialFits() {
        return potetialFits;
    }



}
