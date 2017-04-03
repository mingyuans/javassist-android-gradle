package com.mingyuans.javassist.transformer.former;

import com.mingyuans.javassist.logger.Log;
import com.mingyuans.javassist.javassist.CtClass;
import com.mingyuans.javassist.transformer.InvocationBuilder;

/**
 * Created by yanxq on 16/11/4.
 */

public abstract class BaseClassTransformer {

    protected final Log logger;
    protected InvocationBuilder mInvocationBuilder = new InvocationBuilder();
    public BaseClassTransformer(Log logger) {
        this.logger = logger;
    }

    abstract public void transform(CtClass ctClass);


}
