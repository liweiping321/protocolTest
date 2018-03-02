package com.game.net;

import com.game.cache.Cache;
import com.game.express.eng.FilterExpressChain;
import com.game.http.WebHandler;
import com.game.http.entity.HttpRequestMsg;
import com.game.http.entity.HttpResponseMsg;
import com.game.http.entity.RequestMapping;
import com.game.http.entity.ResponseStatus;
import com.game.tc.TCClientInfo;
import com.game.ts.net.bean.ListenBean;
import com.game.type.SendState;

@RequestMapping("/api")
public class TcWebHandler extends WebHandler{
    
	/**
	 * 上行开关设置
	 * @return
	 */
	@RequestMapping("/up")
	public HttpResponseMsg<?> up(HttpRequestMsg<Boolean> reqMsg){
		
		TCClientInfo clientInfo=Cache.getTCClientInfo(reqMsg.getSessionId());
		clientInfo.setUp(reqMsg.convertBody(Boolean.class));
		
		return new HttpResponseMsg<>();
	}
	
	/**
	 * 未定义协议开关设置
	 * @return
	 */
	@RequestMapping("/upUnConfig")
	public HttpResponseMsg<?> upUnConfig(HttpRequestMsg<Boolean> reqMsg){
		
		TCClientInfo clientInfo=Cache.getTCClientInfo(reqMsg.getSessionId());
		clientInfo.setUpUnConfig(reqMsg.convertBody(Boolean.class));
		
		return new HttpResponseMsg<>();
	}
	/**
	 * 检查TC客户端是否存在
	 */
	public HttpResponseMsg<?> checkTCClientInfo(String sessionId) {
		TCClientInfo clientInfo=Cache.getTCClientInfo(sessionId);
		if(clientInfo==null){
			return new  HttpResponseMsg<>(ResponseStatus.FAIL,"TC客户端已经不存在 sessionId="+sessionId);
		}
		return null;
	}
	
	/**
	 * 下行开关设置
	 * @param reqMsg
	 * @return
	 */
	@RequestMapping("/down")
	public HttpResponseMsg<?> down(HttpRequestMsg<Boolean> reqMsg){
		TCClientInfo clientInfo=Cache.getTCClientInfo(reqMsg.getSessionId());
		
		clientInfo.setDown(reqMsg.convertBody(Boolean.class));
		
		return new HttpResponseMsg<>();
	}
	
	/**
	 * 启动
	 * @param reqMsg
	 * @return
	 */
	@RequestMapping("/start")
	public HttpResponseMsg<?> start(HttpRequestMsg<?> reqMsg){
		
		TCClientInfo clientInfo=Cache.getTCClientInfo(reqMsg.getSessionId());
		clientInfo.setState(SendState.start);
		
		return new HttpResponseMsg<>();
	}
	
	/**
	 * 暂停
	 * @param reqMsg
	 * @return
	 */
	@RequestMapping("/pause")
	public HttpResponseMsg<?> pause(HttpRequestMsg<?> reqMsg){
		TCClientInfo clientInfo=Cache.getTCClientInfo(reqMsg.getSessionId());
		
		clientInfo.setState(SendState.pause);
		
		return new HttpResponseMsg<>();
		 
	}
	
	/**
	 * 暂停
	 * @param reqMsg
	 * @return
	 */
	@RequestMapping("/stop")
	public HttpResponseMsg<?> stop(HttpRequestMsg<?> reqMsg){
		TCClientInfo clientInfo=Cache.getTCClientInfo(reqMsg.getSessionId());
		
		clientInfo.setState(SendState.stop);
		
		return new HttpResponseMsg<>();
	}
	
	
	@RequestMapping("/listenUrl")
	public HttpResponseMsg<?> listenUrl(HttpRequestMsg<ListenBean> reqMsg){
		TCClientInfo clientInfo=Cache.getTCClientInfo(reqMsg.getSessionId());
		clientInfo.setListenBean(reqMsg.convertBody(ListenBean.class));
		return new HttpResponseMsg<>();
	}
	
	@RequestMapping("/setFilter")
	public HttpResponseMsg<?> setFilter(HttpRequestMsg<String> reqMsg){
		TCClientInfo clientInfo=Cache.getTCClientInfo(reqMsg.getSessionId());
		
		FilterExpressChain expressChain=new FilterExpressChain(reqMsg.getBody());
		expressChain.init();
		clientInfo.setFilterChain(expressChain);
		
		return new HttpResponseMsg<>();
	}
}
