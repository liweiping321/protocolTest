package com.game.ts.net.bean;

/**
 * 账号信息
 * @author jason.lin
 *
 */
public class PlayerInfo {
	// 账号名
	private String userName;
	
	// 账号id
	private int userId;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
}
