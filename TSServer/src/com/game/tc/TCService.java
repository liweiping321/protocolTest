package com.game.tc;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.mina.core.session.IoSession;

import com.alibaba.fastjson.JSON;
import com.game.cache.Cache;
import com.game.tc.module.UpdateModule;
import com.game.ts.net.bean.TSBean;
import com.game.ts.net.consts.TSCCmd;
import com.game.utils.LogUtils;

import io.netty.channel.Channel;

/**
 * tc业务层
 * @author jason.lin
 *
 */
public class TCService {
	// 自增长id
	private static AtomicLong autoIncId = new AtomicLong(1);
	
	// 获取当前值并自动加1
	public static long getAutoIncId() {
		return autoIncId.getAndIncrement();
	}

	/**
	 * 添加tcs客户端
	 * @param session
	 */
	public static TCClientInfo addTCClient(IoSession session) {
		TCClientInfo tcClientInfo = new TCClientInfo(session);
		Cache.addTcClient(String.valueOf(session.getId()), tcClientInfo);
		return tcClientInfo;
	}
 
	/**
	 * 消息处理
	 * @param paramObject
	 */
	public static void handleMsg(TCClientInfo tcClientInfo, Object paramObject) {
		try {
			String json = (String)paramObject;
			TSBean tsBean = JSON.parseObject(json, TSBean.class);
			
			switch(tsBean.getTscCmd()){	
					// 修改包信息
				case TSCCmd.UPDATE_PACKAGE:
					tcClientInfo.getModule(UpdateModule.class).updatePackage(tsBean);
					break;
			}
		} catch (Exception e) {
			LogUtils.error("handleMsg paramObject:" + paramObject, e);
		}
		
	}

	/**
	 * 添加h5TC客户端
	 * @param channel
	 * @return
	 */
	public static TCClientInfo addTCClient(Channel channel) {
		TCClientInfo tcClientInfo = new TCClientInfo(channel);
		Cache.addTcClient(tcClientInfo.getId(), tcClientInfo);
		return tcClientInfo;
	}
}
