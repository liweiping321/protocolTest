package com.game.db.database.pool;

/**
 * Date: 2013-5-20
 *
 * Copyright (C) 2013-2015 7Road. All rights reserved.
 */
import java.sql.Connection;

/**
 *
 * @author jinjin.chen
 */
public interface IDBPool
{

    /**
     * @return 数据连接
     */
    Connection getConnection();

    /**
     * 启动连接池
     *
     * @return 成功返回true，失败返回false<br>
     *         可能在加载数据源的配置的时候出错
     */
    boolean startup();

    void shutdown();

    boolean validConn();

    /**
     * 获取当前连接池的状态
     *
     * @格式为 [ total:i, used:j, free:g ]
     *
     * @return 返回连接池的状态
     */
    String getState();

    /**
     *
     * @return 返回当前连接池的连接数
     */
    int getCurConns();
}
