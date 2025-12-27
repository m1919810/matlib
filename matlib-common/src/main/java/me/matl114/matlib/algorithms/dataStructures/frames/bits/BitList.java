package me.matl114.matlib.algorithms.dataStructures.frames.bits;

import java.util.Arrays;
import java.util.stream.IntStream;

public class BitList {
    private static final int ADDRESS_BITS_PER_WORD = 6;
    private static final int BITS_PER_WORD = 1 << ADDRESS_BITS_PER_WORD;
    private static final int BIT_INDEX_MASK = BITS_PER_WORD - 1;
    private static final long[] BIT_TRUE_MASK =
            IntStream.range(0, 64).mapToLong(i -> 1L << i).toArray();
    private static final long[] BIT_FALSE_MASK =
            IntStream.range(0, 64).mapToLong(i -> ~(1L << i)).toArray();
    private static final long WORD_MASK = 0xffffffffffffffffL;
    private static final long LAST_BIT_MASK = 1L << 63;
    private long[] words;
    private int bitLen;

    private void initBits(int size) {
        int newLen = ((size - 1) >> ADDRESS_BITS_PER_WORD) + 1;
        words = new long[newLen];
        bitLen = words.length * BITS_PER_WORD;
    }

    private static int wordIndex(int bitIndex) {
        return bitIndex >> ADDRESS_BITS_PER_WORD;
    }

    private static void checkRange(int fromIndex, int toIndex) {
        if (fromIndex < 0) throw new IndexOutOfBoundsException("fromIndex < 0: " + fromIndex);
        if (toIndex < 0) throw new IndexOutOfBoundsException("toIndex < 0: " + toIndex);
        if (fromIndex > toIndex)
            throw new IndexOutOfBoundsException("fromIndex: " + fromIndex + " > toIndex: " + toIndex);
    }

    private void newWordsLen(int newLen) {
        words = Arrays.copyOf(words, newLen);
        bitLen = words.length * BITS_PER_WORD;
    }

    private void ensureSize(int index) {
        if (index >= bitLen) {
            // index should < len , so add 1 for more place
            int newLen = (index >> ADDRESS_BITS_PER_WORD) + 1;
            if (newLen > words.length) {
                newWordsLen(newLen);
            }
        }
    }

    private int ensureWord(int index) {
        int word = index >> ADDRESS_BITS_PER_WORD;
        if (word >= words.length) {
            ensureSize(index);
        }
        return word;
    }

    private void resize(int index) {
        if (index < bitLen) {
            int newLen = ((index - 1) >> ADDRESS_BITS_PER_WORD) + 1;
            if (newLen == 0) {
                newWordsLen(1);
                words[0] = 0L;
            }
            if (newLen < words.length) {
                newWordsLen(newLen);
            }
        }
    }
    // remove unused block from wordAfter +1, wordAfter +2,... wordsLen -1
    private void removeUnused(int wordAfter) {
        int i = words.length - 1;
        for (; i > wordAfter; --i) {
            if (words[i] != 0) {
                break;
            }
        }
        ++i;
        if (i != words.length) {
            newWordsLen(i);
        }
    }

    public BitList() {
        initBits(BITS_PER_WORD);
    }

    public BitList(int size) {
        initBits(size);
    }

    public BitList(boolean[] booleans) {
        this(booleans.length + 1);
        for (int i = 0; i < booleans.length; ++i) {
            if (booleans[i]) {
                setTrue(i);
            }
        }
    }

    public void setTrue(int index) {
        int wordAt = ensureWord(index);
        int bitAt = index & BIT_INDEX_MASK;
        words[wordAt] |= BIT_TRUE_MASK[bitAt];
    }

    public void setTrue(int from, int to) {
        checkRange(from, to);
        int toWord = ensureWord(to);
        int fromWord = wordIndex(from);
        long fromMask = WORD_MASK << (from & BIT_INDEX_MASK);
        long toMask = WORD_MASK >>> (64 - to & BIT_INDEX_MASK);
        if (toWord == fromWord) {
            words[toWord] |= (fromMask & toMask);
        } else {
            words[toWord] |= toMask;
            words[fromWord] |= fromMask;
            for (int i = fromWord + 1; i < toWord; ++i) {
                words[i] = WORD_MASK;
            }
        }
    }

    public void setFalse(int from, int to) {
        checkRange(from, to);

        if (from > bitLen) {
            return;
        }
        if (to >= bitLen) {
            resize(from);
            return;
        }
        int toWord = wordIndex(to);
        int fromWord = wordIndex(from);
        long fromMask = WORD_MASK << (from & BIT_INDEX_MASK);
        long toMask = WORD_MASK >>> (64 - to & BIT_INDEX_MASK);
        if (toWord == fromWord) {
            words[toWord] &= ~(fromMask & toMask);
        } else {
            words[toWord] &= ~toMask;
            words[fromWord] &= ~fromMask;
            for (int i = fromWord + 1; i < toWord; ++i) {
                words[i] = 0;
            }
        }
    }

    public void setFalse(int index) {
        if (index > bitLen) {
            return;
        }
        int wordAt = ensureWord(index);
        int bitAt = index & BIT_INDEX_MASK;
        words[wordAt] &= BIT_FALSE_MASK[bitAt];
    }

    public boolean get(int index) {
        if (index >= bitLen) {
            return false;
        } else {
            int wordAt = ensureWord(index);
            int bitAt = index & BIT_INDEX_MASK;
            return (words[wordAt] & BIT_TRUE_MASK[bitAt]) != 0;
        }
    }

