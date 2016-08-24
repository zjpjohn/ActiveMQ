package com.zjp.mq.mapper;

import com.zjp.mq.entity.QMessage;

import java.util.List;

/**
 * author:zjprevenge
 * time: 2016/6/27
 * copyright all reserved
 */
public interface QMessageMapper {

    /**
     * 根据messageId获取QMessage
     *
     * @param messageId
     * @return
     */
    QMessage selectQMessageByMessageId(String messageId);

    /**
     * 添加消息
     *
     * @param qMessage 消息
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
