package com.bx.springframework.web.servlet;

import java.util.Map;

/**
 * qiangsheng.wang
 * 2020/6/17 14:16
 **/
public class BxModelAndView {
    private String viewName;
    private Map<String,?> model;

    public BxModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public BxModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}
