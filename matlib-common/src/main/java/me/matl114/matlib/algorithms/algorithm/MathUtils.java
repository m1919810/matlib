package me.matl114.matlib.algorithms.algorithm;

import java.util.UUID;
import javax.annotation.Nonnull;

/**
 * Utility class for mathematical operations and bit manipulation.
 * This class provides methods for bit operations, binary string manipulation,
 * safe arithmetic operations, and mathematical utilities.
 */
public class MathUtils {

    /** Array of characters representing digits 0-9 and letters a-z */
    static final char[] digits = new char[] {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
        'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    /** Array of bit masks for masking operations */
    static final int[] mask;

    /** Array of powers of 2 for bit position calculations */
    static final int[] POW;

    /** String representation of zero in binary (32 zeros) */
    static final String zeroString = toBinaryCode(0);

    static {
        mask = new int[33];
        POW = new int[33];
        for (int i = 0; i < 32; i++) {
            POW[i] = (1 << i);
            mask[i] = (1 << i) - 1;
        }
        mask[32] = -1;
    }

    /**
     * Gets the bit position value (2^k) for the given bit index.
     *
     * @param k The bit index
     * @return The value 2^k
     */
    public static int getBitPos(int k) {
        return POW[k];
    }

    /**
     * Gets the value of a specific bit in an integer.
     * Uses little-endian storage convention for int values.
     *
     * @param code The integer to check
     * @param bit The bit position (0-31)
     * @return true if the bit is set (1), false otherwise
     */
    public static boolean getBits(int code, int bit) {
        return ((code >> bit) & 1) != 0;
    }

    /**
     * Gets the value of a specific bit in a binary string.
     *
     * @param codeStr The binary string representation
     * @param bit The bit position (0-based index)
     * @return true if the bit is '1', false if it's '0'
     */
    public static boolean getBit(String codeStr, int bit) {
        return codeStr.charAt(bit) == '1';
    }

    /**
     * Formats an unsigned integer to a string representation in the specified radix.
     *
     * @param val The value to format
     * @param shift The shift amount (log2 of radix)
     * @param buf The buffer to write to
     * @param len The length of the buffer
     */
    private static void formatUnsignedInt(int val, int shift, byte[] buf, int len) {
        int charPos = 0;
        int radix = 1 << shift;
        int mask = radix - 1;
        do {
            buf[charPos] = (byte) digits[val & mask];
            val >>>= shift;
            ++charPos;
        } while (charPos < len);
    }

    /**
     * Converts an integer to its 32-bit binary string representation.
     *
     * @param num The integer to convert
     * @return A 32-character string representing the binary value
     */
    public static String toBinaryCode(int num) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; ++i) {
            sb.append((num & 1) == 0 ? '0' : '1');
            num = num >> 1;
        }
        return sb.toString();
    }

    /**
     * Performs a bitwise AND operation on two 32-bit binary strings.
     *
     * @param a The first binary string
     * @param b The second binary string
     * @return The result of the AND operation, or zero string if inputs are invalid
     */
    public static String andStr(String a, String b) {
        if (a.length() != 32 || b.length() != 32) {
            return zeroString;
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 32; ++i) {
                sb.append((a.charAt(i) == digits[1] && b.charAt(i) == digits[1]) ? '1' : '0');
            }
            return sb.toString();
        }
    }

    /**
     * Performs a bitwise OR operation on two 32-bit binary strings.
     *
     * @param a The first binary string
     * @param b The second binary string
     * @return The result of the OR operation, or zero string if inputs are invalid
     */
    public static String orStr(String a, String b) {
        if (a.length() != 32 || b.length() != 32) {
            return zeroString;
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 32; ++i) {
                sb.append((a.charAt(i) == digits[1] || b.charAt(i) == digits[1]) ? '1' : '0');
            }
            return sb.toString();
        }
    }

    /**
     * Performs a bitwise XOR operation on two 32-bit binary strings.
     *
     * @param a The first binary string
     * @param b The second binary string
     * @return The result of the XOR operation, or zero string if inputs are invalid
     */
    public static String xorStr(String a, String b) {
        if (a.length() != 32 || b.length() != 32) {
            return zeroString;
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 32; ++i) {
                sb.append((a.charAt(i) == b.charAt(i)) ? '0' : '1');
            }
            return sb.toString();
        }
    }

    /**
     * Performs a bitwise NOT operation on a 32-bit binary string.
     *
     * @param a The binary string to negate
     * @return The result of the NOT operation, or zero string if input is invalid
     */
    public static String notStr(String a) {
        if (a.length() != 32) {
            return zeroString;
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 32; ++i) {
                sb.append((a.charAt(i) == digits[1]) ? '0' : '1');
            }
            return sb.toString();
        }
    }

    /**
     * Performs a left shift operation on a 32-bit binary string.
     *
     * @param a The binary string to shift
     * @return The result of the left shift, or zero string if input is invalid
     */
    public static String leftShiftStr(String a) {
        if (a.length() != 32) {
            return zeroString;
        } else {
            StringBuilder sb = new StringBuilder(a);
            sb.insert(0, digits[0]);
            return sb.substring(0, 32);
        }
    }

    /**
     * Performs an arithmetic right shift operation on a 32-bit binary string.
     *
     * @param a The binary string to shift
     * @return The result of the arithmetic right shift, or zero string if input is invalid
     */
    public static String rightShiftStr(String a) {
        if (a.length() != 32) {
            return zeroString;
        } else {
            StringBuilder sb = new StringBuilder(a);
            sb.append(a.charAt(a.length() - 1));
            return sb.substring(1, a.length() + 1);
        }
    }

    /**
     * Converts a binary string back to an integer.
     * Currently returns 0 - implementation needed.
     *
     * @param code The binary string to convert
     * @return The integer value (currently always 0)
     */
    public static int fromBinaryCode(String code) {
        /**
         * we assume that  code len 32
         */
        return 0;
    }

    /**
     * Masks an integer to the specified number of bits.
     *
     * @param code The integer to mask
     * @param n The number of bits to keep (0-32)
     * @return The masked integer
     */
    public static int maskToN(int code, int n) {
        int maskN = mask[n];
        return code & maskN;
    }

    /**
     * Gets the value of a specific bit in an integer.
     *
     * @param code The integer to check
     * @param pos The bit position (0-31)
     * @return 1 if the bit is set, 0 otherwise
     */
    public static int getBit(int code, int pos) {
        return (code & POW[pos]) == 0 ? 0 : 1;
    }

    /**
     * Counts the number of set bits in an integer up to the specified position.
     * Uses an optimized algorithm for bit counting.
     *
     * @param code The integer to count bits in
     * @param to The maximum bit position to count up to
     * @return The number of set bits
     */
    public static int bitCount(int code, int to) {
        int n = maskToN(code, to);
        n = n - ((n >> 1) & 0x55555555); // 1
        n = (n & 0x33333333) + ((n >> 2) & 0x33333333); // 2
        n = (n + (n >> 4)) & 0x0f0f0f0f; // 3
        n = n + (n >> 8); // 4
        n = n + (n >> 16); // 5
        return n & 0x3f; // 6
    }

    /**
     * Counts the number of set bits in an integer up to the specified position.
     * Uses a simple loop-based algorithm (less efficient than bitCount).
     *
     * @param code The integer to count bits in
     * @param to The maximum bit position to count up to
     * @return The number of set bits
     */
    public static int bitCountStupid(int code, int to) {
        int n = maskToN(code, to);
        int count = 0;
        for (int i = 0; i < 32; ++i) {
            count += code & 1;
            code = code >> 1;
        }
        return count;
    }

    /**
     * Safely converts a Long to an int, clamping to Integer.MAX_VALUE if necessary.
     *
     * @param a The Long value to convert
     * @return The int value, or Integer.MAX_VALUE if the Long is too large
     */
    public static int fromLong(@Nonnull Long a) {
        if (a > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else return a.intValue();
    }

    /**
     * Safely adds two integers, checking for overflow.
     *
     * @param a The first integer
     * @param b The second integer
     * @return The sum, or Integer.MAX_VALUE if overflow occurs
     */
    public static int safeAdd(int a, int b) {
        int x = a + b;
        if ((x ^ a) < 0 && (x ^ b) < 0) {
            return Integer.MAX_VALUE;
        }
        return x;
    }

    /**
     * Safely divides two integers, handling division by zero.
     *
     * @param a The dividend
     * @param b The divisor
     * @return The quotient, or Integer.MAX_VALUE if division by zero occurs
     */
    public static int safeDivide(int a, int b) {
        if (b == 0) {
            return a == 0 ? 0 : Integer.MAX_VALUE;
        } else {
            return a / b;
        }
    }

    /**
     * Clamps an integer value between a minimum and maximum.
     *
     * @param value The value to clamp
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @return The clamped value
     */
    public static int clamp(int value, int min, int max) {
        return (int) Math.min(max, Math.max(value, min));
    }

    /**
     * Clamps a double value between a minimum and maximum.
     *
     * @param value The value to clamp
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @return The clamped value
     */
    public static double clamp(double value, double min, double max) {
        return Math.min(max, Math.max(value, min));
    }

    /**
     * Clamps a float value between a minimum and maximum.
     *
     * @param value The value to clamp
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @return The clamped value
     */
    public static float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }

    /**
     * Checks if a string represents a valid integer.
     * Handles negative numbers and validates that all characters are digits.
     *
     * @param str The string to check
     * @return true if the string represents a valid integer, false otherwise
     */
    public static boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        int i = 0;
        // 处理负号
        if (str.charAt(0) == '-') {
            if (str.length() == 1) {
                return false; // 只有负号不是整数
            }
            i = 1;
        }
        // 遍历字符
        for (; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false; // 非数字字符
            }
        }
        return true;
    }

    public static int[] uuidToIntArray(UUID uuid) {
        long mostSignificantBits = uuid.getMostSignificantBits();
        long leastSignificantBits = uuid.getLeastSignificantBits();
        return leastMostToIntArray(mostSignificantBits, leastSignificantBits);
    }

    private static int[] leastMostToIntArray(long most, long least) {
        return new int[] {(int) (most >> 32), (int) most, (int) (least >> 32), (int) least};
    }

    public static UUID uuidFromIntArray(int[] bits) {
        return new UUID((long) bits[0] << 32 | bits[1] & 4294967295L, (long) bits[2] << 32 | bits[3] & 4294967295L);
    }
}
