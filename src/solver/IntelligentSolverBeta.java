package solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO potential solving optimization with map?


public class IntelligentSolverBeta {

    private Map<Integer, List<SudokuCell>> potentialNumberPositions = new HashMap<>();


    private List<Integer> unusedValues = new ArrayList<>();
    private SudokuCell[][] boxCells;


    public IntelligentSolverBeta(){
        for (int i = 1; i <= 9; i++) {
            potentialNumberPositions.put(i, new ArrayList<>());
        }
    }
    private void clearMapLists() {
        for (int i = 1; i <= 9; i++) {
            potentialNumberPositions.get((Integer) i).clear();
        }
    }
    private void testCode(){
        /**
         * THIS CODE CREATED INCONSITENCE. Why ? I do not know!
         */




        //Initialize Map


        //1. part of "smart" solving
        clearMapLists();
        //end 1 part

        for (int i = 0; i < boxCells.length; i++) {
            for (int j = 0; j < boxCells[i].length; j++) {
                SudokuCell cell = boxCells[i][j];

                if (cell.isSolved() && unusedValues.contains(cell.getValue())) {
                    //removeAvailableValueFromBox(cell.getValue());
                }

                //2. part of "smart" solving
                if (!cell.isSolved() && unusedValues.size() > 1) {
                    /**
                     * wir haben hier eine Map die für jeden möglichen Wert die besetzbaren Zellen speichert (in einer Liste)
                     * Wenn eine der Listen die Länge 1 hat kann der Wert gesetzt werden ( ein Wert kann nur an einer
                     * bestimmten stelle gesetzt werden (!= eine Zelle kann nur noch einen bestimmten wert haben))
                     *
                     * Koennte noch optimeirt werden wenn man außer der Box noch die Constraints angrenzender Boxen bzw. der
                     * korrespondierenden Reiehen und spalten berücksichjtrigt
                     *
                     */

                    for(Integer val: cell.getPotetialFits()){
                        potentialNumberPositions.get((Integer) val).add(cell);
                    }
                }
                //end 2 part
            }
        }
    }


}
