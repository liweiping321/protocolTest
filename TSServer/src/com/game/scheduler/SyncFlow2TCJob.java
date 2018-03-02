package com.game.scheduler;

import java.util.List;

import com.game.cache.Cache;
import com.game.tc.TCClientInfo;
import com.game.tc.module.StatisticModule;
import com.game.utils.DataUtil;

/**
 * 同步流统计给TC的任务
 * @author jason.lin
 *
 */
public class SyncFlow2TCJob implements Runnable{

	@Override
	public void run() {
		List<TCClientInfo> list = Cache.getAllTc();
		if(DataUtil.isEmpty(list)){
			return;
		}
		
		for(TCClientInfo tc : list){
			tc.getModule(StatisticModule.class).syncFlowData2TC();
		}
		
	}

}
