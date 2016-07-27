package com.zjp.mq.disruptor;

import com.zjp.mq.entity.QMessage;

/**
 * author:zjprevenge
 * time: 2016/7/22
 * copyright all reserved
 */
public class MessageEvent {

    private QMessage qMessage;

    public QMessage getqMessage() {
        return qMessage;
    }

    public void setqMessage(QMessage qMessage) {
        this.qMessage = qMessage;
    }
}
