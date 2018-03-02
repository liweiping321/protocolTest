package com.game.proxy.module.minaModule;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.game.TSServer;
import com.game.cache.Cache;
import com.game.config.ServerConfig;
import com.game.consts.LogicConsts;
import com.game.consts.WordConsts;
import com.game.express.eng.Expressions;
import com.game.player.BaseModule;
import com.game.protocal.ProtoConfig;
import com.game.proxy.ConnectProxy;
import com.game.proxy.module.minaModule.actionImpl.CommonWriteAction;
import com.game.proxy.module.minaModule.actionImpl.DelayWriteAction;
import com.game.proxy.module.minaModule.actionImpl.RepeateWriteAction;
import com.game.proxy.module.minaModule.coderImp.BinDecoder;
import com.game.proxy.module.minaModule.coderImp.PBDecoder;
import com.game.tc.TCClientInfo;
import com.game.tc.TCService;
import com.game.tc.module.ClientMsgModule;
import com.game.tc.module.StatisticModule;
import com.game.ts.net.TSMinaMsg;
import com.game.ts.net.bean.PlayerInfo;
import com.game.ts.net.bean.TSBean;
import com.game.ts.net.consts.TSCCmd;
import com.game.type.DirectType;
import com.game.type.ProtocolType;
import com.game.type.UpdateActionType;
import com.game.utils.DataUtil;
import com.game.utils.LogUtils;
import com.game.utils.StringUtil;

@SuppressWarnings("serial")
public class MinaModule extends BaseModule<ConnectProxy> {
	// 协议最大显示位数
	private static final int protoMaxSize = 3;
	
	// 加解密解析器
	private static final Map<ProtocolType, IGameCoder> decoderMap = new HashMap<ProtocolType, IGameCoder>() {
		{
			put(ProtocolType.PB, new PBDecoder());
			put(ProtocolType.BIN, new BinDecoder());
			put(ProtocolType.BIN_FILE, new BinDecoder());
		}
	};

	// 发包模式
	private static final Map<UpdateActionType, IPackWriteAction> actionMap = new HashMap<UpdateActionType, IPackWriteAction>() {
		{
			put(UpdateActionType.COMMOM, new CommonWriteAction());
			put(UpdateActionType.DELAY, new DelayWriteAction());
			put(UpdateActionType.REPEAT, new RepeateWriteAction());
		}
	};
		
	public MinaModule(ConnectProxy player) {
		super(player);
	}

