/*
 * DataReader
 * 
 * 2012-12-27
 * 
 * All rights reserved. This material is confidential and proprietary to 7ROAD
 */
package com.game.db.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * db查询结果集数据读取接口
 * 
 * @author sky
 * 
 */
public interface DataReader<T>
{
    /**
     * 从查询结果集中读取一条记录并返回，本接口为泛型接口，如需读取的数据无明显类型特征（如抽取不规则字段集等），可直接使用Object类型进行读取
     * 
     * @param rs
     * @return
     * @throws SQLException
     */
    public T readData(ResultSet rs, Object... objects) throws Exception;
}
