package com.game.net;

import java.io.IOException;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteToClosedSessionException;

import com.game.cache.Cache;
import com.game.core.net.SessionKey;
import com.game.tc.TCClientInfo;
import com.game.tc.TCService;
import com.game.tc.module.ConnectedModule;
import com.game.utils.LogUtils;

/**
 * 客户端to服务器
 * @author jason.lin
 *
 */
public class TCMinaHandler implements IoHandler {
	
	@Override
	public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
		if (!throwable.getClass().equals(IOException.class) && throwable.getClass().getClass().equals(WriteToClosedSessionException.class)) {
			LogUtils.error("", throwable);
		} else {
			LogUtils.error("", throwable);
//			inputClosed(session);
		}
	}

	@Override
	public void inputClosed(IoSession session) throws Exception {
		session.closeNow();
	}
	

	@Override
	public void messageReceived(IoSession session, Object paramObject) throws Exception {
		LogUtils.debug("===========messageReceived！");
		
		TCClientInfo tcClientInfo = Cache.getTCClientInfo(session);
		TCService.handleMsg(tcClientInfo, paramObject);
	}

	@Override
	public void messageSent(IoSession session, Object paramObject) throws Exception {
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		TCClientInfo tcClientInfo = Cache.getTCClientInfo(session);
		if(tcClientInfo != null){
			tcClientInfo.removeAllConnectProxy();
			
			Cache.removeTcClient(session);
			
			LogUtils.debug("===========sessionClosed！");
		}
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		LogUtils.info("Session Created, IP:  " + session.getRemoteAddress().toString());

		// 添加tc客户端
		TCClientInfo tcClientInfo = TCService.addTCClient(session); 
		
		//发送TC连接完成消息
		tcClientInfo.getModule(ConnectedModule.class).sendConnected();
		
////		//TODO 测试发包
//		tcClientInfo.getModule(TestModule.class).testSendPackage();
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
		LogUtils.info("Session Opened, IP:  " + session.getRemoteAddress().toString());
	}

}
