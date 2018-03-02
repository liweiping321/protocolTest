package com.game.http.entity;

import java.lang.reflect.Method;

import com.game.http.WebHandler;


public class UrlBean {

	private WebHandler webHandler;

	private String methodName;
 
	
	private Method method;
 

	public UrlBean(WebHandler webHandler, String methodName,Method method) {
		this.webHandler = webHandler;
		this.methodName = methodName;
		this.method=method;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	 
	
	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	@Override
	public String toString() {
		return "UrlBean [webHandler=" + webHandler + ", methodName=" + methodName + "]";
	}

	public WebHandler getWebHandler() {
		return webHandler;
	}

	public void setWebHandler(WebHandler webHandler) {
		this.webHandler = webHandler;
	}
	
}
