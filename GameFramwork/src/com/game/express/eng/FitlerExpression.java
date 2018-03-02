/**
 * 
 */
package com.game.express.eng;



/**
 * @author lip.li
 * @date 2017-2-14
 */
public class FitlerExpression {

			
	private ExpressionSegment ifSegment;
	
	private ExpressionSegment elseIfSegment;
	
	private ExpressionSegment elseSegment;
 
	
	
	/**
	 * @param tsBean
	 */
	public String filter(Object tsBean) {
		if(ifSegment!=null){
			if(ifSegment.match(tsBean)){
				 return ifSegment.getAction();
			 }
			
			if(elseIfSegment!=null){
				if(elseIfSegment.match(tsBean)){
					return elseIfSegment.getAction();
				}
				
				if(elseSegment!=null){
					return elseSegment.getAction();
				}
			}else{
				return Expressions.TRANS;
			}
		}
		return Expressions.DEFAULT;
	}
	
	void init(String[] items){
		if (items[0].equalsIgnoreCase(Expressions.IF)) {
			ifSegment = createSegment(items[1], items[2]);
		} else if (items[0].equalsIgnoreCase(Expressions.ELSEIF)) {
			if(ifSegment==null){
				throw new ExpressException("expression  Syntax error, if condition is null ");
			}
			if(elseIfSegment!=null){
				throw new ExpressException("expression  Syntax error,has multi-elseif expression ");
			}
			if(elseSegment!=null){
				throw new ExpressException("expression  Syntax error, else must after elseif expression ");
			}
			elseIfSegment = createSegment(items[1], items[2]);
		}else if (items[0].equalsIgnoreCase(Expressions.ELSE)) {
			if(ifSegment==null){
				throw new ExpressException("expression  Syntax error, if condition is null ");
			}
			if(elseSegment!=null){
				throw new ExpressException("expression  Syntax error, has multi-else expression ");
			}
			elseSegment = createSegment(items[2]);
		}
 
	}
	
	private ExpressionSegment createSegment(String expressionString,String action){
		Expression expression=new Expression(expressionString);
		expression.parse();
		expression.checkSyntax();
		 
		ExpressionSegment segment=new ExpressionSegment();
		segment.setAction(action);
		segment.setExpression(expression);
		
		return segment;
	}
	private ExpressionSegment createSegment(String action){
		
		ExpressionSegment segment=new ExpressionSegment();
		segment.setAction(action);
		
		return segment;
	}
	public ExpressionSegment getIfSegment() {
		return ifSegment;
	}

	public void setIfSegment(ExpressionSegment ifSegment) {
		this.ifSegment = ifSegment;
	}

	public ExpressionSegment getElseIfSegment() {
		return elseIfSegment;
	}

	public void setElseIfSegment(ExpressionSegment elseIfSegment) {
		this.elseIfSegment = elseIfSegment;
	}

	public ExpressionSegment getElseSegment() {
		return elseSegment;
	}

	public void setElseSegment(ExpressionSegment elseSegment) {
		this.elseSegment = elseSegment;
	}
	
	
 

	@Override
	public String toString() {
		return "FitlerExpression [ifSegment=" + ifSegment + ", elseIfSegment="
				+ elseIfSegment + ", elseSegment=" + elseSegment + "]";
	}


	class ExpressionSegment{
		private Expression expression;
		
		private String  action;
 
		boolean match(Object  tsBean){
			return expression.match(tsBean);
		}
		
		public Expression getExpression() {
			return expression;
		}


		public void setExpression(Expression expression) {
			this.expression = expression;
		}


		public String getAction() {
			return action;
		}


		public void setAction(String action) {
			this.action = action;
		}

		@Override
		public String toString() {
			return "ExpressionSegment [expression=" + expression + ", action="
					+ action + "]";
		}
		
	}
 
}
