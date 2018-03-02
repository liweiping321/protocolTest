/**
 * 
 */
package com.game.express.eng;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONPath;

/**
 * @author lip.li
 * @date 2017-2-9
 */
public class ExpressEngine {
	
	private static final Map<String,Expression> expressionMap=new ConcurrentHashMap<>();
	/**
	 * 根据路径获取值
	 */
	public static Object get(Object rootObject, String path) {
		Objects.requireNonNull(rootObject);
		if(StringUtils.isEmpty(path)){
			throw new  NullPointerException();
		}
		
		return JSONPath.eval(rootObject, path);
	}
	
	/**
	 * 表达式匹配
	 */
	public static boolean match(Object rootObject, String expressions) {
		Objects.requireNonNull(rootObject);

		if(StringUtils.isEmpty(expressions)){
			throw new  NullPointerException();
		}
		
		Expression expression = getExpression(expressions);
		 
		return expression.match(rootObject);
	}
	
	
	/**
	 * 检查表达式语法
	 */
	public static void checkSyntax(String expressions){
		if(StringUtils.isEmpty(expressions)){
			throw new  NullPointerException();
		}
		getExpression(expressions);
		
	}
	
	/**
	 * 设置指定路径的志
	 */
	public static void set(Object rootObject,String path,Object value){
		Objects.requireNonNull(rootObject);
		if(StringUtils.isEmpty(path)){
			throw new  NullPointerException();
		}
		JSONPath.set(rootObject, path, value);
	}
	
	private static Expression parseExpression(String expressions) {
	
		Expression expression= new Expression(expressions);
		
		expression.parse();
		
 		expression.checkSyntax();
		
		return expression;
	}
	
	private static Expression getExpression(String expressions){
		
		expressions=StringUtils.trim(expressions);
		
		Expression expression=expressionMap.get(expressions);
		if(expression==null){
			expression= parseExpression(expressions);
			if(expression!=null){
				expressionMap.put(expressions, expression);
			}
		}
		return expression;
		
	}

	 
}
