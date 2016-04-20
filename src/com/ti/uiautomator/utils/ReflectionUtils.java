package com.ti.uiautomator.utils;

public class ReflectionUtils {

    @SuppressWarnings("rawtypes")
    public static void invokeMethod(Object object, String methodName, String... args) {
        java.lang.reflect.Method method;
        try {

            Class[] parameterTypes = new Class[args.length];

            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = String.class;
            }

            method = object.getClass().getMethod(methodName, parameterTypes);
            method.invoke(object, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
