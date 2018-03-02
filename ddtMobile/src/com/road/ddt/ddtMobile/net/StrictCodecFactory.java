package com.road.ddt.ddtMobile.net;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;

import com.alibaba.fastjson.JSONObject;
import com.game.function.IFunction;
import com.game.ts.net.TSMinaMsg;
import com.game.ts.net.factory.TSMinaFactory;
import com.game.utils.LogUtils;
import com.google.protobuf.GeneratedMessage;
import com.road.ddt.command.UserCmdInType;
import com.road.ddt.gen.ChannelHostInMsg.AuditInfoInProto;
import com.road.ddt.gen.UserLoginMsg.UserLoginProto;

/**
 * @author lucas.lu 客户端加解密处理
 */
public class StrictCodecFactory extends TSMinaFactory {
	private static ProtocolEncoderAdapter ENCODER_INSTANCE = new StrictMessageEncoder();

	private static CumulativeProtocolDecoder DECODER_INSTANCE = new StrictMessageDecoder();

	public StrictCodecFactory() {
		registerSpecialParams();
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession paramIoSession)
			throws Exception {
		return ENCODER_INSTANCE;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession paramIoSession)
			throws Exception {
		return DECODER_INSTANCE;
	}

	@Override
	public TSMinaMsg initTSMinaMsgForPB(short code, short subCode,
			GeneratedMessage builder) {

		return new CommonMessage(code, builder);
	}

	@Override
	public String getUserName(IoSession session, IoSession client,
			TSMinaMsg request) {

		try {

			if (request.getCode() ==UserCmdInType.USER_LOGIN) {
				UserLoginProto req = UserLoginProto.parseFrom(request.getBytes());
				return req.getUserName();
			}
			if(request.getCode()==UserCmdInType.AUDIT_INFO||
			  request.getCode()==UserCmdInType.USER_ACCOUNT_LIST||
//			  request.getCode()==UserCmdInType.CLIENT_DOWNLOAD_PROGRESS||
			  request.getCode()==UserCmdInType.USER_CHANNEL||
			  request.getCode()==UserCmdInType.USER_PING||
			  request.getCode()==UserCmdInType.USER_REGION_LIST||
			  request.getCode()== UserCmdInType.USER_REGISTER){
			   
			      return "login";
			}
				

		
		} catch (Exception e1) {
			LogUtils.error("getUserName", e1);
		}
		return null;
	}

 
	@Override
	public void registerSpecialParams() {
		// 参数1
		specialParamList.add(new IFunction<Void>() {
			// 加密
			@Override
			public Void execute1(Object... params) {
				CommonMessage packet = (CommonMessage) params[0];
				JSONObject json = (JSONObject) params[1];
				json.put("userId", packet.getUserID());
				return null;
			}

			// 解码
			@Override
			public Void execute2(Object... params) {
				CommonMessage packet = (CommonMessage) params[0];
				JSONObject json = (JSONObject) params[1];
				packet.setUserID(json.getLongValue("userId"));
				return null;
			}
		});

		// 参数2
		specialParamList.add(new IFunction<Void>() {
			@Override
			public Void execute1(Object... params) {
				CommonMessage packet = (CommonMessage) params[0];
				JSONObject json = (JSONObject) params[1];
				json.put("checkSum", packet.getChecksum());
				return null;
			}

			@Override
			public Void execute2(Object... params) {
				CommonMessage packet = (CommonMessage) params[0];
				JSONObject json = (JSONObject) params[1];
				packet.setChecksum(json.getShortValue("checkSum"));
				return null;
			}
		}); // 参数2
	}
}
