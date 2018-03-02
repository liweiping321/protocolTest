/**
 * Date: 2013-5-20
 * 
 * Copyright (C) 2013-2015 7Road. All rights reserved.
 */
package com.game.db.database.pool.boncp;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.db.database.pool.IDBPool;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

/**
 * bonecp的连接池
 * 
 * @author jinjin.chen
 */
public class BoneCPDBPool implements IDBPool
{

    private static final Logger LOGGER = LoggerFactory
            .getLogger(BoneCPDBPool.class);

    private BoneCP dbPool;
    private BoneCPConfig config;
    private BoneCPConfiguration configuration;

    public BoneCPDBPool(BoneCPConfiguration configuration)
    {
        this.configuration = configuration;
    }

    @Override
    public Connection getConnection()
    {
        try
        {
            if (dbPool != null)
            {
                return dbPool.getConnection();
            }
        }
        catch (SQLException e)
        {
            LOGGER.error(e.toString());
        }
        return null;
    }

    @Override
    public boolean startup()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            InitConfig();
            dbPool = new BoneCP(config);
            return true;
        }
        catch (Exception e)
        {
            LOGGER.error(e.toString());
        }
        return false;
    }

    /**
     * 
     */
    private void InitConfig()
    {
        config = new BoneCPConfig();
        // BoneCP主要配置参数
        config.setJdbcUrl(configuration.getJdbcUrl());
        config.setPartitionCount(4);
        config.setMinConnectionsPerPartition(2);
        config.setMaxConnectionsPerPartition(2);
        config.setAcquireIncrement(configuration.getAcquireIncrement());
        config.setPoolAvailabilityThreshold(configuration
                .getPoolAvailabilityThreshold());
        config.setConnectionTimeoutInMs(configuration
                .getCloseConnectionWatchTimeoutInMs());// 严重
        // BoneCP线程配置参数
        config.setReleaseHelperThreads(configuration.getReleaseHelperThreads());
        config.setStatementReleaseHelperThreads(configuration
                .getStatementReleaseHelperThreads());
        config.setMaxConnectionAgeInSeconds(configuration
                .getMaxConnectionAgeInSeconds());// 严重
        config.setIdleMaxAgeInMinutes(configuration.getIdleMaxAgeInMinutes());
        config.setIdleConnectionTestPeriodInMinutes(configuration
                .getIdleConnectionTestPeriodInMinutes());
        // BoneCP可选配置参数
        config.setAcquireRetryAttempts(configuration.getAcquireRetryAttempts());
        config.setAcquireRetryDelayInMs(configuration
                .getAcquireRetryDelayDelayInMs());
        config.setLazyInit(configuration.isLazyInit());
        config.setDisableJMX(configuration.isDisableJMX());
        // BoneCP调试配置参数
        config.setCloseConnectionWatch(configuration.isCloseConnectionWatch());
        config.setCloseConnectionWatchTimeoutInMs(configuration
                .getCloseConnectionWatchTimeoutInMs());
        config.setTransactionRecoveryEnabled(configuration
                .getTransactionRecoveryEnabled());
        config.setLogStatementsEnabled(configuration.isLogStatementsEnabled());
        config.setQueryExecuteTimeLimitInMs(configuration
                .getQueryExecuteTimeLimitInMs());
        config.setDisableConnectionTracking(configuration
                .getDisableConnectionTracking());
    }

    @Override
    public void shutdown()
    {
        if (dbPool != null)
        {
            dbPool.close();
        }
    }

    @Override
    public boolean validConn()
    {
        if (dbPool == null || getCurConns() <= 0)
            return false;
        return true;
    }

    @Override
    public String getState()
    {
        return null;
    }

    @Override
    public int getCurConns()
    {
        return dbPool.getTotalCreatedConnections();
    }
}
