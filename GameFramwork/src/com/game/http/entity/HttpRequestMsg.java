package com.game.http.entity;

import org.apache.commons.beanutils.ConvertUtils;

import com.alibaba.fastjson.JSONObject;



public class HttpRequestMsg<V> {
	
	private String sessionId;
	
	private V  body;
	
	
	public HttpRequestMsg(){
		
	}
	
	public HttpRequestMsg(String sessionId) {
		super();
		this.sessionId=sessionId;
	}
	
	public HttpRequestMsg(String sessionId, V body) {
		super();
		this.sessionId = sessionId;
		this.body = body;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public V getBody() {
		return body;
	}

	public void setBody(V body) {
		this.body = body;
	}
	
	
	@SuppressWarnings("unchecked")
	public V convertBody(Class<V> clazz ){
		if(body==null){
			return null;
		}
		if(body instanceof JSONObject){
			return ((JSONObject)body).toJavaObject(clazz);
		}
		return (V) ConvertUtils.convert(body, clazz);
		 
	}
}
