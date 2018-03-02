package com.game.operator;

import com.alibaba.fastjson.JSON;
import com.game.ClientUI.Client;
import com.game.ClientUI.PopMenu;
import com.game.ClientUI.dialog.FilterEditorDialog;
import com.game.Data.*;
import com.game.Model.RowNumberTableModel;
import com.game.Model.StatisticsTableModel;
import com.game.TCClient;
import com.game.component.FilterStringPanel;
import com.game.config.TCConfig;
import com.game.consts.TSLink;
import com.game.core.net.ClientConnector;
import com.game.express.eng.FilterExpressChain;
import com.game.http.HttpClient;
import com.game.http.entity.HttpRequestMsg;
import com.game.http.entity.HttpResponseMsg;
import com.game.net.MinaHandler;
import com.game.operator.imp.IClientOperator;
import com.game.scheduler.ThreadPool;
import com.game.style.FilterBoxEditor;
import com.game.task.PlayRecordTask;
import com.game.ts.net.bean.ListenBean;
import com.game.ts.net.bean.StatisticFlowBean;
import com.game.ts.net.bean.TSBean;
import com.game.ts.net.bean.WrapTsBean;
import com.game.util.LoggerUtil;
import com.game.utils.*;
import com.game.utils.language.LanguageMgr;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import static com.game.Data.SaveModule.FilterText;
import static com.game.http.HttpClient.post;
import static com.game.utils.ComponentUtil.insert;
/**
 * @author jianpeng.zhang
 * @since 2017/1/13.
 */
public class ClientProxyOperator implements IClientOperator {
	private Client client;

	public ClientProxyOperator(Client client) {
		this.client = client;
	}

	@Override
	public void showMessage(TSBean tsBean) {

		getTableModel().addDataToTable(tsBean);
		// // 当表格获得焦点且选择的行小于2时会自动滚动到最下面
		// if (client.getTable().hasFocus() && client.getTable().getSelectedRows().length <= 1) {
		//
		// }

		if (!TCConfig.getClientCfg().isFixed())
		{
			Rectangle rect = client.getTable().getCellRect(client.getTable().getRowCount() - 1, 0, true);
			client.getTable().updateUI();
			client.getTable().scrollRectToVisible(rect);
		}
	}

	@Override
	public void showLog(LogLevel level, String message) {
		SimpleAttributeSet attributeSet = null;
		SimpleAttributeSet contentAttr = null;
		String type = "";
		switch (level) {
		case DEBUG:
			attributeSet = StyleUtil.getFontStyle(17);
			contentAttr = StyleUtil.getFontStyle(new Color(0x006FFE), 17, true, false);
			type = " - debug - ";
			break;
		case INFO:
			attributeSet = StyleUtil.getFontStyle(17);
			contentAttr = StyleUtil.getFontStyle(null, 17, true, false);
			type = " - info - ";
			break;
		case WARNING:
			attributeSet = StyleUtil.getFontStyle(17);
			contentAttr = StyleUtil.getFontStyle(new Color(0xFE7900), 17, true, false);
			type = " - warn - ";
			break;
		case ERROR:
			attributeSet = StyleUtil.getFontStyle(17);
			contentAttr = StyleUtil.getFontStyle(new Color(0xFE2416), 17, true, false);
			type = " - error - ";
			break;
		case CRITICAL:
			attributeSet = StyleUtil.getFontStyle(17);
			contentAttr = StyleUtil.getFontStyle(new Color(0xAE0800), 17, true, false);
			type = " - critical - ";
			break;
		}
		insert(client.getMessagePane(), DateUtil.getDataString(), type + message, attributeSet, contentAttr);
	}

	@Override public void showSyncMessage(TSBean tsBean)
	{
		StatisticFlowBean statisticFlowBean =
				JSON.parseObject(tsBean.getContent().toJSONString(), StatisticFlowBean.class);
		((StatisticsTableModel)client.getStatisticsTable().getModel()).showStatisticData(statisticFlowBean);

	}

