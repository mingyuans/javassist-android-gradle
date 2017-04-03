package com.mingyuans.javassist.transformer.invocation;

import com.mingyuans.javassist.logger.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanxq on 16/11/3.
 */

public class InvocationDispatcher implements InvocationHandler {

    private final Log logger;
    private final Map<String,InvocationHandler> mInvocations = new HashMap<>();

    public InvocationDispatcher(Log log) {
        logger = log;
        mInvocations.put("java.lang.ProcessBuilder",new ProcessBuilderInvocationHandler(logger));
        mInvocations.put("com.android.dx.command.dexer.Main",new DexerMainInvocationHandler(logger));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String pkg = InvocationUtils.getInvocationPkg((String) proxy);
        InvocationHandler handler=  mInvocations.get(pkg);
        return handler == null? null : handler.invoke(proxy,method,args);
    }

}
