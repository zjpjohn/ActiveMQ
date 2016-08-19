package com.zjp.mq.consumer;

import com.google.common.collect.Maps;
import com.zjp.mq.config.BrokerConfig;
import com.zjp.mq.config.ConsumerCfg;
import com.zjp.mq.handle.MessageHandle;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.RedeliveryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * author:zjprevenge
 * time: 2016/6/26
 * copyright all reserved
 * request-response模式的消费者
 */
public abstract class AbstractReqRespConsumer extends ConsumerCfg implements MessageListener, MessageHandle, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(AbstractReqRespConsumer.class);
    //broker 连接工厂
    private ActiveMQConnectionFactory connectionFactory;
    //消息连接
    private Connection connection;
    //消息会话
    private Session session;
    //消息处理线程池
    public static ExecutorService executorService;
    //内存消息队列
    private LinkedBlockingQueue<Map<String, String>> messageStore;
    //消息ack队列
    private String ackName;

    @Resource
    private BrokerConfig brokerConfig;

    @Resource
    private MessageBusinessHandle messageBusinessHandle;

    /**
     * 初始化相关参数
     *
     * @throws Exception
     */
    public void afterPropertiesSet() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("init consumer parameters...");
        }
        //消息队列名称ack or ACK 为保留字段，不允许以此开始
        if (destName.startsWith("ack.")
                || destName.startsWith("ACK.")) {
            throw new RuntimeException(destName + "destName must not start with ack. or ACK.");
        }
        //创建回执消息队列名称，以ack. or ACK.开始
        ackName = "ack." + destName;
        //创建内存队列
        messageStore = new LinkedBlockingQueue<Map<String, String>>(10000);
        //创建线程池
        executorService = Executors.newSingleThreadExecutor();

        executorService.submit(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Map<String, String> map = messageStore.take();
                        messageHandle(map);
                    } catch (InterruptedException e) {
                        log.error("get the message error:{}", e);
                    } catch (Exception e) {
                        log.error("handle message error:{}", e);
                    }
                }
            }
        });

        //初始化监听器
        ExecutorService initListener = Executors.newSingleThreadExecutor();
        initListener.submit(new Runnable() {
            public void run() {
                try {
                    connectionFactory = new ActiveMQConnectionFactory(brokerConfig.getUserName()
                            , brokerConfig.getPassword()
                            , brokerConfig.getBrokerUrl());
                    //创建连接
                    connection = connectionFactory.createConnection();
                    RedeliveryPolicy policy = ((ActiveMQConnection) connection).getRedeliveryPolicy();
                    //设置重试策略
                    policy.setInitialRedeliveryDelay(1000);
                    policy.setBackOffMultiplier(0);
                    policy.setUseExponentialBackOff(true);
                    policy.setMaximumRedeliveries(0);

                    //启动链接
                    connection.start();
                    //创建会话
                    session = connection.createSession(transaction, ActiveMQSession.AUTO_ACKNOWLEDGE);

                    //创建消息消费目的地
                    Destination queue = session.createQueue(destName);
                    //创建消费者
                    MessageConsumer consumer = session.createConsumer(queue);
                    //设置消息监听
                    consumer.setMessageListener(AbstractReqRespConsumer.this);
                } catch (JMSException e) {
                    log.error("");
                }
            }
        });
    }

    /**
     * 消息处理
     *
     * @param message 消息内容
     */
    public void onMessage(Message message) {
        try {
            //创建回执消息
            MapMessage mapMessage = (MapMessage) message;

            //回去消息id，判断消息是否有效
            String messageId = mapMessage.getString("messageId");
            //消息有效进行处理
            if (messageId != null) {
                Map<String, String> map = Maps.newHashMap();
                map.put("data", mapMessage.getString("data"));
                map.put("messageId", mapMessage.getString("messageId"));
                map.put("businessMark", mapMessage.getString("businessMark"));
                map.put("timeStamp", mapMessage.getString("timeStamp"));
                //进行业务处理
                messageStore.put(map);

            }
        } catch (Exception e) {
            log.error("handle message error: {}", e);
        } finally {
            try {
                if (transaction) {
                    session.commit();
                } else {
                    message.acknowledge();
                }
            } catch (JMSException e) {
                log.error("acknowledge message error:{}", e);
            }
        }
    }

    /**
     * 将消息处理和添加消费记录放在同一个事物中
     * 方法进行同步，防止竞争问题
     *
     * @param message 消息
     */
    public synchronized void messageHandle(Map<String, String> message) throws Exception {

        String messageId = message.get("messageId");
        //进行消息的业务处理
        messageBusinessHandle.messageHandle(this, message, destName, n2);
        //发送回执消息
        ackMessageSender(messageId);
    }

    /**
     * 发送确认消息
     * 对发送确认消息的异常进行处理，防止影响业务操作和消息记录操作
     *
     * @param messageId 消息Id
     */
    public void ackMessageSender(String messageId) {
        Connection ackConnection = null;
        try {
            //创建回执连接
            ackConnection = connectionFactory.createConnection();

            //打开回执连接
            ackConnection.start();
            //创建连接会话
            Session ackSession = ackConnection.createSession(Boolean.FALSE, ActiveMQSession.AUTO_ACKNOWLEDGE);
            //创建回执消息投送目的地
            Destination ackQueue = ackSession.createQueue(ackName);
            //创建消息发送者
            MessageProducer producer = ackSession.createProducer(ackQueue);
            //创建text类型的消息
            TextMessage textMessage = ackSession.createTextMessage(messageId);
            //发送消息
            log.info("send ack message to ack the message has bean handled...");
            producer.send(textMessage);
        } catch (Exception e) {
            log.error("send callback message error: {}", e);
        } finally {
            if (ackConnection != null) {
                try {
                    ackConnection.close();
                } catch (JMSException e) {
                    log.error("close connection error：{}", e);
                }
            }
        }
    }

    /**
     * 关闭连接
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                log.error("close connection error: {}", e);
            }
        }
    }
}
