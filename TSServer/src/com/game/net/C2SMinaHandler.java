package com.game.net;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteToClosedSessionException;

import com.game.TSServer;
import com.game.cache.Cache;
import com.game.config.ServerConfig;
import com.game.core.net.ClientConnector;
import com.game.core.net.SessionKey;
import com.game.proxy.ConnectProxy;
import com.game.proxy.ProxyService;
import com.game.ts.net.TSMinaMsg;
import com.game.type.DirectType;
import com.game.utils.LogUtils;

/**
 * 客户端to服务器
 * @author jason.lin
 *
 */
public class C2SMinaHandler implements IoHandler {
	
	@Override
	public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
		if (!throwable.getClass().equals(IOException.class) && throwable.getClass().getClass().equals(WriteToClosedSessionException.class)) {
			LogUtils.error("", throwable);
		} else {
			inputClosed(session);
		}
	}

	@Override
	public void inputClosed(IoSession session) throws Exception {
		session.closeNow();
	}

	@Override
	public void messageReceived(IoSession session, Object paramObject) throws Exception {
		ConnectProxy proxy = Cache.getConnectProxy(session);
		proxy.addMsg(paramObject, DirectType.UP);
		LogUtils.debug("===========messageReceived！");
	}

	@Override
	public void messageSent(IoSession session, Object paramObject) throws Exception {
		if(!(paramObject instanceof TSMinaMsg)){
			return;
		}
		
		TSMinaMsg tsMinaMsg= (TSMinaMsg)paramObject;
		LogUtils.debug("=========messageSent！" + tsMinaMsg.getCode());
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// 清理连接映射
		ConnectProxy proxy = Cache.getConnectProxy(session);
		ProxyService.clearProxy(proxy);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		LogUtils.error(session.getId() + "Session Created, IP:  " + session.getRemoteAddress().toString());
		// 设置ip
		String ip = ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();
		session.setAttribute(SessionKey.CLIENT_IP, ip);
		session.setAttribute(SessionKey.PLAYER_CLIENT_MARK, true);
		
		// 创建代理
        ProxyService.createProxy(session, null);
        session.setAttribute(SessionKey.DECODE_FACTORY, TSServer.getCodecFactory());
        
		// 连接服务器
		new ClientConnector(ServerConfig.getGameServerIp(), ServerConfig.getGameServerPort(), TSServer.getCodecFactory(), new S2CMinaHandler(session));
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus idleStatus) throws Exception {
//		LogUtils.error("发呆时间很久了，强制断开");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		LogUtils.debug(session.getId() + " Session Opened, IP:  " + session.getRemoteAddress().toString());
	}

}