	/**
	 * 解码
	 * 
	 * @param paramObject
	 * @return
	 */
	public boolean decodeMsg(Object paramObject, DirectType directType) {
		TSMinaMsg request = (TSMinaMsg) paramObject;
		
		boolean holdUp = false;
		try {
			short code = request.getCode();
			short subCode = request.getSubCode();
			ProtoConfig config = Cache.getProtoConfig(directType, code, subCode);
			JSONObject json = null;
			if (config != null) {
				
				if(config.isFilter()){
					return false;
				}
				// 解压包
				if(config.isCompress()){
					request = request.decompress();
				}
				
				// 判定是否登录协议
				String loginNameKey = config.getLoginName();
				// 登录解密第一种方法： 逻辑解析==游戏接口各自实现
				if (LogicConsts.LOGIN_DECODER_LOGIC.equals(loginNameKey)) {
					String loginName = TSServer.getCodecFactory().getUserName(player.getSession(), player.getClient(),
							request);
					if (!StringUtil.isEmpty(loginName)) {
						PlayerInfo playerInfo = new PlayerInfo();
						playerInfo.setUserName(loginName);
						player.setPlayerInfo(playerInfo);
					}
				}

				// 处理消息 分类型 有PB、二进制。。。 各种
				IGameCoder decoder = getDecoder(config.getProtocolType());
				json = decoder.decodeMsg(config, request);
				if(json == null){
					json = new JSONObject();
				}
				
				// 登录解密第二种方法： 根据配置直接获取json中对应登录名称
				if (loginNameKey != null && !LogicConsts.LOGIN_DECODER_LOGIC.equals(loginNameKey)) {
					decodeLogin(loginNameKey, json);
				}
				
				// 增加特殊参数
				TSServer.getCodecFactory().decodeSpecialPara(request, json);
			}
			
			
			holdUp = false;
			// 往TC 端发送消息
			if (player.getTcClientInfos().size() > 0) {
				
				TSBean tsBean = builderTsBean(request, json, directType, config);
				if(tsBean == null){
					return false;
				}
				
				// 包长度
				int dataLength = request.getLength();
				for (TCClientInfo clientInfo : player.getTcClientInfos()) {
				
					String filterAction=clientInfo.getFilterChain().filter(tsBean);
				   
					boolean sendToTc=isSendToTc(filterAction);
					if(sendToTc){
						clientInfo.getModule(ClientMsgModule.class).sendToTc(tsBean);
					}
				
					// 统计流量
					clientInfo.getModule(StatisticModule.class).staticFlow(dataLength, directType);
					
					if(holdUp||isHoldUp(filterAction)){
						holdUp=true;
					}
					 
				}
			}
		} catch (Exception e) {
			LogUtils.error("decodeMsg id: 0x" + Integer.toHexString(request.getCode()) + " 0x"+Integer.toHexString(request.getSubCode()) , e);
		}
		
		// 是否被截断
		return holdUp;
	}

	/**
	 * 是否发送到客户端
	 */
	private boolean isSendToTc(String filterAction) {
		return Expressions.DEFAULT.equals(filterAction)
				|| Expressions.INTERCEPT.equals(filterAction)
				|| Expressions.VIEW.equals(filterAction)
				|| Expressions.DELETE.equals(filterAction);
	}

	/**
	 * 数据是否被拦截，丢弃
	 */
	private boolean isHoldUp(String filterAction) {

		return Expressions.DROP.equals(filterAction)
				|| Expressions.INTERCEPT.equals(filterAction);
	}

	/**
	 * 组装TsBean
	 * @param request
	 * @param jsonFormat
	 * @param directType
	 * @param config
	 * @return
	 */
	public TSBean builderTsBean(TSMinaMsg request, JSONObject json, DirectType directType, ProtoConfig config) {
		if(json == null || config == null){
			json = new JSONObject();
			json.put(LogicConsts.NO_DEFINE_BODY, JSON.toJSON(request.getBytes()));
			
			// 增加特殊参数
			TSServer.getCodecFactory().decodeSpecialPara(request, json);
		} 
		
		IoSession session = player.getSession();
		String ip = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
		TSBean tsBean = new TSBean();
		tsBean.setId(TCService.getAutoIncId());
		tsBean.setSessionId(String.valueOf(session.getId()));
		tsBean.setLength(request.getLength());
		tsBean.setIp(ip);
		tsBean.setUserName(player.getUserName());
		tsBean.setContent(json);
		tsBean.setCode(getTcRadixCmd(request.getCode()));
		tsBean.setSubCode(getTcRadixCmd(request.getSubCode()));
		tsBean.setDirectType(directType.name());
		tsBean.setTscCmd(TSCCmd.CLIENT_MSG);
		tsBean.setDesc(getDesc(config));
		tsBean.setConfig(config!=null);
		return tsBean;
	}

	/**
	 * 根据配置显示不同进制数
	 * @param code
	 * @return
	 */
	public String getTcRadixCmd(short code){
		if(ServerConfig.tcShowRadixType == 10){
			return String.valueOf(code);
		}
		return DataUtil.toHexString(code, protoMaxSize);
	}
	
	/**
	 * 获取描述信息
	 * @param config
	 * @return
	 */
	private String getDesc(ProtoConfig config) {
		if(config == null){
			return WordConsts.NO_DEFINE_CMD;
		}
		String desc = config.getDesc();
		return desc == null ? WordConsts.NO_DEFINE_CMD : desc;
	}

