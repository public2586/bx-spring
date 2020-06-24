package com.bx.controller;

import com.bx.entity.Person;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * qiangsheng.wang
 * 2020/6/18 16:06
 **/
public class Test {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        Person person = (Person) applicationContext.getBean(Person.class);
        System.out.println(person.toString());

    }
}
