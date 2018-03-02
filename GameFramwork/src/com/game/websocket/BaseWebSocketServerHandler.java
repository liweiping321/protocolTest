package com.game.websocket;

import static io.netty.handler.codec.http.HttpHeaderNames.HOST;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

import com.game.utils.LogUtils;

/**
 * websocket处理器
 * @author jason.lin
 */
public abstract class BaseWebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
	// websocket访问路径
	private String basePath;

	private WebSocketServerHandshaker handshaker;

	public BaseWebSocketServerHandler(String path) {
		this.basePath = path;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if (msg instanceof FullHttpRequest) {
			// 处理握手协议
			handleHttpRequest(ctx, (FullHttpRequest) msg);

		} else if (msg instanceof WebSocketFrame) {
			// 正常通信
			handleWebSocketFrame(ctx, (WebSocketFrame) msg);
		}
	}

	/**
	 * 正常通信
	 * @param ctx
	 * @param frame
	 */
	public void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
		if (frame instanceof CloseWebSocketFrame) {
			handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			close(ctx, frame);
			return;
		}
		
		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		
		if (frame instanceof TextWebSocketFrame) {
			String msg = ((TextWebSocketFrame) frame).text();
	    	handleTextMsg(ctx, msg);
		}
	}

	/**
	 * 关闭链接
	 * @param ctx
	 * @param frame
	 */
	public abstract void close(ChannelHandlerContext ctx, WebSocketFrame frame);
	
	/**
	 * 处理文本消息
	 * @param ctx
	 * @param msg
	 */
	public abstract void handleTextMsg(ChannelHandlerContext ctx, String msg) ;
	
	/**
	 * 处理握手协议
	 * 
	 * @param ctx
	 * @param request
	 */
	public void handleHttpRequest(ChannelHandlerContext ctx,
			FullHttpRequest request) {
		// 如果HTTP解码失败，返回HHTP异常
		if (!request.decoderResult().isSuccess()
				|| (!"websocket".equals(request.headers().get("Upgrade")))) {
			sendHttpResponse(ctx, request, new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}

		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
				getWebSocketLocation(request), null, true);
		handshaker = wsFactory.newHandshaker(request);
		if (handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx
					.channel());
		} else {
			// 握手成功之后,业务逻辑 注册
			ChannelFuture channelFuture = handshaker.handshake(ctx.channel(),
					request);
			
			LogUtils.info("handleHttpRequest result: " + channelFuture.isSuccess());
			
			// 连接成功
			if (channelFuture.isSuccess()) {
				connect(ctx, request);
			}
		}
	}

	/**
	 * 连接成功
	 * @param ctx
	 * @param request
	 */
	public abstract void connect(ChannelHandlerContext ctx, FullHttpRequest request);
	
	/**
	 * 发送响应码
	 * 
	 * @param ctx
	 * @param req
	 * @param res
	 */
	public void sendHttpResponse(ChannelHandlerContext ctx,
			FullHttpRequest req, FullHttpResponse res) {
		if (res.status().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
					CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
			HttpUtil.setContentLength(res, res.content().readableBytes());
		}

		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	/**
	 * 组装websocket完整路径
	 * 
	 * @param req
	 * @return
	 */
	public String getWebSocketLocation(FullHttpRequest req) {
		String location = req.headers().get(HOST) + basePath;
		return "ws://" + location;
	}
	

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		// 当出现异常就关闭连接
		Channel incoming = ctx.channel();
		LogUtils.error("Client:" + incoming.remoteAddress() + "异常", cause);
		ctx.close();
		
	}
	
}
