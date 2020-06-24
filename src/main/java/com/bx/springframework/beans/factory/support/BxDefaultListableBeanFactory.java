package com.bx.springframework.beans.factory.support;

import com.bx.springframework.beans.BxBeanFactory;
import com.bx.springframework.beans.BxBeanWrapper;
import com.bx.springframework.beans.factory.config.BxBeanDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * qiangsheng.wang
 * 2020/6/22 14:44
 **/
public class BxDefaultListableBeanFactory implements BxBeanFactory {
    public  Map<String, BxBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String,BxBeanDefinition>();
    private final List<String> beanDefinitionNames = new ArrayList<String>(64);
    @Override
    public Object getBean(String beanName) {
        return  null;
    }

    @Override
    public Object getBean(Class<?> beanClass) {
        return  null;
    }
    public void registerBeanDefinition(List<BxBeanDefinition> beanDefinitions)  throws  Exception{
        for (BxBeanDefinition beanDefinition : beanDefinitions) {
            if(this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw  new Exception("The" + beanDefinition.getFactoryBeanName() + "is exists!");
            }

            this.beanDefinitionMap.put(beanDefinition.getBeanClassName(),beanDefinition);
            this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);

        }
    }
}
