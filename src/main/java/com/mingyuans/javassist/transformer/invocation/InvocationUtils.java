package com.mingyuans.javassist.transformer.invocation;

import com.mingyuans.javassist.AgentPreMain;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Created by yanxq on 16/11/7.
 */

public class InvocationUtils {
    public static String getInvocationKey(String pkg, String method) {
        return pkg + "#" + method;
    }

    public static String getInvocationPkg(String key) {
        return key == null? "" : key.substring(0,key.indexOf("#"));
    }

    public static String getInvocationMethod(String key) {
        return key == null? "" : key.substring(key.indexOf("#"));
    }

    public static String[] splitInvocationMethod(String key) {
        return key == null? new String[]{"",""} : key.split("#");
    }

    public static String getAgentJarPath() {
        try {
            String classPath = AgentPreMain.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI().getPath();
            return new File(classPath).getAbsolutePath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "";
        }
    }

}
