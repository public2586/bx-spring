package com.bx.springframework.aop.framework;

import com.bx.springframework.aop.aspect.BxAdvice;
import com.bx.springframework.util.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * qiangsheng.wang
 * 2020/6/19 10:44
 **/
public class BxJdkDynamicAopProxy implements BxAopProxy, InvocationHandler, Serializable {

    private static final Log logger = LogFactory.getLog(BxJdkDynamicAopProxy.class);

    private BxAdvisedSupport advised;

    public BxJdkDynamicAopProxy(BxAdvisedSupport config) {
        this.advised = config;
    }
    @Override
    public Object getProxy() {
       return getProxy(ClassUtils.getDefaultClassLoader());
    }
    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,this.advised.getTargetClass().getInterfaces(),this);
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Map<String,BxAdvice> advices = advised.getAdvices(method,null);
        Object returnValue;
        try {
            invokeAdivce(advices.get("before"));
            returnValue = method.invoke(this.advised.getTarget(),args);
            invokeAdivce(advices.get("after"));
        }catch (Exception e){
            invokeAdivce(advices.get("afterThrow"));
            throw e;
        }
        return returnValue;
    }
    private void invokeAdivce(BxAdvice advice) {
        try {
            advice.getAdviceMethod().invoke(advice.getAspect());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
