package com.bx.annotation.config;

import com.bx.entity.Person;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * qiangsheng.wang
 * 2020/6/23 15:06
 **/
public class AnnotationMain {

    @Test
    public void test(){
        AnnotationConfigApplicationContext con = new AnnotationConfigApplicationContext(ControllerConfig.class);
        Person person = (Person)con.getBean(Person.class);
        System.out.println(person);
    }

    @Test
    public void test2(){
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        System.out.println(applicationContext.getBean(Demo02Service.class));
    }

}
