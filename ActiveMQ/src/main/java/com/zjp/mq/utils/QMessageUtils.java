package com.zjp.mq.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * author:zjprevenge
 * time: 2016/6/28
 * copyright all reserved
 */
public class QMessageUtils {

    // 随机数生成器
    private static Random random = new Random();

    //日期格式化
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    /**
     * 生成消息id
     *
     * @return
     */
    public static String createMessageId(Date date) {
        String format = dateFormat.format(date);
        String mark = Long.toHexString(random.nextLong());
        return format.concat(mark);
    }
}
