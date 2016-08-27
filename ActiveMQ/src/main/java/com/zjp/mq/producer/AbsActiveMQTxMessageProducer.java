package com.zjp.mq.producer;

import com.google.common.base.Preconditions;
import com.zjp.mq.config.ProducerCfg;
import com.zjp.mq.entity.QMessage;
import com.zjp.mq.handle.TxMessageCallback;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.jms.*;


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
 * Time: 18:18
 */

public abstract class AbsActiveMQTxMessageProducer extends ProducerCfg implements TxMessageCallback, InitializingBean {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    //activeMQ连接工厂
    private ActiveMQConnectionFactory connectionFactory;

    //activeMQ连接
    private Connection connection;

    public void afterPropertiesSet() throws Exception {
        if (!validate()) {
            throw new RuntimeException("brokerUrl and destName must not be empty...");
        }
        this.connectionFactory = new ActiveMQConnectionFactory(userName, password, brokerUrl);
        this.connection = connectionFactory.createConnection();
    }

    /**
     * 发送消息处理
     *
     * @param message 消息实体
     */
    public void sendMessage(QMessage message) {
        Preconditions.checkArgument(message != null
                && StringUtils.isNotBlank(message.getMessageContent())
                , "message must not be empty...");
        messaging(message);
    }

    /**
     * 发送消息业务逻辑处理
     *
     * @param message
     */
    private void messaging(QMessage message) {
        Session session = null;
        if (log.isDebugEnabled()) {
            log.debug("async send message to activeMQ broker...");
        }
        try {
            //创建发送消息的session会话
            session = connection.createSession(transaction, ActiveMQSession.AUTO_ACKNOWLEDGE);
            //创建消息投递地址
            Destination queue = session.createQueue(destName);
            //创建消息发送者
            MessageProducer producer = session.createProducer(queue);
            //设置消息是否持久化
            producer.setDeliveryMode(persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
            //创建Map类型的消息
            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setString("messageId", message.getMessageId());
            mapMessage.setString("data", message.getMessageContent());
            mapMessage.setString("timeStamp", String.valueOf(message.getTimeStamp()));
            //如果是n2级别的消息，创建业务标识
            if (n2) {
                if (StringUtils.isNotBlank(message.getBusinessMark())) {
                    mapMessage.setString("businessMark", message.getBusinessMark());
                } else {
                    //如果n2级别的消息，businessMark为空，抛出异常
                    throw new RuntimeException("n2 level message require businessMark not empty...");
                }
            }
            producer.send(mapMessage);
            if (transaction) {
                //进行事务提交
                session.commit();
                //消息发送成功后进行处理
                onSuccess(message.getMessageId());
            }
        } catch (JMSException e) {
            log.error("send message error:{}", e);
            //消息发送失败是进行的处理
            onFail(e, message.getMessageId());
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    log.error("close session error:{}", e);
                }
            }
        }
    }
}
