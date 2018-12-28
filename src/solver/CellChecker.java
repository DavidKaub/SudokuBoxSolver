package solver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CellChecker {


    public static boolean checkRelativeKnwoledge(String knowledge){
        return regexChecker("[BOX_][A-I][1-9][,][0-2][,][0-2][:][1-9]",knowledge);

    }

    public static boolean checkAbsoluteKnowledge(String knowledge){
        return regexChecker("^[A-I][1-9][:][1-9]",knowledge);
    }



    public static boolean checkBoxName(String cellName){
        return regexChecker("[A-I][1-9]",cellName);
    }


    public static boolean regexChecker(String theRegex, String str2Check){
        Pattern checkRegex = Pattern.compile(theRegex);
        Matcher regaxMatcher = checkRegex.matcher(str2Check);
        while (regaxMatcher.find()){
            if (regaxMatcher.group().length() != 0){
               return true;
            }
        }
        return false;
    }

    //[A-I]
    //[1-9]


}
