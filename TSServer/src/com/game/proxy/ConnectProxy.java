package com.game.proxy;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.mina.core.session.IoSession;

import com.game.cache.Cache;
import com.game.player.obj.BaseOnlinePlayer;
import com.game.proxy.module.minaModule.MinaModule;
import com.game.queue.IRunnableQueue;
import com.game.queue.SelfDrivenRunnableQueue;
import com.game.tc.TCClientInfo;
import com.game.ts.net.bean.PlayerInfo;
import com.game.type.DirectType;

/**
 * 连接代理
 * 
 * @author jason.lin
 * 
 */
public class ConnectProxy extends BaseOnlinePlayer {
	private static final long serialVersionUID = 1L;

	private IoSession session;

	private IoSession client;
 
	// 账号信息
	private PlayerInfo playerInfo;

	// 监听TC客户端列表
	private final Set<TCClientInfo> listenerTCInfos = new CopyOnWriteArraySet<>();

	private IRunnableQueue<Runnable> queue = new SelfDrivenRunnableQueue<Runnable>(
			SelfDriverExecutorService.getExecutorService());

	// 同步登陆秘钥
	private boolean syncKey = false;

	public ConnectProxy(IoSession session, IoSession client) {
		this.session = session;
		this.client = client;

		// 初始化模块
		initModules();
	}

	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}

	public IRunnableQueue<Runnable> getQueue() {
		return queue;
	}

	public void setQueue(IRunnableQueue<Runnable> queue) {
		this.queue = queue;
	}

	public IoSession getClient() {
		return client;
	}

	public void setClient(IoSession client) {
		this.client = client;
	}

	public String getUserIp() {
		String clientIP = ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
		return clientIP;
	}

	public void addMsg(final Object paramObject, DirectType directType) throws Exception {

		// 是否截包
		if (this.getModule(MinaModule.class).decodeMsg(paramObject, directType)) {
			return;
		}

		addMsgToQueue(paramObject, directType);
	}

	/**
	 * 添加到发送列表
	 * 
	 * @param paramObject
	 */
	public void addMsgToQueue(final Object paramObject, DirectType directType) {
		queue.add(new MsgRunnable(this, paramObject, directType));
	}

	@Override
	public void initModules() {
		addModule(new MinaModule(this));

	}
 
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public PlayerInfo getPlayerInfo() {
		return playerInfo;
	}

	public void setPlayerInfo(PlayerInfo playerInfo) {
		this.playerInfo = playerInfo;

		initAddTcClientInfos();
	}

	public String getUserName() {
		if (playerInfo == null) {
			return null;
		}
		return playerInfo.getUserName();
	}

	public Set<TCClientInfo> getTcClientInfos() {
		return listenerTCInfos;
	}

	public void addTcClientInfo(TCClientInfo tcClientInfo) {
		listenerTCInfos.add(tcClientInfo);
	}

	public void removeTcClientInfo(TCClientInfo tcClientInfo) {
		listenerTCInfos.remove(tcClientInfo);
	}

	public void removeAllTcClientInfo() {

		for (TCClientInfo tcClientInfo : listenerTCInfos) {
			tcClientInfo.getProxys().remove(this);
		}
	}

	/** 用户登录游戏完成后，初始化监听客户端 */
	private void initAddTcClientInfos() {
		List<TCClientInfo> clientInfos = Cache.getAllTc();
		for (TCClientInfo clientInfo : clientInfos) {
			clientInfo.addConnectProxy(this);
		}
	}

	public boolean isSyncKey() {
		return syncKey;
	}

	public void setSyncKey(boolean syncKey) {
		this.syncKey = syncKey;
	}

}
