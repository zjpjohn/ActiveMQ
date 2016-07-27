package com.zjp.mq.config;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * author:zjprevenge
 * time: 2016/6/26
 * copyright all reserved
 */
public class ProducerCfg {

    //broker服务器地址
    protected String brokerUrl;

    //连接broker的用户名
    protected String userName;

    //连接broker的密码
    protected String password;

    //投递消息的地址
    protected String destName;

    //是否持久化，默认支持
    protected boolean persistent = true;

    //是否支持事务，默认支持事务
    protected boolean transaction = true;

    //是否支持n2级别的消息,默认不支持
    protected boolean n2 = false;

    public boolean isN2() {
        return n2;
    }

    public void setN2(boolean n2) {
        this.n2 = n2;
    }

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDestName() {
        return destName;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public boolean isTransaction() {
        return transaction;
    }

    public void setTransaction(boolean transaction) {
        this.transaction = transaction;
    }

    //参数校验
    public boolean validate() {
        //brokerUrl判空
        if (StringUtils.isBlank(brokerUrl)) {
            return false;
        }
        //destName判空,并且消息队列不允许以ack. or ACK.开始
        if (StringUtils.isBlank(destName)
                || destName.startsWith("ack.")
                || destName.startsWith("ACK.")) {
            return false;
        }
        return true;
    }

    /**
     * 反射克隆
     *
     * @param producerCfg
     */
    public void copyState(ProducerCfg producerCfg) {

        for (Field field : ProducerCfg.class.getDeclaredFields()) {
            if (Modifier.isFinal(field.getModifiers())) {
                field.setAccessible(true);
                try {
                    field.set(producerCfg, field.get(this));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to copy ProducerCfg state:" + e.getMessage(), e);
                }
            }
        }
    }
}
