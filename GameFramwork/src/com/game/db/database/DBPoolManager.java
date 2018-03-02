/**
 * Date: 2013-5-20
 * 
 * Copyright (C) 2013-2015 7Road. All rights reserved.
 */
package com.game.db.database;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Element;

import com.game.db.database.pool.DBHelper;
import com.game.db.database.pool.IDBPool;
import com.game.db.database.pool.boncp.BoneCPConfiguration;
import com.game.db.database.pool.boncp.BoneCPDBPool;
import com.game.utils.LogUtils;

/**
 * 数据库连接池管理类
 * 
 * @author jinjin.chen
 */
public final class DBPoolManager
{
	// pools保存所有的连接池的信息 key对应是这个连接池的名字
    private static Map<String, IDBPool> pools = new ConcurrentHashMap<String, IDBPool>();
    private static Map<String, DBHelper> dbHelpers = new ConcurrentHashMap<String, DBHelper>();

    public static final String NAME = "DBPoolManagerComponent";

    private static class LazyHolder
    {
        private static final DBPoolManager INSTANCE = new DBPoolManager();
    }

    /**
     * 获取实例
     * 
     * @return
     */
    public static DBPoolManager getInstance()
    {
        return LazyHolder.INSTANCE;
    }

    /**
     * 启动连接池
     * 
     * @param pool
     */
    private boolean startupPool()
    {
        synchronized (pools)
        {
            Set<Entry<String, IDBPool>> entries = pools.entrySet();
            for (Entry<String, IDBPool> entry : entries)
            {
                if (!entry.getValue().startup())
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 重新启动连接池
     */
    public void restartup()
    {
        synchronized (pools)
        {
            Set<Entry<String, IDBPool>> entries = pools.entrySet();
            for (Entry<String, IDBPool> entry : entries)
            {
                if (!entry.getValue().validConn())
                {
                    entry.getValue().startup();
                }
            }
        }
    }

    /**
     * 关闭所有连接池
     */
    private void shutdown()
    {
        synchronized (pools)
        {
            Set<Entry<String, IDBPool>> entries = pools.entrySet();
            for (Entry<String, IDBPool> entry : entries)
            {
                entry.getValue().shutdown();
            }
        }
    }

    /**
     * 添加连接池，并启动连接池
     * 
     * @param dbName
     * @param pool
     */
    public void addDBPool(String dbName, IDBPool pool)
    {
        synchronized (pools)
        {
            IDBPool temp = pools.get(dbName);
            if (temp != null)
            {
                throw new RuntimeException("Already exit the dbPool!");
            }
            else
            {
                pools.put(dbName, pool);
            }
        }

    }

    /**
     * 添加Hepler
     * 
     * @param dbName
     * @param dbHelper
     */
    public void addDBHelper(String dbName, DBHelper dbHelper)
    {
        synchronized (dbHelpers)
        {
            DBHelper temp = dbHelpers.get(dbName);
            if (temp != null)
            {
                throw new RuntimeException("Already exit the dbHelper!");
            }
            else
            {
                dbHelpers.put(dbName, dbHelper);
            }
        }
    }

    /**
     * 根据XML解析数据库连接池
     * 
     * @param element
     *            传进来xml的database节点
     * 
     * @xml文件的格式应该如下：<br>
     */
    public boolean initWithDbXML(Element element)
    {
        try
        {
            List<?> dbs = element.elements("pool");
            for (int i = 0; i < dbs.size(); i++)
            {
                Element db = (Element) dbs.get(i);// 获取DB的节点，可以有多个DB节点
                List<?> dbConfigs = db.elements();// 获取每个DB节点下面的具体数据库的配置文件
                for (Object object : dbConfigs)
                {
                    Element dbConfig = (Element) object;
                    String dbName = dbConfig.attributeValue("name");
                    BoneCPConfiguration configuration = new BoneCPConfiguration(
                            dbConfig);
                    // TODO 需要修改，弄一个工厂，根据传入的字段判断生产什么连接池
                    BoneCPDBPool boneCPDBPool = new BoneCPDBPool(configuration);
                    DBHelper dbHelper = new DBHelper(boneCPDBPool);
                    addDBPool(dbName, boneCPDBPool);
                    addDBHelper(dbName, dbHelper);
                }
            }
            return true;
        }
        catch (Exception e)
        {
            LogUtils.error("初始化数据库配置文件出错", e);
        }
        return false;

    }

    public IDBPool getDBPool(String dbName)
    {
        return pools.get(dbName);
    }

    public static DBHelper getDBHelper(String dbName)
    {
        return dbHelpers.get(dbName);
    }

    public boolean start()
    {
        startupPool();
        return true;
    }

    public void stop()
    {
        shutdown();
    }

}
