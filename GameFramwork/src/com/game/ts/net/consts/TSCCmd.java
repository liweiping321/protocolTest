package com.game.ts.net.consts;

/**
 * TS指令号
 * @author jason.lin
 *
 */
public class TSCCmd {

	/***客户端的包***/
	public static final int CLIENT_MSG = 0;
	
	
	/***监听某个地址***/
	public static final int LISTEN_TO_IP = 1;
	
	/** 拦截 ***/
	public static final int HOUD_UP = 2;

	/***修改包信息***/
	public static final int UPDATE_PACKAGE = 3;
	
	/**连接完成*/
	public static final int CONNECTED = 4;
	
	/**流量统计数据同步*/
	public static final int SYNC_FLOW_STATISTIC = 5;
}
