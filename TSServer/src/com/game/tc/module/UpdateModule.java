package com.game.tc.module;

import com.game.cache.Cache;
import com.game.player.BaseModule;
import com.game.proxy.ConnectProxy;
import com.game.proxy.module.minaModule.MinaModule;
import com.game.tc.TCClientInfo;
import com.game.ts.net.bean.TSBean;
import com.game.utils.LogUtils;

/**
 * 修改包内容
 * @author jason.lin
 *
 */
@SuppressWarnings("serial")
public class UpdateModule extends BaseModule<TCClientInfo>{

	public UpdateModule(TCClientInfo player) {
		super(player);
	}

	/**
	 * 更新包内容
	 * @param paramObject
	 */
	public void updatePackage(TSBean tsBean) {
		ConnectProxy connectProxy = Cache.findConnectProxy(Long.parseLong(tsBean.getSessionId()));
		if(connectProxy!=null){
			if(!connectProxy.getPlayerInfo().getUserName().equals(tsBean.getUserName())){
				  connectProxy = Cache.findConnectProxyByUserName(tsBean.getUserName());
			}
		}
		
		// 先按userName定位
		if(connectProxy==null){
		    connectProxy = Cache.findConnectProxyByUserName(tsBean.getUserName());
		}
		
		if(connectProxy == null){
			// 在按ip定位
			connectProxy = Cache.findConnectProxy(tsBean.getIp());
			if(connectProxy == null){
				LogUtils.error("no found ConnectProxy for ip: " + tsBean.getIp());
				return;
			}
		}
		
		connectProxy.getModule(MinaModule.class).encodeMsg(tsBean);
	}
	
}
