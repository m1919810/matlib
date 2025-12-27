package me.matl114.matlib.algorithms.algorithm;

import java.util.Random;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for string manipulation and generation operations.
 * This class provides methods for generating random strings with different character sets,
 * string replacement operations, and pattern-based string transformations.
 */
public class StringUtils {
    /** Character set containing uppercase letters, lowercase letters, numbers, and special characters */
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";

    /** Number of uppercase letters in the character set */
    private static final int BIG_LETTER = 26;

    /** Number of letters (uppercase + lowercase) in the character set */
    private static final int LETTER = 52;

    /** Number of letters and numbers in the character set */
    private static final int LETTER_AND_NUM = 62;

    /** Total number of characters in the character set */
    private static final int ALL = CHARACTERS.length();

    /** Random number generator for string generation */
    private static final Random rand = new Random();

    /**
     * Generates a random string of specified length using characters from a specified range.
     *
     * @param len The length of the string to generate
     * @param range The number of characters to choose from (0 to range-1)
     * @return A random string of the specified length
     */
    private static String randStr(int len, int range) {
        var sb = new StringBuilder();
        for (int i = 0; i < len; ++i) {
            sb.append(CHARACTERS.charAt(rand.nextInt(0, range)));
        }
        return sb.toString();
    }

    /**
     * Generates a random string containing only uppercase letters.
     *
     * @param len The length of the string to generate
     * @return A random string of uppercase letters
     */
    public static String randCapLetter(int len) {
        return randStr(len, BIG_LETTER);
    }

    /**
     * Generates a random string containing only letters (uppercase and lowercase).
     *
     * @param len The length of the string to generate
     * @return A random string of letters
     */
    public static String randLetter(int len) {
        return randStr(len, LETTER);
    }

    /**
     * Generates a random string containing letters and numbers.
     *
     * @param len The length of the string to generate
     * @return A random string of letters and numbers
     */
    public static String randString(int len) {
        return randStr(len, LETTER_AND_NUM);
    }

    /**
     * Generates a random string containing all available characters (letters, numbers, and special characters).
     *
     * @param len The length of the string to generate
     * @return A random string of all character types
     */
    public static String randComplex(int len) {
        return randStr(len, ALL);
    }

    /**
     * Replaces a substring in the given string with a new string.
     * This method handles cases where index1 might be greater than index2 by swapping them.
     *
     * @param a1 The original string
     * @param index1 The starting index of the substring to replace
     * @param index2 The ending index of the substring to replace
     * @param n The new string to insert
     * @return The string with the substring replaced
     */
    public static String replaceSub(String a1, int index1, int index2, String n) {
        if (index1 > index2) {
            return replaceSub(a1, index2, index1, n);
        }
        return new StringBuilder(a1.substring(0, index1))
                .append(n)
                .append(a1.substring(index2))
                .toString();
    }

    /**
     * Replaces all occurrences of a pattern in a string using a custom replacement function.
     * This method allows for dynamic replacement where the replacement string depends on the matched content.
     * If the replacement function returns null, the original match is preserved.
     *
     * @param a1 The original string
     * @param pattern The regex pattern to match
     * @param replacingFunction The function that determines the replacement for each match
     * @return The string with all pattern matches replaced according to the function
     */
    public static String replaceAll(String a1, Pattern pattern, Function<String, String> replacingFunction) {
        StringBuilder builder = new StringBuilder();
        int currentIndex = 0;
        Matcher matcher = pattern.matcher(a1);
        while (matcher.find()) {
            int beginIndex = matcher.start();
            int endIndex = matcher.end();

            String pat = a1.substring(beginIndex, endIndex);
            String result = replacingFunction.apply(pat);
            if (result != null) {
                builder.append(a1, currentIndex, beginIndex);
                builder.append(result);
            } else {
                builder.append(a1, currentIndex, endIndex);
            }
            currentIndex = endIndex;
        }
        int size = a1.length();
        if (currentIndex < size) {
            builder.append(a1, currentIndex, size);
        }
        return builder.toString();
    }
}
