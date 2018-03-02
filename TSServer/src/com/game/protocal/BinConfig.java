package com.game.protocal;

import java.util.ArrayList;
import java.util.List;

/**
 * 二级制协议
 * @author jason.lin
 *
 */
public class BinConfig {
	// 属性组
	private List<BinProperty> list = new ArrayList<>();

	public List<BinProperty> getList() {
		return list;
	}

	public void setList(List<BinProperty> list) {
		this.list = list;
	}
	
}
