package com.game.utils.common;


/**
 * 方法参数
 * @author jason.lin
 *
 */
public class MethodPara {
	public Class<?> type;
	
	public MethodPara(Class<?> type) {
		super();
		this.type = type;
	}

	/**
	 * 转换对应类型的参数
	 * @param value
	 * @return
	 */
	public Object getValue(String value){
		if(type == int.class){
			return Integer.valueOf(value);
		}else if(type == boolean.class){
			return Boolean.valueOf(value);
		}else if(type == String.class){
			return value;
		}else {
			return null;
		}
	}
	
	
	
}
