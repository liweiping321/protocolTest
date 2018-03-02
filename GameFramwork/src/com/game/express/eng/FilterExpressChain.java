/**
 * 
 */
package com.game.express.eng;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.game.ts.net.bean.TSBean;
import com.game.utils.StringUtil;

/**
 * @author lip.li
 * @date 2017-2-14
 */
public class FilterExpressChain {
	
	private String expressionStr;
	 
	private List<FitlerExpression> fitlerExpressions=new ArrayList<FitlerExpression>();
	
	
	public FilterExpressChain() {
		super();
	}

	public FilterExpressChain(String expressionStr) {
		super();
		this.expressionStr = expressionStr;
	}
	
	public void init(){
	 
		expressionStr= StringUtils.trim(expressionStr);
		if(StringUtils.isEmpty(expressionStr)){
			return ;
		}
		
		String[] expressArr=expressionStr.split( "\\r|\\n|\\t");
		if(ArrayUtils.isEmpty(expressArr)){
			return ;
		}

		for(String expressionStr:expressArr){
		   String[] items=expressionStr.split(Expressions.ACTIONSPLIT);
		   
		   if(ArrayUtils.isEmpty(items)||items.length!=2){
			   throw new ExpressException("格式错误{"+expressionStr+"}");
		   }
		  
		   StringUtil.trimArray(items);
		   
		   if(!Expressions.contains(items[1])){
			   throw new ExpressException("自定义标签"+Expressions.AllActions+"不支持 ["+items[1]+"], expressionStr="+expressionStr);
		   }
		   
		
			if (items[0].startsWith(Expressions.IF)) {
				initFilterExpression(items, Expressions.IF);
			} else if (items[0].startsWith(Expressions.ELSEIF)) {
				initFilterExpression(items, Expressions.ELSEIF);
			} else if (items[0].startsWith(Expressions.ELSE)) {
				initFilterExpression(items, Expressions.ELSE);
			} else {
				throw new ExpressException("格式错误{" + items[0] + "},{"
						+ expressionStr + "}");
			}
			
			 
		}
		
	
		
		 
	}

	private void initFilterExpression(String[] items,String boolString) {
		
		FitlerExpression fitlerExpression=null;
		if(boolString.equals(Expressions.IF)){
			if(fitlerExpressions.size()>0){
				 throw new ExpressException("expression  Syntax error, has multi-if !");
			}
			fitlerExpression=new FitlerExpression();
			
		}else{
			if(fitlerExpressions.size()==0){
				 throw new ExpressException("expression  Syntax error, if must first row !");
			}
			fitlerExpression=fitlerExpressions.get(fitlerExpressions.size()-1);
			 
		}
		
		String subItem =items[0].substring(boolString.length());
		fitlerExpression.init(new String[]{boolString,subItem,items[1]});
		
		
		if(boolString.equals(Expressions.IF)){
			fitlerExpressions.add(fitlerExpression);
		}
		
	}


	public String filter(TSBean tsBean){
		Object fitlerObject=JSON.toJSON(tsBean);
		for(FitlerExpression fitlerExpression:fitlerExpressions){
			String result=fitlerExpression.filter(fitlerObject);
			if(!StringUtils.isEmpty(result)){
				return result;
			}
		}
		return Expressions.DEFAULT;
	}


	@Override
	public String toString() {
		return "FilterExpressChain [expressionStr=" + expressionStr
				+ ", fitlerExpressions=" + fitlerExpressions + "]";
	}
	
	
}
