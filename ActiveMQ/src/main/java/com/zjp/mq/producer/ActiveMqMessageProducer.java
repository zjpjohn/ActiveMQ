package com.zjp.mq.producer;

import com.google.common.base.Preconditions;
import com.zjp.mq.service.QMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * author:zjprevenge
 * time: 2016/6/28
 * copyright all reserved
 * 消息发送成功，删除本数据库表中的数据
 */
public class ActiveMqMessageProducer extends AbsReqRespMessageProducer {

    private static final Logger log = LoggerFactory.getLogger(ActiveMqMessageProducer.class);


    private QMessageService qMessageService;

    public QMessageService getqMessageService() {
        return qMessageService;
    }

    public void setqMessageService(QMessageService qMessageService) {
        this.qMessageService = qMessageService;
    }

    /**
     * 响应消息，说明消费消息成功，删除本地数据库消息
     *
     * @param messageId 消息内容
     */
    public void handleMessage(String messageId) {
        Preconditions.checkNotNull(messageId);
        try {
            sendAckMessageToQueue(messageId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 处理回执消息业务
     */
    @Override
    public void ackMessageHandle() {
        try {
            String messageId = takeAckMessage();
            int result = qMessageService.deleteQMessage(messageId);
            if (result != 0) {
                log.info("delete db message success:{}", messageId);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public ActiveMqMessageProducer() {
    }

    public ActiveMqMessageProducer(Builder builder) {
        this.brokerUrl = builder.brokerUrl;
        this.userName = builder.userName;
        this.password = builder.password;
        this.destName = builder.destName;
        this.n2 = builder.n2;
        this.qMessageService = builder.qMessageService;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String brokerUrl;
        private String userName;
        private String password;
        private String destName;
        private boolean n2;
        private QMessageService qMessageService;

        public Builder() {
        }


        public Builder brokerUrl(String brokerUrl) {
            this.brokerUrl = brokerUrl;
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder destName(String destName) {
            this.destName = destName;
            return this;
        }

        public Builder n2(boolean n2) {
            this.n2 = n2;
            return this;
        }

        public Builder qMessageService(QMessageService qMessageService) {
            this.qMessageService = qMessageService;
            return this;
        }

        public ActiveMqMessageProducer build() throws Exception {
            ActiveMqMessageProducer producer = new ActiveMqMessageProducer(this);
            producer.afterPropertiesSet();
            return producer;
        }
    }
}
