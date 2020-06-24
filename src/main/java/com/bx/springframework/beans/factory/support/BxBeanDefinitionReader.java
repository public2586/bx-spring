package com.bx.springframework.beans.factory.support;


import com.bx.springframework.beans.factory.config.BxBeanDefinition;
import com.bx.springframework.stereotype.BxController;
import com.bx.springframework.stereotype.BxService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * qiangsheng.wang
 * @date 2020/5/31
 */
public class BxBeanDefinitionReader {

    private Properties config = new Properties();

    private List<String> registyBeanClasses = new ArrayList<String>();

    private final String SCAN_PACKAGE = "scanPackage";

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

    private BxBeanDefinition doCreateBeanDefinition(String factoryBeanName,String beanClassName) {
        BxBeanDefinition bxBeanDefinition = new BxBeanDefinition();
        bxBeanDefinition.setFactoryBeanName(factoryBeanName);
        bxBeanDefinition.setBeanClassName(beanClassName);
        return  bxBeanDefinition;
    }
    private  String toLowerFirstCase(String simpleName){
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return  String.valueOf(chars);
    }
    public Properties getConfig(){
        return this.config;
    }
}
