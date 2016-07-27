package com.zjp.mq.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * author:zjprevenge
 * time: 2016/6/30
 * copyright all reserved
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware ,BeanPostProcessor{

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.setApplicationContext(applicationContext);
    }

    public Object postProcessBeforeInitialization(Object bean, String s) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String s) throws BeansException {
        return bean;
    }
}
