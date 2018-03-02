package com.game.core.net;

import com.google.protobuf.GeneratedMessage;

/**
 * 响应消息
 * 
 * @author Administrator
 * 
 */
public class Response extends CommonMessage {

	/** 响应状态：0-成功，其他为具体错误码 **/
	private short status;

	public short getStatus() {
		return status;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	public static Response valueOf(short cmd, GeneratedMessage message, short status) {
		Response response = valueOf(cmd, message);
		response.setStatus(status);
		return response;
	}

	public static Response valueOf(short cmd, GeneratedMessage message) {
		Response response = valueOf(cmd);
		response.setBody(message.toByteArray());
		return response;
	}
	
	public static Response valueOf(short cmd, byte[] body) {
		Response response = valueOf(cmd);
		response.setBody(body);
		return response;
	}

	public static Response valueOf(short cmd) {
		Response response = new Response();
		response.setCode(cmd);
		response.setStatus((short) 0);
		return response;
	}

}
