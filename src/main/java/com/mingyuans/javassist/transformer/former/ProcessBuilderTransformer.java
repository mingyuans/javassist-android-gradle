package com.mingyuans.javassist.transformer.former;

import com.mingyuans.javassist.logger.Log;
import com.mingyuans.javassist.javassist.CtClass;
import com.mingyuans.javassist.javassist.CtMethod;
import com.mingyuans.javassist.transformer.invocation.InvocationUtils;

/**
 * Created by yanxq on 16/11/4.
 */

public class ProcessBuilderTransformer extends BaseClassTransformer {

    public ProcessBuilderTransformer(Log logger) {
        super(logger);
    }

    @Override
    public void transform(CtClass ctClass) {
        processStartMethod(ctClass);
    }

    private void processStartMethod(CtClass ctClass) {
        try {
            CtMethod startMethod = ctClass.getDeclaredMethod("start");
            String invocationKey = InvocationUtils.getInvocationKey(ctClass.getName(),"start");
            mInvocationBuilder
                    .loadInvokeObject()
                    .loadInvokeCall(invocationKey,"new Object[]{command}")
                    .insertBeforeCtMethod(startMethod);
        } catch (Exception e) {
            logger.e("process ProcessBuilder#start fail!",e);
        }
    }
}
