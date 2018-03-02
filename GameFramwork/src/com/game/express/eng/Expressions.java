/**
 * 
 */
package com.game.express.eng;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author lip.li
 * @date 2017-2-15
 */
public class Expressions {
	/**服务端丢弃不上报TC客户端*/
	public static final String DROP="drop";
	/**不上报TC客户端*/
	public static final String TRANS="trans";
	/**上报到TC客户端*/
	public static final String VIEW="view";
	/**客户端丢弃*/
	public static final String DELETE="delete";
	/**服务端拦截上报到TC客户端*/
	public static final String INTERCEPT="intercept";
	/**默认处理*/
	public static final String DEFAULT="";
	
	public static final String IF="if";
	
	public static final String  ELSE="else";
	
	public static final String  ELSEIF="elseif";
	
	public static final String ACTIONSPLIT=":@";
	
	public static final HashSet<String> SActions=new HashSet<>();
	
	public static final HashSet<String> CActions=new HashSet<>();
	
	public static final HashSet<String> AllActions=new HashSet<>();
	
	static {
		SActions.addAll(Arrays.asList(DROP,TRANS,VIEW,INTERCEPT));
		
		CActions.add(DELETE);
		 
		AllActions.addAll(Arrays.asList(DROP,TRANS,VIEW,INTERCEPT,DELETE));
	}
	
	public static boolean contains(String action){
		return AllActions.contains(action);
	}
	
	public static boolean containsClient(String action){
		return CActions.contains(action);
	}
	
	public static boolean containsServer(String action){
		return SActions.contains(action);
	}
}
