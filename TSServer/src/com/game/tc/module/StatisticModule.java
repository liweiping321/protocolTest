package com.game.tc.module;

import com.alibaba.fastjson.JSON;
import com.game.player.BaseModule;
import com.game.tc.TCClientInfo;
import com.game.ts.net.bean.StatisticFlowBean;
import com.game.ts.net.bean.TSBean;
import com.game.ts.net.consts.TSCCmd;
import com.game.type.DirectType;

/**
 * 统计模块
 * @author jason.lin
 *
 */
public class StatisticModule extends BaseModule<TCClientInfo> {
	private static final long serialVersionUID = 1L;
	// 总流量 上行和下行
	private int[] totalFlowsPack = new int[2];
	
	// 包的数量 上行和下行
	private int[] totalFlowsNum = new int[2];
	
	// 第一次收到包的时间
	private long firstPackTime = 0;

	public StatisticModule(TCClientInfo player) {
		super(player);
	}

	/**
	 * 统计流量
	 * @param length
	 * @param directType
	 */
	public void staticFlow(int length, DirectType directType) {
		int index = getDirectIndex(directType);
		totalFlowsPack[index] += length;
		totalFlowsNum[index] ++;
		
		// 设置第一次收包时间
		if(firstPackTime == 0){
			firstPackTime = System.currentTimeMillis();
		}
	}

	/**
	 * 获取上下行数据索引
	 * @param directType
	 * @return
	 */
	public int getDirectIndex(DirectType directType){
		if(directType == DirectType.UP){
			return 0;
		}
		return 1;
	}

	/**
	 * 同步流统计数据给tc
	 */
	public void syncFlowData2TC() {
		TSBean tsBean=new TSBean();
		tsBean.setTscCmd(TSCCmd.SYNC_FLOW_STATISTIC);
		tsBean.setSessionId(player.getId());
		
		// 组装统计数据
		StatisticFlowBean flowBean = getStatisticFlowBean();
		String jsonStr = JSON.toJSONString(flowBean);
		tsBean.setContent(JSON.parseObject(jsonStr));
		
		String json = JSON.toJSONString(tsBean);
		player.write(json);
	}

	/**
	 * 组装统计数据
	 * @return
	 */
	private StatisticFlowBean getStatisticFlowBean() {
		int passTime = 1;// 单位是秒
		if(firstPackTime > 0){
			passTime = (int)((System.currentTimeMillis() - firstPackTime) / 1000);
			passTime = passTime <= 1? 1 : passTime;
		}
		
		StatisticFlowBean bean = new StatisticFlowBean();
		bean.setUpTotalPackNum(totalFlowsNum[0]);
		bean.setDownTotalPackNum(totalFlowsNum[1]);
		bean.setUpTotalFlow(totalFlowsPack[0]);
		bean.setDownTotalFlow(totalFlowsPack[1]);
		
		// 平均值
//		DecimalFormat format = new  DecimalFormat("#.##");   
//		bean.setUpAvgPackNum(format.format(totalFlowsNum[0] * 1.0 / passTime));
//		bean.setDownAvgPackNum(format.format(totalFlowsNum[1] * 1.0 / passTime));
//		bean.setUpAvgFlowNum(format.format(totalFlowsPack[0] * 1.0 / passTime));
//		bean.setDownAvgFlowNum(format.format(totalFlowsPack[1] * 1.0 / passTime));
		
		bean.setUpAvgPackNum((float)(totalFlowsNum[0] * 1.0 / passTime));
		bean.setDownAvgPackNum((float)(totalFlowsNum[1] * 1.0 / passTime));
		bean.setUpAvgFlowNum((float)(totalFlowsPack[0] * 1.0 / passTime));
		bean.setDownAvgFlowNum((float)(totalFlowsPack[1] * 1.0 / passTime));
		return bean;
	}
	
}
