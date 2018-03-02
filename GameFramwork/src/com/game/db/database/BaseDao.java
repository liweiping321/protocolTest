/**
 * Date: 2013-5-20
 * 
 * Copyright (C) 2013-2015 7Road. All rights reserved.
 */
package com.game.db.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.db.database.pool.DBHelper;

/**
 * IBaseDao中接口的实现类
 * 
 * @author jinjin.chen
 */
public abstract class BaseDao<T>
{
    /**
     * 数据库Helper
     */
    protected DBHelper dbhelper = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDao.class);

    public BaseDao(DBHelper helper)
    {
        setDBHelper(helper);
    }

    public void setDBHelper(DBHelper helper)
    {
        dbhelper = helper;
    }

    protected DBHelper getDBHelper()
    {
        return dbhelper;
    }

    /**
     * 根据脚本执行更新
     * 
     * @param sql
     *            查询的脚本
     * @param paramWrapper
     *            参数
     * @return
     */
    public boolean update(String sql, DBParamWrapper paramWrapper)
    {
        boolean result = false;
        result = getDBHelper().execNoneQuery(sql, paramWrapper) > -1 ? true
                : false;
        return result;
    }

    // public void addBatch(DataSaveInfo<T> dataSaveInfo, Connection conn)
    // throws SQLException
    // {
    //
    // }
    //
    // public void updateBatch(DataSaveInfo<T> dataSaveInfo, Connection conn)
    // throws SQLException
    // {
    //
    // }

    public void close(Statement statement) throws SQLException
    {
        statement.clearBatch();
        statement.close();
        statement = null;
    }

    /**
     * 根据脚本执行查询操作，不带参数
     * 
     * @param sql查询的脚本
     * @return
     */
    public T query(String sql)
    {
        return query(sql, null);
    }

    /**
     * 根据脚本执行查询操作，带参数
     * 
     * @param sql
     *            查询的脚本
     * @param paramWrapper
     *            参数
     * @return
     */
    public T query(String sql, DBParamWrapper paramWrapper)
    {
        T result = getDBHelper().executeQuery(sql, paramWrapper,
                new DataReader<T>()
                {
                    @Override
                    public T readData(ResultSet rs, Object... objects)
                            throws Exception
                    {
                        if (rs.last())
                        {
                            return BaseDao.this.rsToEntity(rs);
                        }
                        return null;
                    }
                });
        return result;
    }

    /**
     * 根据脚本执行查询操作,不带参数
     * 
     * @param sql
     *            查询的脚本
     * @return
     */
    public List<T> queryList(String sql)
    {
        return queryList(sql, null);
    }

    /**
     * 根据脚本执行查询操作
     * 
     * @param sql
     *            sql 查询的脚本
     * @param paramWrapper
     *            参数
     * @return 返回查询结果对象集合
     */
    public List<T> queryList(String sql, DBParamWrapper paramWrapper)
    {
        List<T> entitis = getDBHelper().executeQuery(sql, paramWrapper,
                new DataReader<List<T>>()
                {

                    @Override
                    public List<T> readData(ResultSet rs, Object... objects)
                            throws Exception
                    {
                        return BaseDao.this.rsToEntityList(rs);
                    }
                });
        return entitis;
    }

    /**
     * 根据脚本查询实体，返回key字段作为hash的Map
     * 
     * @param sql
     *            脚本
     * @param paramWrapper
     *            参数
     * @param key
     *            列名，它的值作为哈希的key值<br>
     *            <strong><i>如果key只有一个的时候，返回的泛型可以是任意类型，但是key传多个进行的时候，
     *            返回的泛型只能是String<i><strong>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <K> Map<K, T> queryMap(String sql, DBParamWrapper paramWrapper,
            Object... key)
    {
        Map<K, T> resultMap = getDBHelper().executeQuery(sql, paramWrapper,
                new DataReader<Map<K, T>>()
                {
                    @Override
                    public Map<K, T> readData(ResultSet rs, Object... objects)
                            throws Exception
                    {
                        Map<K, T> resultMap = new HashMap<K, T>();
                        while (rs.next())
                        {
                            if (objects.length > 1)
                            {
                                String hashKey = "";
                                for (Object string : objects)
                                {
                                    hashKey += rs.getObject((String) string)
                                            + "_";
                                }
                                hashKey = hashKey.substring(0,
                                        hashKey.length() - 1);
                                resultMap.put((K) hashKey, rsToEntity(rs));
                            }
                            else if (objects.length == 1)
                            {
                                resultMap.put(
                                        (K) rs.getObject((String) objects[0]),
                                        rsToEntity(rs));
                            }
                        }
                        return resultMap;
                    }
                }, key);

        return resultMap;
    }

    /**
     * 将ResultSet转换成List
     * 
     * @param rs
     * @return
     */
    protected List<T> rsToEntityList(ResultSet rs)
    {
        List<T> entitis = new ArrayList<T>();
        if (rs != null)
        {
            try
            {
                while (rs.next())
                {
                    T entity = rsToEntity(rs);
                    entitis.add(entity);
                }
            }
            catch (Exception e)
            {
                LOGGER.error("Resultset转成实体出错", e);
            }
        }
        return entitis;
    }

    /**
     * 将resultset转为实体对象
     * 
     * @param rs
     * @return
     * @throws SQLException
     */
    protected abstract T rsToEntity(ResultSet rs) throws SQLException;

}
