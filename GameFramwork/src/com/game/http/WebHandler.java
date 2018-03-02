package com.game.http;

import com.game.http.entity.HttpResponseMsg;


/**
 * http接口基类
 * @author jason.lin
 *
 */
public abstract class WebHandler {
  
	public abstract HttpResponseMsg<?> checkTCClientInfo(String sessionId);
}
