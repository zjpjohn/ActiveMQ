package com.zjp.mq.entity;

import java.util.Date;

/**
 * author:zjprevenge
 * time: 2016/6/28
 * copyright all reserved
 * <p/>
 * N2类型的消息消费记录
 * 用于消息同类型的多条消息的最新时间
 * <p/>
 * 需要使用者传递，业务标识
 */

public class N2Record {

    private Integer id;
    //业务标识
    private String businessMark;
    //时间戳
    private Long timeStamp;
    //队列名称
    private String destName;

    public N2Record() {
    }

    public N2Record(Builder builder) {
        this.id = builder.id;
        this.businessMark = builder.businessMark;
        this.timeStamp = builder.timeStamp;
        this.destName = builder.destName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        //业务标识
        private String businessMark;
        //时间戳
        private Long timeStamp;
        //队列名称
        private String destName;

        public Builder() {
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder businessMark(String businessMark) {
            this.businessMark = businessMark;
            return this;
        }

        public Builder timeStamp(Long timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public Builder destName(String destName) {
            this.destName = destName;
            return this;
        }

        public N2Record build() {
            return new N2Record(this);
        }
    }

    public String getDestName() {
        return destName;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBusinessMark() {
        return businessMark;
    }

    public void setBusinessMark(String businessMark) {
        this.businessMark = businessMark;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
