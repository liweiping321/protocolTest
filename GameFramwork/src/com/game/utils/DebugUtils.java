package com.game.utils;

/**
 * 开发工具
 * @author jason.lin
 *
 */
public class DebugUtils {

	/**
	 * 是否debug模式
	 * @return
	 */
	public static boolean debug(){
		return debug("debug");
	}
	
	/**
	 * 是否debug模式
	 * @param debug
	 * @return
	 */
	public static boolean debug(String debug){
		String value = System.getProperty(debug);
		if(value == null){
			return false;
		}
		return  Boolean.valueOf(value);
	}
}
