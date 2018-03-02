package com.game.bean;

import java.util.ArrayList;
import java.util.List;

public class ProtoFile {
	private String fileName;
	
	private String dirName;
	
	private String javaPackage;
	
	private String className;
	
	private List<ProtoMessage> protoMsg=new ArrayList<>();

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getJavaPackage() {
		return javaPackage;
	}

	public void setJavaPackage(String javaPackage) {
		this.javaPackage = javaPackage;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<ProtoMessage> getProtoMsg() {
		return protoMsg;
	}

	public void setProtoMsg(List<ProtoMessage> protoMsg) {
		this.protoMsg = protoMsg;
	}

	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}
	
	
	
}
