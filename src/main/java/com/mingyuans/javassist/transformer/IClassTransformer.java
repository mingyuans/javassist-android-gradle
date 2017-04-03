package com.mingyuans.javassist.transformer;

import java.lang.instrument.ClassFileTransformer;

/**
 * Created by yanxq on 16/11/3.
 */

public interface IClassTransformer extends ClassFileTransformer {
    public boolean isSupportTransform(Class<?> clazz);
}
