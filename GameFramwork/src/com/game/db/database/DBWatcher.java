/**
 * Date: 2013-5-20
 * 
 * Copyright (C) 2013-2015 7Road. All rights reserved.
 */

package com.game.db.database;

import com.game.utils.LogUtils;

/**
 * dao层数据库执行情况观察对象
 * 
 * @author jinjin.chen
 */
public class DBWatcher {
    private long first = 0;
    private long second = 0;
    private long end = 0;

    public DBWatcher()
    {
        first = System.currentTimeMillis();
    }

    /**
     * 记录获取数据库连接点
     */
    public void watchGetConnector()
    {
        second = System.currentTimeMillis();
    }

    /**
     * 记录数据脚本执行点
     */
    public void watchSqlExec()
    {
        end = System.currentTimeMillis();
    }

    /**
     * 查看执行情况
     * 
     * @param procName
     *            此次执行事务的别名
     */
    public void keeyRecords(String procName)
    {
        long spendTime = end - first;
        if (spendTime > 1000) {
            LogUtils.error(String.format(
                    "执行语句%s花耗时间总时间 超过:%sms,获取连接：%sms,执行sql:%sms", procName,
                    spendTime, second - first, end - second));
        }
    }
}
