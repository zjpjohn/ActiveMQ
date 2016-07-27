package com.zjp.mq.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * author:zjprevenge
 * time: 2016/6/27
 * copyright all reserved
 */
public class QMessage implements Serializable {

    private Integer id;

    //消息id
    private String messageId;

    //业务标识
    private String businessMark;

    //消息内容
    private String messageContent;

    //消息状态
    private Integer status;

    //重试次数
    private Integer retry;

    //目标地址
    private String destination;

    //投递类型
    private int destType;

    //时间戳
    private Long timeStamp;

    public QMessage() {
    }

    public QMessage(Builder builder) {
        this.messageId = builder.messageId;
        this.businessMark = builder.businessMark;
        this.destination = builder.destination;
        this.destType = builder.destType;
        this.retry = builder.retry;
        this.timeStamp = builder.timeStamp;
        this.status = builder.status;
        this.messageContent = builder.messageContent;
        this.id = builder.id;
    }

    public static Builder builder() {
        return new Builder();
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

    public String getBusinessMark() {
        return businessMark;
    }

    public void setBusinessMark(String businessMark) {
        this.businessMark = businessMark;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRetry() {
        return retry;
    }

    public void setRetry(Integer retry) {
        this.retry = retry;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getDestType() {
        return destType;
    }

    public void setDestType(int destType) {
        this.destType = destType;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "QMessage{" +
                "id=" + id +
                ", messageId='" + messageId + '\'' +
                ", businessMark='" + businessMark + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", status=" + status +
                ", retry=" + retry +
                ", destination='" + destination + '\'' +
                ", destType=" + destType +
                ", timeStamp=" + timeStamp +
                '}';
    }

    public static class Builder {
        private Integer id;
        private String messageId;
        private String businessMark;
        private String messageContent;
        private Integer status;
        private Integer retry;
        private String destination;
        private int destType;
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

        public Builder businessMark(String businessMark) {
            this.businessMark = businessMark;
            return this;
        }

        public Builder messageContent(String messageContent) {
            this.messageContent = messageContent;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder retry(int retry) {
            this.retry = retry;
            return this;
        }

        public Builder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public Builder destType(int destType) {
            this.destType = destType;
            return this;
        }

        public Builder timeStamp(Long timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public QMessage build() {
            return new QMessage(this);
        }

    }
}
