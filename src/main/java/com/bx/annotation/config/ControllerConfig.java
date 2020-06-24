package com.bx.annotation.config;

import com.bx.entity.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * qiangsheng.wang
 * 2020/6/23 15:00
 **/
@Configuration
public class ControllerConfig {
    @Bean
    public Person getPerson(){
        return  new Person();
    }

}
