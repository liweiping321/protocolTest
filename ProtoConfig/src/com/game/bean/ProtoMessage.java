package com.game.bean;

public class ProtoMessage {
	private Integer code;
	
	private String desc;
	
	private String msgName;
	
	private String directType;
	
	private String className;

	public Integer getCode() {
		return code;
	}

	public void setCode(String code) {
		 if(code.startsWith("0x"))
		 {
			 this.code=Integer.parseInt(code.substring(2),16);
		 }else 
		 {
			this. code=Integer.parseInt(code);
		 }
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
	
		this.desc = 	desc.replaceAll("//", "").replaceAll("\\*", "").replaceAll("/", "").replaceAll("\\\\", "").trim();
	}

	public String getMsgName() {
		return msgName;
	}

	public void setMsgName(String msgName) {
		this.msgName = msgName;
	}

	public String getDirectType() {
		return directType;
	}

	public void setDirectType(String directType) {
		this.directType = directType;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	
}
