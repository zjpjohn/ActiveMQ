package com.zjp.mq.tx;

import com.zjp.mq.disruptor.impl.DisruptorQueue;
import com.zjp.mq.entity.QMessage;
import com.zjp.mq.service.QMessageService;
import com.zjp.mq.utils.MessageHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private QMessageService qMessageService;

    private DisruptorQueue disruptorQueue;

    private ExecutorService executorService;

    public ActiveMQTransactionSynchronizationAdapter() {
    }

    public ActiveMQTransactionSynchronizationAdapter(QMessageService qMessageService,
                                                     DisruptorQueue disruptorQueue) {
        this.qMessageService = qMessageService;
        this.disruptorQueue = disruptorQueue;
        executorService = Executors.newSingleThreadExecutor();
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
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void afterCompletion(int status) {
        if (STATUS_COMMITTED == status) {
            log.info("事务提交成功后,向activeMQ broker 发送消息");
            final CopyOnWriteArrayList<String> strings = new CopyOnWriteArrayList<String>(MessageHolder.get());
            //事务提交成功，向broker中发送消息
            executorService.execute(new Runnable() {
                public void run() {
                    sendMessageToBroker(strings);
                }
            });
        }
        if (STATUS_ROLLED_BACK == status) {
            log.info("事务提交失败，数据库回滚后，清空缓存中的消息：{}", MessageHolder.get());
        }
        MessageHolder.remove();
    }

    /**
     * 向broker中发送消息
     */
    private void sendMessageToBroker(List<String> list) {
        try {
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
        }
    }
}
