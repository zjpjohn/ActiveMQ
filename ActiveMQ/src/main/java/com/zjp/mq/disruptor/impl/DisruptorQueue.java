package com.zjp.mq.disruptor.impl;

import com.google.common.base.Preconditions;
import com.lmax.disruptor.RingBuffer;
import com.zjp.mq.cache.impl.ProducerCache;
import com.zjp.mq.config.BrokerConfig;
import com.zjp.mq.disruptor.Disruptor;
import com.zjp.mq.disruptor.EventFactory;
import com.zjp.mq.disruptor.EventWorkHandle;
import com.zjp.mq.disruptor.MessageEvent;
import com.zjp.mq.entity.QMessage;
import com.zjp.mq.service.QMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * author:zjprevenge
 * time: 2016/7/22
 * copyright all reserved
 * 基于disruptor的队列
 */
public class DisruptorQueue implements Disruptor, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(DisruptorQueue.class);

    @Resource
    private BrokerConfig brokerConfig;

    @Resource
    private ProducerCache producerCache;

    @Resource(name = "QMessageService")
    private QMessageService qMessageService;

    private RingBuffer<MessageEvent> ringBuffer;

    private com.lmax.disruptor.dsl.Disruptor<MessageEvent> disruptor;

    private ExecutorService executor;

    private volatile int INDEX = 0;

    //线程的名称
    private String threadName = "disruptorQueue";

    //线程数量
    private int thread;

    //ringBuffer大小2幂次方
    private int bufferSize;

    //消费线程的数量
    private int consume;

    public int getConsume() {
        return consume;
    }

    public void setConsume(int consume) {
        this.consume = consume;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    /**
     * 向队列中添加消息
     *
     * @param message
     */
    public void publish(QMessage message) {
        if (message != null) {
            if (log.isDebugEnabled()) {
                log.debug("add message to disruptor queue...");
            }
            long next = ringBuffer.next();
            try {
                MessageEvent messageEvent = ringBuffer.get(next);
                messageEvent.setqMessage(message);
            } finally {
                ringBuffer.publish(next);
            }
        }
    }

    /**
     * 停止queue
     */
    public void shutDown() {
        disruptor.shutdown();
        executor.shutdown();
    }

    /**
     * @param timeout
     * @param unit
     * @throws InterruptedException
     */
    public void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        executor.awaitTermination(timeout, unit);
    }

    public void afterPropertiesSet() throws Exception {
        Preconditions.checkState(bufferSize > 0 && thread > 0 && consume > 0, "bufferSize and thread must great than zero...");

        //创建线程池
        executor = Executors.newFixedThreadPool(thread, new ThreadFactory() {
            public Thread newThread(Runnable r) {
                String name = threadName;
                if (thread > 1) {
                    name = name + INDEX--;
                }
                Thread thread = new Thread(r, name);
                thread.setDaemon(true);
                return thread;
            }
        });
        disruptor = new com.lmax.disruptor.dsl.Disruptor<MessageEvent>(new EventFactory(), bufferSize, executor);

        EventWorkHandle[] handles = new EventWorkHandle[consume];
        for (int i = 0; i < consume; i++) {
            handles[i] = EventWorkHandle.builder()
                    .brokerConfig(brokerConfig)
                    .producerCache(producerCache)
                    .qMessageService(qMessageService)
                    .build();
        }

        //绑定消息消费者
        disruptor.handleEventsWithWorkerPool(handles);

        ringBuffer = disruptor.getRingBuffer();
        //开启disruptor
        disruptor.start();
    }
}
