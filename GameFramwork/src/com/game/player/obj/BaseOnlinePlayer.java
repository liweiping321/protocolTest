package com.game.player.obj;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

import com.game.player.IModule;

/**
 * 在线玩家
 * @author jason.lin
 *
 */
public abstract class BaseOnlinePlayer implements Serializable{
	private static final long serialVersionUID = 1L;

	// 玩家id
	protected int playerId;

	// session
	protected IoSession session;
	
	// 模块列表
	protected Map<Class<?>, IModule> moduleMap = new LinkedHashMap<>();
	
	// 模块初始化
	public abstract void initModules();

	/**
	 * 添加模块
	 * @param moduleId
	 * @param module
	 */
	public void addModule(IModule module){
		moduleMap.put(module.getClass(), module);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getModule(Class<T> T){
		return (T)moduleMap.get(T);
	}
	
	
//	/**
//	 * 加载数据
//	 * @return
//	 */
//	public boolean loadDB(){
//		for(IModule module : moduleMap.values()){
//			try {
//				module.loadDB();
//			} catch (Exception e) {
//				LogUtils.error(e);
//				return false;
//			}
//		}
//		return true;
//	}
//	
//	/**
//	 * 数据加载之后
//	 * @return
//	 */
//	public boolean afterLoadDB(){
//		for(IModule module : moduleMap.values()){
//			try {
//				module.afterLoadDB();
//			} catch (Exception e) {
//				LogUtils.error(e);
//				return false;
//			}
//		}
//		return true;
//	}
//	
//	/**
//	 * 数据入库
//	 * @return
//	 */
//	public boolean save(){
//		for(IModule module : moduleMap.values()){
//			try {
//				module.save();
//			} catch (Exception e) {
//				LogUtils.error(e);
//				return false;
//			}
//		}
//		return true;
//	}
	
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}
	
	public static String getChannelAccountKey(String channel, String areaId, String uid){
		return String.format("%s_%s_%s", channel, areaId, uid);
	}
	
}
