package com.zjp.mq.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * author:zjprevenge
 * time: 2016/6/28
 * copyright all reserved
 */
public class MessageHolder {

    private static ThreadLocal<List<String>> messageHolder = new ThreadLocal<List<String>>(){

        @Override
        protected List<String> initialValue() {
            return new ArrayList<String>();
        }
    };

    /**
     * 获取数据
     *
     * @return
     */
    public static List<String> get() {
        return messageHolder.get();
    }

    /**
     * 存放消息
     *
     * @param message 消息内容
     */
    public static void set(String message) {
        List<String> list = messageHolder.get();
        list.add(message);
        messageHolder.set(list);
    }

    /**
     * 清空数据
     */
    public static void remove() {
        messageHolder.remove();
    }

}
