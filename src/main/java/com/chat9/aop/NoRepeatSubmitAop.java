package com.chat9.aop;


import javax.servlet.http.HttpServletRequest;

import com.chat9.annotation.NoRepeatSubmit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.cache.Cache;

@Aspect
@Component
public class NoRepeatSubmitAop {

    private Log logger = LogFactory.getLog(getClass());

    @Autowired
    private Cache<String, Integer> cache;

    @Around("execution(* com.chat9..*Controller.*(..)) && @annotation(nrs)")
    public Object arround(ProceedingJoinPoint pjp, NoRepeatSubmit nrs) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String sessionId = RequestContextHolder.getRequestAttributes().getSessionId();
            HttpServletRequest request = attributes.getRequest();
            String key = sessionId + "-" + request.getServletPath();
            System.out.println(key);
            if (cache.getIfPresent(key) == null) {// if url exist,duplicated
                Object o = pjp.proceed();
                cache.put(key, 0);
                return o;
            } else {
                logger.error("duplicated request");
                return null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            logger.error("duplicated handler error");
            return null;
        }

    }

}
