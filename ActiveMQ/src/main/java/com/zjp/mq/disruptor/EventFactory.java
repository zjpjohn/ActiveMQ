package com.zjp.mq.disruptor;

/**
 * author:zjprevenge
 * time: 2016/7/22
 * copyright all reserved
 */
public class EventFactory implements com.lmax.disruptor.EventFactory<MessageEvent> {
    public MessageEvent newInstance() {
        return new MessageEvent();
    }
}
