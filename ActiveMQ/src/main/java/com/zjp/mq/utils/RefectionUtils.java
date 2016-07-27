package com.zjp.mq.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * author:zjprevenge
 * time: 2016/6/26
 * copyright all reserved
 */

public class RefectionUtils {

    private static final Logger log = LoggerFactory.getLogger(RefectionUtils.class);

    /**
     * 反射获取对象的的字段的值
     *
     * @param target 目标对象
     * @param clazz  field的类型
     * @param <T>
     * @return
     */
    public static <T> T getFieldValue(Object target, Class<T> clazz) {
        for (Field field : target.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getClass() == clazz) {
                try {
                    return (T) field.get(target);
                } catch (IllegalAccessException e) {
                    log.error("get target field value error: {}", e);
                }
            }
        }
        return null;
    }
}
