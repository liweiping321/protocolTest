/**
 * 
 */
package com.game.express.eng;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.game.consts.Splitable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lip.li
 * @date 2017-2-9
 * 
 * 
 * 
 */
public class Expression {
	
	public String op;
	
	private String expression;
	 
	private final List<Expression> children=new ArrayList<Expression>();

	public Expression (String op,String expression){
		this.op=op;
		this.expression=expression;
		trim();
	}
	
	public Expression (String expression){
		this.expression=expression;
		trim();
	}
	public String getOp() {
		return op;
	}


	public void setOp(String op) {
		this.op = op;
	}
	public String getExpression() {
		return expression;
	}


	public void setExpression(String expression) {
		this.expression = expression;
	}


	public List<Expression> getChildren() {
		return children;
	}
	
	public void checkSyntax(){
		if(CollectionUtils.isNotEmpty(children)){
			for(Expression express:children){
				express.checkSyntax();
			}
		}else{
			 JSONPath.contains(JSON.toJSON(new Object()),expression);

		}
	}
	
	public boolean  match(Object rootObject){
		boolean match=true;
		int index=0;
		if(CollectionUtils.isNotEmpty(children)){
			boolean andFlag=true;
			for(Expression express:children){
				index++;
				if(express.getOp().equals(Splitable.AND)){
					if(!express.match(rootObject)||!andFlag){
						andFlag= false;
						continue;
					}
				}else if(express.getOp().equals(Splitable.OR)){
					if(express.match(rootObject)||(andFlag&&index>1)){
						return true;
					}
				}
				 
			}
			
			return andFlag;
			
		}else{
			match= JSONPath.contains(rootObject, expression);
		}
		 
		 return match;
	}
	
   private boolean hasChildren(){
		return expression.contains("&&")||expression.contains("||");
	}
	 
	public void parse() {
		if (!hasChildren()) {
			return;
		}
		ExpressParse expressParse = new ExpressParse(Splitable.AND, expression,this);
		expressParse.parse();
	}

 
	
	/**
	 * 
	 */
	private void trim() {
		trimOp();
		trimKuohu();
		trimOp();

	}

	private void trimKuohu() {
		expression=StringUtils.trim(expression);
		if (expression.endsWith(Splitable.KUOHU_BEHIND)&&expression.startsWith(Splitable.KUOHU_FRONT)) {
			
			String tempExpression=expression;
		 
			tempExpression = tempExpression.substring(1);
			tempExpression = tempExpression.substring(0, tempExpression.length() - 1);
			
			int i1 = 0, i2 = 0;
			for (int i = 0; i < tempExpression.length(); i++) {
				char c = tempExpression.charAt(i);
				if (c == '(') {
					i1++;
				} else if (c == ')') {
					i2++;
				}
				if(i2>i1){
					throw new ExpressException("expression  Syntax error! ");
				}
			}
			expression=tempExpression;
		}
	}

	private void trimOp() {
		expression=StringUtils.trim(expression);
		if(expression.startsWith("||")||expression.startsWith("&&")){
			expression=expression.substring(2);
		}else if(expression.endsWith("||")||expression.endsWith("&&")){
			expression=expression.substring(0, expression.length() - 2);
		}
	}

	@Override
	public String toString() {
		return "Expression [op=" + op + ", expression=" + expression
				+ ", children=" + children + "]";
	}
 
	
	
}
