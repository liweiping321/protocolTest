package com.game.ts.net.bean;

/**
 * 流量统计数据
 * 
 * @author jason.lin
 *
 */
public class StatisticFlowBean {

	private int upTotalPackNum; // 上行包总数量

	private int downTotalPackNum; // 下行包总数量

	private int upTotalFlow; // 上行包总流量

	private int downTotalFlow; // 下行包总流量

	private float upAvgPackNum; // 上行包 平均发包数（每妙）

	private float downAvgPackNum; // 下行包 平均发包数（每妙）

	private float upAvgFlowNum; // 上行包 平均流量（每妙）

	private float downAvgFlowNum; // 下行包 平均流量（每妙）

	public int getUpTotalPackNum() {
		return upTotalPackNum;
	}

	public void setUpTotalPackNum(int upTotalPackNum) {
		this.upTotalPackNum = upTotalPackNum;
	}

	public int getDownTotalPackNum() {
		return downTotalPackNum;
	}

	public void setDownTotalPackNum(int downTotalPackNum) {
		this.downTotalPackNum = downTotalPackNum;
	}

	public int getUpTotalFlow() {
		return upTotalFlow;
	}

	public void setUpTotalFlow(int upTotalFlow) {
		this.upTotalFlow = upTotalFlow;
	}

	public int getDownTotalFlow() {
		return downTotalFlow;
	}

	public void setDownTotalFlow(int downTotalFlow) {
		this.downTotalFlow = downTotalFlow;
	}

	public float getUpAvgPackNum() {
		return upAvgPackNum;
	}

	public void setUpAvgPackNum(float upAvgPackNum) {
		this.upAvgPackNum = upAvgPackNum;
	}

	public float getDownAvgPackNum() {
		return downAvgPackNum;
	}

	public void setDownAvgPackNum(float downAvgPackNum) {
		this.downAvgPackNum = downAvgPackNum;
	}

	public float getUpAvgFlowNum() {
		return upAvgFlowNum;
	}

	public void setUpAvgFlowNum(float upAvgFlowNum) {
		this.upAvgFlowNum = upAvgFlowNum;
	}

	public float getDownAvgFlowNum() {
		return downAvgFlowNum;
	}

	public void setDownAvgFlowNum(float downAvgFlowNum) {
		this.downAvgFlowNum = downAvgFlowNum;
	}
}
