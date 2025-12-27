package me.matl114.matlib.implement.custom.inventory;

import com.mojang.serialization.Codec;
import me.matl114.matlib.utils.serialization.Codecs;


public enum SlotType {
    BACKGROUND,
    COMMON_BUTTON,
    PAGE_CONTENT,
    BACK_BUTTON,
    PREV_PAGE,
    NEXT_PAGE,
    BLANK;
    public static final Codec<SlotType> CODEC = Codecs.enumLowerCase(SlotType.class);
}
