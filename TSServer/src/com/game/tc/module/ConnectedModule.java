package com.game.tc.module;

import com.alibaba.fastjson.JSON;
import com.game.player.BaseModule;
import com.game.tc.TCClientInfo;
import com.game.ts.net.bean.TSBean;
import com.game.ts.net.consts.TSCCmd;

/**
 * 测试模块
 * @author jason.lin
 *
 */
@SuppressWarnings("serial")
public class ConnectedModule extends BaseModule<TCClientInfo>{

	public ConnectedModule(TCClientInfo player) {
		super(player);
	}

	/**tc 连接ts完成*/
	public void sendConnected(){
		TSBean tsBean=new TSBean();
		tsBean.setTscCmd(TSCCmd.CONNECTED);
		tsBean.setSessionId(player.getId());
		
		String json = JSON.toJSONString(tsBean);
		player.write(json);
	}
}
