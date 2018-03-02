package com.game.net;

import com.alibaba.fastjson.JSON;
import com.game.TCClient;
import com.game.core.net.SessionKey;
import com.game.module.msg.MsgModule;
import com.game.object.TCConnector;
import com.game.ts.net.bean.TSBean;
import com.game.util.LoggerUtil;
import com.game.utils.LogUtils;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteToClosedSessionException;

import java.io.IOException;

/**
 * 服务器to客户端
 * 
 * @author jason.lin
 *
 */
public class MinaHandler implements IoHandler {

	@Override
	public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
		if (!throwable.getClass().equals(IOException.class)
				&& throwable.getClass().getClass().equals(WriteToClosedSessionException.class)) {
			LogUtils.error("", throwable);
		} else {
			inputClosed(session);
		}

		LogUtils.error("===========exceptionCaught！");
	}

	@Override
	public void inputClosed(IoSession session) throws Exception {
		session.setAttribute(SessionKey.ERROR, "client close by self!");
		session.closeNow();

		LogUtils.error("===========inputClosed！");
	}

	@Override
	public void messageReceived(IoSession session, Object paramObject) throws Exception {
		String json = (String) paramObject;
		TSBean tsBean = JSON.parseObject(json, TSBean.class);
		TCClient.connector.getModule(MsgModule.class).handleMsg(tsBean);
	}

	@Override
	public void messageSent(IoSession session, Object paramObject) throws Exception {
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		LoggerUtil.critical(TCClient.clientUI, "与服务器断开连接！");

		if (TCClient.clientUI != null) {
			TCClient.clientUI.getReConnectBtn().setEnabled(true);
		}
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// LogUtils.error("===========连接成功！");

		TCClient.connector = new TCConnector(session);
		LoggerUtil.info(TCClient.clientUI, "连接服务器成功！");
		if (TCClient.clientUI != null) {
			TCClient.clientUI.getReConnectBtn().setEnabled(false);
		}

		// TCClient.connector.getModule(MsgModule.class).holdUpIp("mark_5");
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
