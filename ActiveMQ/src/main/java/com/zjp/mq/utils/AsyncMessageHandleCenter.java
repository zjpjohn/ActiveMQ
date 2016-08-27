package com.zjp.mq.utils;

import com.zjp.mq.cache.impl.ProducerCache;
import com.zjp.mq.config.BrokerConfig;
import com.zjp.mq.entity.QMessage;
import com.zjp.mq.producer.ActiveMQTxMessageProducer;
import com.zjp.mq.producer.AsyncMessageQueue;
import com.zjp.mq.service.QMessageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author:zjprevenge
 * time: 2016/7/5
 * copyright all reserved
 */
@Component
public class AsyncMessageHandleCenter implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(AsyncMessageHandleCenter.class);

    @Resource
    private BrokerConfig brokerConfig;
    //生产者缓存
    @Resource
    private ProducerCache producerCache;

    @Resource(name = "QMessageService")
    private QMessageService qMessageService;

    private ExecutorService handleCenter;

    public void afterPropertiesSet() throws Exception {
        //创建线程池
        handleCenter = Executors.newSingleThreadExecutor();

        //监听异步队列中的消息
        handleCenter.submit(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        QMessage message = AsyncMessageQueue.receiveMessage();
                        ActiveMQTxMessageProducer producer = producerCache.get(message.getDestination());
                        //如果生产者为空，，则创建生产者
                        if (producer == null) {
                            producer = ActiveMQTxMessageProducer.builder()
                                    .brokerUrl(brokerConfig.getBrokerUrl())
                                    .userName(brokerConfig.getUserName())
                                    .password(brokerConfig.getPassword())
                                    .destName(message.getDestination())
                                    .n2(StringUtils.isNotBlank(message.getBusinessMark()))
                                    .qMessageService(qMessageService)
                                    .build();
                            //加入缓存中
                            producerCache.set(message.getDestination(), producer);
                        }
                        //发送消息
                        producer.sendMessage(message);
                    } catch (Exception e) {
                        log.error("handle message error: {}", e);
                    }
                }
            }
        });
    }
}
