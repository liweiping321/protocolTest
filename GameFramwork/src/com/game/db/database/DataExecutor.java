/**
 * Date: 2013-6-6
 * 
 * Copyright (C) 2013-2015 7Road. All rights reserved.
 */

package com.game.db.database;

import java.sql.PreparedStatement;

/**
 * db执行statement接口
 * 
 * @author jinjin.chen
 */
public interface DataExecutor<T>
{
    /**
     * 该方法为泛型方法
     * 
     * @param statement
     *            需要执行的statement
     * @param objects
     *            传入的参数集合
     * @return
     * @throws Exception
     */
    public T execute(PreparedStatement statement, Object... objects)
            throws Exception;
}
