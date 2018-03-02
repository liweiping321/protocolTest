package com.game.proxy.module.minaModule.actionImpl;

import org.apache.mina.core.session.IoSession;

import com.game.proxy.module.minaModule.IPackWriteAction;
import com.game.ts.net.TSMinaMsg;

/**
 * 延迟发包
 * @author jason.lin
 *
 */
public class CommonWriteAction implements IPackWriteAction{

	@Override
	public void handleActionWrite(String[] args, IoSession session, TSMinaMsg request) {
		session.write(request);
	}

}
