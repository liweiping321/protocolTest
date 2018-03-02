package com.game.scheduler;

import com.game.TSServer;
import com.game.cache.Cache;


/**
 * 协议配置加载任务
 * @author jason.lin
 *
 */
public class ProtoConfigLoadJob extends BaseWatchJob{

	public ProtoConfigLoadJob(String loadPath) {
		super(loadPath);
	}

	@Override
	public void excute(String path) {
		if(!path.endsWith(".xml")){
			return;
		}
		
		Cache.init(TSServer.getGameProtoPath());
	}
}
