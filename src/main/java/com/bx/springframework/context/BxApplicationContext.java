package com.bx.springframework.context;



import com.bx.springframework.aop.config.BxAopConfig;
import com.bx.springframework.aop.framework.BxAdvisedSupport;
import com.bx.springframework.aop.framework.BxJdkDynamicAopProxy;
import com.bx.springframework.beans.BxBeanFactory;
import com.bx.springframework.beans.BxBeanWrapper;
import com.bx.springframework.beans.factory.annotation.BxAutowired;
import com.bx.springframework.beans.factory.config.BxBeanDefinition;
import com.bx.springframework.beans.factory.support.BxBeanDefinitionReader;
import com.bx.springframework.beans.factory.support.BxDefaultListableBeanFactory;
import com.bx.springframework.stereotype.BxController;
import com.bx.springframework.stereotype.BxService;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class BxApplicationContext implements BxBeanFactory {

    private  Map<String, BxBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String,BxBeanWrapper>();

    private  Map<String,Object> factoryBeanObjectCache = new ConcurrentHashMap<String,Object>();

    private Map<String, BxBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String,BxBeanDefinition>();

    private String[] configLactions;

    private BxBeanDefinitionReader reader;

    private BxDefaultListableBeanFactory regitry = new BxDefaultListableBeanFactory();

    public BxApplicationContext(String... configLactions) {
        this.configLactions = configLactions;
        //1、加载配置文件
        reader = new BxBeanDefinitionReader(this.configLactions);
        try{
            // 2、解析配置文件，封装成 beanDefinitions
            List<BxBeanDefinition>  beanDefinitions =  reader.loadBeanDefinitions();
            // 3、注册，把配置信息放到容器里
              registerBeanDefinition(beanDefinitions);
//            regitry.registerBeanDefinition(beanDefinitions);
            // 4、完成自动依赖注入
            doAutowired();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void registerBeanDefinition(List<BxBeanDefinition> beanDefinitions)  throws  Exception{
        for (BxBeanDefinition beanDefinition : beanDefinitions) {
            if(this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw  new Exception("The" + beanDefinition.getFactoryBeanName() + "is exists!");
            }
            this.beanDefinitionMap.put(beanDefinition.getBeanClassName(),beanDefinition);
            this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);

        }
    }
    private void doAutowired() {
        for (Map.Entry<String, BxBeanDefinition> bxBeanDefinitionEntry : beanDefinitionMap.entrySet()) {
            String beanName = bxBeanDefinitionEntry.getKey();
            getBean(beanName);
        }
    }
    public Object getBean(String beanName){
        BxBeanDefinition bxBeanDefinition =this.beanDefinitionMap.get(beanName);
        Object instance = instantiateBean(beanName,bxBeanDefinition);
        BxBeanWrapper bxBeanWrapper = new BxBeanWrapper(instance);
        this.factoryBeanInstanceCache.put(beanName,bxBeanWrapper);
        populateBean(beanName,new BxBeanDefinition(),bxBeanWrapper);
        return  bxBeanWrapper.getWrapperInstance();
    }
    public Object getBean(Class<?> beanClass){
        return getBean(beanClass.getName());
    }
    private Object instantiateBean(String beanName, BxBeanDefinition bxBeanDefinition) {
        String className = bxBeanDefinition.getBeanClassName();
        Object instance = null;
        try{
            if(factoryBeanObjectCache.containsKey(beanName)){
                instance = factoryBeanObjectCache.get(beanName);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                /*--------AOP开始---------*/
                BxAdvisedSupport config = instantionAopConfig(bxBeanDefinition);
                config.setTargetClass(clazz);
                config.setTarget(instance);
                if(config.pointCutMath()){
                    instance = new BxJdkDynamicAopProxy(config).getProxy();
                }
                /*--------AOP结束--------*/

                this.factoryBeanObjectCache.put(beanName,instance);
                this.factoryBeanObjectCache.put(bxBeanDefinition.getFactoryBeanName(),instance);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return  instance;
    }

    private BxAdvisedSupport instantionAopConfig(BxBeanDefinition bxBeanDefinition) {
        BxAopConfig config = new BxAopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new BxAdvisedSupport(config);
    }

    private void populateBean(String beanName, BxBeanDefinition bxBeanDefinition, BxBeanWrapper bxBeanWrapper) {
       Object instance =  bxBeanWrapper.getWrapperInstance();
       Class<?> clazz =  bxBeanWrapper.getWrapperClass();
       if(!(clazz.isAnnotationPresent(BxController.class) || clazz.isAnnotationPresent(BxService.class))){return;}
        Field[] fields =  clazz.getDeclaredFields();
        for (Field field : fields) {
            if(!field.isAnnotationPresent(BxAutowired.class)){continue;}
            BxAutowired autowired =  field.getAnnotation(BxAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);
            if(this.factoryBeanInstanceCache.get(autowiredBeanName) == null) {continue;}
            try{
                field.set(instance,this.factoryBeanInstanceCache.get(autowiredBeanName).getWrapperInstance());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public String[] getBeanDefinitionNames() {
       // return StringUtils.toStringArray(this.beanDefinitionNames);
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);

    }
    public Properties getConfig(){
        return this.reader.getConfig();
    }

}