	/**
	 * 解密登录
	 * 
	 * @param loginNameKey
	 * @param jsonFormat
	 */
	private void decodeLogin(String loginNameKey, JSONObject json) {
		try {
			if (StringUtil.isEmpty(loginNameKey)) {
				return;
			}

			if (json == null) {
				return;
			}

			// 设置玩家信息
			String loginName = json.getString(loginNameKey);
			PlayerInfo playerInfo = new PlayerInfo();
			playerInfo.setUserName(loginName);
			player.setPlayerInfo(playerInfo);
		} catch (Exception e) {
			LogUtils.error("decodeLogin", e);
		}
	}

	/**
	 * 获取解码器
	 * 
	 * @param protocolType
	 * @return
	 */
	private IGameCoder getDecoder(String protocolType) {
		ProtocolType type = ProtocolType.valueOf(protocolType);
		return decoderMap.get(type);
	}

	/**
	 * 加密
	 * 
	 * @param paramObject
	 * @return
	 */
	public boolean encodeMsg(TSBean tsBean) {
		short code = ProtoConfig.toRadixShort(tsBean.getCode());
		short subCode = ProtoConfig.toRadixShort(tsBean.getSubCode());
		try {
			DirectType directType=DirectType.forName(tsBean.getDirectType());
			ProtoConfig config = Cache.getProtoConfig(directType, code, subCode);
			IoSession session=(DirectType.UP==directType?player.getClient():player.getSession());
			TSMinaMsg request = null;
			if (config != null) {
				// 处理消息 分类型 有PB、二进制。。。 各种
				IGameCoder decoder = getDecoder(config.getProtocolType());
				JSONObject json = tsBean.getContent();
				request = decoder.encodeMsg(config, json);
				if (request == null) {
					return false;
				}
				
				// 增加特殊参数
				TSServer.getCodecFactory().encodeSpecialPara(request, json);
				
				// 压缩包
				if(config.isCompress()){
					request.compress();
				}
			}else {
				request = TSServer.getCodecFactory().initTSMinaMsgForBin(code, subCode);
				if (request == null) {
					return false;
				}
				
				// 增加特殊参数
				JSONObject json = tsBean.getContent();
				TSServer.getCodecFactory().encodeSpecialPara(request, json);
				
				JSONArray array = json.getJSONArray(LogicConsts.NO_DEFINE_BODY);
				byte[] body=TypeUtils.castToJavaBean(array, byte[].class);
				if(body != null){
					request.putBytes(body);
				}
			}

			// 根据动作需求，不同处理
			actionWrite(tsBean.getAction(), session, request);
		} catch (Exception e) {
			LogUtils.error("encodeMsg id: 0x" + Integer.toHexString(code) + " 0x"+Integer.toHexString(subCode) , e);
		}
		return false;
	}
	
	/**
	 * 根据动作需求，不同处理
	 * @param actionStr
	 * @param session
	 */
	public void actionWrite(String actionStr, IoSession session, TSMinaMsg request){
//		actionStr = "";
//		actionStr = "DELY[100]";
//		actionStr = "REP[5,5000]";
		
		String action = null;  // 动作头
		String[] args = null;  // 参数列表 ','分隔
		if(actionStr != null && actionStr.contains("[")){
			String[] actionArray = actionStr.split("\\[");
			action = actionArray[0];
			args = actionArray[1].replace("[", "").replace("]", "").split(",");
		}
		
		// 处理写逻辑
		getActionType(action).handleActionWrite(args, session, request);
	}
	
	/**
	 * 获取发包类型
	 * @param action
	 * @return
	 */
	public static IPackWriteAction getActionType(String action){
		return actionMap.get(UpdateActionType.parse(action));
	} 
}
