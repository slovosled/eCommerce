package com.example.demo;

import java.lang.reflect.Field;

public class TestUtils {

    public static void injectObjects(Object target, String fieldName, Object inject) {
        boolean wasPrivate = false;
        try {
            Field field = target.getClass().getDeclaredField(fieldName);

            if (!field.isAccessible()) {
                field.setAccessible(true);
                wasPrivate = true;
            }
            field.set(target, inject);
            if (wasPrivate) {
                field.setAccessible(false);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

}
