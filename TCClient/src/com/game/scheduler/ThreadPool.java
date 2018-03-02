package com.game.scheduler;

import com.game.utils.LogUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jianpeng.zhang
 * @since 2017/2/24.
 */
public class ThreadPool
{
    private static ExecutorService cacheExecutor;

    // 初始化
    public static void init() {
        cacheExecutor = Executors.newCachedThreadPool();
    }

    public static void executeWithCachePool(Runnable command)
    {
        if(cacheExecutor == null){
            LogUtils.error("not init pool!");
            return;
        }
        cacheExecutor.execute(command);
    }

    private static Object lock = new Object();
    public static void main(String[] args)
    {

        for (int i = 0; i < 10; i++)
        {
            try
            {
                synchronized (lock)
                {
                    lock.wait(2000);
                }

            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
