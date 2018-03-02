/**
 * 
 */
package com.game.express.eng;

import com.game.consts.Splitable;

/**
 * @author lip.li
 * @date 2017-2-13
 */
public class ExpressParse{
	 private  int  startCount=0;
	 private int endCount=0;
	 private int andCount=0;
	 private int orCount=0;
	 
	 private String currChildrenOp;
	 
	 private  StringBuffer buff=new StringBuffer();
	 
	 private String expression;
	 
	 private Expression parent;
	 
	 
	public ExpressParse(String currChildrenOp, String expression,Expression parent) {
		super();
		this.currChildrenOp = currChildrenOp;
		this.expression = expression;
		this.parent=parent;
	}
 
	 
	private void createChild(String currOp) {
		Expression myExreExpression=new Expression(currChildrenOp,buff.toString());
		parent.getChildren().add(myExreExpression);
		myExreExpression.parse();
		
		reset(currOp);
	}
 
	private void reset(String currOp) {
		startCount = 0;
		endCount = 0;
		andCount = 0;
		orCount = 0;
		currChildrenOp = currOp;
		buff = new StringBuffer();

	}


	private void charCount(char c) {
		switch (c) {
		case '(':
			startCount++;
			break;

		case ')':
			endCount++;
			break;

		case '&':
			if (startCount == endCount) {
				andCount++;
			}
			break;

		case '|':
			if (startCount == endCount) {
				orCount++;
			}
			break;
		default:
			break;
		}
	} 
	
	public void parse() {
		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);

			charCount(c);

			if ((andCount > 0 || orCount > 0)) {

				if (andCount == 2) {
					createChild(Splitable.AND);

				}
				if (orCount == 2) {
					createChild(Splitable.OR);
				}

			} else {
				buff.append(c);
			}

		}

		if (expression.equals(buff.toString())) {
			throw new ExpressException("expression  Syntax error! ");
		}
		createChild(Splitable.AND);
	}
}
