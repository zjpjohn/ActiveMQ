package com.zjp.mq.service.impl;

import com.zjp.mq.entity.N2Record;
import com.zjp.mq.mapper.N2RecordMapper;
import com.zjp.mq.service.N2RecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

/**
 * author:zjprevenge
 * time: 2016/6/28
 * copyright all reserved
 */
@Service("N2RecordService")
public class N2RecordServiceImpl implements N2RecordService {

    @Resource
    private N2RecordMapper n2RecordMapper;

    /**
     * 根据业务标识查询N2类型的记录
     *
     * @param params 参数
     * @return
     */
    public N2Record selectN2RecordByMark(Map<String, Object> params) {
        if (params == null || params.size() == 0) {
            return null;
        }
        return n2RecordMapper.selectN2RecordByMark(params);
    }

    /**
     * 根据参数查询N2类型的记录
     *
     * @param params 查询参数
     * @return
     */
    public N2Record selectN2Record(Map<String, Object> params) {
        if (params == null || params.size() == 0) {
            return null;
        }
        return n2RecordMapper.selectN2Record(params);
    }

    /**
     * 添加N2类型的记录
     *
     * @param n2Record N2记录
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public int addN2Record(N2Record n2Record) {
        if (n2Record == null) {
            return 0;
        }
        return n2RecordMapper.addN2Record(n2Record);
    }

    /**
     * 更新N2类型的记录
     *
     * @param n2Record N2类型的记录
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public int updateN2Record(N2Record n2Record) {
        if (n2Record == null) {
            return 0;
        }
        return n2RecordMapper.updateN2Record(n2Record);
    }
}
