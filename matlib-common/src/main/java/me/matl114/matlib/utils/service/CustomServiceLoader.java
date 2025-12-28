package me.matl114.matlib.utils.service;

import javax.annotation.Nonnull;
import java.util.ServiceLoader;

public class CustomServiceLoader {
    @Nonnull
    public static <T> T loadPlaceHolder(Class<T> clazz) {
        return null;
    }
}
