package com.game.core.net;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;

public class Request extends CommonMessage {

	private Integer playerId; // 玩家ID

	private long timestamp;// 请求时间

	public Integer getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Integer accountId) {
		this.playerId = accountId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public static Request valueOf(short cmd, byte[] params) {
		Request request = new Request();
		request.setCode(cmd);
		request.setBody(params);
		request.setTimestamp(System.currentTimeMillis());
		return request;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T parseParams(GeneratedMessage.Builder builder) throws InvalidProtocolBufferException {
		return (T) builder.mergeFrom(getBody()).buildPartial();
	}
}
