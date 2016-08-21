package com.zjp.mq.producer;

import com.google.common.base.Preconditions;
import com.zjp.mq.cache.impl.ProducerCache;
import com.zjp.mq.config.BrokerConfig;
import com.zjp.mq.config.ProducerCfg;
import com.zjp.mq.disruptor.impl.DisruptorQueue;
import com.zjp.mq.service.QMessageService;
import com.zjp.mq.tx.ActiveMQTransactionSynchronizationAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.util.Map;

/**
 * ━━━━━━南无阿弥陀佛━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃stay hungry stay foolish
 * 　　　　┃　　　┃Code is far away from bug with the animal protecting
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━萌萌哒━━━━━━
 * Module Desc:com.zjp.mq.producer
 * User: zjprevenge
 * Date: 2016/8/21
 * Time: 15:13
 */
public class MessageSenderProvider extends ProducerCfg {
    private static final Logger log = LoggerFactory.getLogger(MessageSender.class);

    @Resource(name = "QMessageService")
    private QMessageService qMessageService;

    @Resource
    private ProducerCache producerCache;

    @Resource
    private BrokerConfig brokerConfig;

    @Resource
    private DisruptorQueue disruptorQueue;

    public MessageSender getSender() {
        MessageSender messageSender = null;
        if (TransactionSynchronizationManager.hasResource(this)) {
            messageSender = (MessageSender) TransactionSynchronizationManager.getResource(this);
        } else {
            messageSender = MessageSender.builder()
                    .qMessageService(qMessageService)
                    .brokerConfig(brokerConfig)
                    .disruptorQueue(disruptorQueue)
                    .producerCache(producerCache)
                    .destName(destName)
                    .n2(n2)
                    .build();
            TransactionSynchronization synchronization = new ActiveMQTransactionSynchronizationAdapter(messageSender, qMessageService, disruptorQueue);
            TransactionSynchronizationManager.registerSynchronization(synchronization);
            TransactionSynchronizationManager.bindResource(this, messageSender);
        }
        return messageSender;
    }

    /**
     * 发送消息
     *
     * @param message 消息内容
     */
    public void sendMessage(Map<String, String> message) {
        MessageSender messageSender = Preconditions.checkNotNull(getSender());
        messageSender.sendMessage(message);
    }
}
