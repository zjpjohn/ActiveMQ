package com.zjp.mq.service.impl;

import com.zjp.mq.entity.QMessage;
import com.zjp.mq.mapper.QMessageMapper;
import com.zjp.mq.service.QMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * author:zjprevenge
 * time: 2016/6/27
 * copyright all reserved
 */
@Service("QMessageService")
public class QMessageServiceImpl implements QMessageService {

    @Resource(name = "QMessageMapper")
    private QMessageMapper qMessageMapper;

    /**
     * 获取QMessage
     *
     * @param messageId
     * @return
     */
    public QMessage getMessage(String messageId) {
        if (StringUtils.isBlank(messageId)) {
            return null;
        }
        return qMessageMapper.selectQMessageByMessageId(messageId);
    }

    /**
     * 添加消息
     *
     * @param qMessage
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public int addQMessage(QMessage qMessage) {
        if (qMessage == null) {
            return 0;
        }
        return qMessageMapper.addQMessage(qMessage);
    }

    /**
     * 更新消息
     *
     * @param qMessage
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public int updateQMessage(QMessage qMessage) {
        if (qMessage == null) {
            return 0;
        }
        return qMessageMapper.updateQMessage(qMessage);
    }

    /**
     * 删除消息
     *
     * @param messageId
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public int deleteQMessage(String messageId) {
        if (StringUtils.isBlank(messageId)) {
            return 0;
        }
        return qMessageMapper.deleteQMessage(messageId);
    }

    /**
     * 获取所有消息
     *
     * @return
     */
    public List<QMessage> selectAllQMessage(Long currentTime) {
        return qMessageMapper.selectAllQMessage(currentTime);
    }
}
