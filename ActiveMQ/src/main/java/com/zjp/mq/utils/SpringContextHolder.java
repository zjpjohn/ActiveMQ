package com.zjp.mq.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * author:zjprevenge
 * time: 2016/6/29
 * copyright all reserved
 */
public class SpringContextHolder{

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    /**
     * 根据beanName获取bean
     *
     * @param beanName bean名称
     * @return
     */
    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    /**
     * 根据bean名称和类型获取bean
     *
     * @param beanName bean名称
     * @param clazz    类型
     * @param <T>
     * @return
     */
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return applicationContext.getBean(beanName, clazz);
    }

    /**
     * 根据类型获取bean
     *
     * @param clazz 类型
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}