	/**
	 * 将数据发给服务器，并标记数据为发送成功
	 */
	@SuppressWarnings("unchecked")
	private void sendMessageToServer(int[] rows) {
		if (rows == null) {
			return;
		}

		if (TCClient.connector != null) {
			client.getTable().getSelectedRows();

			RowNumberTableModel model = getTableModel();
			ArrayList<Vector> sendData = new ArrayList<>();

			for (int row : rows) {
				Vector<Object> vector = (Vector<Object>) model.getDataVector().get(row);

				sendData.add(vector);
			}
			sendMessageToServer(sendData);
		} else {
			LoggerUtil.error(client, LanguageMgr.getTranslation("TClient.TServer.DisConnect"));
		}
	}

	private void sendMessageToServer(java.util.List<Vector> sendData) {
		if (sendData == null) {
			return;
		}

		if (TCClient.connector != null) {

			StringBuilder sb = new StringBuilder();
			RowNumberTableModel model = getTableModel();

			for (Vector vector : sendData) {
				WrapTsBean tsBean = (WrapTsBean) vector.get(model.findColumn("tsBean"));
				sb.append(tsBean.getTsBean().getId()).append(",");
				TCClient.connector.sendMsg(tsBean.getTsBean());
				// 设置为发送成功的
				tsBean.setHasSend(true);
				tsBean.setHasMark(false);

				if (RecordInfo.state == RecordInfo.RecordState.recording && !RecordInfo.isOnlyMark() && (
						RecordInfo.bothSave() || RecordInfo.getRecordType()
								.equals(tsBean.getTsBean().getDirectType().toLowerCase().trim())))
				{

					DataProvider.getInstance().addModuleData(SaveModule.RecordData, tsBean.getTsBean());
				}
			}

			client.getTable().updateUI();

			if (sb.length() > 0) {
				sb.delete(sb.length() - 1, sb.length());
				LoggerUtil.info(client,
						String.format(LanguageMgr.getTranslation("TClient.Button.SendMessage.Log"), sb.toString()));
			}

		} else {
			LoggerUtil.error(client, LanguageMgr.getTranslation("TClient.TServer.DisConnect"));
		}
	}

	private void sendMessagesToServer(java.util.List<WrapTsBean> sendData) {
		if (sendData == null) {
			return;
		}

		if (TCClient.connector != null) {

			StringBuilder sb = new StringBuilder();
			for (WrapTsBean tsBean : sendData) {
				sb.append(tsBean.getTsBean().getId()).append(",");
				TCClient.connector.sendMsg(tsBean.getTsBean());
				// 设置为发送成功的
				tsBean.setHasSend(true);
				tsBean.setHasMark(false);

				if (RecordInfo.state == RecordInfo.RecordState.recording && !RecordInfo.isOnlyMark() && (
						RecordInfo.bothSave() || RecordInfo.getRecordType()
								.equals(tsBean.getTsBean().getDirectType().toLowerCase().trim())))
				{
					DataProvider.getInstance().addModuleData(SaveModule.RecordData, tsBean.getTsBean());
				}
			}

			client.getTable().updateUI();

			if (sb.length() > 0) {
				sb.delete(sb.length() - 1, sb.length());
				LoggerUtil.info(client,
						String.format(LanguageMgr.getTranslation("TClient.Button.SendMessage.Log"), sb.toString()));
			}

		} else {
			LoggerUtil.error(client, LanguageMgr.getTranslation("TClient.TServer.DisConnect"));
		}
	}

	private RowNumberTableModel getTableModel() {
		return (RowNumberTableModel) client.getTable().getModel();
	}

	public void addOrRemoveMark(int row) {
		WrapTsBean tsBean = getTableModel().getWrapBeanAt(row);
		tsBean.setHasMark(!tsBean.isHasMark());
	}

	@Override
	public void markItem() {

		for (int row : client.getTable().getSelectedRows()) {
			getTableModel().getWrapBeanAt(row).setHasMark(true);
		}
	}

