package com.mingyuans.javassist.transformer;

import com.mingyuans.javassist.javassist.ByteArrayClassPath;
import com.mingyuans.javassist.logger.ConsoleLog;
import com.mingyuans.javassist.logger.Log;
import com.mingyuans.javassist.transformer.former.BaseClassTransformer;
import com.mingyuans.javassist.transformer.former.DexerMainTransformer;
import com.mingyuans.javassist.transformer.former.ProcessBuilderTransformer;
import com.mingyuans.javassist.javassist.ClassPool;
import com.mingyuans.javassist.javassist.CtClass;
import com.mingyuans.javassist.javassist.NotFoundException;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;

/**
 * Created by yanxq on 16/11/4.
 */

public class ClassTransformerImpl implements IClassTransformer {

    public static final String DEBUG_DUMP_DIR = "./javassist_dump";
    private final Log logger;
    private final HashMap<String,BaseClassTransformer> mClassTransformerFactory = new HashMap<>();

    public ClassTransformerImpl(Log logger) {
        this.logger = logger;

        mClassTransformerFactory.put("java/lang/ProcessBuilder"
                , new ProcessBuilderTransformer(logger));

        mClassTransformerFactory.put("com/android/dx/command/dexer/Main"
                , new DexerMainTransformer(logger));
    }

    @Override
    public boolean isSupportTransform(Class<?> clazz) {
        return false;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {

        BaseClassTransformer transformer = mClassTransformerFactory.get(className);
        if (transformer != null) {
            try {
                className = className.replaceAll("/",".");
                CtClass ctClass = getCtClass(className,classfileBuffer);

                if (ConsoleLog.isDebug()) {
                    CtClass.debugDump = DEBUG_DUMP_DIR;
                }

                if (ctClass.isFrozen()) {
                    return classfileBuffer;
                }

                transformer.transform(ctClass);
                logger.d("class transform success : " + className);
                return ctClass.toBytecode();
            } catch (Exception e) {
                logger.e("transform fail! " + className,e);
            }
        }

        return classfileBuffer;
    }

    private CtClass getCtClass(String className,byte[] classfileBuffer) throws NotFoundException {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.getOrNull(className);
        if (ctClass == null && !isEmptyByteArray(classfileBuffer)) {
            classPool.appendClassPath(new ByteArrayClassPath(className,classfileBuffer));
        }
        return classPool.get(className);
    }

    private boolean isEmptyByteArray(byte[] byteArray) {
        return byteArray == null || byteArray.length == 0;
    }

}
