package com.game.ts.net.bean;

import com.alibaba.fastjson.JSONObject;

/**
 * TS和TC交互的消息结构体
 * 
 * @author jason.lin
 *
 */
public class TSBean implements Cloneable{

	// Action 操作
	private String action;

	// 序列号
	private long id;

	// tsc指令
	private int tscCmd;

	// sessionId
	private String sessionId;

	// 消息长度
	private int length;

	// 消息内容
	private JSONObject content;

	// ip地址
	private String ip;

	// 一级指令
	private String code;

	// 二级指令
	private String subCode;

	// 返回结果
	private boolean result = true;
	// 用户名
	private String userName;

	private String directType = "up";

	// 是否配置
	private boolean config;

	// 描述
	private String desc;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public TSBean() {

	}

//	public JSONObject getContentJson() {
//		if (StringUtil.isEmpty(content)) {
//			return null;
//		}
//
//		return JSONObject.parseObject(content);
//	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public JSONObject getContent() {
		return content;
	}

	public void setContent(JSONObject content) {
		this.content = content;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSubCode() {
		return subCode;
	}

	public void setSubCode(String subCode) {
		this.subCode = subCode;
	}

	public int getTscCmd() {
		return tscCmd;
	}

	public void setTscCmd(int tscCmd) {
		this.tscCmd = tscCmd;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public boolean getResult() {
		return this.result;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDirectType() {
		return directType;
	}

	public void setDirectType(String directType) {
		this.directType = directType;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean isConfig() {
		return config;
	}

	public void setConfig(boolean config) {
		this.config = config;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Object clone()
	{
		TSBean bean=null;
		try
		{
			bean = (TSBean) super.clone();
			bean.setContent((JSONObject) content.clone());
		}
		catch(CloneNotSupportedException e)
		{
			System.out.println(e.toString());
		}
		return bean;
	}
}
