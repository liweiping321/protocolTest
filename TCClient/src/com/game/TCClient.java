package com.game;

import com.game.ClientUI.Client;
import com.game.Data.DataProvider;
import com.game.config.TCConfig;
import com.game.object.TCConnector;
import com.game.scheduler.ThreadPool;
import com.game.util.LoggerUtil;
import com.game.utils.LogUtils;
import com.game.utils.PropertyConfigUtil;
import com.game.utils.language.LanguageMgr;

/**
 * TC客户端启动类
 * @author jason.lin
 *
 */
public class TCClient {
	/**服务端sessionId*/
	public static String SSID;
	
	public static  Client clientUI;
	
	public static TCConnector connector;
	
	public static void main(String[] args) {
		// 初始化log配置
		LogUtils.logConfigure();
		
		// 初始化property配置
		PropertyConfigUtil.init(TCConfig.class.getPackage());
		// 初始化客户端的设置
		boolean success = TCConfig.init();

		DataProvider.getInstance().init();

		ThreadPool.init();

		clientUI = new Client();
		clientUI.connect();
		if (!success)
		{
			LoggerUtil.critical(clientUI, LanguageMgr.getTranslation("TClient.Xml.Config.Error"));
		}
	}
}
