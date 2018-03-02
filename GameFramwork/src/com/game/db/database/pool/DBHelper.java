/**
 * Date: 2013-5-20
 *
 * Copyright (C) 2013-2015 7Road. All rights reserved.
 */
package com.game.db.database.pool;

import com.game.db.database.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jinjin.chen
 */
public class DBHelper
{
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DBHelper.class);

    private IDBPool pool;

    public DBHelper(IDBPool pool)
    {
        this.pool = pool;
    }

    /**
     * @Action INSERT, UPDATE or DELETE
     * @param sql
     *            执行的脚本
     * @return
     */
    public int execNoneQuery(String sql)
    {
        return execNoneQuery(sql, null);
    }

    /**
     * @Action INSERT, UPDATE or DELETE
     * @param sql
     *            执行的脚本
     * @param params
     *            脚本参数
     * @return
     */
    public int execNoneQuery(String sql, DBParamWrapper params)
    {
        int result = 0;
        DBWatcher watcher = new DBWatcher();
        Connection conn = openConn();
        watcher.watchGetConnector();
        if (conn == null)
        {
            LOGGER.error("获取数据库链接失败 !!!!");
            return result;
        }
        PreparedStatement pstmt = null;
        try
        {
            pstmt = conn.prepareStatement(sql);
            prepareCommand(pstmt, getParams(params));
            result = pstmt.executeUpdate();
            watcher.watchSqlExec();
        }
        catch (SQLException e)
        {
            LOGGER.error("执行脚本出错" + sql, e);
        }
        finally
        {
            closeConn(conn, pstmt);
            watcher.keeyRecords(sql);
        }
        return result;
    }

    /**
     * 执行无参查询并返回单一记录
     *
     * @param sql
     *            执行的脚本
     * @param reader
     *            记录读取接口，实现单一记录读取过程
     * @return
     */
    public <T> T executeQuery(String sql, DataReader<T> reader)
    {
        return executeQuery(sql, null, reader);
    }

    /**
     * 调用存储过程(不支持返回多列 暂时无返回多列的需求)
     *
     * @param sql
     * @param params
     * @param reader
     * @param objects
     * @return
     */
    public <T> T callProcedure(String sql, DBParamWrapper params,
            DataReader<T> reader)
    {
        DBWatcher watcher = new DBWatcher();
        CallableStatement pstmt = null;
        ResultSet rs = null;
        T resultData = null;
        Connection conn = openConn();

        if (conn != null)
        {
            try
            {
                pstmt = conn.prepareCall(sql);
                prepareCommand(pstmt, params.getParams());
                prepareOutCommand(pstmt, params.getOutParams());
                pstmt.execute();
                resultData = reader.readData(null, pstmt);
            }
            catch (Exception e)
            {
                LOGGER.error("执行sp脚本出错", e);

            }
            finally
            {
                closeConn(conn, pstmt, rs);
                watcher.keeyRecords(sql);
            }
        }
        else
        {
            LOGGER.error("获取数据库链接失败 !!!!");
        }
        return resultData;
    }

    /**
     * 执行查询并返回单一记录
     *
     * @param sql
     *            执行的脚本
     * @param params
     *            脚本参数
     * @param reader
     *            记录读取接口，实现单一记录读取过程
     * @param objects
     *            额外传递的参数
     * @return
     */
    public <T> T executeQuery(String sql, DBParamWrapper params,
            DataReader<T> reader, Object... objects)
    {
        DBWatcher watcher = new DBWatcher();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        T resultData = null;
        Connection conn = openConn();

        if (conn != null)
        {
            try
            {
                pstmt = conn.prepareStatement(sql);
                prepareCommand(pstmt, getParams(params));
                rs = pstmt.executeQuery();
                resultData = reader.readData(rs, objects);
            }
            catch (Exception e)
            {
                LOGGER.error("执行脚本出错", e);

            }
            finally
            {
                closeConn(conn, pstmt, rs);
                watcher.keeyRecords(sql);
            }
        }
        else
            LOGGER.error("获取数据库链接失败 !!!!");

        return resultData;
    }

    /**
     * 执行查询并返回单一记录
     *
     * @param sql
     *            执行的脚本
     * @param executor
     *            statement执行接口，实现单一记录读取过程
     * @return
     */
    public <T> T executeQuery(String sql, DataExecutor<T> executor)
    {
        return executeQuery(sql, null, executor);
    }

    /**
     * 执行查询并返回单一记录
     *
     * @param sql
     *            执行的脚本
     * @param params
     *            脚本参数
     * @param executor
     *            statement执行接口，实现单一记录读取过程
     * @param objects
     *            额外传递的参数
     * @return
     */
    public <T> T executeQuery(String sql, DBParamWrapper params,
            DataExecutor<T> executor, Object... objects)
    {
        DBWatcher watcher = new DBWatcher();
        PreparedStatement pstmt = null;
        T resultData = null;
        Connection conn = openConn();

        if (conn != null)
        {
            try
            {
                pstmt = conn.prepareStatement(sql);
                prepareCommand(pstmt, getParams(params));
                resultData = executor.execute(pstmt, objects);
            }
            catch (Exception e)
            {
                LOGGER.error("执行脚本出错", e);

            }
            finally
            {
                closeConn(conn, pstmt);
                watcher.keeyRecords(sql);
            }
        }
        else
        {
            LOGGER.error("获取数据库链接失败 !!!!");
        }

        return resultData;
    }

    /**
     *
     * @param sql
     *            执行批量处理的脚本
     * @param entities
     *            实体集合
     * @param executor
     *            回调
     * @return
     */
    public <T, V> T sqlBatch(String sql, Collection<V> entities,
            DataExecutor<T> executor)
    {
        DBWatcher watcher = new DBWatcher();
        Connection conn = openConn();
        watcher.watchGetConnector();
        if (conn == null)
        {
            LOGGER.error("获取数据库链接失败 !!!!");
            return null;
        }
        PreparedStatement pstmt = null;
        try
        {
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
                                          ResultSet.CONCUR_READ_ONLY);
            T result = executor.execute(pstmt, entities);
            conn.commit();
            return result;

        }
        catch (SQLException e)
        {
            LOGGER.error("执行批量脚本出错", e);
        }
        catch (Exception e)
        {
            LOGGER.error("执行批量脚本出错", e);
        }
        finally
        {

            closeConn(conn, pstmt);
            watcher.keeyRecords(sql);
        }
        return null;
    }

    public void sqlBatch(Collection<DataSaveInfo<?>> entities)
    {
        try
        {
            // conn.setAutoCommit(false);
            for (DataSaveInfo<?> info : entities)
            {
                info.prepareStatement();
            }
            // conn.commit();
        }
        catch (Exception e)
        {
            LOGGER.error("执行批量脚本出错", e);
        }
    }

    /**
     * 批量更新
     *
     * @param list
     * @param claz
     */
    public <T> void sqlBatch(List<T> list, Class<T> claz, short op)
    {
        if (list == null || list.size() <= 0)
            return;

        try
        {
            DataSaveInfo<T> dataSaveInfo = new DataSaveInfo<T>(claz);
            dataSaveInfo.setOp(op);
            dataSaveInfo.getSet().addAll(list);
            long start = System.currentTimeMillis();
            dataSaveInfo.prepareStatement();
            long cost = System.currentTimeMillis() - start;
            // if (cost > 3000)
            // LOGGER.error("插入日志耗时：" + cost);
            LOGGER.error(claz + "----插入日志: " + list.size() + "  耗时：" + cost);
        }
        catch (Exception e)
        {
            LOGGER.error("执行批量脚本出错", e);
        }
    }

    /**
     * @Action INSERT_Batch, UPDATE_Batch or DELETE_Batch
     * @param sqlComm
     *            执行的脚本
     * @return 返回影响行数
     */
    public int sqlBatch(List<String> sqlComm)
    {
        int[] results = null;
        int result = 0;
        Connection conn = openConn();

        if (conn == null)
        {
            LOGGER.error("获取数据库链接失败 !!!!");
            return result;
        }
        Statement stmt = null;
        try
        {
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            for (int i = 0; i < sqlComm.size(); i++)
            {
                stmt.addBatch(sqlComm.get(i));
            }
            results = stmt.executeBatch();
            conn.commit();
        }
        catch (SQLException e)
        {
            LOGGER.error("执行批量脚本出错", e);
        }
        finally
        {
            closeConn(conn, stmt);
        }
        if (results != null)
        {
            for (int i = 0; i < results.length; i++)
            {
                result += results[i];
            }
        }
        return result;
    }

    /**
     * 给Statement赋值
     *
     * @param pstmt
     * @param parms
     * @throws SQLException
     */
    public PreparedStatement prepareCommand(PreparedStatement pstmt,
            Map<Integer, DBParameter> parms) throws SQLException
    {
        if (pstmt == null || parms == null)
            return null;
        for (Map.Entry<Integer, DBParameter> entry : parms.entrySet())
        {
            pstmt.setObject(entry.getKey(), entry.getValue().getResult());
        }

        return pstmt;
    }

    /**
     * 给Statement赋值
     *
     * @param pstmt
     * @param parms
     * @throws SQLException
     */
    public PreparedStatement prepareOutCommand(CallableStatement pstmt,
            Map<Integer, DBParameter> parms) throws SQLException
    {
        if (pstmt == null || parms == null)
            return null;
        for (Map.Entry<Integer, DBParameter> entry : parms.entrySet())
        {
            pstmt.registerOutParameter(entry.getKey(), entry.getValue()
                    .getDbtype());
        }

        return pstmt;
    }

    /**
     * 获取连接池的连接
     *
     * @return
     */
    public Connection openConn()
    {
        return pool.getConnection();
    }

    private Map<Integer, DBParameter> getParams(DBParamWrapper paramWrapper)
    {
        Map<Integer, DBParameter> params = null;
        if (paramWrapper != null)
        {
            params = paramWrapper.getParams();
        }
        return params;
    }

    /**
     * 关闭Connection、Statem和ResultSet
     *
     * @param conn
     * @param stmt
     * @param rs
     */
    private void closeConn(Connection conn, Statement stmt, ResultSet rs)
    {
        try
        {
            if (rs != null && rs.isClosed() == false)
            {
                rs.close();
                rs = null;
            }
        }
        catch (SQLException e)
        {
            LOGGER.error("关闭Resultset出错", e);
        }
        finally
        {
            closeConn(conn, stmt);
        }

    }

    /**
     * 关闭Conne和Statement
     *
     * @param conn
     * @param stmt
     */
    private void closeConn(Connection conn, Statement stmt)
    {
        try
        {
            if (stmt != null && stmt.isClosed() == false)
            {
                if (stmt instanceof PreparedStatement)
                {

                    ((PreparedStatement) stmt).clearParameters();
                }
                stmt.close();
                stmt = null;
            }
        }
        catch (SQLException e)
        {
            LOGGER.error("关闭statement出错", e);
        }
        finally
        {
            closeConn(conn);
        }
    }

    /**
     * 关闭Connection
     *
     * @param conn
     */
    private void closeConn(Connection conn)
    {

        try
        {
            if (conn != null && conn.isClosed() == false)
            {
                conn.setAutoCommit(true);
                conn.close();
                conn = null;
            }
        }
        catch (SQLException e)
        {
            LOGGER.error("关闭数据库连接出错", e);
        }
    }
}
