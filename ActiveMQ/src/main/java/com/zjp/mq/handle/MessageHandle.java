package com.zjp.mq.handle;

/**
 * author:zjprevenge
 * time: 2016/6/26
 * copyright all reserved
 */
public interface MessageHandle {

    /**
     * 在此进行业务处理
     *
     * @param message 消息内容
     */
    void handleMessage(String message);
}
