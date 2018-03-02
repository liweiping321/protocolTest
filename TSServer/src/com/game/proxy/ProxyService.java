package com.game.proxy;

import org.apache.mina.core.session.IoSession;

import com.game.cache.Cache;

/**
 * 代理业务逻辑
 * @author jason.lin
 *
 */
public class ProxyService {

	/**
	 * 创建链接代理
	 * @param session
	 * @param client
	 */
	public static ConnectProxy createProxy(IoSession session, IoSession client) {
		// 匹配方
		ConnectProxy mapping = Cache.getConnectProxy(session);
		if (mapping != null) {
			mapping.setClient(client);
		} else {
			mapping = new ConnectProxy(session, client);
			Cache.addConnectProxy(mapping);
		}

		return mapping;
	}

	/**
	 * 清理代理
	 * @param session
	 */
	public static void clearProxy(ConnectProxy connectProxy) {
		// 移除缓存
		IoSession other = connectProxy.getClient();
		Cache.remove(connectProxy.getSession());
		
		connectProxy.removeAllTcClientInfo();
		
		// 关闭session
		if(connectProxy.getSession() != null){
			connectProxy.getSession().closeNow();
		}
		if(other != null){
			other.closeNow();
		}
	}

}
