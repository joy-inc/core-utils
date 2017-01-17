package com.joy.utils;

import java.lang.reflect.Field;

/**
 * Created by Daisw on 2017/1/11.
 */

public class ReflectionUtil {

    public static <T> T getField(Class<?> clazz, String fieldName, Object o) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(o);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
