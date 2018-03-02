package com.game.scheduler;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.game.pool.ExecutorServicePoolFactory;
import com.game.utils.LogUtils;

/**
 * 定时调度线程池
 * 
 * @author jason.lin
 *
 */
public class SecheduledPool {
	private static ScheduledExecutorService pool = null;

	// 初始化
	public static void init() {
		pool = ExecutorServicePoolFactory.createScheduledExecutorService(1, "Scheduled_Thread");
	}

	public static ScheduledExecutorService getScheduledExecutorService() {
		return pool;
	}

	/**
	 * 开启定时任务
	 * @param command
	 * @param initialDelay
	 * @param period
	 * @param unit
	 */
	public static void scheduleWithFixedDelay(Runnable command, long initialDelay, long period, TimeUnit unit) {
		if(pool == null){
			LogUtils.error("not init pool!");
			return;
		}
		
		pool.scheduleAtFixedRate(command, initialDelay, period, unit);
	}
}
