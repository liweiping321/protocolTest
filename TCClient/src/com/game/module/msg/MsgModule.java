package com.game.module.msg;

import com.alibaba.fastjson.JSONObject;
import com.game.TCClient;
import com.game.object.TCConnector;
import com.game.player.BaseModule;
import com.game.ts.net.bean.TSBean;
import com.game.ts.net.consts.TSCCmd;

/**
 * 消息模块
 * 
 * @author jason.lin
 *
 */
@SuppressWarnings("serial")
public class MsgModule extends BaseModule<TCConnector> {
	public MsgModule(TCConnector player) {
		super(player);
	}

	/**
	 * 处理消息
	 * 
	 * @param tsBean
	 */
	public void handleMsg(TSBean tsBean) {
		// LogUtils.error(String.format("===========messageReceived！%s",
		// player.getGson().toJson(tsBean)));

		// TODO测试修改包
		int tscCmd = tsBean.getTscCmd();
		if (tscCmd == 0) {
			tsBean.setTscCmd(TSCCmd.UPDATE_PACKAGE);
			// JSONObject obj = tsBean.getContent();
			// // LogUtils.error(String.format("===========content！%s", obj));
			// tsBean.setContent(obj);
			TCClient.clientUI.showMessage(tsBean);
			// player.sendMsg(tsBean);
		} else if (TSCCmd.CONNECTED == tsBean.getTscCmd()) {
			TCClient.SSID = tsBean.getSessionId();
		} else if (TSCCmd.SYNC_FLOW_STATISTIC == tsBean.getTscCmd()) {
			TCClient.clientUI.showSyncMessage(tsBean);
		}

	}

	/**
	 * 截断某个ip
	 * 
	 * @param ip
	 */
	public void holdUpIp(String ip) {
		TSBean tsBean = new TSBean();
		tsBean.setTscCmd(TSCCmd.HOUD_UP);

		JSONObject json = new JSONObject();
		json.put("userName", ip);

		tsBean.setContent(json);
		player.sendMsg(tsBean);
	}

}