    public void addTrue(int index) {
        int wordIndex = ensureWord(index);
        removeUnused(wordIndex + 1);
        int lastIndex = words.length - 1;
        if ((words[lastIndex] & LAST_BIT_MASK) != 0) {
            // words length may change here
            // index +1 是len
            newWordsLen(lastIndex + 2);
            words[lastIndex + 1] = 1L;
        }

        for (int i = lastIndex; i > wordIndex; --i) {
            // 先计算 上一迭代(首次已经执行了所以跳过首次)没有计算完的 需要使用words[i]的末位填充到words[i+1]的首位
            if (i != lastIndex) {
                if ((words[i] & BIT_TRUE_MASK[63]) != 0) words[i + 1] |= 1L;
            }
            // 移动 移走末位
            words[i] <<= 1;
        }
        // 执行最后一次迭代没有执行完的（如果有
        if (wordIndex < lastIndex) {
            if ((words[wordIndex] & BIT_TRUE_MASK[63]) != 0) {
                words[wordIndex + 1] |= 1L;
            }
        }
        int bitIndex = index & BIT_INDEX_MASK;
        long afterIndexMask = WORD_MASK << (bitIndex);
        long afterIndex = words[wordIndex] & afterIndexMask;
        afterIndex <<= 1;
        afterIndex |= BIT_TRUE_MASK[bitIndex];
        // fill afterIndex with 0
        words[wordIndex] &= ~afterIndexMask;
        // paste afterIndex after shift and appending true at first
        words[wordIndex] |= afterIndex;
    }

    public void addFalse(int index) {
        if (index >= bitLen) {
            return;
        }
        int wordIndex = wordIndex(index);
        removeUnused(wordIndex + 1);
        int lastIndex = words.length - 1;
        if ((words[lastIndex] & LAST_BIT_MASK) != 0) {
            // words length may change here
            // index +1 是len
            newWordsLen(lastIndex + 2);
            words[lastIndex + 1] = 1L;
        }

        for (int i = lastIndex; i > wordIndex; --i) {
            // 先计算 上一迭代(首次已经执行了所以跳过首次)没有计算完的 需要使用words[i]的末位填充到words[i+1]的首位
            if (i != lastIndex) {
                if ((words[i] & LAST_BIT_MASK) != 0) words[i + 1] |= 1L;
            }
            // 移动 移走末位
            words[i] <<= 1;
        }
        // 执行最后一次迭代没有执行完的（如果有
        if (wordIndex < lastIndex) {
            if ((words[wordIndex] & LAST_BIT_MASK) != 0) {
                words[wordIndex + 1] |= 1L;
            }
        }
        long afterIndexMask = WORD_MASK << (index & BIT_INDEX_MASK);
        long afterIndex = words[wordIndex] & afterIndexMask;
        afterIndex <<= 1;
        // fill afterIndex with 0
        words[wordIndex] &= ~afterIndexMask;
        // paste afterIndex after shift and appending false at first
        words[wordIndex] |= afterIndex;
    }

    public boolean remove(int index) {
        if (index >= bitLen) {
            return false;
        } else if (index == bitLen - 1) {
            int lastIndex = words.length - 1;
            if ((words[lastIndex] & LAST_BIT_MASK) != 0) {
                // set to 0 at last bit
                words[lastIndex] &= ~(LAST_BIT_MASK);
                return true;
            } else {
                // nothing changed
                return false;
            }
        } else {
            int wordIndex = wordIndex(index);
            removeUnused(wordIndex);
            int bitIndex = index & BIT_INDEX_MASK;
            // 操作从bitIndex +1开始的位置左移动
            long afterIndexMask = WORD_MASK << (bitIndex + 1);
            boolean ret = (words[wordIndex] & BIT_TRUE_MASK[bitIndex]) != 0;
            long afterIndex = words[wordIndex] & afterIndexMask;
            afterIndex >>>= 1;
            // 覆盖 bitIndex位以免出问题
            // 多要一位 使用算数右移给他补上0
            words[wordIndex] &= ~(afterIndexMask >> 1);
            words[wordIndex] |= afterIndex;
            if (wordIndex < words.length - 1) {
                if ((words[wordIndex + 1] & 1) != 0) {
                    words[wordIndex] |= LAST_BIT_MASK;
                }
            }
            for (int i = wordIndex + 1; i < words.length; ++i) {
                // 执行移动
                words[i] >>>= 1;
                // 从下一位拿回来补全这一位
                if (i != words.length - 1) {
                    if ((words[i + 1] & 1) != 0) words[i] |= LAST_BIT_MASK;
                }
            }
            return ret;
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("BitList[");

        for (var l : words) {
            for (int i = 0; i < 64; ++i) {
                if ((l & BIT_TRUE_MASK[i]) != 0) {
                    builder.append('1');
                } else {
                    builder.append('0');
                }
                if ((i & 7) == 7) {
                    builder.append(" ");
                }
            }
            builder.append("; ");
        }
        builder.append("]");
        return builder.toString();
    }

    public boolean equals(Object value) {
        if (value instanceof BitList bitList) {
            int len = this.words.length;
            int len2 = bitList.words.length;
            int len3 = Math.min(len, len2);
            for (int i = 0; i < len3; ++i) {
                if (words[i] != bitList.words[i]) return false;
            }
            if (len == len2) {

            } else if (len3 == len) {
                for (int i = len3; i < len2; ++i) {
                    if (bitList.words[i] != 0L) return false;
                }
            } else {
                for (int i = len3; i < len; ++i) {
                    if (this.words[i] != 0L) return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void clear() {
        newWordsLen(1);
        words[0] = 0L;
    }
}
