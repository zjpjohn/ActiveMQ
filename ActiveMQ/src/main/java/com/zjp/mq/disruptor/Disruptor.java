package com.zjp.mq.disruptor;

import com.zjp.mq.entity.QMessage;

import java.util.concurrent.TimeUnit;

/**
 * author:zjprevenge
 * time: 2016/7/22
 * copyright all reserved
 */
public interface Disruptor {

    /**
     * 向队列中添加消息
     *
     * @param message
     */
    void publish(QMessage message);

    /**
     * 停止queue
     */
    void shutDown();

    /**
     * @param timeout
     * @param unit
     * @throws InterruptedException
     */
    void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

}
