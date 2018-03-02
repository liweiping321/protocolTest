package com.game.type;

/**
 * 方向类型
 * @author jason.lin
 *
 */
public enum DirectType {
	
	/***上行**/
	UP,
	
	/***下行**/
	DOWN,
	;
	
  public static DirectType  forName(String name){
	for(DirectType directType:  DirectType.values()){
		if(directType.name().equals(name)){
			return directType;
		}
	}
	return null;
  }
}

