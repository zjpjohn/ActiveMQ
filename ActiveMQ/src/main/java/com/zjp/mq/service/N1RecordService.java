package com.zjp.mq.service;

import com.zjp.mq.entity.N1Record;

import java.util.Date;

/**
 * author:zjprevenge
 * time: 2016/6/28
 * copyright all reserved
 */
public interface N1RecordService {

    /**
     * 获取N1Record
     *
     * @param messageId
     * @return
     */
    N1Record selectN1Record(String messageId);

    /**
     * 添加N1Record
     *
     * @param n1Record
     * @return
     */
    int addN1Record(N1Record n1Record);

    /**
     * 删除N1Record
     *
     * @param timeStamp
     * @return
     */
    int deleteN1Record(Date timeStamp);
}
