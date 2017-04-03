package com.mingyuans.javassist.transformer.invocation;

import com.mingyuans.javassist.logger.ConsoleLog;
import com.mingyuans.javassist.logger.Log;
import com.mingyuans.javassist.javassist.ByteArrayClassPath;
import com.mingyuans.javassist.javassist.ClassPool;
import com.mingyuans.javassist.javassist.CtClass;
import com.mingyuans.javassist.transformer.ClassTransformerImpl;
import com.mingyuans.javassist.custom.CustomClassTransformer;
import com.mingyuans.javassist.custom.CustomClassTransformerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yanxq on 16/11/7.
 */

public class DexerMainInvocationHandler implements InvocationHandler {
    private final Log logger;
    private List<String> mClassPoolAppendPathCache = new LinkedList<>();
    public DexerMainInvocationHandler(Log logger) {
        this.logger = logger;
        CustomClassTransformerFactory.init(logger);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String[] keySplit = InvocationUtils.splitInvocationMethod((String) proxy);
        String methodName = keySplit[1];

        switch (methodName) {
            case "processClass" :
                return processProcessClassInvoke(args);
            default:
                break;
        }
        return null;
    }

    private Object processProcessClassInvoke(Object[] args) {
        String classFileName = (String) args[0];
        byte[] classFileBuffer = (byte[]) args[1];
        Object dexArgs = args[2];
        insertClassPath(dexArgs);

        String className = getClassName(classFileName);
        logger.d("try get transformer : " + className);
        CustomClassTransformer transformer = CustomClassTransformerFactory.getTransformer(className);
        if (transformer != null) {
            CtClass ctClass = getCtClass(className,classFileBuffer);
            if (ctClass != null && !ctClass.isFrozen()) {
                byte[] transformByte = transformer.transform(ctClass, ClassPool.getDefault());
                if (transformByte != null) {
                    logger.d("success " + className);
                    return transformByte;
                }
            }
        }
        return classFileBuffer;
    }

    /**
     * 追加 class path ,以防止之后的 insertBefore 等操作出现无法导入某个类的问题
     * @param dexArgs
     */
    private void insertClassPath(Object dexArgs) {
        try {
            Field field = dexArgs.getClass().getDeclaredField("fileNames");
            field.setAccessible(true);
            String[] fileNames = (String[]) field.get(dexArgs);
            ClassPool classPool = getClassPool();
            for (String fileName : fileNames) {
                if (!mClassPoolAppendPathCache.contains(fileName)) {
                    classPool.appendClassPath(fileName);
                    mClassPoolAppendPathCache.add(fileName);
                }
            }
        } catch (Exception e) {
            logger.e("processClass Invoke : ",e);
        }
    }

    private String getClassName(String fileName) {
        String noClassSuffix = fileName == null? "" : fileName.substring(0,fileName.lastIndexOf("."));
        return noClassSuffix.replaceAll("/",".");
    }

    private CtClass getCtClass(String className,byte[] classFileBuffer) {
        ClassPool classPool = getClassPool();
        CtClass ctClass = classPool.getOrNull(className);
        if (ctClass == null && classFileBuffer != null && classFileBuffer.length > 0) {
            classPool.appendClassPath(new ByteArrayClassPath(className,classFileBuffer));
        }
        ctClass = classPool.getOrNull(className);
        if (ConsoleLog.isDebug() && ctClass != null) {
            ctClass.debugDump = ClassTransformerImpl.DEBUG_DUMP_DIR;
        }
        return ctClass;
    }

    private ClassPool getClassPool() {
        return ClassPool.getDefault();
    }
}
