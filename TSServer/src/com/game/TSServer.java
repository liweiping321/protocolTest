package com.game;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.apache.mina.filter.codec.textline.TextLineCodecFactory;

import com.game.cache.Cache;
import com.game.config.ServerConfig;
import com.game.core.net.SocketServer;
import com.game.function.IFunction1;
import com.game.http.WebServer;
import com.game.net.C2SMinaHandler;
import com.game.net.H5WebSocketHandler;
import com.game.net.TCMinaHandler;
import com.game.net.TcWebHandler;
import com.game.net.keyUpdate.GameKeyUpdateBussiness;
import com.game.proxy.SelfDriverExecutorService;
import com.game.scheduler.ProtoConfigLoadJob;
import com.game.scheduler.SecheduledPool;
import com.game.scheduler.SyncFlow2TCJob;
import com.game.ts.net.factory.TSMinaFactory;
import com.game.utils.JarLoaderUtils;
import com.game.utils.LogUtils;
import com.game.utils.PropertyConfigUtil;
import com.game.websocket.BaseWebSocketServerHandler;
import com.game.websocket.WebSocketServer;


/**
 * 模拟TS服务器
 * @author jason.lin
 *
 */
public class TSServer {
	// mina加解密工厂
	private static TSMinaFactory tsMinaFactory;
	
	// 游戏timer处理延迟线程
	public static Timer timer = new Timer();
	
	public static void main(String[] args) throws Exception {
		// 初始化log配置
		LogUtils.logConfigure();
		// 初始化property配置
		PropertyConfigUtil.init(ServerConfig.class.getPackage());

		// 解析配置
		Cache.init(getGameProtoPath());
		
		// 初始化自驱动线程池
		SelfDriverExecutorService.init();
		
		// 初始化定时调度线程池
		SecheduledPool.init();
		
		// 增加同步流统计的定时任务
		SecheduledPool.scheduleWithFixedDelay(new SyncFlow2TCJob(),
				5, 5, TimeUnit.SECONDS);
		
		// 启动配置热更新
		new ProtoConfigLoadJob(getGameProtoPath()).start();
		
		// 处理游戏逻辑
		new SocketServer().start(ServerConfig.socketPort, ServerConfig.minaThreadCount, new C2SMinaHandler(), 
				getCodecFactory());
		
		// 处理TC逻辑
		TextLineCodecFactory lineCodec = new TextLineCodecFactory();
		lineCodec.setDecoderMaxLineLength(1024 * 1024); //1M  
		lineCodec.setEncoderMaxLineLength(1024 * 1024); //1M  
		new SocketServer().start(ServerConfig.tcPort, ServerConfig.tcMinaThreadCount, new TCMinaHandler(), 
				lineCodec);

		//启动jetty
		WebServer.getInstance().startHttp(ServerConfig.httpPort,new TcWebHandler());
		
		// 启动WebSocket=>H5
		WebSocketServer.getInstance().start(ServerConfig.webSorcketPort, new IFunction1<BaseWebSocketServerHandler>() {
			@Override
			public BaseWebSocketServerHandler execute(Object... params) {
				return new H5WebSocketHandler();
			}
		});
	}
	
	/**
	 * 获取加解密模式
	 * @return
	 */
	public static TSMinaFactory getCodecFactory(){
		if(tsMinaFactory == null){
			String jarPath = getGameJarName();
			tsMinaFactory = JarLoaderUtils.load(jarPath, TSMinaFactory.class);
			tsMinaFactory.setGameBussiness(new GameKeyUpdateBussiness());
		}
		
		return tsMinaFactory;
	}

	/**
	 * 获取游戏根目录
	 * @return
	 */
	public static String getGameBasePath(){
		String base = System.getProperty("user.dir");
		String gameName = ServerConfig.getGameName();
		base += "/";
		base += gameName;
		return base;
	}
	
	/**
	 * 获取游戏根目录
	 * @return
	 */
	public static String getGameProtoPath(){
		String base = getGameBasePath();
		base += "/proto/";
		return base;
	}
	
	
	/**
	 * 获取游戏jar名
	 * @return
	 */
	private static String getGameJarName() {
		String base = getGameBasePath();
		base += "/jar/";
		base += ServerConfig.getGameName();
		base += ".jar";
		return base;
	}
	
}
