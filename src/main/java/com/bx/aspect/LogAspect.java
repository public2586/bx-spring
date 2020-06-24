package com.bx.aspect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * qiangsheng.wang
 * 2020/6/19 11:29
 **/

public class LogAspect {

    private static final Log log = LogFactory.getLog(LogAspect.class);

    //在调用一个方法之前，执行before方法
    public void before(){
        //这个方法中的逻辑，是由我们自己写的
        log.info("nvoker Before Method!!!");
    }
    //在调用一个方法之后，执行after方法
    public void after(){
        log.info("Invoker After Method!!!");
    }

    public void afterThrowing(){
        log.info("出现异常");
    }
}
