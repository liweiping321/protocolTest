package com.game.ClientUI;

import com.game.Data.DataProvider;
import com.game.Data.LogLevel;
import com.game.Listener.ButtonListener;
import com.game.Listener.MenuListener;
import com.game.Listener.TableMouseListener;
import com.game.Listener.TreeTableListener;
import com.game.Model.CustomerTreeTableModel;
import com.game.Model.RowNumberTableModel;
import com.game.Model.StatisticsTableModel;
import com.game.Render.FilterBoxListRender;
import com.game.Render.MainContentTableRender;
import com.game.Render.RowHeadRender;
import com.game.component.FilterStringPanel;
import com.game.config.TCConfig;
import com.game.operator.ClientProxyOperator;
import com.game.operator.imp.IClientOperator;
import com.game.style.FilterBoxEditor;
import com.game.ts.net.bean.TSBean;
import com.game.ts.net.bean.WrapTsBean;
import com.game.utils.ComponentUtil;
import com.game.utils.language.LanguageMgr;
import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import static com.game.Data.DataProvider.*;
import static com.game.Data.SaveModule.FilterIp;
import static com.game.Data.SaveModule.FilterText;
import static com.game.utils.ComponentUtil.*;

/**
 * @author jianpeng.zhang
 * @since 2017/1/9.
 */
public class Client implements IClientOperator {
	private JFrame frame;

	private JPanel mainPanel;

	private JPanel funcPanel;
	private JPanel showMainPanel;
	private JPanel footPanel;

	private JPanel itemPanel;
	private JPanel messagePanel;
	private JPanel detailPanel;

	private JToolBar bar;
	private JToolBar bar2;

	private JTextPane messagePane = new JTextPane();

	private JMenuBar menubar;

	private JTabbedPane jTabbedPane;
	private JTabbedPane jTabbedPane1;

	private JTable table;
	private JTable statisticsTable;

	private JXTreeTable treetable;

	/**
	 * 地址栏的
	 */
	private JComboBox<String> addressField;
	/**
	 * 过滤表达式栏
	 */
	private JComboBox<FilterStringPanel> comboBox1;
	/**
	 * 数据分类栏
	 */
	private JComboBox<String> filterComboBox;

	private JTextArea xmlArea;


	private TreeTableListener treeTableManager;

	private TreeTableMenu treeTableMenu;

	private ClientProxyOperator proxyOperator;

	public Client() {
		proxyOperator = new ClientProxyOperator(this);
		buildUI();
	}

	private void buildUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			funcPanel = new JPanel();
			showMainPanel = new JPanel();
			footPanel = new JPanel();

			mainPanel = new JPanel();
			mainPanel.setPreferredSize(new Dimension(1400, 900));
			mainPanel.setLayout(new BorderLayout());

			mainPanel.add("North", funcPanel);
			mainPanel.add("Center", showMainPanel);
			mainPanel.add("South", footPanel);

			buildMainPanel();
			buildFuncBar();
			buildFooter();
			buildMenu();

