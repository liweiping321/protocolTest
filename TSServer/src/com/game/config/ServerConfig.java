package com.game.config;

import com.game.type.Configuration;

@Configuration(config = "config/serverConfig.properties")
public class ServerConfig {
	// 服务器线程池大小
	private static int serverPoolSize;

	// 玩家线程池大小
	private static int playerPoolSize;

	// 战斗服线程池大小
	private static int fightPoolSize;
	
	// 游戏服端口
	public static int socketPort;
	
	// 游戏线程池大小
	public static int minaThreadCount;
	
	// tc逻辑服务器端口
	public static int tcPort;
	
	// tc逻辑服务器线程数
	public static int tcMinaThreadCount;
	
	// 游戏名
	public static String gameName;
	
	// 监听的服务器ip地址
	public static String gameServerIp;
	
	// 监听的服务器ip地址
	public static int gameServerPort;
	//http服务器端口
	public static int httpPort;
	//消息缓存队列大小
	public static int msgCacheSize;
	
	// tc端显示的进制类型
	public static int tcShowRadixType;
	
	//webSocket服务器端口
	public static int webSorcketPort;
	
	public static int getHttpPort() {
		return httpPort;
	}

	public static void setHttpPort(int httpPort) {
		ServerConfig.httpPort = httpPort;
	}

	public static int getServerPoolSize() {
		return serverPoolSize;
	}

	public static void setServerPoolSize(int serverPoolSize) {
		ServerConfig.serverPoolSize = serverPoolSize;
	}

	public static int getPlayerPoolSize() {
		return playerPoolSize;
	}

	public static void setPlayerPoolSize(int playerPoolSize) {
		ServerConfig.playerPoolSize = playerPoolSize;
	}

	public static int getFightPoolSize() {
		return fightPoolSize;
	}

	public static void setFightPoolSize(int fightPoolSize) {
		ServerConfig.fightPoolSize = fightPoolSize;
	}

	public static int getSocketPort() {
		return socketPort;
	}

	public void setSocketPort(int socketPort) {
		ServerConfig.socketPort = socketPort;
	}

	public static int getMinaThreadCount() {
		return minaThreadCount;
	}

	public void setMinaThreadCount(int minaThreadCount) {
		ServerConfig.minaThreadCount = minaThreadCount;
	}

	public static int getTcPort() {
		return tcPort;
	}

	public static void setTcPort(int tcPort) {
		ServerConfig.tcPort = tcPort;
	}

	public static int getTcMinaThreadCount() {
		return tcMinaThreadCount;
	}

	public static void setTcMinaThreadCount(int tcMinaThreadCount) {
		ServerConfig.tcMinaThreadCount = tcMinaThreadCount;
	}

	public static String getGameName() {
		return gameName;
	}

	public static void setGameName(String gameName) {
		ServerConfig.gameName = gameName;
	}

	public static String getGameServerIp() {
		return gameServerIp;
	}

	public static void setGameServerIp(String gameServerIp) {
		ServerConfig.gameServerIp = gameServerIp;
	}

	public static int getGameServerPort() {
		return gameServerPort;
	}

	public static void setGameServerPort(int gameServerPort) {
		ServerConfig.gameServerPort = gameServerPort;
	}

	public static int getMsgCacheSize() {
		return msgCacheSize;
	}

	public static void setMsgCacheSize(int msgCacheSize) {
		ServerConfig.msgCacheSize = msgCacheSize;
	}

	public static int getWebSorcketPort() {
		return webSorcketPort;
	}

	public static void setWebSorcketPort(int webSorcketPort) {
		ServerConfig.webSorcketPort = webSorcketPort;
	}

	public static int getTcShowRadixType() {
		return tcShowRadixType;
	}

	public static void setTcShowRadixType(int tcShowRadixType) {
		ServerConfig.tcShowRadixType = tcShowRadixType;
	}

}
