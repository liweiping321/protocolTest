package com.game.proxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 自驱动线程池
 * @author jason.lin
 *
 */
public class SelfDriverExecutorService {

	private static ExecutorService executor = null;
	
	public static void init(){
		// 调整实际使用的消息处理线程数量
		executor = Executors.newFixedThreadPool(4, new ThreadFactory() {
			private int index = 0;
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
		        thread.setName("pool-SelfDriver-" + index++);
		        return thread;
			}
		});
	}
	
	/**
     * 获取玩家指令专用执行线程池
     * 
     * @return 单列的执行线程池
     */
    public static ExecutorService getExecutorService()
    {
        return executor;
    }
	
}
