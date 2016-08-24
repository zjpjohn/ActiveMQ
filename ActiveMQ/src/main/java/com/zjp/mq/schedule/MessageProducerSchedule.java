package com.zjp.mq.schedule;

import com.zjp.mq.disruptor.impl.DisruptorQueue;
import com.zjp.mq.entity.QMessage;
import com.zjp.mq.service.QMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * author:zjprevenge
 * time: 2016/6/28
 * copyright all reserved
 */
@Component
public class MessageProducerSchedule {

    private static final Logger log = LoggerFactory.getLogger(MessageProducerSchedule.class);

    @Resource
    private DisruptorQueue disruptorQueue;

    @Resource(name = "QMessageService")
    private QMessageService qMessageService;

    //每隔10分钟去发送消息
    @Scheduled(cron = "0 0/10 * * * ?")
    public void scheduleJob() {
        log.info("schedule job to send message...");
        //获取全部的消息进行发送
        List<QMessage> messages = null;
        try {
            messages = qMessageService.selectAllQMessage(System.currentTimeMillis());
        } catch (Exception e) {
            log.error("query unconsumed message error:{}", e);
        }
        //没有消息，则停止该job
        if (messages == null || messages.size() == 0) {
            return;
        }
        //对每一条消息进行发送
        for (QMessage message : messages) {
            disruptorQueue.publish(message);
        }
    }
}
