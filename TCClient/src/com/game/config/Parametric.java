package com.game.config;

/**
 * @author jianpeng.zhang
 * @since 2017/2/28.
 */

import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;

@Root(name = "Parametric")
public class Parametric {

	@ElementArray(name = "String", entry = "Value")
	private String[] strings;

	@ElementArray(name = "Number", entry = "Value")
	private Long[] numbers;

	@ElementArray(name = "Decimal", entry = "Value")
	private Double[] decimals;

	@ElementArray(name = "Boolean", entry = "Value")
	private Boolean[] boos;

	public String[] getStrings() {
		return strings;
	}

	public void setStrings(String[] strings) {
		this.strings = strings;
	}

	public Long[] getNumbers() {
		return numbers;
	}

	public void setNumbers(Long[] numbers) {
		this.numbers = numbers;
	}

	public Double[] getDecimals() {
		return decimals;
	}

	public void setDecimals(Double[] decimals) {
		this.decimals = decimals;
	}

	public Boolean[] getBoos() {
		return boos;
	}

	public void setBoos(Boolean[] boos) {
		this.boos = boos;
	}
}
