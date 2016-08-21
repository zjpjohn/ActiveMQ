package com.zjp.mq.producer;

import com.google.common.base.Preconditions;
import com.zjp.mq.config.ProducerCfg;
import com.zjp.mq.entity.QMessage;
import com.zjp.mq.handle.MessageHandle;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.jms.*;
import java.util.concurrent.*;

/**
 * author:zjprevenge
 * time: 2016/6/26
 * copyright all reserved
 */
public abstract class AbsReqRespMessageProducer extends ProducerCfg implements MessageListener, MessageHandle, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(AbsReqRespMessageProducer.class);
    //broker连接器
    private ActiveMQConnectionFactory connectionFactory;
    //ack消息的连接器
    private Connection ackConnection;
    //创建线程池，进行异步发送消息
    private ExecutorService executorService;
    //消息id异步队列
    private BlockingQueue<String> messageIdQueue;
    //ack队列名称
    private String ackName;

    public String getAckName() {
        return ackName;
    }

    public void setAckName(String ackName) {
        this.ackName = ackName;
    }

    /**
     * 处理回执消息,获取消息进行业务处理
     *
     * @param message 回执消息
     */
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            log.info("handle the callback message...");
            String text = textMessage.getText();
            handleMessage(text);
        } catch (JMSException e) {
            log.error("handle message error： {}", e);
        }
    }

    /**
     * 发送消息
     *
     * @param qMessage 消息内容
     */
    public void sendMessage(final QMessage qMessage) throws Exception {
        Preconditions.checkArgument(qMessage != null
                        && StringUtils.isNotBlank(qMessage.getMessageContent()),
                "message must not be empty...");
        //进行消息发送
        messaging(qMessage);
    }

    /**
     * 创建消息队列发送消息
     *
     * @param message 消息内容
     */
    private void messaging(QMessage message) {
        Connection connection = null;
        Session session = null;
        try {
            if (log.isDebugEnabled()) {
                log.debug("async send message to activeMQ broker...");
            }
            connection = connectionFactory.createConnection();
            //创建session会话
            session = connection.createSession(transaction, ActiveMQSession.AUTO_ACKNOWLEDGE);
            //创建消息投递目的地
            Destination queue = session.createQueue(destName);
            //创建生产者
            MessageProducer producer = session.createProducer(queue);
            //设置消息是否持久化
            producer.setDeliveryMode(persistent ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
            //创建Map类型的消息
            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setString("messageId", message.getMessageId());
            mapMessage.setString("data", message.getMessageContent());
            mapMessage.setString("timeStamp", String.valueOf(message.getTimeStamp()));
            //如果支持n2级别的消息
            if (n2) {
                //如果业务标识存在，消息中要带上业务标识以及时间戳
                if (StringUtils.isNotBlank(message.getBusinessMark())) {
                    mapMessage.setString("businessMark", message.getBusinessMark());
                } else {
                    //如果n2级别的消息，businessMark为空，抛出异常
                    throw new RuntimeException("n2 level message require businessMark not empty...");
                }
            }
            //发送消息
            producer.send(mapMessage);
            //开启事务，以事物的方式提交
            if (transaction) {
                session.commit();
            }
        } catch (Exception e) {
            log.error("send message error: {}", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    log.error("close connection error: {}", e);
                }
            }
        }
    }

    /**
     * 进行参数校验
     *
     * @throws Exception
     */
    public void afterPropertiesSet() throws Exception {
        //参数校验
        if (!validate()) {
            throw new RuntimeException("brokerUrl and destName must not be empty...");
        }
        //消息队列名称不允许以ack. or ACK.开始
        if (destName.startsWith("ack.")
                || destName.startsWith("ACK.")) {
            throw new RuntimeException("destName must not start with ack. or ACK.");
        }

        connectionFactory = new ActiveMQConnectionFactory(userName, password, brokerUrl);
        ExecutorService initListener = Executors.newSingleThreadExecutor();
        initListener.submit(new Runnable() {
            public void run() {
                try {
                    //创建连接
                    ackConnection = connectionFactory.createConnection();
                    //创建回执消息队列名称
                    ackName = "ack." + destName;
                    //开启回执消息监听
                    ackHandle();
                } catch (Exception e) {
                    log.error("connect broker error：{}", e);
                }
            }
        });

        executorService = Executors.newSingleThreadExecutor();
        messageIdQueue = new LinkedBlockingDeque<String>(1000);
        executorService.submit(new Runnable() {
            public void run() {
                while (true) {
                    ackMessageHandle();
                }
            }
        });
    }

    /**
     * 向异步队列中添加消息，队列满是阻塞
     *
     * @param messageId
     * @throws Exception
     */
    public void sendAckMessageToQueue(String messageId) throws Exception {
        messageIdQueue.put(messageId);
    }

    /**
     * 从异步队列中获取消息，没有消息是阻塞
     *
     * @return
     * @throws Exception
     */
    public String takeAckMessage() throws Exception {
        return messageIdQueue.take();
    }

    /**
     * 处理回执消息业务
     */
    public abstract void ackMessageHandle();

    /**
     * 回执消息处理
     */
    public void ackHandle() throws Exception {
        //创建回执消息连接
        if (ackConnection == null) {
            ackConnection = connectionFactory.createConnection();
        }
        //开启连接
        ackConnection.start();
        //创建连接会话
        Session ackSession = ackConnection.createSession(Boolean.FALSE, ActiveMQSession.AUTO_ACKNOWLEDGE);
        //创建回执队列地址
        Destination ackQueue = ackSession.createQueue(ackName);
        //创建消费者
        MessageConsumer consumer = ackSession.createConsumer(ackQueue);
        //设置回执消息监听
        consumer.setMessageListener(AbsReqRespMessageProducer.this);
    }

    /**
     * 关闭ackConnection
     */
    public void closeConnection() {
        if (ackConnection != null) {
            try {
                ackConnection.close();
            } catch (JMSException e) {
                log.error("close ackConnection error: {}", e);
            }
        }
    }
}
