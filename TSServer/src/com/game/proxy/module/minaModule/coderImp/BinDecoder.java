package com.game.proxy.module.minaModule.coderImp;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.game.TSServer;
import com.game.cache.Cache;
import com.game.config.ServerConfig;
import com.game.protocal.BinConfig;
import com.game.protocal.BinProperty;
import com.game.protocal.ProtoConfig;
import com.game.proxy.module.minaModule.IGameCoder;
import com.game.ts.net.TSMinaMsg;
import com.game.ts.net.bean.TSBean;
import com.game.type.DirectType;
import com.game.utils.DataUtil;
import com.game.utils.LogUtils;
import com.game.utils.PropertyConfigUtil;

/**
 * 二级制解码
 * 
 * @author jason.lin
 *
 */
public class BinDecoder implements IGameCoder {

	@Override
	public JSONObject decodeMsg(ProtoConfig config, TSMinaMsg request) {
		BinConfig binConfig = config.getBinConfig();
		if (binConfig == null) {
			return null;
		}

		List<BinProperty> list = binConfig.getList();
		if (DataUtil.isEmpty(list)) {
			return null;
		}

		// 解析成json对象
		JSONObject json = decodePropertyList(list, request);
		return json;
	}

	/**
	 * 解析成json对象
	 * 
	 * @param list
	 * @param request
	 * @return
	 */
	private JSONObject decodePropertyList(List<BinProperty> list, TSMinaMsg request) {
		JSONObject json = new JSONObject();
		for (BinProperty property : list) {
			String name = property.getName();
			String type = property.getType().toLowerCase();
			
//			// 处理特殊字段
//			if(TSServer.getCodecFactory().decodeSpecialPara(name, request, json)){
//				continue;
//			}
			
			switch (type) {
			case "float":
				json.put(name, request.getFloat());
			case "double":
				json.put(name, request.getDouble());
				break;
			case "byte":
				json.put(name, request.getByte());
				break;
			case "short":
				json.put(name, request.getShort());
				break;
			case "int":
				json.put(name, request.getInt());
				break;
			case "long":
				json.put(name, request.getLong());
				break;
			case "boolean":
				json.put(name, request.getBoolean());
				break;
			case "char":
				json.put(name, request.getChar());
				break;
			case "string":
				json.put(name, request.getString());
				break;
			case "date":
				json.put(name, request.getDate());
				break;
			case "bytes":
				json.put(name, request.getBytes());
				break;
			case "list":
				String sizeType = property.getSizeType().toLowerCase();
				int size = "short".equals(sizeType) ? request.getShort() : request.getInt();
				JSONArray array = new JSONArray();
				for (int index = 0; index < size; index++) {
					JSONObject subObj = decodePropertyList(property.getList(), request);
					array.add(subObj);
				}
				json.put(name, array);
				break;
			default:
				LogUtils.error("no type for : " + type);
				break;
			}
		}
		return json;
	}

	@Override
	public TSMinaMsg encodeMsg(ProtoConfig config, JSONObject json) {
		TSMinaMsg tsMinaMsg = TSServer.getCodecFactory().initTSMinaMsgForBin(config.getCodeShort(), config.getSubCodeShort());

		List<BinProperty> list = config.getBinConfig().getList();
		if (DataUtil.isEmpty(list)) {
			return tsMinaMsg;
		}

		// 按配置格式编码
		encodePropertyList(list, json, tsMinaMsg);
		return tsMinaMsg;
	}

	/**
	 * 按配置格式编码
	 * 
	 * @param list
	 * @param jsonObj
	 * @param tsMinaMsg
	 */
	public void encodePropertyList(List<BinProperty> list, JSONObject jsonObj, TSMinaMsg tsMinaMsg) {
		for (BinProperty property : list) {
			String name = property.getName();
			String type = property.getType().toLowerCase();
			switch (type) {
			case "float":
				tsMinaMsg.putFloat(jsonObj.getFloat(name));
			case "double":
				tsMinaMsg.putDouble(jsonObj.getDouble(name));
				break;
			case "byte":
				tsMinaMsg.putByte(jsonObj.getByte(name));
				break;
			case "short":
				tsMinaMsg.putShort(jsonObj.getShort(name));
				break;
			case "int":
				tsMinaMsg.putInt(jsonObj.getInteger(name));
				break;
			case "long":
				tsMinaMsg.putLong(jsonObj.getLong(name));
				break;
			case "boolean":
				tsMinaMsg.putBoolean(jsonObj.getBoolean(name));
				break;
			case "char":
				tsMinaMsg.putString(jsonObj.getString(name));
				break;
			case "string":
				tsMinaMsg.putString(jsonObj.getString(name));
				break;
			case "date":
				tsMinaMsg.putDate(jsonObj.getDate(name));
				break;
			case "bytes":
				tsMinaMsg.putBytes(jsonObj.getBytes(name));
				break;
			case "list":
				JSONArray array = jsonObj.getJSONArray(name);
				String sizeType = property.getSizeType().toLowerCase();
				if ("short".equals(sizeType)) {
					tsMinaMsg.putShort((short) array.size());
				} else {
					tsMinaMsg.putInt(array.size());
				}

				for (int index = 0; index < array.size(); index++) {
					encodePropertyList(property.getList(), array.getJSONObject(index), tsMinaMsg);
				}
				break;
			default:
				LogUtils.error("no type for : " + type);
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		// 初始化log配置
		LogUtils.logConfigure();
		
		// 初始化property配置
		PropertyConfigUtil.init(ServerConfig.class.getPackage());

		// 解析配置
		Cache.init(TSServer.getGameProtoPath());
		
		TSServer.getCodecFactory();
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("id", 100);
		JSONArray array = new JSONArray();
		JSONObject subObj = new JSONObject();
		subObj.put("friendId", 20);
		array.add(subObj);
		jsonObj.put("friendList", array);
		
		TSBean tsBean = new TSBean();
		tsBean.setContent(jsonObj);
		System.err.println(tsBean);
		
		ProtoConfig config = Cache.getProtoConfig(DirectType.DOWN, 10001, 0);
		TSMinaMsg request = new BinDecoder().encodeMsg(config, tsBean.getContent());
		JSONObject str = new BinDecoder().decodeMsg(config, request);
		System.err.println(str);
	}

}
