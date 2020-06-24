package com.bx.springframework.aop.aspect;

import java.lang.reflect.Method;

/**
 * qiangsheng.wang
 * 2020/6/19 10:41
 **/
public class BxAdvice {
    private Object aspect;
    private Method adviceMethod;
    private String throwName;

    public BxAdvice(Object aspect, Method adviceMethod) {
        this.aspect = aspect;
        this.adviceMethod = adviceMethod;
    }

    public Object getAspect() {
        return aspect;
    }

    public void setAspect(Object aspect) {
        this.aspect = aspect;
    }

    public Method getAdviceMethod() {
        return adviceMethod;
    }

    public void setAdviceMethod(Method adviceMethod) {
        this.adviceMethod = adviceMethod;
    }

    public String getThrowName() {
        return throwName;
    }

    public void setThrowName(String throwName) {
        this.throwName = throwName;
    }
}
