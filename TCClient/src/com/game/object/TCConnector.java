package com.game.object;

import org.apache.mina.core.session.IoSession;

import com.alibaba.fastjson.JSON;
import com.game.module.msg.MsgModule;
import com.game.player.obj.BaseOnlinePlayer;
import com.game.ts.net.bean.TSBean;

@SuppressWarnings("serial")
public class TCConnector extends BaseOnlinePlayer {
	private IoSession session;

	public TCConnector(IoSession session) {
		this.session = session;
		initModules();
	}

	@Override
	public void initModules() {
		addModule(new MsgModule(this));
	}

	public void sendMsg(TSBean tsBean) {
		session.write(JSON.toJSONString(tsBean));
	}

	@Override
	public IoSession getSession() {
		return session;
	}
}
