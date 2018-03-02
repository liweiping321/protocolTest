package com.game.proxy.module.minaModule;

import org.apache.mina.core.session.IoSession;

import com.game.ts.net.TSMinaMsg;

/**
 * 发包动作
 * @author jason.lin
 *
 */
public interface IPackWriteAction {

	/**
	 * 处理发包逻辑
	 * @param args
	 * @param session
	 * @param request
	 */
	void handleActionWrite(String[] args, IoSession session, TSMinaMsg request);

}
