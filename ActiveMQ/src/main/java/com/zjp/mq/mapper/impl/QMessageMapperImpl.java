package com.zjp.mq.mapper.impl;

import com.zjp.mq.entity.QMessage;
import com.zjp.mq.mapper.QMessageMapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * author:zjprevenge
 * time: 2016/6/27
 * copyright all reserved
 */
@Repository("QMessageMapper")
public class QMessageMapperImpl implements QMessageMapper {

    @Resource
    private SqlSession sqlSession;

    /**
     * 根据messageId获取QMessage
     *
     * @param messageId
     * @return
     */
    public QMessage selectQMessageByMessageId(String messageId) {
        return sqlSession.selectOne("com.zjp.mq.mapper.QMessageMapper.selectQMessageByMessageId", messageId);
    }

    /**
     * 添加消息
     *
     * @param qMessage 消息
     */
    public int addQMessage(QMessage qMessage) {
        return sqlSession.insert("com.zjp.mq.mapper.QMessageMapper.addQMessage", qMessage);
    }

    /**
     * 更新消息
     *
     * @param qMessage
     */
    public int updateQMessage(QMessage qMessage) {
        return sqlSession.update("com.zjp.mq.mapper.QMessageMapper.updateQMessage", qMessage);
    }

    /**
     * 删除消息
     *
     * @param messageId
     */
    public int deleteQMessage(String messageId) {
        return sqlSession.delete("com.zjp.mq.mapper.QMessageMapper.deleteQMessage", messageId);
    }

    /**
     * 获取所有消息
     *
     * @return
     */
    public List<QMessage> selectAllQMessage() {
        return sqlSession.selectList("com.zjp.mq.mapper.QMessageMapper.selectAllQMessage");
    }
}
