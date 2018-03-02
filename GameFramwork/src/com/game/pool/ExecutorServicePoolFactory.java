package com.game.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 线程池工厂
 * @author jason.lin
 *
 */
public class ExecutorServicePoolFactory {
	
	/**
	 * 创建线程池
	 * @param poolSize
	 * @param name
	 * @return
	 */
	public static ExecutorService createFixExecutorService(int poolSize, String name){
		return Executors.newFixedThreadPool(poolSize, new GameThreadFactory(name));
	}
	
	/**
	 * 创建定时调度线程池
	 * @param poolSize
	 * @param name
	 * @return
	 */
	public static ScheduledExecutorService createScheduledExecutorService(int poolSize, String name){
		return new ScheduledThreadPoolExecutor(poolSize, new GameThreadFactory(name));
	}
	
}
