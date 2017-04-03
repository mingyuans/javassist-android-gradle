package com.mingyuans.javassist.transformer;

import com.mingyuans.javassist.javassist.CannotCompileException;
import com.mingyuans.javassist.javassist.CtMethod;

/**
 * Created by yanxq on 16/11/4.
 */

public class InvocationBuilder {

    private static final String INVOKE_OBJECT = "dispatcher";

    private StringBuilder mInsertStringBuilder = new StringBuilder();


    public InvocationBuilder() {

    }

    public InvocationBuilder loadInvokeObject() {
        mInsertStringBuilder.append("java.lang.reflect.Field field = java.util.logging.Logger.class.getDeclaredField(\"treeLock\");");
        mInsertStringBuilder.append("field.setAccessible(true);");
        mInsertStringBuilder.append(String.format("java.lang.reflect.InvocationHandler %s = (java.lang.reflect.InvocationHandler)field.get(null);",INVOKE_OBJECT));
        return this;
    }

    public InvocationBuilder loadInvokeCall(String proxy,String argArray) {
        argArray = argArray == null? "null" : argArray;
        mInsertStringBuilder.append(String.format("%s.invoke(\"%s\",null,%s);",INVOKE_OBJECT,proxy,argArray));
        return this;
    }

    public InvocationBuilder loadInvokeCall(String prefix,String proxy,String argArray) {
        argArray = argArray == null? "null" : argArray;
        mInsertStringBuilder.append(String.format("%s.invoke(\"%s\",null,%s);",prefix + INVOKE_OBJECT,proxy,argArray));
        return this;
    }

    public String insertBeforeCtMethod(CtMethod ctMethod) throws CannotCompileException {
        String text = mInsertStringBuilder.toString();
        if (ctMethod != null) {
            ctMethod.insertBefore(text);
        }
        return text;
    }

    public StringBuilder getInvokeTextBuilder() {
        return mInsertStringBuilder;
    }
}
