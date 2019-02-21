package solver;

import Initializer.Debugger;

import java.util.Arrays;
import java.util.StringTokenizer;

public class TCP_BOX extends SudokuBox{



    private NetworkHandler networkHandler;
    private SudokuCell[][] boxCells;
    private boolean isSolved = false;
    private String boxUri;



    public TCP_BOX(String boxName, String uri, String boxManagerUri, int boxManagerPort, String initialValues) {
        this.boxName = boxName;
        this.boxUri = uri;

        unusedValues.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        StringTokenizer stringTokenizer = new StringTokenizer(boxName, "_");
        stringTokenizer.nextToken();
        String boxColRow = stringTokenizer.nextToken();
        column = boxColRow.charAt(0);
        column = Character.toUpperCase(column);
        row = Integer.parseInt("" + boxColRow.charAt(1));




        networkHandler = new TCPHandler(this, boxManagerUri, boxManagerPort);
        networkHandler.start();

        setInitialValues(initialValues);
        Debugger.__("initialized!", this);
        Debugger.__(this.toString(), this);
        fireLocalUpdate();
        sendInitialState();
    }


}
