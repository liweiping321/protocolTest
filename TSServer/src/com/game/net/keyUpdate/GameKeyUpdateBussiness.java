package com.game.net.keyUpdate;

import org.apache.mina.core.session.IoSession;

import com.game.cache.Cache;
import com.game.core.net.SessionKey;
import com.game.protocal.ProtoConfig;
import com.game.proxy.ConnectProxy;
import com.game.ts.net.factory.IGameKeyUpdateBussiness;
import com.game.type.DirectType;

public class GameKeyUpdateBussiness implements IGameKeyUpdateBussiness{

	@Override
	public int[] getKeysCache(boolean isPlayer, IoSession session) {
		ConnectProxy proxy = null;
		if(isPlayer){
			proxy = Cache.getConnectProxy(session);
		}else {
			proxy = (ConnectProxy) session.getAttribute(SessionKey.CONNECT_PROXY);
		}
		
		if(!proxy.getSession().containsAttribute(SessionKey.KEY_CACEH)){
			return null;
		}
		
		return (int[])proxy.getSession().getAttribute(SessionKey.KEY_CACEH);
	}

	@Override
	public boolean needUpdateKey(DirectType directType, short code, short subCode) {
		ProtoConfig config = Cache.getProtoConfig(directType, code, subCode);
		if (config != null && config.getUpdateKey()) {
			return true;
		}
		
		return false;
	}

}
