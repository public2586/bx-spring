package com.bx.service.impl;

import com.bx.service.IDemoService;
import com.bx.springframework.stereotype.BxService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@BxService
public class DemoService implements IDemoService {
    private static final Log logger = LogFactory.getLog(DemoService.class);
    @Override
    public String get(String name) {
        logger.info("业务代码！");
        return name;
    }
}
