package com.mingyuans.javassist;

import com.mingyuans.javassist.logger.Log;
import com.mingyuans.javassist.logger.ConsoleLog;
import com.mingyuans.javassist.transformer.IClassTransformer;
import com.mingyuans.javassist.transformer.ClassTransformerImpl;
import com.mingyuans.javassist.transformer.invocation.InvocationDispatcher;
import com.mingyuans.javassist.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

public class AgentPreMain {

    public static final Class LOGGER_CLASS = Logger.class;
    public static final Set<String> dx = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(new String[] { "dx", "dx.bat" })));
    public static final Set<String> java = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(new String[] { "java", "java.exe" })));

    public static void agentmain(String agentArgs,Instrumentation instrumentation) {
        premain(agentArgs,instrumentation,true);
    }

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        premain(agentArgs,instrumentation,false);
    }

    public static void premain(String agentArgs,Instrumentation instrumentation,boolean isAgentPremain) {
        AgentArgsParser parser = new AgentArgsParser(agentArgs);
        ConsoleLog.setDebug(parser.isDebug());
        ConsoleLog logger = new ConsoleLog();

        try {
            IClassTransformer transformer = new ClassTransformerImpl(logger);
            createInvocationDispatcher(logger);
            instrumentation.addTransformer(transformer,true);
            Class[] classes = instrumentation.getAllLoadedClasses();
            ArrayList<Class> classesToBeTransform = new ArrayList<>();
            for (Class clazz : classes) {
                if (transformer.isSupportTransform(clazz)) {
                    classesToBeTransform.add(clazz);
                }
            }

            if (!classesToBeTransform.isEmpty()) {
                if (instrumentation.isRetransformClassesSupported()) {
                    int transformClassSize = classesToBeTransform.size();
                    instrumentation.retransformClasses(classesToBeTransform.toArray(new Class[transformClassSize]));
                } else {
                    logger.e("can not find transform classes!");
                }
            }

            redefineClass(instrumentation,transformer,ProcessBuilder.class);
        } catch (Throwable throwable) {
            logger.e("exec premain fail!",throwable);
        }
    }

    /**
     * 设置 callback，之后我们注入时把需要的变量都通过这个 callback 传上来
     * @param log
     * @throws Exception
     */
    private static void createInvocationDispatcher(Log log) throws Exception {
        Field treeLock = LOGGER_CLASS.getDeclaredField("treeLock");
        treeLock.setAccessible(true);
        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(treeLock, treeLock.getModifiers() & 0xFFFFFFEF);//去掉final
        if (!(treeLock.get(null) instanceof InvocationDispatcher)) {
            treeLock.set(null, new InvocationDispatcher(log));
        }
    }

    /**
     * 注入 ProcessBuilder 类，让其他进程启动时也带上 javaagent 参数
     * @param instrumentation
     * @param transformer
     * @param clazz
     * @throws IOException
     * @throws IllegalClassFormatException
     * @throws ClassNotFoundException
     * @throws UnmodifiableClassException
     */
    private static void redefineClass(Instrumentation instrumentation, ClassFileTransformer transformer, Class<?> clazz)
            throws IOException, IllegalClassFormatException, ClassNotFoundException, UnmodifiableClassException {
        String internalName = clazz.getName().replace('.', '/');
        String fullName = internalName + ".class";
        ClassLoader classLoader = clazz.getClassLoader() == null ? AgentPreMain.class.getClassLoader() : clazz.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fullName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamUtils.copy(inputStream, outputStream);
        inputStream.close();
        byte[] arrayOfByte = transformer.transform(clazz.getClassLoader(), internalName, clazz
                , null, outputStream.toByteArray());
        ClassDefinition classDefinition = new ClassDefinition(clazz, arrayOfByte);
        instrumentation.redefineClasses(classDefinition);
    }

    private static class AgentArgsParser {
        private Map<String,String> agentArgMap = new HashMap<>();
        public AgentArgsParser(String agentArgs) {
            if (agentArgs == null || agentArgs.isEmpty()) {
                return;
            }

            for (String agentArgLine : agentArgs.split(";")) {
                String[] agentArg = agentArgLine.split("=");
                if (agentArg.length == 2) {
                    agentArgMap.put(agentArg[0],agentArg[1]);
                }
            }
        }

        public boolean isDebug() {
            return "true".equalsIgnoreCase(getAgentArg("debug"));
        }

        public String getAgentArg(String name) {
            return agentArgMap.get(name);
        }
    }
}
