package com.game.Data;

/**
 * @author jianpeng.zhang
 * @since 2017/1/13.
 */
public enum LogLevel {
	CRITICAL(5), ERROR(4), WARNING(3), INFO(2), DEBUG(1);

	private int type;

	LogLevel(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
}
