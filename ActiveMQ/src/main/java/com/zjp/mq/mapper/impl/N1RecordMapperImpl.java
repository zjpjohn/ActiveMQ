package com.zjp.mq.mapper.impl;

import com.zjp.mq.entity.N1Record;
import com.zjp.mq.mapper.N1RecordMapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;

/**
 * author:zjprevenge
 * time: 2016/6/28
 * copyright all reserved
 */
@Repository
public class N1RecordMapperImpl implements N1RecordMapper {

    @Resource
    private SqlSession sqlSession;

    /**
     * 根据消息id获取消费记录
     *
     * @param messageId 消息id
     * @return
     */
    public N1Record selectN1Record(String messageId) {
        return sqlSession.selectOne("com.zjp.mq.mapper.N1RecordMapper.selectN1Record", messageId);
    }

    /**
     * 添加消息消费记录
     *
     * @param n1Record 消费记录
     * @return
     */
    public int addN1Record(N1Record n1Record) {
        return sqlSession.insert("com.zjp.mq.mapper.N1RecordMapper.addN1Record",n1Record);
    }

    /**
     * 删除消费记录
     *
     * @param timeStamp 时间戳
     * @return
     */
    public int deleteN1Record(Date timeStamp) {
        return sqlSession.delete("com.zjp.mq.mapper.N1RecordMapper.deleteN1Record",timeStamp);
    }
}
