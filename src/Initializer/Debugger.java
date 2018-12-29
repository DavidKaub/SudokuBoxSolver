package Initializer;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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


    private void lockTest(){
        Lock lock = new ReentrantLock();
        if (lock.tryLock())
        {
            // Got the lock
            try
            {
                // Process record
            }
            finally
            {
                // Make sure to unlock so that we don't cause a deadlock
                lock.unlock();
            }
        }
        else
        {
            // Someone else had the lock, abort
        }
    }


}
