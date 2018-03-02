package com.game.net;

import java.io.IOException;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteToClosedSessionException;

import com.game.TSServer;
import com.game.core.net.SessionKey;
import com.game.proxy.ConnectProxy;
import com.game.proxy.ProxyService;
import com.game.ts.net.TSMinaMsg;
import com.game.type.DirectType;
import com.game.utils.LogUtils;

/**
 * 服务器to客户端
 * 
 * @author jason.lin
 *
 */
public class S2CMinaHandler implements IoHandler {
	private IoSession client;

	public S2CMinaHandler(IoSession session) {
		this.client = session;
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
		if (!throwable.getClass().equals(IOException.class)
				&& throwable.getClass().getClass().equals(WriteToClosedSessionException.class)) {
			LogUtils.error("", throwable);
		} else {
//			inputClosed(session);
		}

		LogUtils.error("===========exceptionCaught！");
	}

	@Override
	public void inputClosed(IoSession session) throws Exception {
		session.setAttribute(SessionKey.ERROR, "client close by self!");
		session.closeNow();

		LogUtils.debug(session.getId() + " ===========inputClosed！");
	}

	@Override
	public void messageReceived(IoSession session, Object paramObject) throws Exception {
		ConnectProxy connectProxy = (ConnectProxy) session.getAttribute(SessionKey.CONNECT_PROXY);
		TSMinaMsg tsMinaMsg= (TSMinaMsg)paramObject;
		connectProxy.addMsg(paramObject, DirectType.DOWN);
		LogUtils.debug(session.getId() + " ===========messageReceived！" + tsMinaMsg.getCode());
	}

	@Override
	public void messageSent(IoSession session, Object paramObject) throws Exception {
		if(!(paramObject instanceof TSMinaMsg)){
			return;
		}
		
		TSMinaMsg msg = (TSMinaMsg)paramObject;
		LogUtils.debug(session.getId() + "===========messageSent！" + msg.getCode());
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		LogUtils.debug(session.getId() + "===========close！");
		ConnectProxy proxy = (ConnectProxy) session.getAttribute(SessionKey.CONNECT_PROXY);
		ProxyService.clearProxy(proxy);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		LogUtils.debug(session.getId() + "===========连接成功！" + client.getId());

		// 创建代理
		ConnectProxy connectProxy = ProxyService.createProxy(client, session);
		session.setAttribute(SessionKey.CONNECT_PROXY, connectProxy);
		session.setAttribute(SessionKey.DECODE_FACTORY, TSServer.getCodecFactory());
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus idleStatus) throws Exception {
		if (session.getIdleCount(idleStatus) > 3) {
			// 发呆时间很久，强制断开
			session.setAttribute(SessionKey.ERROR, "发呆时间很久了，强制断开");
		}
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		LogUtils.info(session.getId() + " Session Opened, IP:  " + session.getRemoteAddress().toString());
	}

}
