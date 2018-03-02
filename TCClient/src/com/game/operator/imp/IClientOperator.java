package com.game.operator.imp;

import com.game.Data.LogLevel;
import com.game.ts.net.bean.TSBean;
import com.game.ts.net.bean.WrapTsBean;

import java.util.List;

/**
 * @author jianpeng.zhang
 * @since 2017/1/13.
 */
public interface IClientOperator {
	/**
	 * 将从服务器接受到的信息显示到客户端
	 */
	public void showMessage(TSBean tsBean);

	/**
	 * 输出日志到客户端控制台
	 */
	public void showLog(LogLevel level, String message);

	/**
	 * 将从服务器接受的统计信息显示到统计面板上
	 */
	public void showSyncMessage(TSBean tsBean);
	/**
	 * 发送选中的数据到服务端
	 */
	public void sendSelectItem();

	/**
	 * 发送已标记的数据
	 */
	public void sendMarkItem();

	/**
	 * 标记为待发送数据
	 */
	public void markItem();

	/**
	 * 发送当前所有未发送数据
	 */
	public void sendNotSendItem();

	/**
	 * 取消选中数据的发送标记
	 */
	public void cancelMarkItem();

	/**
	 * 删除当前所有数据
	 */
	public void deleteAllItem();

	/**
	 * 请求拦截某些特定信息
	 */
	public boolean listen(boolean isCancel);

	/**
	 * 取消特定信息拦截
	 */
	public void cancelListen();

	/**
	 * 是否不用发送二进制数据到客户端
	 */
	public void receiveBinary(boolean isReceive);

	/**
	 * 发送接收上行数据
	 */
	public void up(boolean isUp);

	/**
	 * 发送接收下行数据
	 */
	public void down(boolean isDown);

	/**
	 * 开始接收数据
	 */
	public void start();

	/**
	 * 暂停接收数据
	 */
	public void pause();

	/**
	 * 停止接收数据
	 */
	public void stop();

	/**
	 * 连接服务器
	 */
	public void connect();

	/**
	 * 弹出编辑窗口
	 */
	public void showEditFilterDialog();

	/**
	 * 应用筛选条件
	 */
	public boolean filterWithCondition(String conditionStr);

	/**
	 * 重置筛选条件
	 */
	public void resetFilterCondition();

	/**
	 * 设置查找条件
	 */
	public void setFindCondition();

	/**
	 * 根据条件显示数据
	 */
	public void filterData(String item);

	/**
	 * 设置xml的显示
     */
	public void setXmlData(TSBean tsBean);

	/**
	 * 隐藏列
	 */
	public void hideColumn(String colName, boolean save);

	/**
	 * 显示列
	 */
	public void showColumn(String colName, boolean save);

	/**
	 * 设置回放数据
	 */
	public void setRecordData(List<WrapTsBean> tsBeans);

	/**
	 * 播放录制数据
	 */
	public void replay();

	/**
	 * 暂停播放数据
	 */
	public void pausePlay();

	/**
	 * 停止播放数据
	 */
	public void stopPlay();

	/**
	 * 将选中行上移一行
	 */
	public void moveUp();

	/**
	 * 将选中行下移一行
	 */
	public void moveDown();

	/**
	 * 定位到最后选中的行
	 */
	public void aimAt();

	/**
	 * 设置新消息来时table是否滚到最下边
     */
	public void setFixed(boolean flag);
}
