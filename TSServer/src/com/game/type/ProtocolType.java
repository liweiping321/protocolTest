package com.game.type;

/**
 * 协议类型
 * @author jason.lin
 *
 */
public enum ProtocolType {
	/** protobuffer格式 **/
	PB, 
	
	/** 二进制格式(配置在同一个文件) **/
	BIN,
	
	/** 二进制格式（配置文件分开） **/
	BIN_FILE,
	
	/** 空协议体 **/
	NULL,
	;
}
