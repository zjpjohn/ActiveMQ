package com.zjp.mq.service;

import com.zjp.mq.entity.QMessage;

import java.util.List;

/**
 * author:zjprevenge
 * time: 2016/6/27
 * copyright all reserved
 */
public interface QMessageService {

    /**
     * 获取QMessage
     *
     * @param messageId
     * @return
     */
    QMessage getMessage(String messageId);

    /**
     * 添加消息
     *
     * @param qMessage
     */
    int addQMessage(QMessage qMessage);

    /**
     * 更新消息
     *
     * @param qMessage
     */
    int updateQMessage(QMessage qMessage);

    /**
     * 删除消息
     *
     * @param messageId
     */
    int deleteQMessage(String messageId);

    /**
     * 获取所有消息
     *
     * @return
     */
    List<QMessage> selectAllQMessage(Long currentTime);
}
