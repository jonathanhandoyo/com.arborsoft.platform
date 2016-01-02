package com.arborsoft.platform.api.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResponseTimeLoggingInterceptor extends HandlerInterceptorAdapter {
    private static final Log LOG = LogFactory.getLog(ResponseTimeLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOG.info(String.format(">>  Request -->               @ %s %s %s", request.getMethod(), request.getRequestURI(), request.getQueryString()));
        request.setAttribute("timestamp-start", System.currentTimeMillis());
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        long duration = System.currentTimeMillis() - (Long) request.getAttribute("timestamp-start");
        LOG.info(String.format(">> Response --> %10s ms @ %s %s %s", duration, request.getMethod(), request.getRequestURI(), request.getQueryString()));
    }
}
