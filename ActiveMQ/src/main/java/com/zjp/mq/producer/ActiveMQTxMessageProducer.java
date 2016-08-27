package com.zjp.mq.producer;

import com.zjp.mq.service.QMessageService;

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
 * Date: 2016/8/27
 * Time: 18:54
 */

public class ActiveMQTxMessageProducer extends AbsActiveMQTxMessageProducer {

    private QMessageService qMessageService;

    public QMessageService getqMessageService() {
        return qMessageService;
    }

    public void setqMessageService(QMessageService qMessageService) {
        this.qMessageService = qMessageService;
    }

    /**
     * 事务消息处理成功后的处理
     * 事务消息处理成功，从消息表中删除对应的消息
     *
     * @param messageId 消息id
     */
    public void onSuccess(String messageId) {
        log.info("tx message--{}--commit success", messageId);
        //事务消息处理成功后，删除数据库中对应的消息
        qMessageService.deleteQMessage(messageId);
    }

    /**
     * 事务消息处理失败后的处理
     * 事务消息处理失败户，进行日志相关记录
     * 或者其他相关数据库操作
     *
     * @param e         消息发送异常
     * @param messageId 消息id
     */
    public void onFail(Exception e, String messageId) {
        log.error("send tx message:{} ,error:{}", messageId, e.getMessage());
    }

    public ActiveMQTxMessageProducer() {
    }

    public ActiveMQTxMessageProducer(Builder builder) {
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

        public ActiveMQTxMessageProducer build() throws Exception {
            ActiveMQTxMessageProducer producer = new ActiveMQTxMessageProducer(this);
            producer.afterPropertiesSet();
            return producer;
        }
    }
}
