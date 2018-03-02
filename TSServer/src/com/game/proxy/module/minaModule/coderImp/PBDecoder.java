package com.game.proxy.module.minaModule.coderImp;

import com.alibaba.fastjson.JSONObject;
import com.game.TSServer;
import com.game.protocal.ProtoConfig;
import com.game.proxy.module.minaModule.IGameCoder;
import com.game.ts.net.TSMinaMsg;
import com.game.utils.LogUtils;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessage.Builder;
import com.googlecode.protobuf.format.JsonFormat;
import com.googlecode.protobuf.format.JsonFormat.ParseException;

/**
 * protobuffer解码
 * @author jason.lin
 *
 */
public class PBDecoder implements IGameCoder{

	@Override
	public JSONObject decodeMsg(ProtoConfig config, TSMinaMsg request) {
		Class<?> clazz = config.getBodyClass();
		if(clazz == null){
			return null;
		}
		
		String json = null;
		try {
			Object obj = config.getPbInitMethod().invoke(clazz);
			Builder<?> builder = (Builder<?>)obj;
			request.parseParams(builder);
			json = JsonFormat.printToString(builder.build());
			return JSONObject.parseObject(json);
		} catch (Exception e) {
			LogUtils.error("handleMsg:"+json, e);
		}
		
		return null;
	}
	
	@Override
	public TSMinaMsg encodeMsg(ProtoConfig config, JSONObject json) {
		Class<?> clazz = config.getBodyClass();
		
		if(clazz == null){
			return TSServer.getCodecFactory().initTSMinaMsgForBin(config.getCodeShort(), config.getSubCodeShort());
		}
		
		try {
			Object obj = config.getPbInitMethod().invoke(clazz);
			Builder<?> builder = (Builder<?>)obj;
			JsonFormat.merge(json.toJSONString(), builder);
			
			return TSServer.getCodecFactory().initTSMinaMsgForPB(config.getCodeShort(), config.getSubCodeShort(), (GeneratedMessage)builder.build());
		} catch (Exception e) {
			LogUtils.error("handleMsg", e);
		}
		
		return null;
	}

	
	public static void main(String[] args) throws ParseException {
//		JSONObject.parseObject("{\"version\": 2612558,\"desktopType\": 0,\"userId\": 30000000513407,\"src\": \"L3\u0017x\uff80\uff98\uff8aAh\ufff2\u0003\uffd2\uffccJ\uffeax\uffa5\uffeb?\uff9eG\uffc5/^\uff82\uff85d\u000e\ufff71\u001c\u0010\a\uffb92t\uff86\uff97_\uffb5y*1\uff84Y\ufffb\uff904\uffe1\uff80\uffeaLO\uffd2\uffb6\n\uffef\uff84\uffd4\uffad\uffca\uffa1\b\uff9e\uff92\uff80\uff9b\uffd0\uffca$\uffd3r+\uffa6:B\uffaf\uffa7\uffb9\uffe6\u000f\uff99Y\u001f\uffe8\uffcdX\u001a\uffa5\uffc1g\uff9a\uffb9\uffaf\'\uffc6\uffde\ufffb\uffc1a\u000e\ufff04\uffc9\uffc6\uff9d\uffdbC:#\uffdb\uffc4\uff80k\uffa4\uff91\uffe95D\u0013N\uffbe3eB\uffa2\uffd6D\",\"accout\": \"pk001\"}");
		
//		JSONObject json = new JSONObject();
//		json.put("str1", "dddd");
//		json.put("str2", "dddd2");
//		json.put("para1", "dddd");
//		json.put("para2", "dddd");
//		json.put("para3", "dddd");
//		json.put("para4", "dddd");
//		CommomMsg.Builder builder = CommomMsg.newBuilder();
//		JsonFormat.merge(json.toJSONString(), builder);
//		System.err.println(builder.getStr1());
//		System.err.println(builder.getStr2());
		
		
		System.out.println(0xc2);
	}
}
