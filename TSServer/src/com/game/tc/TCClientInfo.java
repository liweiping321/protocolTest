package com.game.tc;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.session.IoSession;

import com.alibaba.fastjson.JSON;
import com.game.cache.Cache;
import com.game.consts.CommonConsts;
import com.game.express.eng.FilterExpressChain;
import com.game.player.obj.BaseOnlinePlayer;
import com.game.proxy.ConnectProxy;
import com.game.tc.module.ClientMsgModule;
import com.game.tc.module.ConnectedModule;
import com.game.tc.module.StatisticModule;
import com.game.tc.module.TestModule;
import com.game.tc.module.UpdateModule;
import com.game.ts.net.bean.ListenBean;
import com.game.ts.net.bean.TSBean;
import com.game.type.SendState;
import com.game.utils.LogUtils;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * tc客户端对象
 * @author jason.lin
 *
 */
public class TCClientInfo extends BaseOnlinePlayer{
	private static final long serialVersionUID = 2543044597477921299L;

	private IoSession session;
	/**是否上传上行数据*/
	private boolean up=true;
	/**是否上传下行数据*/
	private boolean down=true;
	/**TS往TC发送状态*/
	private SendState state=SendState.start;
	/**TC监听*/
	private ListenBean listenBean=new ListenBean();
	
	/**是否上传未定义协议数据*/
	private boolean upUnConfig=false;
	
	/**被监听的连接代理*/
	private final Set<ConnectProxy> beListenerProxy=new CopyOnWriteArraySet<>();
	
	/**是否h5TC客户端*/
	private boolean isH5Tc = false;
	
	/**websocket通道*/
	private Channel channel;
	
	private FilterExpressChain filterChain=new FilterExpressChain();
	
	public TCClientInfo(IoSession session){
		this.session = session;
		initModules();
	}
	
	public TCClientInfo(Channel channel) {
		isH5Tc = true;
		this.channel = channel;
		initModules();
	}

	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}

	public ListenBean getListenBean() {
		return listenBean;
	}

	@Override
	public void initModules() {
		addModule(new UpdateModule(this));
		addModule(new ClientMsgModule(this));
		addModule(new TestModule(this));
		addModule(new ConnectedModule(this));
		addModule(new StatisticModule(this));
		
		initBeListenerProxy();
	}

	/**
	 * 初始化可以监听的连接代理
	 */
	private void initBeListenerProxy() {
		 for(ConnectProxy connectProxy:  Cache.getAllConnectProxy()){
			 addConnectProxy(connectProxy);
		 }
	}

	/**
	 * 返回结果
	 * @param tscCmd
	 * @param result
	 */
	public void sendResult(int tscCmd, boolean result) {
		TSBean tsBean = new TSBean();
		tsBean.setId(TCService.getAutoIncId());
		tsBean.setTscCmd(tscCmd);
		tsBean.setResult(result);
		session.write(JSON.toJSONString(tsBean));
	}

	/**
	 * 写数据
	 * @param json
	 */
	public void write(String json){
		if(isH5Tc){
			channel.writeAndFlush(new TextWebSocketFrame(json));
		}else if(session != null){
			session.write(json);
		}else {
			LogUtils.error("no write json: " + json);
		}
	}
	

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

	public SendState getState() {
		return state;
	}

	public void setState(SendState state) {
		//状态没有变更
		if(this.state==state){
			return ;
		}
		
		SendState oldState=this.state;
		this.state = state;
		
		if(oldState==SendState.pause){
			if(state==SendState.start){
				//发送缓存数据到TC客户端
				getModule(ClientMsgModule.class).sendCacheMsgs();
				
			}else if(state==SendState.stop){
				//清空缓存消息
				getModule(ClientMsgModule.class).clearCacheMsgs();
			}
		} 
		
	}

	public void setListenBean(ListenBean listenBean) {
		 if(listenBean==null){
			 listenBean=new ListenBean();
		 }
		 //没有什么变化
		 if(listenBean.equals(this.listenBean)){
			 return ;
		 }
		 this.listenBean=listenBean;
		 //移除先前监听的代理
		 removeAllConnectProxy();
		 //重新添加连接代理
		 initBeListenerProxy();
	}

	public Set<ConnectProxy> getProxys() {
		return beListenerProxy;
	}
 
	public void addConnectProxy(ConnectProxy proxy){
		if(checkAddConnectProxy(proxy)){
			beListenerProxy.add(proxy);
			proxy.addTcClientInfo(this);
		}
	}
	
	/**监听客户端是否能监听连接代理*/
	private boolean checkAddConnectProxy(ConnectProxy proxy) {
		
		if(proxy.getPlayerInfo()==null){
			return false;
		}
		// 账号匹配
		if (!StringUtils.isEmpty(listenBean.getUserName())
				&& !"null".equalsIgnoreCase(listenBean.getUserName())) {// 账号匹配

			String[] itemNames = listenBean.getUserName().split(",");
			for (String itemName : itemNames) {
				boolean match = proxy.getPlayerInfo()
						.getUserName().equals(itemName);
				
				if (match) {
					return true;
				}
			}
			return false;

		}
		//IP地址匹配
		if(listenBean.getIp().equals(CommonConsts.MatchAllIp)){
			return true;
		}
		return StringUtils.equals(listenBean.getIp(), proxy.getUserIp());
	}

	public void removeConnectProxy(ConnectProxy proxy){
		 beListenerProxy.remove(proxy);
		 proxy.removeTcClientInfo(this);
	}
	
	public void removeAllConnectProxy(){
		for(ConnectProxy proxy:beListenerProxy){
			proxy.removeTcClientInfo(this);
		}
		beListenerProxy.clear();
	}

	public boolean isUpUnConfig() {
		return upUnConfig;
	}

	public void setUpUnConfig(boolean upUnConfig) {
		this.upUnConfig = upUnConfig;
	}

	public boolean isH5Tc() {
		return isH5Tc;
	}

	public void setH5Tc(boolean isH5Tc) {
		this.isH5Tc = isH5Tc;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getId() {
		if(isH5Tc){
			return toChannleId(channel.id().asShortText());
		}
		return String.valueOf(session.getId());
	}
	
	public FilterExpressChain getFilterChain() {
		return filterChain;
	}

	public void setFilterChain(FilterExpressChain filterChain) {
		this.filterChain = filterChain;
	}

	/**
	 * h5通道加前缀
	 * @param id
	 * @return
	 */
	public static String toChannleId(String id){
		return new StringBuilder("h5").append(id).toString();
	}

}
