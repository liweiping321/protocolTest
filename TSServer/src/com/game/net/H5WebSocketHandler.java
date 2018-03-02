package com.game.net;

import com.game.cache.Cache;
import com.game.tc.TCClientInfo;
import com.game.tc.TCService;
import com.game.tc.module.ConnectedModule;
import com.game.utils.LogUtils;
import com.game.websocket.BaseWebSocketServerHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class H5WebSocketHandler extends BaseWebSocketServerHandler {

	public H5WebSocketHandler() {
		super("websocket");
	}
	
	public H5WebSocketHandler(String path) {
		super(path);
	}

	@Override
	public void handleTextMsg(ChannelHandlerContext ctx, String msg) {
		LogUtils.error("收到消息：" + msg);
		String tcChannelId = TCClientInfo.toChannleId(ctx.channel().id().asShortText());
		TCClientInfo tcClientInfo = Cache.getTCClientInfo(tcChannelId);
		TCService.handleMsg(tcClientInfo, msg);
	}

	@Override
	public void connect(ChannelHandlerContext ctx, FullHttpRequest request) {
		String channelId = TCClientInfo.toChannleId(ctx.channel().id().asShortText());
		LogUtils.error("链接成功：" + channelId);

		// 添加tc客户端
		TCClientInfo tcClientInfo = TCService.addTCClient(ctx.channel()); 
		
		//发送TC连接完成消息
		tcClientInfo.getModule(ConnectedModule.class).sendConnected();
	}

	@Override
	public void close(ChannelHandlerContext ctx, WebSocketFrame frame) {
		LogUtils.error("关闭链接：" + ctx.channel().id());
		
		String channelId = TCClientInfo.toChannleId(ctx.channel().id().asShortText());
		TCClientInfo tcClientInfo = Cache.getTCClientInfo(channelId);
		if(tcClientInfo!=null){
			tcClientInfo.removeAllConnectProxy();
			Cache.removeTcClient(channelId);
		}
	}
}
