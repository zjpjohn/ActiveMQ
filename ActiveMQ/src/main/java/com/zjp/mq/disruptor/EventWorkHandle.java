package com.zjp.mq.disruptor;

import com.lmax.disruptor.WorkHandler;
import com.zjp.mq.cache.impl.ProducerCache;
import com.zjp.mq.config.BrokerConfig;
import com.zjp.mq.entity.QMessage;
import com.zjp.mq.producer.ActiveMQTxMessageProducer;
import com.zjp.mq.service.QMessageService;
import org.apache.commons.lang3.StringUtils;

/**
 * author:zjprevenge
 * time: 2016/7/22
 * copyright all reserved
 * 队列事件的处理，向broker中发送消息
 */
public class EventWorkHandle implements WorkHandler<MessageEvent> {

    //broker 配置信息
    private BrokerConfig brokerConfig;
    //生产者缓存
    private ProducerCache producerCache;
    //消息操作
    private QMessageService qMessageService;

    public BrokerConfig getBrokerConfig() {
        return brokerConfig;
    }

    public void setBrokerConfig(BrokerConfig brokerConfig) {
        this.brokerConfig = brokerConfig;
    }

    public ProducerCache getProducerCache() {
        return producerCache;
    }

    public void setProducerCache(ProducerCache producerCache) {
        this.producerCache = producerCache;
    }

    public QMessageService getqMessageService() {
        return qMessageService;
    }

    public void setqMessageService(QMessageService qMessageService) {
        this.qMessageService = qMessageService;
    }

    public EventWorkHandle() {
    }

    public EventWorkHandle(Builder builder) {
        this.brokerConfig = builder.brokerConfig;
        this.producerCache = builder.producerCache;
        this.qMessageService = builder.qMessageService;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BrokerConfig brokerConfig;

        private ProducerCache producerCache;

        private QMessageService qMessageService;

        public Builder() {
        }

        public Builder brokerConfig(BrokerConfig brokerConfig) {
            this.brokerConfig = brokerConfig;
            return this;
        }

        public Builder producerCache(ProducerCache producerCache) {
            this.producerCache = producerCache;
            return this;
        }

        public Builder qMessageService(QMessageService qMessageService) {
            this.qMessageService = qMessageService;
            return this;
        }

        public EventWorkHandle build() {
            return new EventWorkHandle(this);
        }
    }

    public void onEvent(MessageEvent event) throws Exception {
        //获取消息内容
        QMessage message = event.getqMessage();
        //从缓存中获取activeMQ生产者
        ActiveMQTxMessageProducer producer = producerCache.get(message.getDestination());
        if (producer == null) {
            //如果为空,创建
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
    }
}
