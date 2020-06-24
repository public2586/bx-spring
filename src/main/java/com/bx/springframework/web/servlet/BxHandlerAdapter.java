package com.bx.springframework.web.servlet;

import com.bx.springframework.web.bind.annotation.BxRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * qiangsheng.wang
 * 2020/6/17 12:54
 **/
public class BxHandlerAdapter {

    public BxModelAndView handle(HttpServletRequest request, HttpServletResponse response, BxHandlerMapping handle) throws InvocationTargetException, IllegalAccessException {

        Map<String,Integer> paramIndexMapping = new HashMap<String,Integer>();
        Annotation[][] pa = handle.getMethod().getParameterAnnotations();
        for (int i = 0; i <pa.length ; i++) {
            for (Annotation a : pa[i]) {
              if(a instanceof BxRequestParam){
                 String  paramName = ((BxRequestParam) a).value();
                  if(!"".equals(paramName.trim())){
                     paramIndexMapping.put(paramName,i);
                 }
              }
            }
        }
        Class<?>[] paramTypes =  handle.getMethod().getParameterTypes();
        for (int i = 0; i <paramTypes.length ; i++) {
           Class<?> type = paramTypes[i];
           if(type == HttpServletRequest.class || type == HttpServletResponse.class){
               paramIndexMapping.put(type.getName(),i);
           }
        }
        Map<String,String[]> params = request.getParameterMap();
        Object[]  paramValues = new Object[paramTypes.length];
        for (Map.Entry<String, String[]> param : params.entrySet()) {
           String value =  Arrays.toString(param.getValue()).replaceAll("\\[|\\]",",").replaceAll("\\s","");
           if(!paramIndexMapping.containsKey(param.getKey())) {continue;}
           int index =  paramIndexMapping.get(param.getKey());
            paramValues[index] = caseValue(value,paramTypes[index]);
        }
        if(paramIndexMapping.containsKey(HttpServletRequest.class.getName())){
            int reqIndex = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = request;
        }
        if(paramIndexMapping.containsKey(HttpServletResponse.class.getName())){
            int repIndex = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[repIndex] = response;
        }
        Object result = handle.getMethod().invoke(handle.getController(),paramValues);
        if(result == null || result instanceof Void) { return  null;}
        boolean isModelAndView =  handle.getMethod().getReturnType() == BxModelAndView.class;
        if(isModelAndView) {
            return (BxModelAndView) result;
        }
       return  null;
    }
    private Object caseValue(String value, Class<?> paramType) {
        if(String.class == paramType){
            return value;
        }else if(Integer.class == paramType){
            return  Integer.valueOf(value);
        } else if(Double.class == paramType){
            return Double.valueOf(value);
        } else if(Float.class == paramType){
            return Float.valueOf(value);
        } else{
            if(value != null){
                return value;
            }
            return  null;
        }
    }
}
