package com.mingyuans.javassist.transformer.invocation;

import com.mingyuans.javassist.logger.Log;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yanxq on 16/11/7.
 */

public class ProcessBuilderInvocationHandler implements InvocationHandler {
    public static final Set<String> java = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(new String[] { "java", "java.exe" })));

    private final Log logger;
    public ProcessBuilderInvocationHandler(Log logger) {
        this.logger = logger;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        logger.d(String.valueOf(proxy));
        String[] keySplit = InvocationUtils.splitInvocationMethod((String) proxy);
        String methodName = keySplit[1];

        switch (methodName) {
            case "start" :
                return processStartMethodInvoke(args);
            default:
                break;
        }


        return null;
    }

    private Object processStartMethodInvoke(Object[] args) {
        List<String> command = (List<String>) args[0];
        String execFilePath = command.get(0);
        File execFile = new File(execFilePath);

        String appendParams = "";
        if (java.contains(execFile.getName().toLowerCase())) {
            appendParams = "-javaagent:" + InvocationUtils.getAgentJarPath();
        }

        if (appendParams != null && appendParams.length() > 0) {
            command.add(1,appendParams);
//            debugPrintCommand(command);
        }
        return null;
    }

    private void debugPrintCommand(List<String> command) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < command.size(); i++) {
            builder.append(command.get(i));
            builder.append(" ");
        }
        logger.d(builder.toString());
    }
}
