package com.zjp.mq.tx;

import com.zjp.mq.disruptor.impl.DisruptorQueue;
import com.zjp.mq.entity.QMessage;
import com.zjp.mq.producer.MessageSender;
import com.zjp.mq.service.QMessageService;
import com.zjp.mq.utils.MessageHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

import java.util.List;

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
 * Module Desc:com.zjp.mq.tx
 * User: zjprevenge
 * Date: 2016/8/21
 * Time: 14:51
 */

public class ActiveMQTransactionSynchronizationAdapter extends TransactionSynchronizationAdapter {

    private static final Logger log = LoggerFactory.getLogger(ActiveMQTransactionSynchronizationAdapter.class);

    private MessageSender messageSender;

    private QMessageService qMessageService;

    private DisruptorQueue disruptorQueue;

    public ActiveMQTransactionSynchronizationAdapter() {
    }

    public ActiveMQTransactionSynchronizationAdapter(MessageSender messageSender,
                                                     QMessageService qMessageService,
                                                     DisruptorQueue disruptorQueue) {
        this.messageSender = messageSender;
        this.qMessageService = qMessageService;
        this.disruptorQueue = disruptorQueue;
    }

    public MessageSender getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public QMessageService getqMessageService() {
        return qMessageService;
    }

    public void setqMessageService(QMessageService qMessageService) {
        this.qMessageService = qMessageService;
    }

    public DisruptorQueue getDisruptorQueue() {
        return disruptorQueue;
    }

    public void setDisruptorQueue(DisruptorQueue disruptorQueue) {
        this.disruptorQueue = disruptorQueue;
    }

    @Override
    public void afterCompletion(int status) {
        if (STATUS_COMMITTED == status) {
            log.info("事务提交成功,向activeMQ broker 发送消息");
            //事务提交成功，向broker中发送消息
            sendMessageToBroker();
        } else if (STATUS_ROLLED_BACK == status) {
            log.warn("事务提交失败,忽略消息:{}", MessageHolder.get());
            //事务提交失败，清空内存中的数据
            MessageHolder.remove();
        }
    }

    /**
     * 向broker中发送消息
     */
    private void sendMessageToBroker() {
        try {
            List<String> list = MessageHolder.get();
            if (list == null || list.size() == 0) {
                return;
            }
            if (log.isDebugEnabled()) {
                log.debug("send message to activeMQ broker start... ");
            }
            if (log.isDebugEnabled()) {
                log.debug("get the message size:{}", list.size());
            }
            for (String messageId : list) {
                //获取消息内容
                QMessage message = qMessageService.getMessage(messageId);
                //获取消息对应的生产者
                if (message == null) {
                    continue;
                }
                disruptorQueue.publish(message);
            }
            if (log.isDebugEnabled()) {
                log.debug("send message to activeMQ broker end...");
            }
        } catch (Exception e) {
            log.error("send message error: {}", e);
        } finally {
            //清除数据，减小内存占用
            MessageHolder.remove();
        }
    }
}
