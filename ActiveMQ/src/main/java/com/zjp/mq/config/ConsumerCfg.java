package com.zjp.mq.config;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * author:zjprevenge
 * time: 2016/6/26
 * copyright all reserved
 */

public class ConsumerCfg {

    //broker服务器地址
    protected String brokerUrl;

    //连接broker的用户名
    protected String userName;

    //连接broker的密码
    protected String password;

    //消费消息的地址
    protected String destName;

    //是否支持事务，默认支持事务
    protected boolean transaction = false;

    //是否支持n2级别的消息，默认不支持
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

    public String getDestName() {
        return destName;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isTransaction() {
        return transaction;
    }

    public void setTransaction(boolean transaction) {
        this.transaction = transaction;
    }

    /**
     * 校验参数
     *
     * @return
     */
    public boolean validate() {
        //brokerUrl不允许为空
        if (StringUtils.isBlank(brokerUrl)) {
            return false;
        }
        //destName不允许为空
        if (StringUtils.isBlank(destName)) {
            return false;
        }
        return true;
    }

    /**
     * 反射克隆
     *
     * @param consumerCfg
     */
    public void copyState(ConsumerCfg consumerCfg) {
        for (Field field : ConsumerCfg.class.getDeclaredFields()) {
            if (!Modifier.isFinal(field.getModifiers())) {
                field.setAccessible(true);
                try {
                    field.set(consumerCfg, field.get(this));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to copy ConsumerCfg state:" + e.getMessage(), e);
                }
            }
        }
    }
}
