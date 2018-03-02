package com.game.ts.net.factory;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;

import com.alibaba.fastjson.JSONObject;
import com.game.core.net.SessionKey;
import com.game.function.IFunction;
import com.game.ts.net.TSMinaMsg;
import com.game.type.CodeType;
import com.game.type.DirectType;
import com.google.protobuf.GeneratedMessage;

/**
 * ts加密解密工厂
 * @author jason.lin
 *
 */
public abstract class TSMinaFactory implements ProtocolCodecFactory{
	protected IGameKeyUpdateBussiness gameBussiness;
	
	protected List<IFunction<Void>> specialParamList = new ArrayList<IFunction<Void>>();
	
	public void registerSpecialParams()
	{
		
	}
	
	public boolean decodeSpecialPara(TSMinaMsg request, JSONObject json) {
		JSONObject head = new JSONObject();
		for (IFunction<Void> function : specialParamList) {
			 
			function.execute1(request, head);
		}
		json.put("head", head);
		return true;
	}

 
	public boolean encodeSpecialPara(TSMinaMsg msg, JSONObject json) {
		if (!json.containsKey("head")) {
			return false;
		}

		JSONObject head = json.getJSONObject("head");
		for (IFunction<Void> function : specialParamList) {
			function.execute2(msg, head);
		}
		return true;
	}
	/**
	 * 初始化消息(PB)
	 */
	public abstract TSMinaMsg initTSMinaMsgForPB(short code, short subCode, GeneratedMessage builder);
	/**
	 * 初始化消息(二进制)
	 */
	public  TSMinaMsg initTSMinaMsgForBin(short code, short subCode)
	{
		return null;
	}
	/**
	 * 获取登录账号
	 */
	public abstract String getUserName(IoSession session, IoSession client, TSMinaMsg request);
	/**
	 * 同步加解密
	 */
	public  void syncDecodeKey(IoSession session,  int[] keyCache){
		
	}
 

	public IGameKeyUpdateBussiness getGameBussiness() {
		return gameBussiness;
	}

	public void setGameBussiness(IGameKeyUpdateBussiness gameBussiness) {
		this.gameBussiness = gameBussiness;
	}
	
	/**
	 * 更新秘钥
	 * @param directType
	 * @param session
	 * @param code
	 * @param subCode
	 */
	public void updateKey(CodeType codeType, IoSession session, short code, short subCode) {
		// 判定上行、下行
		boolean isPlayerClient = isPlayerClient(session);
		
		// 玩家并且是解码   或者非玩家并且是编码  =>上行
		DirectType directType = (isPlayerClient && codeType == CodeType.DECODE) || 
				(!isPlayerClient && codeType == CodeType.ENCODE) ? DirectType.UP : DirectType.DOWN;
		
		// 检验是否需要更改秘钥
		if(!gameBussiness.needUpdateKey(directType, code, subCode)){
			return;
		}
		
		int[] keys = gameBussiness.getKeysCache(isPlayerClient, session);
		if(keys != null){
			syncDecodeKey(session, keys);
		}
	}
	
	/**
	 * 是否是玩家客户端
	 * @param session
	 */
	public boolean isPlayerClient(IoSession session){
		return session.containsAttribute(SessionKey.PLAYER_CLIENT_MARK);
	}
}
