package me.matl114.matlib;

public class StrUtil {
    public static String oooooo(String IIIiIIiiIi) {
        int var10000 = 4 << 4 ^ 3 << 2 ^ 3;
        int var10001 = (2 ^ 5) << 4 ^ 2 << 1;
        int var10002 = (2 ^ 5) << 3 ^ 3;
        int var10003 = (IIIiIIiiIi = (String) IIIiIIiiIi).length();
        char[] var10004 = new char[var10003];
        boolean var10006 = true;
        int var5 = var10003 - 1;
        var10003 = var10002;
        int var3;
        var10002 = var3 = var5;
        char[] var1 = var10004;
        int var4 = var10003;
        var10000 = var10002;

        for (int var2 = var10001; var10000 >= 0; var10000 = var3) {
            var10001 = var3;
            char var6 = IIIiIIiiIi.charAt(var3);
            --var3;
            var1[var10001] = (char) (var6 ^ var2);
            if (var3 < 0) {
                break;
            }

            var10002 = var3--;
            var1[var10002] = (char) (IIIiIIiiIi.charAt(var10002) ^ var4);
        }

        return new String(var1);
    }

    public static String oooooo1(String IIIiIIiiIi) {
        int var10000 = (2 ^ 5) << 4 ^ 1;
        int var10001 = 3 << 3;
        int var10002 = (2 ^ 5) << 4 ^ 5 << 1;
        int var10003 = (IIIiIIiiIi = (String) IIIiIIiiIi).length();
        char[] var10004 = new char[var10003];
        boolean var10006 = true;
        int var5 = var10003 - 1;
        var10003 = var10002;
        int var3;
        var10002 = var3 = var5;
        char[] var1 = var10004;
        int var4 = var10003;
        var10001 = var10000;
        var10000 = var10002;

        for (int var2 = var10001; var10000 >= 0; var10000 = var3) {
            var10001 = var3;
            char var6 = IIIiIIiiIi.charAt(var3);
            --var3;
            var1[var10001] = (char) (var6 ^ var2);
            if (var3 < 0) {
                break;
            }

            var10002 = var3--;
            var1[var10002] = (char) (IIIiIIiiIi.charAt(var10002) ^ var4);
        }

        return new String(var1);
    }
}
