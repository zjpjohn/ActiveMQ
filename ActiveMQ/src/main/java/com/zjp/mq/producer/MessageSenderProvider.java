package com.zjp.mq.producer;

import com.zjp.mq.cache.impl.ProducerCache;
import com.zjp.mq.config.BrokerConfig;
import com.zjp.mq.config.ProducerCfg;
import com.zjp.mq.disruptor.impl.DisruptorQueue;
import com.zjp.mq.service.QMessageService;
import com.zjp.mq.tx.ActiveMQTransactionSynchronizationAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
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
public class MessageSenderProvider extends ProducerCfg implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(MessageSender.class);

    @Resource(name = "QMessageService")
    private QMessageService qMessageService;

    @Resource
    private ProducerCache producerCache;

    @Resource
    private BrokerConfig brokerConfig;

    @Resource
    private DisruptorQueue disruptorQueue;

    private MessageSender messageSender;

    public MessageSender getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    /**
     * 设置事务同步
     */
    private void txSynchronize() {
        TransactionSynchronization adapter = new ActiveMQTransactionSynchronizationAdapter(qMessageService, disruptorQueue);
        TransactionSynchronizationManager.registerSynchronization(adapter);
    }

    /**
     * 发送消息
     *
     * @param message 消息内容
     */
    public void sendMessage(Map<String, String> message) {
        txSynchronize();
        messageSender.sendMessage(message);
    }

    public void afterPropertiesSet() throws Exception {
        messageSender = MessageSender.builder()
                .qMessageService(qMessageService)
                .producerCache(producerCache)
                .brokerConfig(brokerConfig)
                .destName(destName)
                .n2(n2)
                .build();
    }
}
