package com.mingyuans.javassist.logger;

/**
 * Created by yanxq on 16/11/3.
 */

public interface Log {

    public void v(String message);

    public void d(String message);

    public void w(String message);

    public void e(String message);

    public void e(String message,Throwable e);
}
