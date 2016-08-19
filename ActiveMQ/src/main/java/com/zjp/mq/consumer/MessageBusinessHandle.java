package com.zjp.mq.consumer;

import com.google.common.collect.Maps;
import com.zjp.mq.entity.N1Record;
import com.zjp.mq.entity.N2Record;
import com.zjp.mq.handle.MessageHandle;
import com.zjp.mq.service.N1RecordService;
import com.zjp.mq.service.N2RecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

/**
 * author:zjprevenge
 * time: 2016/7/15
 * copyright all reserved
 */
@Component
public class MessageBusinessHandle {

    private static final Logger log = LoggerFactory.getLogger(MessageBusinessHandle.class);

    @Resource
    private N1RecordService n1RecordService;

    @Resource
    private N2RecordService n2RecordService;

    /**
     * 添加消息消费记录，并进行消息业务无处理
     *
     * @param messageHandle 消息业务处理器
     * @param message       消息内容
     * @param destName      消息地址
     * @param n2            n2类型消息
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class})
    public void messageHandle(MessageHandle messageHandle,
                              Map<String, String> message,
                              String destName,
                              boolean n2) {
        //获取消息中的业务数据
        String messageContent = message.get("data");
        String messageId = message.get("messageId");
        String timeStr = message.get("timeStamp");

        //n1级别的要求
        if (!n2) {
            N1Record n1Record = n1RecordService.selectN1Record(messageId);
            //发送过来的消息不是重复消息，重复消息丢弃
            if (n1Record == null) {
                if (log.isDebugEnabled()) {
                    log.debug("none this type message,consume it and add record...");
                }
                //添加消费记录
                n1RecordService.addN1Record(
                        N1Record.builder()
                                .messageId(messageId)
                                .timeStamp(Long.valueOf(timeStr))
                                .build());
                //业务相关处理
                messageHandle.handleMessage(messageContent);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("this message is repeat message,discard it...");
                }
                //已经有消息记录存在，直接丢弃
                return;
            }

        } else {
            //n2级别的要求
            String businessMark = message.get("businessMark");

            Map<String, Object> params = Maps.newHashMap();
            params.put("businessMark", businessMark);
            params.put("destName", destName);
            N2Record n2Record = n2RecordService.selectN2RecordByMark(params);

            long timeStamp = Long.valueOf(timeStr);

            //如果没有消息记录，则添加
            if (n2Record == null) {
                if (log.isDebugEnabled()) {
                    log.debug("none this type message ,add message to record...");
                }
                //添加消息记录
                n2RecordService.addN2Record(
                        N2Record.builder()
                                .businessMark(businessMark)
                                .timeStamp(timeStamp)
                                .destName(destName)
                                .build());
                //业务相关处理
                messageHandle.handleMessage(messageContent);
            } else if (timeStamp > n2Record.getTimeStamp()) {
                //消费的消息的时间戳是最新，进行消息消费,否则丢弃消息
                if (log.isDebugEnabled()) {
                    log.debug("this message is new ,update the message record...");
                }
                //更新消费记录
                n2RecordService.updateN2Record(
                        N2Record.builder()
                                .businessMark(businessMark)
                                .timeStamp(timeStamp)
                                .destName(destName)
                                .build());
                //业务相关处理
                messageHandle.handleMessage(messageContent);
            } else {
                //如果消息是旧的消息，进行丢弃
                if (log.isDebugEnabled()) {
                    log.debug("this message is outdated,discard it...");
                }
                return;
            }
        }
    }
}