	@Override
	public void cancelMarkItem() {
		for (int row : client.getTable().getSelectedRows()) {
			getTableModel().getWrapBeanAt(row).setHasMark(false);
		}
	}

	@Override
	public void sendSelectItem() {
		sendMessageToServer(client.getTable().getSelectedRows());
		DataProvider.turnColor();
	}

	@Override
	public void sendMarkItem() {

		ArrayList<WrapTsBean> markItems = getTableModel().getDateProvider().getMarkData();
		sendMessagesToServer(markItems);
		DataProvider.turnColor();
	}

	@Override
	public void sendNotSendItem() {

		sendMessagesToServer(getTableModel().getDateProvider().getNoSendData());
	}

	@Override
	public void deleteAllItem() {

		getTableModel().getDateProvider().removeAll();
		getTableModel().setDataVector(null);
		client.getTable().updateUI();

	}

	/**
	 * 检查是否和服务端保持连接
	 */
	private boolean checkConnect() {
		if (TCClient.connector == null || TCClient.connector.getSession() == null) {
			LoggerUtil.critical(client, LanguageMgr.getTranslation("TClient.TServer.DisConnect"));
			return false;
		}
		return true;
	}

	private boolean logResponseResult(HttpResponseMsg<?> responseMsg, String action) {
		if (responseMsg != null && responseMsg.getStatus() == 0) {
			LoggerUtil.info(client, action + "成功");
			return true;
		} else {
			LoggerUtil.error(client,
					action + "失败。  " + (responseMsg != null ? responseMsg.getBody() : " responseMsg = null"));
			return false;
		}
	}

