package com.bx.springframework.web.servlet;


import java.io.File;

/**
 * qiangsheng.wang
 * 2020/6/17 16:03
 **/
public class BxViewResolver {
    private final String DEFAULT_TEMPLATE_SUFFIX=".html";
    private File templateRootDir;
    public BxViewResolver(String templfateRoot) {
       String templateRootPath =  this.getClass().getClassLoader().getResource(templfateRoot).getFile();
        templateRootDir= new File(templateRootPath);
    }
    public BxView resolverViewName(String viewName){
        if(null == viewName || "".equals(viewName.trim())) {return null;}
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
        File templfateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+","/"));
        return new BxView(templfateFile);
    }

}
