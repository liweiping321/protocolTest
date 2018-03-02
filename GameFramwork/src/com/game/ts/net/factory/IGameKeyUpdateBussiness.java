package com.game.ts.net.factory;

import org.apache.mina.core.session.IoSession;

import com.game.type.DirectType;

/**
 * 游戏业务层
 * @author jason.lin
 *
 */
public interface IGameKeyUpdateBussiness {

	/**
	 * 根据客户端获取秘钥
	 * @param isPlayer
	 * @param session
	 * @return
	 */
	int[] getKeysCache(boolean isPlayer, IoSession session);

	/**
	 * 是否需要更改秘钥
	 * @param directType
	 * @param code
	 * @param subCode
	 * @return
	 */
	boolean needUpdateKey(DirectType directType, short code, short subCode);

}
