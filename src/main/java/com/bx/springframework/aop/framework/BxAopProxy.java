package com.bx.springframework.aop.framework;

/**
 * qiangsheng.wang
 * 2020/6/19 10:44
 **/
public interface BxAopProxy {

    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
