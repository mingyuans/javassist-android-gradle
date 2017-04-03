package com.mingyuans.javassist.logger;

import java.util.regex.Pattern;

/**
 * Created by yanxq on 16/11/3.
 */

public class ConsoleLog implements Log {

    private static final String TAG = "JavassistAgent";

    private static boolean DEBUG_ENABLE = false;
    public static boolean setDebug(boolean debugEnable) {
        DEBUG_ENABLE = debugEnable;
        return DEBUG_ENABLE;
    }

    public static boolean isDebug() {
        return DEBUG_ENABLE;
    }

    private static final int CALLED_METHOD_STACK_INDEX= 3;
    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");

    @Override
    public void v(String message) {
        consolePrint("v",message);
    }

    @Override
    public void d(String message) {
        if (DEBUG_ENABLE) {
            consolePrint("d",message);
        }
    }

    @Override
    public void w(String message) {
        consolePrint("w",message);
    }

    @Override
    public void e(String message) {
        consoleErrorPrint(message,null);
    }

    @Override
    public void e(String message, Throwable t) {
        consoleErrorPrint(message,t);
    }

    private void consolePrint(String prefix,String message) {
        message = String.format("%s %s",getCalledMethodName(),message);
        message = String.format(TAG + ".%s: %s",prefix,message);
        System.out.println(message);
    }

    private void consoleErrorPrint(String message,Throwable throwable) {
        message = String.format("%s %s",getCalledMethodName(),message);
        message = String.format(TAG + ".%s: %s","e",message);
        System.err.println(message);
        if (throwable != null) {
            throwable.printStackTrace(System.err);
        }
    }

    private String getCalledMethodName() {
        Throwable throwable = new Throwable();
        StackTraceElement[] elements = throwable.getStackTrace();
        if (elements.length <= CALLED_METHOD_STACK_INDEX) {
            return "unknown";
        } else {
            StackTraceElement calledElement = elements[CALLED_METHOD_STACK_INDEX];
            String methodName = calledElement.getMethodName();
            String className = calledElement.getClassName();
            String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
            return simpleClassName + "#" + methodName;
        }
    }

}
