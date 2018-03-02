package com.game.type;

import com.game.utils.StringUtil;

/**
 * 发包模式
 * 
 * @author jason.lin
 *
 */
public enum UpdateActionType {
	/** 普通模式 **/
	COMMOM(""), 
	
	/** 延迟发送 **/
	DELAY("DELY"),
	
	/** 重复发包 **/
	REPEAT("REP"),
	;
	
	public String key;
	private UpdateActionType(String key){
		this.key = key;
	}
	
	public static UpdateActionType parse(String key){
		if(StringUtil.isEmpty(key)){
			return COMMOM;
		}
		
		for(UpdateActionType type : UpdateActionType.values()){
			if(type.key.equalsIgnoreCase(key)){
				return type;
			}
		}
		return COMMOM;
	}
}
