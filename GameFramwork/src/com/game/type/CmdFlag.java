package com.game.type;

/**
 * cmd标识
 * @author jason.lin
 */
public @interface CmdFlag {
	/**cmd指令***/
	short cmd();
	
	/***描述**/
	String desc();
}
