package com.zjp.mq.config;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

/**
 * author:zjprevenge
 * time: 2016/7/22
 * copyright all reserved
 */
public class BrokerConfig implements InitializingBean {

    private String brokerUrl;

    private String userName;

    private String password;

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

    //参数校验
    public boolean validate() {
        return StringUtils.isNotBlank(brokerUrl);
    }

    public void afterPropertiesSet() throws Exception {
        Preconditions.checkState(validate(), "brokerUrl must not be empty...");
    }
}
