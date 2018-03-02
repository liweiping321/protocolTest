package com.game.protocal;

import java.util.List;

/**
 * 二进制协议配置
 * 
 * @author jason.lin
 *
 */
public class BinProperty {
	// 类型
	private String type;

	// 字段名
	private String name;

	// 描述
	private String desc;

	// List数组长度类型 （int 或 short）
	private String sizeType;

	// 数组元素
	private List<BinProperty> list;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getSizeType() {
		return sizeType;
	}

	public void setSizeType(String sizeType) {
		this.sizeType = sizeType;
	}

	public List<BinProperty> getList() {
		return list;
	}

	public void setList(List<BinProperty> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "BinProperty [type=" + type + ", name=" + name + ", desc=" + desc + ", sizeType=" + sizeType + ", list="
				+ list + "]\n";
	}
}
