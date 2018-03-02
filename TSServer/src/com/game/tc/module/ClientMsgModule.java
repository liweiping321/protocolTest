package com.game.tc.module;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;
import com.game.config.ServerConfig;
import com.game.player.BaseModule;
import com.game.tc.TCClientInfo;
import com.game.ts.net.bean.TSBean;
import com.game.type.DirectType;
import com.game.type.SendState;

/**
 * 修改包内容
 * @author jason.lin
 *
 */
@SuppressWarnings("serial")
public class ClientMsgModule extends BaseModule<TCClientInfo>{
	
	private ConcurrentLinkedQueue<TSBean> msgCacheList=new ConcurrentLinkedQueue<>();
	/**缓存计数器*/
	private AtomicInteger cahceCounter=new AtomicInteger();
	
	public ClientMsgModule(TCClientInfo player) {
		super(player);
	}

	/**
	 * 发送给TC
	 * @param session
	 * @param jsonFormat
	 */
	public void sendToTc(TSBean tsBean) {
		//停止监听
		if(player.getState()==SendState.stop){
			return ;
		}
		if(!player.isUpUnConfig()&&!tsBean.isConfig()){
			return ;
		}
		//上行数据拒绝
		if(!player.isUp()&&tsBean.getDirectType().equals(DirectType.UP.name())){
			return ;
		}
		//下行数据拒绝
		if(!player.isDown()&&tsBean.getDirectType().equals(DirectType.DOWN.name())){
			return ;
		}
		
		//后续添加消息长都，消息号，某一个字段 等规则过滤
		
		//暂停发送,开始缓存
		if(player.getState()==SendState.pause){
			addCacheMsg(tsBean);
		}else if(player.getState()==SendState.start){
			writeMsBean(tsBean);
		}
	}
	
	/**添加缓存消息*/
	private void addCacheMsg(TSBean tsBean) {
		
		if(cahceCounter.get()>=ServerConfig.msgCacheSize){
			msgCacheList.poll();
		}else {
			cahceCounter.incrementAndGet();
		}
		
		msgCacheList.add(tsBean);
		
	}

	private void writeMsBean(TSBean tsBean) {
		String json = JSON.toJSONString(tsBean);
		player.write(json);
	}
	
	public void clearCacheMsgs(){
		msgCacheList.clear();
		cahceCounter.set(0);
	}
	
	public void sendCacheMsgs(){
		for(TSBean tsBean:msgCacheList){
			writeMsBean(tsBean);
		}
		clearCacheMsgs();
	}
 
}
