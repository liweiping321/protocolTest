package com.game.proxy.module.minaModule;

import com.alibaba.fastjson.JSONObject;
import com.game.protocal.ProtoConfig;
import com.game.ts.net.TSMinaMsg;

/**
 * 加解密接口
 * @author jason.lin
 *
 */
public interface IGameCoder {
	
	/**
	 * 解密
	 * @param config
	 * @param request
	 */
	JSONObject decodeMsg(ProtoConfig config, TSMinaMsg request);

	/**
	 * 加密
	 * @param config
	 * @param json
	 */
	TSMinaMsg encodeMsg(ProtoConfig config, JSONObject json);

}
