package com.game.pool;

import java.util.concurrent.ThreadFactory;

/**
 * 游戏线程工厂
 * @author jason.lin
 */
public class GameThreadFactory implements ThreadFactory {
	private String name;
	private int index = 0;
	
	public GameThreadFactory(String name){
		this.name = name;
	}
	
	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		thread.setName("pool-" + name + "-" + index ++);
		return thread;
	}

}
