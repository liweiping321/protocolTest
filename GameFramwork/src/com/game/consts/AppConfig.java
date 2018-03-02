package com.game.consts;

import com.game.type.Configuration;


/**
 * 应用配置
 * 
 * @author jason.lin
 *
 */
@Configuration(config = "config/config.properties")
public class AppConfig {

	public static int socketPort;

	public static int webPort;

	public static int maxOnlineCount;

	public static String centerUrl;

	public static String centerPublicKey;

	public static int maxLogCache;
	
	
	public static int gamePoolThreadCount;
	
	public static int minaThreadCount;
	
	public static String neteaseAppKey = "de860aa1bf46f9238484284201d744d8";
	
	public static String neteaseAppSecret = "dc1f88e05cc6";
	
	public static int getSocketPort() {
		return socketPort;
	}

	public void setSocketPort(int socketPort) {
		AppConfig.socketPort = socketPort;
	}

	public static int getWebPort() {
		return webPort;
	}

	public void setWebPort(int webPort) {
		AppConfig.webPort = webPort;
	}

	public int getMaxOnlineCount() {
		return maxOnlineCount;
	}

	public void setMaxOnlineCount(int maxOnlineCount) {
		AppConfig.maxOnlineCount = maxOnlineCount;
	}

	public String getCenterUrl() {
		return centerUrl;
	}

	public void setCenterUrl(String centerUrl) {
		AppConfig.centerUrl = centerUrl;
	}

	public String getCenterPublicKey() {
		return centerPublicKey;
	}

	public void setCenterPublicKey(String centerPublicKey) {
		AppConfig.centerPublicKey = centerPublicKey;
	}

	public int getMaxLogCache() {
		return maxLogCache;
	}

	public void setMaxLogCache(int maxLogCache) {
		AppConfig.maxLogCache = maxLogCache;
	}

	public static int getGamePoolThreadCount() {
		return gamePoolThreadCount;
	}

	public void setGamePoolThreadCount(int gamePoolThreadCount) {
		AppConfig.gamePoolThreadCount = gamePoolThreadCount;
	}

	public static int getMinaThreadCount() {
		return minaThreadCount;
	}

	public void setMinaThreadCount(int minaThreadCount) {
		AppConfig.minaThreadCount = minaThreadCount;
	}

	public static String getNeteaseAppKey() {
		return neteaseAppKey;
	}

	public void setNeteaseAppKey(String neteaseAppKey) {
		AppConfig.neteaseAppKey = neteaseAppKey;
	}

	public static String getNeteaseAppSecret() {
		return neteaseAppSecret;
	}

	public void setNeteaseAppSecret(String neteaseAppSecret) {
		AppConfig.neteaseAppSecret = neteaseAppSecret;
	}
	
}
