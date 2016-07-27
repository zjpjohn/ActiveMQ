package com.zjp.mq.entity;

import java.util.Date;

/**
 * author:zjprevenge
 * time: 2016/6/28
 * copyright all reserved
 * <p/>
 * 消息消费记录
 * <p/>
 * 用于消除单条消息的重复消费
 */

public class N1Record {

    private Integer id;
    //消息id
    private String messageId;
    //消息时间戳
    private Long timeStamp;

    public N1Record() {
    }

    public N1Record(Builder builder) {
        this.id = builder.id;
        this.messageId = builder.messageId;
        this.timeStamp = builder.timeStamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        //消息id
        private String messageId;
        //消息时间戳
        private Long timeStamp;

        public Builder() {
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder messageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder timeStamp(Long timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public N1Record build() {
            return new N1Record(this);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
