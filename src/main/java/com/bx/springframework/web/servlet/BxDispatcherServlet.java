package com.bx.springframework.web.servlet;


import com.bx.springframework.context.BxApplicationContext;
import com.bx.springframework.stereotype.BxController;
import com.bx.springframework.web.bind.annotation.BxRequestMapping;
import com.bx.springframework.web.util.BxWebUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BxDispatcherServlet extends HttpServlet {


    public static final String PAGE_NOT_FOUND_LOG_CATEGORY = "com.bx.web.servlet.PageNotFound";

    protected static final Log pageNotFoundLogger = LogFactory.getLog(PAGE_NOT_FOUND_LOG_CATEGORY);

    private boolean throwExceptionIfNoHandlerFound = false;

    private Properties contextConfig = new Properties();

    private List<BxHandlerMapping> handlerMappings = new ArrayList<BxHandlerMapping>();

    private Map<BxHandlerMapping, BxHandlerAdapter> handlerAdapters = new HashMap<BxHandlerMapping, BxHandlerAdapter>();

    private List<BxViewResolver> viewResolvers = new ArrayList<BxViewResolver>();

    private BxApplicationContext context;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.doDispatcher(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                processDispatchResult(request,response,new BxModelAndView("500"));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void doDispatcher(HttpServletRequest request, HttpServletResponse response) throws  Exception {
            BxHandlerMapping handle = getHandler(request);
            if(handle == null){
    //            processDispatchResult(request,response,new BxModelAndView("404"));
                noHandlerFound(request,response);
                return;
            }
        BxHandlerAdapter handlerAdapter = getHandlerAdapter(handle);
        BxModelAndView mv = handlerAdapter.handle(request,response,handle);
        processDispatchResult(request,response,mv);

    }
    private BxHandlerMapping getHandler(HttpServletRequest request) {
        if(this.handlerMappings.isEmpty()){return  null;}
        String url = request.getRequestURI();
        String  contextPath = request.getContextPath();
        url = url.replaceAll(contextPath,"").replaceAll("/+","/");
        for (BxHandlerMapping handler : handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            if(!matcher.matches()){ continue;}
            return  handler;
        }
        return  null;
    }
    private BxHandlerAdapter getHandlerAdapter(BxHandlerMapping handle) {
        if(this.handlerAdapters.isEmpty()){return  null;}
        return  this.handlerAdapters.get(handle);
    }

    private void processDispatchResult(HttpServletRequest request, HttpServletResponse response, BxModelAndView mv) throws Exception {
        if(null == mv){return; }
        if(viewResolvers.isEmpty()){return;}
        for (BxViewResolver viewResolver : this.viewResolvers) {
            BxView view = viewResolver.resolverViewName(mv.getViewName());
            view.render(mv.getModel(),request,response);
            return;

        }
    }
    protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (pageNotFoundLogger.isWarnEnabled()) {
            pageNotFoundLogger.warn("No mapping for " + request.getMethod() + " " + getRequestUri(request));
        }
        else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    @Override
    public void init(ServletConfig config) throws ServletException {
        //1、初始化ApplicationContext
        context = new BxApplicationContext(config.getInitParameter("contextConfigLocation"));
        //2、初始化Spring mvc 九大组件
        initStrategies(context);
    }

    /**
     * spring   DispatcherServlet 源码
     * protected void initStrategies(ApplicationContext context) {
     * 		initMultipartResolver(context);
     * 		initLocaleResolver(context);
     * 		initThemeResolver(context);
     * 		initHandlerMappings(context);
     * 		initHandlerAdapters(context);
     * 		initHandlerExceptionResolvers(context);
     * 		initRequestToViewNameTranslator(context);
     * 		initViewResolvers(context);
     * 		initFlashMapManager(context);
     *   }
     *
     */
    private void initStrategies(BxApplicationContext context) {
        //1、初始化HandlerMapping
        initHandlerMapping(context);
        //2、初始化参数适配器
        initHandlerAdapter(context);
        //3、初始化视图解析器
        initViewResolvers(context);
    }
    private void initHandlerMapping(BxApplicationContext context) {
        String[]  beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object controller  = context.getBean(beanName);
            Class<?> clazz = controller.getClass();
            if(!clazz.isAnnotationPresent(BxController.class)) {continue;}
            String baseUrl = "";
            if(clazz.isAnnotationPresent(BxRequestMapping.class)){
                BxRequestMapping requestMapping =  clazz.getAnnotation(BxRequestMapping.class);
                baseUrl =  requestMapping.value();
            }
            for (Method method : clazz.getMethods()) {
                if(!method.isAnnotationPresent(BxRequestMapping.class)) {continue;}
                BxRequestMapping requestMapping =  method.getAnnotation(BxRequestMapping.class);
                String regex = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*",".*")).replaceAll("/+","/");
                Pattern pattern = Pattern.compile(regex);
                this.handlerMappings.add(new BxHandlerMapping(pattern,method,controller));
            }
        }
    }
    private void initHandlerAdapter(BxApplicationContext context) {
        for (BxHandlerMapping handlerMapping : handlerMappings) {
            this.handlerAdapters.put(handlerMapping,new BxHandlerAdapter());
        }
    }
    private void initViewResolvers(BxApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);
        for (File file : templateRootDir.listFiles()) {
            this.viewResolvers.add(new BxViewResolver(templateRoot));
        }
    }
    private static String getRequestUri(HttpServletRequest request) {
        String uri = (String) request.getAttribute(BxWebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE);
        if (uri == null) {
            uri = request.getRequestURI();
        }
        return uri;
    }


}