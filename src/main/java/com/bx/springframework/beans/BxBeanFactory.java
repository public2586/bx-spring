package com.bx.springframework.beans;

import org.springframework.beans.BeansException;

/**
 * qiangsheng.wang
 * 2020/6/18 17:46
 **/
public interface BxBeanFactory {

    Object getBean(String beanName);

    Object getBean(Class<?> beanClass);
}
