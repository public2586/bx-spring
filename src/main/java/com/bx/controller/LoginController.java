package com.bx.controller;

import com.bx.service.IDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * qiangsheng.wang
 * 2020/6/19 14:45
 **/
@Controller
public class LoginController {

    @Autowired
    private IDemoService demoService;

    @RequestMapping("/index")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response , @RequestParam("name") String name){

        String result = demoService.get(name);
        Map map = new HashMap();
        map.put("name",name);
        return new ModelAndView("index",map);

    }

}
