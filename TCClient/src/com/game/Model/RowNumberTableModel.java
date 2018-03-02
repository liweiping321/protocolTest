package com.game.Model;

import com.game.Data.DataProvider;
import com.game.Data.TableDateProvider;
import com.game.Render.MainContentTableRender;
import com.game.TCClient;
import com.game.style.FilterBoxEditor;
import com.game.ts.net.bean.TSBean;
import com.game.ts.net.bean.WrapTsBean;
import com.game.util.LoggerUtil;
import com.game.utils.language.LanguageMgr;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static com.game.Data.TableDateProvider.tsBean2Vector;

/**
 * @author jianpeng.zhang
 * @since 2017/1/6.
 */
public class RowNumberTableModel extends DefaultTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable jTable;
	private Class<?>[] typeArray = { Object.class, Object.class, Object.class, Object.class, Object.class, Object.class,
			Object.class, Object.class, Object.class, Object.class, Object.class, WrapTsBean.class, Boolean.class };

	private final Vector columnIdentifiers;
	private TableDateProvider dateProvider = new TableDateProvider();

	private final int tsBeanRow;

	/**
	 * 显示在表格的数据。 添加的顺序要与 要与{@link #typeArray} 和 标题栏
	 * {@link com.game.Data.DataProvider#header}对应
	 */
	public void addDataToTable(TSBean tsBean) {

		if (tsBean != null) {
			WrapTsBean wrapTsBean = new WrapTsBean(tsBean);
			wrapTsBean.setBackgroundColor(DataProvider.COLORS[DataProvider.currentColorIndex]);
			Vector vector = dateProvider.add(wrapTsBean);
			if (vector != null && dateProvider.canShowData(tsBean)) {
				insertRow(getRowCount(), vector);
				//限制表格显示数据为5000条
				if (getDataVector().size() > 5000)
				{
					for (int i = getDataVector().size() - 1; i >= 5000; i--)
					{
						removeRow(i);
					}
				}
			}
		}
	}

	public RowNumberTableModel(JTable jTables, Object[][] data, Object[] columnNames) {
		super(data, columnNames);
		this.jTable = jTables;
		addTableModelListener(new ContentTableListener());
		columnIdentifiers = convertToVector(DataProvider.header);
		tsBeanRow = findColumn("tsBean");
	}

	public TableDateProvider getDateProvider() {
		return dateProvider;
	}

	public WrapTsBean getWrapBeanAt(int row) {
		return (WrapTsBean) ((Vector) getDataVector().get(row)).get(tsBeanRow);
	}

	public void setDataCondition(String condition) {
		dataVector = dateProvider.getConditionResult(condition);
		dateProvider.setState(TableDateProvider.FILTERING);
		TCClient.clientUI.getTable().updateUI();
		TCClient.clientUI.getFilterComboBox().getModel()
				.setSelectedItem(LanguageMgr.getTranslation("TClient.JComboBox.Result"));
	}

	public void resetCondition() {
		((FilterBoxEditor) (TCClient.clientUI.getComboBox1().getEditor())).reset();

	}

	public void setFilterData(String item) {
		Vector data = null;
		int newState = 0;
		if (LanguageMgr.getTranslation("TClient.JComboBox.SendData").equals(item)) {
			data = dateProvider.getSendTypeList();
			newState = TableDateProvider.SEND_DATA;
		} else if (LanguageMgr.getTranslation("TClient.JComboBox.MarkData").equals(item)) {
			data = dateProvider.getMarkTypeList();
			newState = TableDateProvider.MARK_DATA;
		} else if (LanguageMgr.getTranslation("TClient.JComboBox.DelData").equals(item)) {
			data = dateProvider.getDelTypeList();
			newState = TableDateProvider.DEL_DATA;
		} else if (LanguageMgr.getTranslation("TClient.JComboBox.UpData").equals(item)) {
			data = dateProvider.getUpTypeList();
			newState = TableDateProvider.UP_DATA;
		} else if (LanguageMgr.getTranslation("TClient.JComboBox.DownData").equals(item)) {
			data = dateProvider.getDownTypeList();
			newState = TableDateProvider.DOWN_DATA;
		} else if (LanguageMgr.getTranslation("TClient.JComboBox.AllData").equals(item)) {
			data = dateProvider.getAllTypeList();
			newState = TableDateProvider.ALL_DATA;
		}

		if (data != null) {
			dataVector = data;
			dateProvider.setState(newState);
			TCClient.clientUI.getTable().updateUI();
		}
	}

	public void setDataVector(Vector data) {
		if (data == null) {
			data = new Vector();
		}
		dataVector = data;
	}

	private List<WrapTsBean> recordList;

	public List<WrapTsBean> getRecordList()
	{
		return recordList;
	}

	public void setTableData(List<WrapTsBean> tsBeen)
	{
		if (tsBeen == null)
		{
			tsBeen = new ArrayList<>();
		}
		recordList = tsBeen;
		Vector<Object> vector = new Vector<>();
		for (WrapTsBean bean : tsBeen) {
			vector.add(tsBean2Vector(bean));
		}
		dataVector = vector;
		dateProvider.setState(TableDateProvider.OTHER);
		dateProvider.removeAll();
		dateProvider.addAll(recordList);
		TCClient.clientUI.getTable().updateUI();

	}

	@Override public void setValueAt(Object aValue, int row, int column)
	{
		if (column == findColumn("userName"))
		{
			super.setValueAt(aValue, row, column);
		}
	}

	@Override
	public Object getValueAt(int row, int column) {
		if (column == 0) {
			return row + 1;
		}
		return super.getValueAt(row, column);
	}

	public Class<?> getColumnClass(int c) {
		return typeArray[c];
	}

	@Override
	public boolean isCellEditable(int row, int column) {

		return column == findColumn("userName") || column == findColumn("code") || column == findColumn("subCode");
	}

	private class ContentTableListener implements TableModelListener {

		@Override
		public void tableChanged(TableModelEvent e) {
			TableCellRenderer renderer = jTable.getDefaultRenderer(Object.class);
			if (renderer instanceof MainContentTableRender) {
				if (e.getLastRow() < 0 || e.getColumn() < 0) {
					return;
				}

				// 修改表格里的值
				if (jTable.getModel() instanceof RowNumberTableModel) {
					Vector<?> itemData = (Vector<?>) ((RowNumberTableModel) jTable.getModel()).getDataVector()
							.get(e.getLastRow());

					TSBean tsBean = ((WrapTsBean) itemData.get(findColumn("tsBean"))).getTsBean();
					switch (getColumnName(e.getColumn())) {
					case "action":
						tsBean.setAction(itemData.get(findColumn("action")).toString());
						break;
					case "id":
						tsBean.setId(Long.parseLong(itemData.get(findColumn("id")).toString()));
						break;
					case "directType":
						tsBean.setDirectType((String) itemData.get(findColumn("directType")));
						break;
					case "ip":
						tsBean.setIp(itemData.get(findColumn("ip")).toString());
						break;
					case "userName":
						if (isChangeValue(tsBean, findColumn("userName"), tsBean.getUserName(),
								itemData.get(findColumn("userName")).toString())) {
							tsBean.setUserName(itemData.get(findColumn("userName")).toString());
						}
						break;
					case "length":
						tsBean.setLength(Integer.parseInt(itemData.get(findColumn("length")).toString()));
						break;
					// case "Code":
						// tsBean.setCode(itemData.get(findColumn("Code")).toString());
						// break;
					// case "SubCode":
						// tsBean.setSubCode(itemData.get(findColumn("SubCode")).toString());
						// break;
					}
				}
			}
		}
	}

	private boolean isChangeValue(TSBean tsBean, int index, String oldValue, String newValue) {
		if (oldValue.equals(newValue)) {
			return false;
		}

		LoggerUtil.warn(TCClient.clientUI, getFormatString(tsBean.getId(), index, oldValue, newValue));
		return true;
	}

	private String getFormatString(long id, int index, String oldValue, String newValue) {
		return String.format(LanguageMgr.getTranslation("TClient.Table.ChangeValue.Log"), id,
				jTable.getColumnName(index), oldValue, newValue);
	}

	// private boolean isChangeValue(TSBean tsBean, int index, long oldValue,
	// long newValue)
	// {
	// if (oldValue == newValue)
	// {
	// return false;
	// }
	// LoggerUtil.warn(TCClient.clientUI,
	// getFormatString(tsBean.getId(), index, String.valueOf(oldValue),
	// String.valueOf(newValue)));
	// return true;
	// }
	//
	// private boolean isChangeValue(TSBean tsBean, int index, int oldValue, int
	// newValue)
	// {
	// if (oldValue == newValue)
	// {
	// return false;
	// }
	// LoggerUtil.warn(TCClient.clientUI,
	// getFormatString(tsBean.getId(), index, String.valueOf(oldValue),
	// String.valueOf(newValue)));
	// return true;
	// }
	//
	// private boolean isChangeValue(TSBean tsBean, int index, short oldValue,
	// short newValue)
	// {
	// if (oldValue == newValue)
	// {
	// return false;
	// }
	// LoggerUtil.warn(TCClient.clientUI,
	// getFormatString(tsBean.getId(), index, String.valueOf(oldValue),
	// String.valueOf(newValue)));
	// return true;
	// }

}
