package com.bx.springframework.util;

/**
 * qiangsheng.wang
 * 2020/6/17 14:35
 **/
public abstract class Assert {

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
