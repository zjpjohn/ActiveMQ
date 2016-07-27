package com.zjp.mq.producer;

import com.zjp.mq.entity.QMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * author:zjprevenge
 * time: 2016/7/5
 * copyright all reserved
 */
public class AsyncMessageQueue {

    private static final Logger log = LoggerFactory.getLogger(AsyncMessageQueue.class);

    private static LinkedBlockingQueue<QMessage> messageQueue;

    static {
        messageQueue = new LinkedBlockingQueue<QMessage>(10000);
    }

    public static void sendMessage(QMessage qMessage) throws InterruptedException {
        messageQueue.put(qMessage);
    }

    public static QMessage receiveMessage() throws InterruptedException {
        return messageQueue.take();
    }
}