	@Override
	public boolean listen(boolean isCancel) {

		if (checkConnect()) {
			ListenBean listenBean = new ListenBean();
			StringBuilder sb = new StringBuilder();
			String inputString = client.getAddressField().getEditor().getItem().toString();
			if (StringUtil.isEmpty(inputString)) {
				listenBean.setIp("0.0.0.0");
				sb.append("gip=0.0.0.0&uin=null");
			} else {
				try {
					HashMap<String, String> map = new HashMap<>();
					String[] conditions = inputString.split("&");
					for (String condition : conditions) {
						String[] ss = condition.split("=");
						if (ss.length == 2) {
							map.put(ss[0], ss[1]);
						}
					}
					if (!(map.containsKey("gip") || map.containsKey("uin"))) {
						throw new Exception();
					}

					if (map.containsKey("gip")) {
						listenBean.setIp(map.get("gip"));
						sb.append("gip=").append(map.get("gip")).append("&");
					} else {
						listenBean.setIp("0.0.0.0");
						sb.append("gip=0.0.0.0&");
					}

					if (map.containsKey("uin") && !map.get("uin").equals("null")) {
						listenBean.setUserName(map.get("uin"));
						sb.append("uin=").append(map.get("uin"));
					} else {
						listenBean.setUserName(null);
						sb.append("uin=null");
					}
				} catch (Exception e) {
					LoggerUtil.error(client, LanguageMgr.getTranslation("TClient.Button.Listen.Tip"));
					LogUtils.error(e);
					return false;
				}
			}
			((JTextField) client.getAddressField().getEditor().getEditorComponent()).setText(sb.toString());
			HttpRequestMsg<ListenBean> requestMsg = new HttpRequestMsg<>();
			requestMsg.setBody(listenBean);
			requestMsg.setSessionId(TCClient.SSID);
			try {
				HttpResponseMsg<?> responseMsg = post(TSLink.LISTEN, requestMsg);
				if (logResponseResult(responseMsg, isCancel ? LanguageMgr.getTranslation("TClient.Button.Listen.Cancel")
						: LanguageMgr.getTranslation("TClient.Button.Listen"))) {
					// 保存记录
					if (client.getAddressField().getModel() instanceof DefaultComboBoxModel
							&& !StringUtil.isEmpty(sb.toString())) {
						DefaultComboBoxModel<String> comboBoxModel = ((DefaultComboBoxModel<String>) client
								.getAddressField().getModel());
						// 过滤重复的
						for (int i = 0; i < comboBoxModel.getSize(); i++) {
							String string = comboBoxModel.getElementAt(i);
							if (string == null || sb.toString().trim().equals(string.trim())) {
								return true;
							}
						}
						((DefaultComboBoxModel<String>) client.getAddressField().getModel()).addElement(sb.toString());
						DataProvider.getInstance().addModuleData(SaveModule.FilterIp, sb.toString());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				LoggerUtil.error(client, isCancel
						? LanguageMgr.getTranslation("TClient.Button.Listen.Cancel.Exception") + "\n" + e.getMessage()
						: LanguageMgr.getTranslation("TClient.Button.Listen.Exception") + "\n" + e.getMessage());
				return false;
			}
		}
		return false;
	}

	private void setFilter(String statement) {
		if (checkConnect()) {
			HttpRequestMsg<String> requestMsg = new HttpRequestMsg<>();
			requestMsg.setSessionId(TCClient.SSID);
			requestMsg.setBody(statement);

			try {
				HttpResponseMsg<?> responseMsg = HttpClient.post(TSLink.FILTER, requestMsg);
				if (!StringUtil.isEmpty(statement)) {
					if (logResponseResult(responseMsg, LanguageMgr.getTranslation("TClient.Action.Set.Filter"))) {
						((FilterBoxEditor) client.getComboBox1().getEditor()).setRight();
					} else {
						((FilterBoxEditor) client.getComboBox1().getEditor()).setError();
					}
				} else {
					if (logResponseResult(responseMsg, LanguageMgr.getTranslation("TClient.Action.Clear.Filter"))) {
						((FilterBoxEditor) client.getComboBox1().getEditor()).reset();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				LoggerUtil.error(client,
						LanguageMgr.getTranslation("TClient.Action.Set.Filter") + "\n" + e.getMessage());
			}
		}
	}

	@Override
	public void cancelListen() {
		((JTextField) client.getAddressField().getEditor().getEditorComponent()).setText("");
		listen(true);
	}

	@Override
	public void receiveBinary(boolean isReceive) {
		HttpRequestMsg<Boolean> requestMsg = new HttpRequestMsg<>();
		requestMsg.setSessionId(TCClient.SSID);
		requestMsg.setBody(isReceive);
		try {
			HttpResponseMsg<?> responseMsg = post(TSLink.UPUNCONFIG, requestMsg);
			if (logResponseResult(responseMsg,
					isReceive ? LanguageMgr.getTranslation("TClient.Button.Receive.Binary.Open")
							: LanguageMgr.getTranslation("TClient.Button.Receive.Binary.Close"))) {
				if (isReceive) {
					// TODO: 2017/1/18 设置按钮图片
					client.getReceiveBinaryBtn().setIcon(new ImageIcon("icon/button.png"));
				} else {
					client.getReceiveBinaryBtn().setIcon(null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(client, LanguageMgr.getTranslation("TClient.Request.Error") + "\n" + e.getMessage());
		}
	}

	@Override
	public void up(boolean isUp) {
		HttpRequestMsg<Boolean> requestMsg = new HttpRequestMsg<>();
		requestMsg.setSessionId(TCClient.SSID);
		requestMsg.setBody(isUp);
		try {
			HttpResponseMsg<?> responseMsg = post(TSLink.UP, requestMsg);
			if (logResponseResult(responseMsg, isUp ? LanguageMgr.getTranslation("TClient.Button.Up.Open")
					: LanguageMgr.getTranslation("TClient.Button.Up.Close"))) {
				if (isUp) {
					// TODO: 2017/1/18 设置按钮图片
					client.getUpBtn().setIcon(new ImageIcon("icon/up.png"));
				} else {
					client.getUpBtn().setIcon(null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(client, LanguageMgr.getTranslation("TClient.Button.Up") + "\n" + e.getMessage());
		}
	}

	@Override
	public void down(boolean isDown) {
		HttpRequestMsg<Boolean> requestMsg = new HttpRequestMsg<>();
		requestMsg.setSessionId(TCClient.SSID);
		requestMsg.setBody(isDown);
		try {
			HttpResponseMsg<?> responseMsg = post(TSLink.DOWN, requestMsg);
			if (logResponseResult(responseMsg, isDown ? LanguageMgr.getTranslation("TClient.Button.Down.Open")
					: LanguageMgr.getTranslation("TClient.Button.Down.Close"))) {
				if (isDown) {
					// TODO: 2017/1/18 设置按钮图片
					client.getDownBtn().setIcon(new ImageIcon("icon/down.png"));
				} else {
					client.getDownBtn().setIcon(null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(client, LanguageMgr.getTranslation("TClient.Button.Down") + "\n" + e.getMessage());
		}
	}

	@Override
	public void start() {
		HttpRequestMsg<Boolean> requestMsg = new HttpRequestMsg<>();
		requestMsg.setSessionId(TCClient.SSID);
		try {
			HttpResponseMsg<?> responseMsg = post(TSLink.START, requestMsg);
			if (logResponseResult(responseMsg, LanguageMgr.getTranslation("TClient.Button.Start"))) {
				client.getStartBtn().setEnabled(false);
				client.getPauseBtn().setEnabled(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(client, LanguageMgr.getTranslation("TClient.Button.Start") + "\n" + e.getMessage());
		}
	}

	@Override
	public void pause() {
		HttpRequestMsg<Boolean> requestMsg = new HttpRequestMsg<>();
		requestMsg.setSessionId(TCClient.SSID);
		try {
			HttpResponseMsg<?> responseMsg = post(TSLink.PAUSE, requestMsg);
			if (logResponseResult(responseMsg, LanguageMgr.getTranslation("TClient.Button.Pause"))) {
				client.getStartBtn().setEnabled(true);
				client.getPauseBtn().setEnabled(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(client,
					LanguageMgr.getTranslation("TClient.Button.Pause.Exception") + "\n" + e.getMessage());
		}
	}

	@Override
	public void stop() {
		HttpRequestMsg<Boolean> requestMsg = new HttpRequestMsg<>();
		requestMsg.setSessionId(TCClient.SSID);
		try {
			HttpResponseMsg<?> responseMsg = post(TSLink.STOP, requestMsg);
			if (logResponseResult(responseMsg, LanguageMgr.getTranslation("TClient.Button.Stop"))) {
				client.getStartBtn().setEnabled(true);
				client.getPauseBtn().setEnabled(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LoggerUtil.error(client,
					LanguageMgr.getTranslation("TClient.Button.Stop.Exception") + "\n" + e.getMessage());
		}
		if (RecordInfo.state == RecordInfo.RecordState.recording)
		{
			saveRecoding();
			RecordInfo.state = RecordInfo.RecordState.none;
		}
	}

	@Override
	public void connect() {
		// 连接服务器
		TextLineCodecFactory lineCodec = new TextLineCodecFactory();
		lineCodec.setDecoderMaxLineLength(1024 * 1024); // 1M
		lineCodec.setEncoderMaxLineLength(1024 * 1024); // 1M
		new ClientConnector(TCConfig.getHost(), TCConfig.getPort(), lineCodec, new MinaHandler());
	}

	@Override
	public void showEditFilterDialog() {
		FilterEditorDialog dialog = new FilterEditorDialog(client.getFrame(),
				LanguageMgr.getTranslation("TClient.Button.FilterDialog.Title"));
		dialog.setVisible(true);
	}

	@Override
	public boolean filterWithCondition(String conditionStr) {

		if (!StringUtil.isEmpty(conditionStr)) {
			FilterExpressChain filterExpressChain = new FilterExpressChain(conditionStr);
			try {
				filterExpressChain.init();
				setFilter(conditionStr);
				DataProvider.getInstance().addModuleData(SaveModule.FilterText, conditionStr);
				client.getComboBox1().setModel(new DefaultComboBoxModel<FilterStringPanel>(
						DataProvider.getInstance().getModuleData(FilterText, FilterStringPanel[].class)));
				client.getComboBox1().getEditor().setItem(new FilterStringPanel(conditionStr));
			} catch (Exception e) {
				e.printStackTrace();
				LoggerUtil.error(client, LanguageMgr.getTranslation(e.getMessage()));
				return false;
			}
		} else {
			setFilter("");
		}
		return true;
	}

	@Override
	public void resetFilterCondition() {
		getTableModel().resetCondition();
	}

	@Override
	public void setFindCondition() {
		client.showEditFilterDialog();
	}

	@Override
	public void filterData(String item) {
		if (client.getTable().getModel() instanceof RowNumberTableModel) {
			getTableModel().setFilterData(item);
			// 切换时数据分类时，清除剪切的数据缓存
			PopMenu.getInstance(client.getTable()).clearCut();
		}
	}

	@Override
	public void setXmlData(TSBean tsBean) {
		if (tsBean == null) {
			client.getXmlArea().setText(null);
			return;
		}
		try {
			JSONObject jsonObject = JSONObject.fromObject(tsBean);
			String xml = new XMLSerializer().write(jsonObject);
			client.getXmlArea().setText(formatXml(xml));
		} catch (Exception e) {
			client.getXmlArea().setText(null);

			if (tsBean.getDesc() != null && TCConfig.getDialysisDesc().contains(tsBean.getDesc().trim())) {
				LoggerUtil.warn(client, LanguageMgr.getTranslation("TClient.Xml.Format.Dialysis"));
			} else {
				LoggerUtil.error(client, LanguageMgr.getTranslation("TClient.Xml.Format.Error"));
				LogUtils.error(e);
				e.printStackTrace();
			}
		}
	}

	public static String formatXml(String str) throws Exception {
		Document document = null;
		document = DocumentHelper.parseText(str);
		// 格式化输出格式
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("utf-8");
		StringWriter writer = new StringWriter();
		// 格式化输出流
		XMLWriter xmlWriter = new XMLWriter(writer, format);
		// 将document写入到输出流
		xmlWriter.write(document);
		xmlWriter.close();
		return writer.toString();
	}

	@Override
	public void hideColumn(String colName, boolean save) {
		ComponentUtil.setJTableWidth(client.getTable(), getTableModel().findColumn(colName), 0);
		if (save)
		{
			saveHideColumnData();
		}
	}

	@Override
	public void showColumn(String colName, boolean save) {
		TableColumn column =
				client.getTable().getTableHeader().getColumnModel().getColumn(getTableModel().findColumn(colName));
		column.setPreferredWidth(80);
		column.setMinWidth(80);
		column.setMaxWidth(80);

		client.getTable().getTableHeader().setResizingColumn(column);

		column = client.getTable().getColumnModel().getColumn(getTableModel().findColumn(colName));
		column.setPreferredWidth(80);
		column.setMinWidth(80);
		column.setMaxWidth(80);

		if (save)
		{
			saveHideColumnData();
		}
	}

	public void saveHideColumnData() {
		ArrayList<String> hideColumns = new ArrayList<>();
		for (Component component : client.getHideMenu().getMenuComponents()) {
			if (component instanceof JCheckBoxMenuItem) {
				if (((JCheckBoxMenuItem) component).isSelected()) {
					hideColumns.add(((JCheckBoxMenuItem) component).getText());
				}
			}
		}
		TCConfig.getClientCfg().setHideColumn(hideColumns.toArray(new String[hideColumns.size()]));
	}

	@Override public void setRecordData(List<WrapTsBean> tsBeans)
	{
		if (!client.getStartBtn().isEnabled())
		{
			stop();
		}
		LoggerUtil.info(client, LanguageMgr.getTranslation("TClient.Info.Record.Model"));
		getTableModel().setTableData(tsBeans);
	}

	@Override public void replay()
	{
		client.getReplayBtn().setEnabled(false);
		client.getPausePlayBtn().setEnabled(true);
		client.getStopPlayBtn().setEnabled(true);

		//回放时要重连
		if (client.getStartBtn().isEnabled())
		{
			client.start();
		}

		if (RecordInfo.state == RecordInfo.RecordState.none)
		{
			ThreadPool.executeWithCachePool(new PlayRecordTask(client, getTableModel().getRecordList()));
			RecordInfo.state = RecordInfo.RecordState.playing;

		}
		else if (RecordInfo.state == RecordInfo.RecordState.pause)
		{
			RecordInfo.state = RecordInfo.RecordState.playing;
		}
	}

	@Override public void pausePlay()
	{
		client.getReplayBtn().setEnabled(true);
		client.getPausePlayBtn().setEnabled(false);
		RecordInfo.state = RecordInfo.RecordState.pause;
	}

	@Override public void stopPlay()
	{
		client.getReplayBtn().setEnabled(false);
		client.getPausePlayBtn().setEnabled(false);
		client.getStopPlayBtn().setEnabled(false);
		RecordInfo.state = RecordInfo.RecordState.none;
	}

	@Override
	public void moveUp()
	{
		if (client.getTable().getSelectedRow() > 0)
		{
			int selectRow = client.getTable().getSelectedRow();
			int swapRow = selectRow - 1;

			Vector<?> vector = (Vector<?>) getTableModel().getDataVector().get(swapRow);

			getTableModel().removeRow(swapRow);
			getTableModel().insertRow(selectRow, vector);

			getTableModel().getDateProvider().swap(getTableModel().getWrapBeanAt(selectRow),
												   getTableModel().getWrapBeanAt(swapRow));
			client.getTable().setRowSelectionInterval(swapRow, swapRow);
		}
	}

	@Override
	public void moveDown()
	{
		if (client.getTable().getSelectedRow() < client.getTable().getRowCount() - 1)
		{
			int selectRow = client.getTable().getSelectedRow();
			int swapRow = selectRow + 1;

			Vector<?> vector = (Vector<?>) getTableModel().getDataVector().get(swapRow);

			getTableModel().removeRow(swapRow);
			getTableModel().insertRow(selectRow, vector);

			getTableModel().getDateProvider().swap(getTableModel().getWrapBeanAt(selectRow),
												   getTableModel().getWrapBeanAt(swapRow));
			client.getTable().setRowSelectionInterval(swapRow, swapRow);
		}
	}

	@Override
	public void aimAt()
	{
		Rectangle rect = client.getTable().getCellRect(client.getTable().getSelectedRow(), 0, true);
		client.getTable().updateUI();
		client.getTable().scrollRectToVisible(rect);
	}

	@Override public void setFixed(boolean flag)
	{
		TCConfig.getClientCfg().setFixed(flag);
		if (flag)
		{
			client.getFixedBtn().setText("unfixed");
		}
		else
		{
			client.getFixedBtn().setText("fixed");
		}
	}

	/**
	 * 结束录制，保存录制信息
	 */
	public void saveRecoding()
	{
		//只保存标记的数据
		if (RecordInfo.state == RecordInfo.RecordState.recording && RecordInfo.isOnlyMark())
		{
			DataProvider.getInstance().getModule(SaveModule.RecordData, RecordData.class).clear();
			for (WrapTsBean tsBean : getTableModel().getDateProvider().getAllData())
			{
				if (tsBean.isHasMark() && (RecordInfo.bothSave() || RecordInfo.getRecordType()
						.equals(tsBean.getTsBean().getDirectType().toLowerCase().trim())))
				{
					DataProvider.getInstance().addModuleData(SaveModule.RecordData, tsBean.getTsBean());
				}
			}
		}

		DataProvider.getInstance().getModule(SaveModule.RecordData, RecordData.class).save();
		RecordInfo.reset();
		LoggerUtil.info(client, LanguageMgr.getTranslation("TClient.Info.Record.Complete"));
	}

}
