package com.mingyuans.javassist.custom;

import com.mingyuans.javassist.logger.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanxq on 16/11/7.
 */

public class CustomClassTransformerFactory {

    private static final Map<String,CustomClassTransformer> CUSTOM_CLASS_TRANSFORMER_MAP = new HashMap<>();
    public static void init(Log logger) {
//        CUSTOM_CLASS_TRANSFORMER_MAP.put("okhttp3.OkHttpClient",new OkHttp3ClassTransformer(logger));
//        CUSTOM_CLASS_TRANSFORMER_MAP.put("org.apache.http.impl.conn.DefaultClientConnectionOperator",new HttpClientClassTransformer(logger));
    }

    public static CustomClassTransformer getTransformer(String className) {
        return CUSTOM_CLASS_TRANSFORMER_MAP.get(className);
    }
}