			frame = new JFrame("协议测试工具");
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setResizable(false);
			frame.setJMenuBar(menubar);
			frame.setContentPane(mainPanel);
			frame.pack();
			frame.setVisible(true);

			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					DataProvider.getInstance().save();
					super.windowClosing(e);
				}
			});

			frame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - frame.getWidth()) / 2,
							 (Toolkit.getDefaultToolkit().getScreenSize().height - frame.getHeight()) / 2);

		} catch (IllegalAccessException | InstantiationException | ClassNotFoundException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}


	private JMenuItem filterManagerMenu;
	private JMenu hideMenu;

	private JMenuItem saveOperator;
	private JMenuItem loadOperator;
	private JMenuItem reloadMenu;

	private void buildMenu() {
		menubar = new JMenuBar();
		menubar.setBackground(new Color(0xF9F9F9));
		JMenu startMenu = new JMenu("开始");
		JMenu menu2 = new JMenu("菜单2");
		JMenu menu3 = new JMenu("菜单3");
		JMenu filterMenu = new JMenu("过滤");
		JMenu settingMenu = new JMenu("设置");

		hideMenu = new JMenu("隐藏列");
		filterManagerMenu = new JMenuItem(LanguageMgr.getTranslation("TClient.Filter.Manager.Title"));
		saveOperator = new JMenuItem("保存");
		loadOperator = new JMenuItem("打开");
		reloadMenu = new JMenuItem("重新加载配置");

		menubar.add(startMenu);
		menubar.add(menu2);
		menubar.add(menu3);
		menubar.add(filterMenu);
		menubar.add(settingMenu);

		settingMenu.add(hideMenu);
		filterMenu.add(filterManagerMenu);
		startMenu.add(saveOperator);
		startMenu.add(loadOperator);
		startMenu.add(reloadMenu);

		// 给菜单添加监听
		new MenuListener(this).listen();
	}

	/**
	 * 拦截ip按钮
	 */
	private JButton listenBtn;
	/**
	 * 取消拦截按钮
	 */
	private JButton clearListenBtn;

	private JButton upBtn;
	private JButton downBtn;
	private JButton startBtn;
	private JButton pauseBtn;
	private JButton stopBtn;

	private JButton SSBtn;
	private JButton SMBtn;
	private JButton SABtn;
	private JButton SNBtn;
	private JButton CSBtn;
	private JButton DABtn;

	private JButton reConnectBtn;
	private JButton editBtn;
	private JButton filterBtn;
	private JButton findBtn;
	private JButton clearFilterBtn;
	private JButton receiveBinaryBtn;

	private JButton moveUpBtn;
	private JButton moveDownBtn;
	private JButton aimBtn;
	private JButton fixedBtn;

	private JButton replayBtn;
	private JButton pausePlayBtn;
	private JButton stopPlayBtn;

	private void buildFuncBar() {
		bar = new JToolBar();
		bar.setFloatable(false);
		bar.setBackground(new Color(0xF9F9F9));
		bar.setBorder(new EmptyBorder(10, 10, 7, 7));

		listenBtn = new JButton(LanguageMgr.getTranslation("TClient.Button.ListenBtn"));
		clearListenBtn = new JButton(LanguageMgr.getTranslation("TClient.Button.Cancel"));
		receiveBinaryBtn = createButton(LanguageMgr.getTranslation("TClient.Button.Receive.Binary"),
				LanguageMgr.getTranslation("TClient.Button.Receive.Binary.Tip"), 30, 20);
		addressField = createComboBox(DataProvider.getInstance().getModuleData(FilterIp, String[].class), true, true,
				new Dimension(500, 25));
		addressField.setFont(new Font(null, 0, 14));
		((JTextField) addressField.getEditor().getEditorComponent()).setText("gip=0.0.0.0&uin=null");

		bar.add(addressField);
		addSeparator(bar, JSeparator.VERTICAL, new Color(0xF9F9F9), new Dimension(6, 25));
		bar.add(listenBtn);
		addSeparator(bar, JSeparator.VERTICAL, new Color(0xF9F9F9), new Dimension(7, 25));
		bar.add(clearListenBtn);
		addEmptyLabel(bar, 4, 5);
		addSeparator(bar, JSeparator.VERTICAL, new Color(0xA0A6B5), new Dimension(6, 50));
		bar.add(receiveBinaryBtn);

		bar2 = new JToolBar();
		bar2.setFloatable(false);
		bar2.setBackground(new Color(0xF9F9F9));
		bar2.setBorder(new EmptyBorder(7, 10, 7, 7));

		upBtn = ComponentUtil.createButton("up", LanguageMgr.getTranslation("TClient.Button.UpBtn.Tip"), 30, 20);
		upBtn.setIcon(new ImageIcon("icon/up.png"));
		downBtn = ComponentUtil.createButton("down", LanguageMgr.getTranslation("TClient.Button.DownBtn.Tip"), 30, 20);
		downBtn.setIcon(new ImageIcon("icon/down.png"));
		startBtn = ComponentUtil.createButton("start", LanguageMgr.getTranslation("TClient.Button.StartBtn.Tip"));
		startBtn.setEnabled(false);
		pauseBtn = ComponentUtil.createButton("pause", LanguageMgr.getTranslation("TClient.Button.PauseBtn.Tip"));
		stopBtn = ComponentUtil.createButton("stop", LanguageMgr.getTranslation("TClient.Button.StopBtn.Tip"));
		editBtn = ComponentUtil.createButton("edit", LanguageMgr.getTranslation("TClient.Button.EditBtn.Tip"));
		filterBtn = ComponentUtil.createButton("OK", LanguageMgr.getTranslation("TClient.Button.FilterBtn.Tip"));
		findBtn = ComponentUtil.createButton("find", LanguageMgr.getTranslation("TClient.Button.findBtn.Tip"));

		bar2.add(upBtn);
		addEmptyLabel(bar2, 5, 5);
		bar2.add(downBtn);
		addEmptyLabel(bar2, 5, 5);
		bar2.add(startBtn);
		addEmptyLabel(bar2, 5, 5);
		bar2.add(pauseBtn);
		addEmptyLabel(bar2, 5, 5);
		bar2.add(stopBtn);
		addEmptyLabel(bar2, 4, 5);
		addSeparator(bar2, JSeparator.VERTICAL, new Color(0xA0A6B5), new Dimension(6, 50));

		replayBtn = ComponentUtil.createButton("play", LanguageMgr.getTranslation("TClient.Button.PlayBtn.Tip"));
		replayBtn.setEnabled(false);
		pausePlayBtn = ComponentUtil.createButton("stop", LanguageMgr.getTranslation("TClient.Button.StopPlayBtn.Tip"));
		pausePlayBtn.setEnabled(false);
		stopPlayBtn =
				ComponentUtil.createButton("release", LanguageMgr.getTranslation("TClient.Button.ReleaseBtn.Tip"));
		stopPlayBtn.setEnabled(false);

		bar2.add(replayBtn);
		addEmptyLabel(bar2, 5, 5);
		bar2.add(pausePlayBtn);
		addEmptyLabel(bar2, 5, 5);
		bar2.add(stopPlayBtn);
		addEmptyLabel(bar2, 4, 5);
		addSeparator(bar2, JSeparator.VERTICAL, new Color(0xA0A6B5), new Dimension(6, 50));

		moveUpBtn = ComponentUtil.createButton("moveUp", LanguageMgr.getTranslation("TClient.Button.MoveUp"));
		bar2.add(moveUpBtn);
		addEmptyLabel(bar2, 5, 5);
		moveDownBtn = ComponentUtil.createButton("moveDown", LanguageMgr.getTranslation("TClient.Button.MoveDown"));
		bar2.add(moveDownBtn);
		addEmptyLabel(bar2, 5, 5);
		aimBtn = ComponentUtil.createButton("aim", LanguageMgr.getTranslation("TClient.Button.aim"));
		bar2.add(aimBtn);
		addEmptyLabel(bar2, 5, 5);
		fixedBtn = ComponentUtil.createButton(TCConfig.getClientCfg().isFixed() ? "unfixed" : "fixed",
											  LanguageMgr.getTranslation("TClient.Button.Fixed"));
		bar2.add(fixedBtn);
		addEmptyLabel(bar2, 4, 5);
		addSeparator(bar2, JSeparator.VERTICAL, new Color(0xA0A6B5), new Dimension(6, 50));

		SSBtn = ComponentUtil.createButton("SS", LanguageMgr.getTranslation("TClient.Button.SendSelect"));
		SSBtn.setMnemonic(KeyEvent.VK_F5);
		SMBtn = ComponentUtil.createButton("SM", LanguageMgr.getTranslation("TClient.Button.SendMark"));
		SMBtn.setMnemonic(KeyEvent.VK_F6);
		SABtn = ComponentUtil.createButton("SA", LanguageMgr.getTranslation("TClient.Button.Mark2Send"));
		SABtn.setMnemonic(KeyEvent.VK_F7);
		SNBtn = ComponentUtil.createButton("SN", LanguageMgr.getTranslation("TClient.Button.SendNoSend"));
		// SNBtn.setMnemonic(KeyEvent.VK_F6);
		CSBtn = ComponentUtil.createButton("CS", LanguageMgr.getTranslation("TClient.Button.CancelMark"));
		// CSBtn.setMnemonic(KeyEvent.VK_F6);
		DABtn = ComponentUtil.createButton("DA", LanguageMgr.getTranslation("TClient.Button.DeleteAll"));
		// DABtn.setMnemonic(KeyEvent.VK_F6);
		reConnectBtn = ComponentUtil.createButton(LanguageMgr.getTranslation("TClient.Button.Connect"),
				LanguageMgr.getTranslation("TClient.Button.ReConnect"));
		reConnectBtn.setEnabled(false);
		clearFilterBtn =
				ComponentUtil.createButton("clear", LanguageMgr.getTranslation("TClient.Button.Statement.Clear.Tip"));

		bar2.add(SSBtn);
		addEmptyLabel(bar2, 5, 5);
		bar2.add(SMBtn);
		addEmptyLabel(bar2, 5, 5);
		bar2.add(SABtn);
		addEmptyLabel(bar2, 5, 5);
		bar2.add(SNBtn);
		addEmptyLabel(bar2, 5, 5);
		bar2.add(CSBtn);
		addEmptyLabel(bar2, 5, 5);
		bar2.add(DABtn);
		addEmptyLabel(bar2, 4, 5);
		addSeparator(bar2, JSeparator.VERTICAL, new Color(0xA0A6B5), new Dimension(6, 50));

		bar2.add(reConnectBtn);
		addEmptyLabel(bar2, 4, 5);
		addSeparator(bar2, JSeparator.VERTICAL, new Color(0xA0A6B5), new Dimension(6, 50));
		bar2.add(clearFilterBtn);
		addEmptyLabel(bar2, 5, 5);
		bar2.add(editBtn);
		addEmptyLabel(bar2, 5, 5);

		comboBox1 = createComboBox(new Dimension(400, 23));
		comboBox1.setModel(new DefaultComboBoxModel<FilterStringPanel>(
				DataProvider.getInstance().getModuleData(FilterText, FilterStringPanel[].class)));
		comboBox1.setEditor(new FilterBoxEditor());
		comboBox1.setRenderer(new FilterBoxListRender());
		comboBox1.getEditor().setItem(new FilterStringPanel());

		bar2.add(comboBox1);
		addEmptyLabel(bar2, 5, 5);
		bar2.add(filterBtn);
		addEmptyLabel(bar2, 6, 5);
		bar2.add(findBtn);
		addEmptyLabel(bar2, 8, 5);

		filterComboBox = createComboBox(filterMode, false, false, new Dimension(100, 20));
		filterComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				System.out.println(e.getItemSelectable());
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (!LanguageMgr.getTranslation("TClient.JComboBox.Result").equals(e.getItem())) {
						filterData(e.getItem().toString());
					}
				}
			}
		});
		bar2.add(filterComboBox);

		// 给所有按钮添加监听
		new ButtonListener(this).listen();

		funcPanel.setLayout(new BorderLayout());
		funcPanel.add("North", bar);
		addSeparator(funcPanel, JSeparator.HORIZONTAL, new Color(0xB5B2B5), new Dimension(1400, 1));
		funcPanel.add("South", bar2);
 
	}

	private void buildMainPanel() {
		itemPanel = new JPanel();
		itemPanel.setPreferredSize(new Dimension(850, 530));
		itemPanel.setMinimumSize(new Dimension(850, 530));
		itemPanel.setLayout(new BorderLayout());
		itemPanel.setBorder(new EmptyBorder(5, 5, 0, 5));

		messagePanel = new JPanel();
		messagePanel.setPreferredSize(new Dimension(850, 250));
		messagePanel.setMinimumSize(new Dimension(850, 250));

		detailPanel = new JPanel();
		detailPanel.setPreferredSize(new Dimension(545, 780));
		detailPanel.setMinimumSize(new Dimension(545, 780));

		GridBagLayout layout = new GridBagLayout();
		showMainPanel.setLayout(layout);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		showMainPanel.add(itemPanel, gbc);
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.gridx = 0;
		gbc.gridy = 1;
		showMainPanel.add(messagePanel, gbc);
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.gridheight = 2;
		gbc.gridx = 1;
		gbc.gridy = 0;
		showMainPanel.add(detailPanel, gbc);

		addContentPanel();
		addInfoPanel();
		addDetailPanel();
	}

	/**
	 * 主表格的界面
	 */
	private void addContentPanel() {

		table = new JTable()
		{
			public String getToolTipText(MouseEvent e)
			{
				int descRow = ((RowNumberTableModel) table.getModel()).findColumn("desc");
				int row = table.rowAtPoint(e.getPoint());
				int col = table.columnAtPoint(e.getPoint());
				String tipText = null;
				if (row > -1 && col == descRow)
				{
					Object value = table.getValueAt(row, col);
					if (null != value && !"".equals(value))
						tipText = value.toString();//悬浮显示单元格内容
				}
				return tipText;
			}
		};

		table.setModel(new RowNumberTableModel(table, new Object[][] {}, header));
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setPreferredSize(new Dimension(1, 25));
		((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
		table.setSelectionBackground(new Color(0x217EFF));
		table.setRowHeight(33);
		table.setDefaultRenderer(Object.class, new MainContentTableRender(table));
		table.addMouseListener(new TableMouseListener(this));
		table.getColumnModel().getColumn(0).setCellRenderer(new RowHeadRender(table));
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					// 双击
					int row = ((JTable) e.getSource()).rowAtPoint(e.getPoint()); // 获得行位置
					proxyOperator.addOrRemoveMark(row);
				}
			}
		});
		PopMenu.getInstance(table);

		ComponentUtil.setJTableWidth(table, 0, 35);
		// 隐藏表格最后列
		hideColumn("tsBean", false);


		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(table);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getViewport().setBackground(new Color(0xF9F9F9));
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 3));

		jTabbedPane = new JTabbedPane();
		jTabbedPane.addTab("1:game", scrollPane);
		itemPanel.add(jTabbedPane, "Center");
	}

	/**
	 * 日志面板
	 */
	private void addInfoPanel() {
		jTabbedPane1 = new JTabbedPane();
		jTabbedPane1.setTabPlacement(JTabbedPane.BOTTOM);

		messagePane.setEditable(false);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(messagePane);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xB5B2B5), 1));
		jTabbedPane1.addTab("日志", scrollPane);

		statisticsTable = new JTable();
		statisticsTable.setModel(new StatisticsTableModel(statisticsTable, new Object[][] {}, statistics_header));
		statisticsTable.getTableHeader().setReorderingAllowed(false);
		statisticsTable.getTableHeader().setPreferredSize(new Dimension(1, 30));
		((DefaultTableCellRenderer) statisticsTable.getTableHeader().getDefaultRenderer())
				.setHorizontalAlignment(JLabel.CENTER);
		((DefaultTableCellRenderer) statisticsTable.getDefaultRenderer(Object.class))
				.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		statisticsTable.setSelectionBackground(new Color(0x217EFF));
		statisticsTable.setRowHeight(33);
		statisticsTable.getColumnModel().getColumn(0).setCellRenderer(new RowHeadRender(statisticsTable));

		JScrollPane jScrollPane = new JScrollPane();
		jScrollPane.setViewportView(statisticsTable);

		jTabbedPane1.addTab("统计", jScrollPane);

		messagePanel.setLayout(new BorderLayout());
		messagePanel.setBorder(new EmptyBorder(5, 5, 0, 5));
		messagePanel.add(jTabbedPane1, "Center");
	}

	/**
	 * 协议树面板
	 */
	private void addDetailPanel() {
		treetable = new JXTreeTable();
		treetable.setTreeTableModel(new CustomerTreeTableModel(treetable));
		treetable.getTableHeader().setReorderingAllowed(false);
		treetable.getColumnModel().getColumn(0).setPreferredWidth(50);
		treetable.setRootVisible(true);
		treetable.expandAll();

		treeTableMenu = new TreeTableMenu();
		treeTableManager = new TreeTableListener(treetable, treeTableMenu);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(treetable);
		JTabbedPane jTabbedPane2 = new JTabbedPane();
		jTabbedPane2.setTabPlacement(JTabbedPane.BOTTOM);
		jTabbedPane2.addTab("协议树", scrollPane);

		xmlArea = new JTextArea();
		xmlArea.setEditable(false);

		JScrollPane jScrollPane = new JScrollPane();
		jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jScrollPane.setViewportView(xmlArea);
		jScrollPane.setBorder(new EmptyBorder(10, 7, 10, 7));

		jTabbedPane2.addTab("xml视图", jScrollPane);

		detailPanel.setLayout(new BorderLayout());
		detailPanel.add(jTabbedPane2, "Center");
		detailPanel.setBorder(new EmptyBorder(5, 0, 0, 5));
	}

	private void buildFooter() {
		footPanel.setLayout(new BorderLayout());
		footPanel.add(createSeparator(JSeparator.HORIZONTAL, new Color(0xB5B2B5), new Dimension(1400, 50)), "North");
		JLabel button = new JLabel();
		button.setPreferredSize(new Dimension(1400, 30));
		button.setMinimumSize(new Dimension(1400, 30));
		button.setMaximumSize(new Dimension(1400, 30));
		footPanel.add("Center", button);
	}

	public JFrame getFrame() {
		return frame;
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public JPanel getFuncPanel() {
		return funcPanel;
	}

	public JPanel getShowMainPanel() {
		return showMainPanel;
	}

	public JPanel getFootPanel() {
		return footPanel;
	}

	public JPanel getItemPanel() {
		return itemPanel;
	}

	public JPanel getMessagePanel() {
		return messagePanel;
	}

	public JPanel getDetailPanel() {
		return detailPanel;
	}

	public JToolBar getBar() {
		return bar;
	}

	public JToolBar getBar2() {
		return bar2;
	}

	public JTextPane getMessagePane() {
		return messagePane;
	}

	public JMenuBar getMenubar() {
		return menubar;
	}

	public JTabbedPane getjTabbedPane() {
		return jTabbedPane;
	}

	public JTabbedPane getjTabbedPane1() {
		return jTabbedPane1;
	}

	public JTable getTable() {
		return table;
	}

	public JTable getStatisticsTable()
	{
		return statisticsTable;
	}

	public JXTreeTable getTreetable() {
		return treetable;
	}

	public JComboBox<String> getAddressField() {
		return addressField;
	}

	public JComboBox<FilterStringPanel> getComboBox1() {
		return comboBox1;
	}

	public JComboBox<String> getFilterComboBox() {
		return filterComboBox;
	}

	public TreeTableListener getTreeTableManager() {
		return treeTableManager;
	}

	public TreeTableMenu getTreeTableMenu()
	{
		return treeTableMenu;
	}

	public JButton getAimBtn()
	{
		return aimBtn;
	}

	public JButton getMoveDownBtn()
	{
		return moveDownBtn;
	}

	public JButton getMoveUpBtn()
	{
		return moveUpBtn;
	}

	public JButton getFixedBtn()
	{
		return fixedBtn;
	}

	public JButton getReplayBtn()
	{
		return replayBtn;
	}

	public JButton getPausePlayBtn()
	{
		return pausePlayBtn;
	}

	public JButton getStopPlayBtn()
	{
		return stopPlayBtn;
	}

	public JButton getSSBtn() {
		return SSBtn;
	}

	public JButton getSMBtn() {
		return SMBtn;
	}

	public JButton getSABtn() {
		return SABtn;
	}

	public JButton getSNBtn() {
		return SNBtn;
	}

	public JButton getCSBtn() {
		return CSBtn;
	}

	public JButton getDABtn() {
		return DABtn;
	}

	public JButton getListenBtn() {
		return listenBtn;
	}

	public JButton getClearListenBtn() {
		return clearListenBtn;
	}

	public JButton getUpBtn() {
		return upBtn;
	}

	public JButton getDownBtn() {
		return downBtn;
	}

	public JButton getStartBtn() {
		return startBtn;
	}

	public JButton getStopBtn() {
		return stopBtn;
	}

	public JButton getPauseBtn() {
		return pauseBtn;
	}

	public JButton getReConnectBtn() {
		return reConnectBtn;
	}

	public JButton getClearFilterBtn()
	{
		return clearFilterBtn;
	}

	public JButton getEditBtn() {
		return editBtn;
	}

	public JButton getReceiveBinaryBtn() {
		return receiveBinaryBtn;
	}

	public JButton getFilterBtn() {
		return filterBtn;
	}

	public JButton getFindBtn() {
		return findBtn;
	}

	public JTextArea getXmlArea()
	{
		return xmlArea;
	}

	public JMenu getHideMenu()
	{
		return hideMenu;
	}

	public JMenuItem getReloadMenu()
	{
		return reloadMenu;
	}

	public JMenuItem getFilterManagerMenu()
	{
		return filterManagerMenu;
	}

	public JMenuItem getSaveOperator()
	{
		return saveOperator;
	}

	public JMenuItem getLoadOperator()
	{
		return loadOperator;
	}

	@Override
	public void showMessage(TSBean tsBean) {
		proxyOperator.showMessage(tsBean);
	}

	@Override
	public void showLog(LogLevel level, String message) {
		proxyOperator.showLog(level, message);
	}

	@Override public void showSyncMessage(TSBean tsBean)
	{
		proxyOperator.showSyncMessage(tsBean);
	}

	@Override
	public void sendSelectItem() {
		proxyOperator.sendSelectItem();
	}

	@Override
	public void sendMarkItem() {
		proxyOperator.sendMarkItem();
	}

	@Override
	public void markItem() {
		proxyOperator.markItem();
	}

	@Override
	public void sendNotSendItem() {
		proxyOperator.sendNotSendItem();
	}

	@Override
	public void cancelMarkItem() {
		proxyOperator.cancelMarkItem();
	}

	@Override
	public void deleteAllItem() {
		proxyOperator.deleteAllItem();
	}

	@Override
	public boolean listen(boolean isCancel) {
		return proxyOperator.listen(isCancel);
	}

	@Override
	public void cancelListen() {
		proxyOperator.cancelListen();
	}

	@Override
	public void receiveBinary(boolean isReceive) {
		proxyOperator.receiveBinary(isReceive);
	}

	@Override
	public void up(boolean isUp) {
		proxyOperator.up(isUp);
	}

	@Override
	public void down(boolean isDown) {
		proxyOperator.down(isDown);
	}

	@Override
	public void start() {
		proxyOperator.start();
	}

	@Override
	public void pause() {
		proxyOperator.pause();
	}

	@Override
	public void stop() {
		proxyOperator.stop();
	}

	@Override
	public void connect() {
		proxyOperator.connect();
	}

	@Override
	public void showEditFilterDialog() {
		proxyOperator.showEditFilterDialog();
	}

	@Override
	public boolean filterWithCondition(String conditionStr) {
		return proxyOperator.filterWithCondition(conditionStr);
	}

	@Override
	public void resetFilterCondition() {
		proxyOperator.resetFilterCondition();
	}

	@Override
	public void setFindCondition() {
		proxyOperator.setFindCondition();
	}

	@Override
	public void filterData(String item) {
		proxyOperator.filterData(item);
	}

	@Override public void setXmlData(TSBean tsBean)
	{
		proxyOperator.setXmlData(tsBean);
	}

	@Override public void hideColumn(String colName, boolean save)
	{
		proxyOperator.hideColumn(colName, save);
	}

	@Override public void showColumn(String colName, boolean save)
	{
		proxyOperator.showColumn(colName, save);
	}

	@Override public void setRecordData(List<WrapTsBean> tsBeans)
	{
		proxyOperator.setRecordData(tsBeans);
	}

	@Override public void replay()
	{
		proxyOperator.replay();
	}

	@Override public void pausePlay()
	{
		proxyOperator.pausePlay();
	}

	@Override public void stopPlay()
	{
		proxyOperator.stopPlay();
	}

	@Override
	public void moveUp()
	{
		proxyOperator.moveUp();
	}

	@Override
	public void moveDown()
	{
		proxyOperator.moveDown();
	}

	@Override
	public void aimAt()
	{
		proxyOperator.aimAt();
	}

	@Override public void setFixed(boolean flag)
	{
		proxyOperator.setFixed(flag);
	}


}
