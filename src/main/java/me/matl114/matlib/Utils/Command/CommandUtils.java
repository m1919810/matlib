package me.matl114.matlib.Utils.Command;

public class CommandUtils {
    public static String getOrDefault(String[] args,int index,String defaultValue){
        return args.length>index?args[index]:defaultValue;
    }
    public static int parseIntOrDefault(String value,int defaultValue){
        try {
            return Integer.parseInt(value);
        }catch (NumberFormatException e){
            return defaultValue;
        }
    }
    public static Integer parseIntegerOrDefault(String value,Integer defaultValue){
        try {
            return Integer.parseInt(value);
        }catch (NumberFormatException e){
            return defaultValue;
        }
    }
    public static int validRange(int value,int min,int max){
        return Math.max( Math.min(max,value),min);
    }

}
