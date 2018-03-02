package com.game.ClientUI;

import com.game.ClientUI.dialog.ChangeDataDialog;
import com.game.ClientUI.dialog.DelayDialog;
import com.game.ClientUI.dialog.RepeatDialog;
import com.game.Data.DataProvider;
import com.game.Data.TableDateProvider;
import com.game.Model.RowNumberTableModel;
import com.game.Render.MainContentTableRender;
import com.game.TCClient;
import com.game.ts.net.bean.TSBean;
import com.game.ts.net.bean.WrapTsBean;
import com.game.util.LoggerUtil;
import com.game.utils.language.LanguageMgr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;

/**
 * @author jianpeng.zhang
 * @since 2017/1/11.
 */
public class  PopMenu extends JPopupMenu implements MouseListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JMenuItem cutItem;
	private JMenuItem copyItem;
	private JMenuItem pasteItem;
	private JMenuItem insertItem;
	private JMenuItem deleteItem;
	private JMenuItem delaySend;
	private JMenuItem repeatSend;
	private JMenuItem changeDataItem;

	private static PopMenu instance;
	private static final Object object = new Object();

	private JTable table;

	private ArrayList<Vector<?>> copyData = new ArrayList<>();
	/**
	 * 存放剪切时的数据
	 */
	private ArrayList<Vector<?>> cutData = new ArrayList<>();
	/**
	 * 存放剪切的行数
	 */
	private ArrayList<Integer> selectRows = new ArrayList<>();

	private PopMenu(JTable table) {
		this.table = table;
		installPopEvent(table);
		initView();
		initEvent();
		initKeyListener();
	}

	private void initView() {
		insertItem = new JMenuItem("插入");
		copyItem = new JMenuItem("复制");
		cutItem = new JMenuItem("剪切");
		pasteItem = new JMenuItem("粘贴");
		deleteItem = new JMenuItem("删除");
		JMenu menu = new JMenu("附加操作");
		delaySend = new JMenuItem("延时");
		repeatSend = new JMenuItem("重复发送");
		changeDataItem = new JMenuItem("批量修改节点");
		menu.add(delaySend);
		menu.add(repeatSend);

		insertItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
		copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		deleteItem.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
		delaySend.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_MASK));
		repeatSend.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_MASK));
		changeDataItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));

		this.addSeparator();
		this.add(insertItem);
		this.add(copyItem);
		this.add(cutItem);
		this.add(pasteItem);
		this.add(deleteItem);
		this.addSeparator();
		this.add(changeDataItem);
		this.addSeparator();
		this.add(menu);
	}

	private void initEvent() {
		insertItem.addActionListener(this);
		copyItem.addActionListener(this);
		cutItem.addActionListener(this);
		pasteItem.addActionListener(this);
		deleteItem.addActionListener(this);
		delaySend.addActionListener(this);
		repeatSend.addActionListener(this);
		changeDataItem.addActionListener(this);
	}

	private void initKeyListener() {
		table.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (TableDateProvider.canOperate())
				{
					if (e.getModifiers() == InputEvent.CTRL_MASK)
					{
						if (e.getKeyCode() == KeyEvent.VK_X)
						{
							// TCClient.clientUI.showLog(LogLevel.DEBUG, "CTRL+X......");
							cutValues();
						}
						else if (e.getKeyCode() == KeyEvent.VK_C)
						{
							// TCClient.clientUI.showLog(LogLevel.DEBUG, "CTRL+C......");
							copyValues();
						}
						else if (e.getKeyCode() == KeyEvent.VK_V)
						{
							// TCClient.clientUI.showLog(LogLevel.DEBUG, "CTRL+V......");
							paste();
						}
						else if (e.getKeyCode() == KeyEvent.VK_I)
						{
							// TCClient.clientUI.showLog(LogLevel.DEBUG, "CTRL+I......");
							insert();
						}
						else if (e.getKeyCode() == KeyEvent.VK_W)
						{
							showChangeDataDialog();
						}
					}
					else if (e.getModifiers() == 0)
					{
						if (e.getKeyCode() == 127)
						{
							// TCClient.clientUI.showLog(LogLevel.DEBUG, "Del......");
							delete();
						}
						else if (e.getKeyCode() == KeyEvent.VK_F5)
						{
							TCClient.clientUI.sendSelectItem();
						}
						else if (e.getKeyCode() == KeyEvent.VK_F6)
						{
							TCClient.clientUI.sendMarkItem();
						}
						else if (e.getKeyCode() == KeyEvent.VK_F7)
						{
							TCClient.clientUI.markItem();
						}
					}
					else if (e.getModifiers() == InputEvent.ALT_MASK)
					{
						if (e.getKeyCode() == KeyEvent.VK_P)
						{
							setDelay();
						}
						else if (e.getKeyCode() == KeyEvent.VK_R)
						{
							setRepeat();
						}
					}
				}
			}
		});
	}

	public static PopMenu getInstance(JTable table) {
		if (null == instance) {
			synchronized (object) {
				if (null == instance) {
					instance = new PopMenu(table);
				}
			}
		}
		return instance;
	}

	/**
	 * 为文本组件添加右键菜单
	 */
	private boolean installPopEvent(JTable tableComponent) {
		boolean isHasInstalled = false;
		MouseListener[] listeners = tableComponent.getMouseListeners();

		if (null != listeners) {
			for (MouseListener listener : listeners) {
				if (listener == this) {
					isHasInstalled = true;
				}
			}
		}

		if (!isHasInstalled) {
			tableComponent.addMouseListener(this);
			return true;
		}

		return false;
	}

	/**
	 * 从文本组件移除右键菜单
	 */
	public static void uninstallFromTextComponent(JTable tableComponent) {
		tableComponent.removeMouseListener(PopMenu.getInstance(tableComponent));
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//满足条件才会弹窗菜单
		if (TableDateProvider.canOperate())
		{
			if (e.isPopupTrigger() && e.getSource() instanceof JTable)
			{
				if (((JTable) e.getSource()).columnAtPoint(e.getPoint()) <= 0)
				{
					return;
				}
				int selectRow = ((JTable) e.getSource()).rowAtPoint(e.getPoint());
				if (selectRow >= 0)
				{
					((JTable) e.getSource()).setRowSelectionInterval(selectRow, selectRow);
				}
				this.show((JComponent) e.getSource(), e.getX(), e.getY());
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void show(Component invoker, int x, int y) {
		pasteItem.setEnabled(!(copyData.isEmpty() && cutData.isEmpty()));
		super.show(invoker, x, y);
	}

	// 右键菜单操作
	@Override
	public void actionPerformed(ActionEvent e) {
		JTable tableComponent = (JTable) this.getInvoker();

		if (!(tableComponent.getModel() instanceof RowNumberTableModel)) {
			return;
		}

		RowNumberTableModel tableModel = getTableModel();

		if (e.getSource() == this.insertItem) {

			insert();

		} else if (e.getSource() == this.copyItem || e.getSource() == this.cutItem) {

			Vector<?> data = (Vector<?>) tableModel.getDataVector().get(tableComponent.getSelectedRow());
			if (data != null) {
				if (e.getSource() == this.copyItem) {
					copyValues();
				} else {
					cutValues();
				}
			}
		} else if (e.getSource() == this.pasteItem) {

			paste();

		} else if (e.getSource() == this.deleteItem) {

			delete();

		}
		else if (e.getSource() == this.delaySend)
		{
			setDelay();
		}
		else if (e.getSource() == this.repeatSend)
		{
			setRepeat();
		}
		else if (e.getSource() == this.changeDataItem)
		{
			showChangeDataDialog();
		}
	}

	private void setDelay()
	{
		DelayDialog dialog = new DelayDialog(TCClient.clientUI.getFrame());
		dialog.setVisible(true);
	}

	private void setRepeat()
	{
		RepeatDialog dialog = new RepeatDialog(TCClient.clientUI.getFrame());
		dialog.setVisible(true);
	}

	private void showChangeDataDialog()
	{
		ChangeDataDialog dialog = new ChangeDataDialog(TCClient.clientUI.getFrame());
		dialog.setVisible(true);
	}


	private void copyValues() {
		StringBuilder sb = new StringBuilder();
		copy(copyData, sb);
		if (sb.length() > 0) {
			sb.delete(sb.length() - 1, sb.length());
			LoggerUtil.info(TCClient.clientUI,
					String.format(LanguageMgr.getTranslation("TClient.Button.Copy.Log"), sb.toString()));
		}
	}

	private void cutValues() {
		StringBuilder sb = new StringBuilder();
		copy(cutData, sb);
		if (sb.length() > 0) {
			sb.delete(sb.length() - 1, sb.length());
			LoggerUtil.info(TCClient.clientUI,
					String.format(LanguageMgr.getTranslation("TClient.Button.Cut.Log"), sb.toString()));
		}
	}

	private void copy(ArrayList<Vector<?>> source, StringBuilder sb) {

		selectRows.clear();
		for (int row : table.getSelectedRows()) {
			selectRows.add(row);
		}

		RowNumberTableModel tableModel = getTableModel();

		if (selectRows.size() != 0) {
			copyData.clear();
			cutData.clear();

			for (int row : selectRows) {
				Vector<?> vector = (Vector<?>) tableModel.getDataVector().get(row);
				if (vector != null) {
					source.add(vector);
					sb.append(vector.get(tableModel.findColumn("id"))).append(",");
				}
			}
		}
	}

	private void paste() {
		RowNumberTableModel tableModel = getTableModel();

		boolean canPaste = false;
		String id = null;

		int tsBeanCol = tableModel.findColumn("tsBean");
		ArrayList<WrapTsBean> addList = new ArrayList<>();

		if (!copyData.isEmpty()) {
			canPaste = true;
			id = tableModel.getValueAt(table.getSelectedRow(), tableModel.findColumn("id")).toString();
			WrapTsBean selectBean = tableModel.getWrapBeanAt(table.getSelectedRow());

			for (int i = 0; i < copyData.size(); i++) {
				WrapTsBean copyBean = ((WrapTsBean) copyData.get(i).get(tsBeanCol)).clone();
				addList.add(copyBean);
				tableModel.insertRow(table.getSelectedRow() + i + 1, TableDateProvider.tsBean2Vector(copyBean));
			}
			tableModel.getDateProvider().addAfter(addList, selectBean);

		} else if (!cutData.isEmpty()) {
			int selectRow = table.getSelectedRow();
			int selectRowCopy = selectRow;
			int left = 0, right = cutData.size() - 1;
			int middle;

			// 二分法找插入位置在选择的列里的位置
			while (left != right) {
				middle = (left + right + 1) / 2;
				if (selectRows.get(middle) > selectRow) {
					right = middle;
				} else if (selectRows.get(middle) <= selectRow) {
					left = middle;
				}

				if (left + 1 == right) {
					if (selectRows.get(selectRows.size() - 1) <= selectRow) {
						left = right;
						right++;
					} else if (selectRows.get(0) > selectRow) {
						right = left;
						left--;
					}
					break;
				}
			}
			if (left == right) {
				// 从下复制一条数据到上面的情况
				if (left == 0 && selectRow < selectRows.get(left)) {
					left--;
				} else {
					right++;
				}
			}

			WrapTsBean selectBean = tableModel.getWrapBeanAt(table.getSelectedRow());

			ArrayList<WrapTsBean> cutList = new ArrayList<>();
			if (table.getDefaultRenderer(Object.class) instanceof MainContentTableRender) {
				canPaste = true;
				id = tableModel.getValueAt(table.getSelectedRow(), 2).toString();
				for (; left >= 0; left--) {
					cutList.add((WrapTsBean) cutData.get(left).get(tsBeanCol));
					addList.add(0, ((WrapTsBean) cutData.get(left).get(tsBeanCol)).clone());
					tableModel.insertRow(selectRow + 1, cutData.get(left));
					tableModel.removeRow(selectRows.get(left));
					selectRow--;
				}
				selectRow = selectRowCopy;
				for (; right < selectRows.size(); right++) {
					cutList.add((WrapTsBean) cutData.get(right).get(tsBeanCol));
					addList.add(((WrapTsBean) cutData.get(right).get(tsBeanCol)).clone());
					tableModel.removeRow(selectRows.get(right));
					tableModel.insertRow(selectRow + 1, cutData.get(right));
					selectRow++;
				}
			}
			tableModel.getDateProvider().cut(addList, cutList, selectBean);
			cutData.clear();
		}

		if (canPaste) {
			LoggerUtil.info(TCClient.clientUI,
					String.format(LanguageMgr.getTranslation("TClient.Button.Paste.Log"), id));
		}
	}

	public void clearCut()
	{
		if (cutData != null)
		{
			cutData.clear();
		}
	}
	private void insert() {
		RowNumberTableModel tableModel = getTableModel();
		String id = tableModel.getValueAt(table.getSelectedRow(), 2).toString();
		LoggerUtil.info(TCClient.clientUI, String.format(LanguageMgr.getTranslation("TClient.Button.Insert.Log"), id));

		WrapTsBean selectBean = tableModel.getWrapBeanAt(table.getSelectedRow());

		WrapTsBean tsBean = new WrapTsBean(new TSBean());
		tsBean.setBackgroundColor(DataProvider.COLORS[DataProvider.currentColorIndex]);
		tableModel.insertRow(table.getSelectedRow() + 1, tableModel.getDateProvider().tsBean2Vector(tsBean));
		tableModel.getDateProvider().addAfter(tsBean, selectBean);

		changeLine(table.getSelectedRow(), 1);
	}

	private void delete() {
		RowNumberTableModel tableModel = getTableModel();
		int[] rows = table.getSelectedRows();

		ArrayList<WrapTsBean> list = new ArrayList<>();
		StringBuilder sb = new StringBuilder();

		for (int i = rows.length - 1; i >= 0; i--)
		{
			WrapTsBean bean = tableModel.getWrapBeanAt(rows[i]);
			sb.append(bean.getTsBean().getId()).append(",");
			list.add(bean);
			tableModel.removeRow(rows[i]);
			changeLine(rows[i], -1);
		}
		tableModel.getDateProvider().remove(list);

		if (sb.length() > 0)
		{
			sb.delete(sb.length() - 1, sb.length());
			LoggerUtil.info(TCClient.clientUI,
							String.format(LanguageMgr.getTranslation("TClient.Button.Delete.Log"), sb.toString()));
		}
	}

	/**
	 * 增加或删除行时，剪切数据的行要变化
	 * 
	 * @param selectIndex
	 *            选中的行数
	 * @param move
	 *            移动的行数，增加一行是1, 减少一行是-1
	 */
	private void changeLine(int selectIndex, int move) {
		if (cutData.size() > 0 && selectIndex > 0) {
			int i = selectRows.size() - 1;
			for (; i >= 0 && selectRows.get(i) > selectIndex; i--) {
				selectRows.set(i, selectRows.get(i) + move);
			}
			// 删除的行中有剪切的数据
			if (i >= 0 && selectRows.get(i) == selectIndex && move < 0) {
				selectRows.remove(i);
				cutData.remove(i);
			}
		}
	}

	private RowNumberTableModel getTableModel()
	{
		return (RowNumberTableModel) table.getModel();
	}


	private boolean canOpera()
	{
		// TODO: 2017/2/10 判断Button功能能不能用 比如在丢弃面板 没有右侧菜单
		return true;
	}
}
