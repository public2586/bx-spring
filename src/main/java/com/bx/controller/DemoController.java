package com.bx.controller;


import com.bx.service.IDemoService;
import com.bx.springframework.beans.factory.annotation.BxAutowired;
import com.bx.springframework.stereotype.BxController;
import com.bx.springframework.web.bind.annotation.BxRequestMapping;
import com.bx.springframework.web.bind.annotation.BxRequestParam;
import com.bx.springframework.web.servlet.BxModelAndView;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@BxController
@BxRequestMapping("/demo")
public class DemoController {
     @BxAutowired
     private IDemoService demoService;
    @BxRequestMapping("/query.json")
    public BxModelAndView query(@BxRequestParam("teacher") String teacher){
        String  result = demoService.get(teacher);
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("teacher", teacher);
        model.put("data", result);
        model.put("token", "123456");
        return new BxModelAndView("first.html",model);
    }
    private BxModelAndView out(HttpServletResponse resp, String str){
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
