package com.mingyuans.javassist.custom;

import com.mingyuans.javassist.javassist.ClassPool;
import com.mingyuans.javassist.javassist.CtClass;
import com.mingyuans.javassist.logger.Log;

/**
 * Created by yanxq on 16/11/7.
 */

public abstract class CustomClassTransformer {

    protected final Log logger;

    public CustomClassTransformer(Log logger) {
        this.logger = logger;
    }

    abstract public byte[] transform(CtClass ctClass, ClassPool classPool);

}
