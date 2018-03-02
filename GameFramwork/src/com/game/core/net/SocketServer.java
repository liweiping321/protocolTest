package com.game.core.net;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.Executors;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.game.cache.CacheManager;
import com.game.player.obj.BaseOnlinePlayer;
import com.game.utils.IPUtil;
import com.game.utils.LogUtils;

public class SocketServer {
	final IoAcceptor acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);

	public void start(int port, int minaThreadCount, IoHandler handler, ProtocolCodecFactory factory) throws Exception {
		configSession(acceptor);
//		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(factory));
		acceptor.getFilterChain().addLast("executor", new ExecutorFilter(Executors.newFixedThreadPool(minaThreadCount)));
//		acceptor.getFilterChain().addLast("executor", new ExecutorFilter(Executors.newCachedThreadPool()));
		acceptor.setHandler(handler);

		InetSocketAddress address = new InetSocketAddress(port);
		acceptor.bind(address);
		LogUtils.error("server start! " + IPUtil.getHostAddress() + ":" + address.getPort());
	}

	private void configSession(IoAcceptor acceptor) {
		DefaultSocketSessionConfig sessionConfig = (DefaultSocketSessionConfig) acceptor.getSessionConfig();
		sessionConfig.setSoLinger(0);
		sessionConfig.setKeepAlive(true);
		sessionConfig.setReuseAddress(true);
		sessionConfig.setTcpNoDelay(true);
		sessionConfig.setSendBufferSize(10240);
		sessionConfig.setReadBufferSize(10240);
		sessionConfig.setBothIdleTime(15);
		sessionConfig.setIdleTime(IdleStatus.READER_IDLE, 10);// 单位秒
		sessionConfig.setWriteTimeout(15000);

	}

	public void stop() {
		LogUtils.error("shutdown server==============start dump to database");
		if (acceptor != null) {
			try {
				Collection<BaseOnlinePlayer> allPlayers = CacheManager.getAllPlayer();
				for (BaseOnlinePlayer player : allPlayers) {
					IoSession ioSession = player.getSession();
					ioSession.closeNow();
				}
				LogUtils.error("等待5秒Socket连接释放...");
				try {
					Thread.sleep(5 * 1000);
				} catch (InterruptedException e) {
				}
				LogUtils.error("有效的连接Session数量：" + acceptor.getManagedSessionCount());
			} catch (Exception e) {
				LogUtils.error("", e);
			}
		}
//		// 数据库dump
//		logger.error("Socket连接释放完成，正在数据存储...");
//		SchedulerService scheduler = ApplicationContext.getBean(SchedulerService.class);
//		if (scheduler.executeDump()) {
//			logger.info("数据存储完成");
//		}
//		logger.error("shutdown server==============end dump to database");

	}
}
