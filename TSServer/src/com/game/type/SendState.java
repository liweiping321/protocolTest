package com.game.type;
/**
 * TS往TC客户端发包状态
 * @author lip.li
 *
 */
public enum SendState {
	start(),//往TC发包
	pause(),//暂停往TC发包
	stop();//停止往TC发包
}
