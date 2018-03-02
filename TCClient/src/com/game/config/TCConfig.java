package com.game.config;

import com.game.type.Configuration;
import com.game.utils.StringUtil;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

@Configuration(config = "config/TCConfig.properties")
public class TCConfig {

	private static String host;

	private static int port;

	private static int serverId;

	private static String tcWebUrl;

	private static String savePath;

	private static String configPath;

	//录制默认文件夹
	private static String recordPath;

	private static HashSet<String> dialysisDesc;

	private static ClientCfg clientCfg;

	/**
	 * xml config的path
     */
	public static Path getConfigPath()
	{
		if (!StringUtil.isEmpty(configPath))
			return Paths.get(configPath, "ClientConfig.xml");
		else
			return Paths.get(new File("").getPath(), "config", "ClientConfig.xml");
	}
	/**
	 * 加载xml配置文件
	 * @return 缺少改文件或文件内容有误时返回false
     */
	public static boolean init()
	{
		Serializer serializer = new Persister();
		File cfg = getConfigPath().toFile();
		try
		{
			clientCfg = serializer.read(ClientCfg.class, cfg);
		}
		catch (Exception e)
		{
			clientCfg = new ClientCfg();
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static String getHost() {
		return host;
	}

	public void setHost(String host) {
		TCConfig.host = host;
	}

	public static int getPort() {
		return port;
	}

	public void setPort(int port) {
		TCConfig.port = port;
	}

	public static int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		TCConfig.serverId = serverId;
	}

	public static String getTcWebUrl() {
		return tcWebUrl;
	}

	public static void setTcWebUrl(String tcWebUrl) {
		TCConfig.tcWebUrl = tcWebUrl;
	}

	public static String getSavePath() {
		return savePath;
	}

	public static void setSavePath(String savePath) {
		TCConfig.savePath = savePath;
	}

	public static void setConfigPath(String configPath)
	{
		TCConfig.configPath = configPath;
	}

	public static void setRecordPath(String recordPath)
	{
		TCConfig.recordPath = recordPath;
	}

	public static String getRecordPath()
	{
		return recordPath;
	}

	public static HashSet<String> getDialysisDesc() {
		return dialysisDesc;
	}

	public static void setDialysisDesc(String dialysisDesc) {
		if (dialysisDesc != null) {
			String ss[] = dialysisDesc.split("#");
			if (TCConfig.dialysisDesc == null) {
				TCConfig.dialysisDesc = new HashSet<>();
			}
			for (String s : ss) {
				TCConfig.dialysisDesc.add(s);
			}
		}
	}

	public static ClientCfg getClientCfg()
	{
		return clientCfg;
	}

}
