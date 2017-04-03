package com.mingyuans.javassist.transformer.former;

import com.mingyuans.javassist.logger.Log;
import com.mingyuans.javassist.javassist.CtClass;
import com.mingyuans.javassist.javassist.CtMethod;
import com.mingyuans.javassist.transformer.invocation.InvocationUtils;

/**
 * Created by yanxq on 16/11/7.
 */

public class DexerMainTransformer extends BaseClassTransformer {

    public DexerMainTransformer(Log logger) {
        super(logger);
    }

    @Override
    public void transform(CtClass ctClass) {
        processProcessClassMethod(ctClass);
    }

    private void processProcessClassMethod(CtClass ctClass) {
        try {
            CtMethod processClassMethod = ctClass.getDeclaredMethod("processClass");
            String invocationKey = InvocationUtils.getInvocationKey(ctClass.getName(),"processClass");
            mInvocationBuilder
                    .loadInvokeObject()
                    .loadInvokeCall("bytes=(byte[])",invocationKey,"new Object[]{$1,$2,args}")
                    .insertBeforeCtMethod(processClassMethod);
        } catch (Exception e) {
            logger.e("process DexerMain#processClass fail!",e);
        }
    }
}
