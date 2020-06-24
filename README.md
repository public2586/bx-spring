# bx-spring

#### 介绍
 **bx-spring是基于Spring源码写的Spring框架** 

#### 部分代码说明

（1）加载配置文件 applicationContext.properties 和  扫描包

```
scanPackage=com.bx.controller;com.bx.service
templateRoot=layouts

#切面表达式expression#
pointCut=public .* com.bx.service..*Service..*(.*)
#切面类
aspectClass=com.bx.aspect.LogAspect
#前置通知回调方法
aspectBefore=before
#后置通知回调方法
aspectAfter=after
#异常通知回调方法
aspectAfterThrow=afterThrowing
#异常类型捕获
aspectAfterThrowingName=java.lang.Exception
```


```
    public BxBeanDefinitionReader(String... locations) {
        InputStream in =  this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:",""));
        try {
            config.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        doScanner(config.getProperty(SCAN_PACKAGE));
    }
```

```
    private void doScanner(String scanPackage) {
        if("".equals(scanPackage)) {return;}
        String[] scanPackages = scanPackage.split(";");
        for (int i = 0; i <scanPackages.length ; i++) {
            URL url =  this.getClass().getClassLoader().getResource(scanPackages[i].replaceAll("\\.","/"));
            File classPath = new File(url.getFile());
            for(File file:classPath.listFiles()){
                if(file.isDirectory()){
                    doScanner(scanPackages[i]+ "."+ file.getName());
                }else{
                    if(!file.getName().endsWith(".class")) {continue;}
                    registyBeanClasses.add(scanPackages[i] + "." +file.getName().replace(".class",""));
                }
            }
        }
    }

```

（2）解析配置文件，封装成 beanDefinitions

```
List<BxBeanDefinition>  beanDefinitions =  reader.loadBeanDefinitions();
```

```
    public List<BxBeanDefinition> loadBeanDefinitions(){
        List<BxBeanDefinition> result = new ArrayList<BxBeanDefinition>();
        try {
            for (String className : registyBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                if(beanClass.isInterface()) {continue;}
                if(beanClass.isAnnotationPresent(BxController.class)){
                    result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
                } else if(beanClass.isAnnotationPresent(BxService.class)){
                    String beanName =toLowerFirstCase( beanClass.getSimpleName());
                    BxService service =   beanClass.getAnnotation(BxService.class);
                    if(!"".equals(service.value())){
                        beanName = service.value().trim();
                    }
                    result.add(doCreateBeanDefinition(beanName, beanClass.getName()));
                }
                Class<?>[] interfaces =  beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    result.add(doCreateBeanDefinition(i.getName(),beanClass.getName()));
                }
              }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  result;
}
```
（3） 注册，把配置信息放到容器里

```
    private void registerBeanDefinition(List<BxBeanDefinition> beanDefinitions)  throws  Exception{
        for (BxBeanDefinition beanDefinition : beanDefinitions) {
            if(this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw  new Exception("The" + beanDefinition.getFactoryBeanName() + "is exists!");
            }
            this.beanDefinitionMap.put(beanDefinition.getBeanClassName(),beanDefinition);
            this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);

        }
    }
```
（4） 完成自动依赖注入

```
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
```



