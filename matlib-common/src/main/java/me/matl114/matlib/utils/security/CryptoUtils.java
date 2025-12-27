package me.matl114.matlib.utils.security;

public class CryptoUtils {
    public static String illilili(String val, int time) {
        for (var i = 0; i < time; ++i) {
            val = iIliIili(val);
        }
        return val;
    }

    public static String iIliIili(String val) {
        char[] result = new char[val.length()];
        int val11, val22;
        val11 = val22 = result.length - 1;
        for (int ss = (2 ^ 5) << 4 ^ 2 << 1, tt = (2 ^ 5) << 3 ^ 3; val11 >= 0; val11 = val22) {
            result[val11] = (char) ((val.charAt(val22--) ^ ss) + (1 << 15));
            if (val22 < 0) break;
            result[val22] = (char) ((val.charAt(--val11) ^ tt) + (1 << 15));
        }
        return new String(result);
    }
}
