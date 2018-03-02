package com.game.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.session.IoSession;
import org.dom4j.Element;

import com.game.core.net.SessionKey;
import com.game.protocal.ProtoConfig;
import com.game.proxy.ConnectProxy;
import com.game.tc.TCClientInfo;
import com.game.type.DirectType;
import com.game.utils.DataUtil;
import com.game.utils.LogUtils;
import com.game.utils.StringUtil;
import com.game.utils.XMLUtil;

/**
 * 缓存
 * @author jason.lin
 *
 */
public class Cache {
	// proto配置Map
	private static Map<String, ProtoConfig> protoConfig = new HashMap<String, ProtoConfig>();

	// proto配置List
	private static List<ProtoConfig> protoConfigList = null;
	
	// 代理Map
	private static Map<IoSession, ConnectProxy> proxyMap = new ConcurrentHashMap<IoSession, ConnectProxy>();
	
	// tc集合
	private static Map<String, TCClientInfo> tcMap = new ConcurrentHashMap<>();
	
	// userName映射tc监听
	// private static Map<String, TCClientInfo> listenerTcMap = new ConcurrentHashMap<>();
	
	public static void init(String path){
		try {
			FileInputStream in = new FileInputStream(new File(path + "/ProtoConfig.xml"));
			List<ProtoConfig> list = XMLUtil.fromXMLToObjectList(in, "root/module/ProtoConfig", ProtoConfig.class);
			Cache.reload(list, path);
		} catch (FileNotFoundException e) {
			LogUtils.error("init", e);
		}
	}
	
	/**
	 * proto配置Map
	 * @param protoConfig
	 */
	public static void reload(List<ProtoConfig> protoConfigList, String path) {
		Cache.protoConfigList = protoConfigList;
		
		initProtoMap(protoConfigList, path);
	}
	
	public static  Collection<ConnectProxy> getAllConnectProxy(){
		return proxyMap.values();
	}
	/**
	 * proto配置List
	 * @param protoConfig
	 */
	private static void initProtoMap(List<ProtoConfig> protoConfigList, String path) {
		if(DataUtil.isEmpty(protoConfigList)){
			return;
		}
		
		protoConfig.clear();
		Map<String, Element> elementMap = XMLUtil.getXmlElementMap(path + "/BinConfig.xml", "root/module/protocol", "key");
		for(ProtoConfig config : protoConfigList){
			String key = getProtoKey(config.getType(), config.getCodeShort(), config.getSubCodeShort());
			config.initCoderClass(elementMap);
			protoConfig.put(key, config);
		}
	}

	/**
	 * 获取协议KEY
	 * @param type
	 * @param code
	 * @param subCode
	 * @return
	 */
	public static String getProtoKey(String type, int code, int subCode){
		return new StringBuilder(type).append("_").append(code).append("_").append(subCode).toString();
	}
	
	/**
	 * 获取配置信息
	 * @param type
	 * @param code
	 * @param subCode
	 * @return
	 */
	public static ProtoConfig getProtoConfig(DirectType type, int code, int subCode){
		if(protoConfig == null){
			return null;
		}
		
		return protoConfig.get(getProtoKey(type.name(), code, subCode));
	}
	
	/**
	 * 获取所有配置
	 * @return
	 */
	public static List<ProtoConfig> getProtoConfigList(){
		if(protoConfigList == null){
			return null;
		}
		
		return new ArrayList<ProtoConfig>(protoConfigList);
	}

	/**
	 * 添加代理
	 * @param proxy
	 */
	public static void addConnectProxy(ConnectProxy proxy) {
		proxyMap.put(proxy.getSession(), proxy);
	}

	/**
	 * 获取对应代理
	 * @param client
	 * @return
	 */
	public static ConnectProxy getConnectProxy(IoSession client) {
		if(client==null){
			return null;
		}
		return proxyMap.get(client);
	}

	/**
	 * 移除代理
	 * @param other
	 */
	public static void remove(IoSession other) {
		if(other==null){
			return ;
		}
		proxyMap.remove(other);
	}
	
	/**
	 * 添加tc客户端
	 * @param tcClientInfo
	 */
	public static void addTcClient(String key, TCClientInfo tcClientInfo){
		tcMap.put(key, tcClientInfo);
	}
	
	
	public static void removeTcClient(IoSession session){
		removeTcClient(String.valueOf(session.getId()));
	}
	
	public static void removeTcClient(String channleId){
		tcMap.remove(channleId);
	}
	
	/**
	 * 获取tc客户端
	 * @param tc
	 * @return
	 */
	public static TCClientInfo getTCClientInfo(IoSession tc){
		return tcMap.get(String.valueOf(tc.getId()));
	}
	
	public static TCClientInfo getTCClientInfo(String sessionId){
		return tcMap.get(sessionId);
	}
	
	public static TCClientInfo getTCClientInfo(long sessionId){
		return tcMap.get(String.valueOf(sessionId));
	}
	
	/**
	 * 获取所有tc
	 * @return
	 */
	public static List<TCClientInfo> getAllTc(){
		return new ArrayList<TCClientInfo>(tcMap.values());
	}

	/**
	 * 添加ip监听
	 * @param userName
	 * @return
	 */
//	public static boolean addListenerToUserName(String userName, TCClientInfo tcClientInfo) {
//		if(listenerTcMap.containsKey(userName)){
//			return false;
//		}
//		
//		listenerTcMap.put(userName,  tcClientInfo);
//		return true;
//	}

	/**
	 * 获取监听
	 * @param userName
	 * @return
	 */
//	public static TCClientInfo getTCClientListener(String userName){
//		return listenerTcMap.get(userName);
//	}
	
	/**
	 * 根据ip找到对应连接代理
	 * @param ip
	 * @return
	 */
	public static ConnectProxy findConnectProxy(String ip) {
		for(IoSession session : proxyMap.keySet()){
			String clientIp = (String)session.getAttribute(SessionKey.CLIENT_IP);
			if(ip.equals(clientIp)){
				return proxyMap.get(session);
			}
		}
		
		return null;
	}
	
	public static ConnectProxy findConnectProxy(long sessionId) {
		for(IoSession session : proxyMap.keySet()){ 
			if(session.getId()==sessionId){
				return proxyMap.get(session);
			}
		}
		
		return null;
	}
	
	/**
	 * 根据用户名找到对应连接代理
	 * @param ip
	 * @return
	 */
	public static ConnectProxy findConnectProxyByUserName(String userName) {
		if(StringUtil.isEmpty(userName)){
			return null;
		}
		
		for(ConnectProxy proxy : proxyMap.values()){
			String proxyName = proxy.getUserName();
			if(userName.equals(proxyName)){
				return proxy;
			}
		}
		
		return null;
	}
	
}
