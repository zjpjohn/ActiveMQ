package com.zjp.mq.producer;

import com.google.common.base.Preconditions;
import com.zjp.mq.cache.impl.ProducerCache;
import com.zjp.mq.config.BrokerConfig;
import com.zjp.mq.entity.QMessage;
import com.zjp.mq.service.QMessageService;
import com.zjp.mq.utils.MessageHolder;
import com.zjp.mq.utils.QMessageUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.Date;
import java.util.Map;

/**
 * author:zjprevenge
 * time: 2016/6/28
 * copyright all reserved
 */
public class MessageSender implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(MessageSender.class);

    private QMessageService qMessageService;

    private ProducerCache producerCache;

    private BrokerConfig brokerConfig;

    private boolean n2;

    private String destName;

    /**
     * 发送消息
     * 只是数据存储到本地数据库中
     *
     * @param message 消息内容
     */
    public void sendMessage(Map<String, String> message) {
        Preconditions.checkArgument(message != null && message.size() != 0, "message must not be empty...");
        //时间戳
        Date date = new Date();
        //生成消息Id
        String messageId = QMessageUtils.createMessageId(date);
        //创建消息
        QMessage qMessage = null;

        //是否是n2级别的消息
        if (n2) {
            //businessMark不允许为空
            Preconditions.checkArgument(StringUtils.isNotBlank(message.get("businessMark")),
                    "This queueName:" + destName + "is n2,businessMark must not be empty...");
            //创建消息
            qMessage = QMessage.builder()
                    .messageId(messageId)
                    .businessMark(message.get("businessMark"))
                    .destination(destName)
                    .status(0)
                    .destType(0)
                    .messageContent(message.get("message"))
                    .retry(0)
                    .timeStamp(date.getTime())
                    .build();
        } else {
            //只需要满足n1级别
            qMessage = QMessage.builder()
                    .messageId(messageId)
                    .destination(destName)
                    .status(0)
                    .destType(0)
                    .messageContent(message.get("message"))
                    .retry(0)
                    .timeStamp(date.getTime())
                    .build();
        }
        //将消息保存到数据库中
        int result = qMessageService.addQMessage(qMessage);
        //将消息Id共享出去
        if (result != 0) {
            MessageHolder.set(messageId);
        }
    }

    /**
     * 参数校验
     *
     * @throws Exception
     */
    public void afterPropertiesSet() throws Exception {
        //消息队列名称不允许以ack. or ACK.开始
        if (destName.startsWith("ack.")
                || destName.startsWith("ACK.")) {
            log.error("destName must not start with ack. or ACK.");
            throw new RuntimeException("destName must not start with ack. or ACK.");
        }
        //启动时初始化生产者
        ActiveMQTxMessageProducer producer = producerCache.get(destName);
        if (producer == null) {
            producer = ActiveMQTxMessageProducer.builder()
                    .brokerUrl(brokerConfig.getBrokerUrl())
                    .userName(brokerConfig.getUserName())
                    .password(brokerConfig.getPassword())
                    .destName(destName)
                    .n2(n2)
                    .qMessageService(qMessageService)
                    .build();
            //加入缓存中
            producerCache.set(destName, producer);
        }
    }

    public MessageSender() {
    }

    public MessageSender(Builder builder) {
        this.brokerConfig = builder.brokerConfig;
        this.qMessageService = builder.qMessageService;
        this.producerCache = builder.producerCache;
        this.n2 = builder.n2;
        this.destName = builder.destName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private QMessageService qMessageService;

        private ProducerCache producerCache;

        private BrokerConfig brokerConfig;

        private boolean n2;

        private String destName;

        public Builder() {
        }

        public Builder qMessageService(QMessageService qMessageService) {
            this.qMessageService = qMessageService;
            return this;
        }

        public Builder producerCache(ProducerCache producerCache) {
            this.producerCache = producerCache;
            return this;
        }

        public Builder brokerConfig(BrokerConfig brokerConfig) {
            this.brokerConfig = brokerConfig;
            return this;
        }

        public Builder n2(boolean n2) {
            this.n2 = n2;
            return this;
        }

        public Builder destName(String destName) {
            this.destName = destName;
            return this;
        }

        public MessageSender build() {
            MessageSender messageSender = null;
            try {
                messageSender = new MessageSender(this);
                messageSender.afterPropertiesSet();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return messageSender;
        }
    }
}
