package com.zjp.mq.mapper.impl;

import com.zjp.mq.entity.N2Record;
import com.zjp.mq.mapper.N2RecordMapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Map;

/**
 * author:zjprevenge
 * time: 2016/6/28
 * copyright all reserved
 */
@Repository
public class N2RecordMapperImpl implements N2RecordMapper {

    @Resource
    private SqlSession sqlSession;

    /**
     * 根据业务标识查询N2类型的记录
     *
     * @param params 查询参数
     * @return
     */
    public N2Record selectN2RecordByMark(Map<String, Object> params) {
        return sqlSession.selectOne("com.zjp.mq.mapper.N2RecordMapper.selectN2RecordByMark", params);
    }

    /**
     * 根据参数查询N2类型的记录
     *
     * @param params 查询参数
     * @return
     */
    public N2Record selectN2Record(Map<String, Object> params) {
        return sqlSession.selectOne("com.zjp.mq.mapper.N2RecordMapper.selectN2Record", params);
    }

    /**
     * 添加N2类型的记录
     *
     * @param n2Record N2记录
     * @return
     */
    public int addN2Record(N2Record n2Record) {
        return sqlSession.insert("com.zjp.mq.mapper.N2RecordMapper.addN2Record", n2Record);
    }

    /**
     * 更新N2类型的记录
     *
     * @param n2Record N2类型的记录
     * @return
     */
    public int updateN2Record(N2Record n2Record) {
        return sqlSession.update("com.zjp.mq.mapper.N2RecordMapper.updateN2Record", n2Record);
    }
}
