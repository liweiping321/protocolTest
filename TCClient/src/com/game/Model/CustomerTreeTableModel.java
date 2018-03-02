package com.game.Model;

import com.game.TCClient;
import com.game.ts.net.bean.InfoBean;
import com.game.ts.net.bean.WrapTsBean;
import com.game.util.LoggerUtil;
import com.game.utils.InfoBeanUtils;
import com.game.utils.language.LanguageMgr;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

/**
 * @author jianpeng.zhang
 * @since 2017/1/9.
 */
public class CustomerTreeTableModel extends DefaultTreeTableModel {
	private String[] _names = { "MODE", "VALUE"};
	private Class<?>[] _types = { String.class, String.class};

	private WrapTsBean bean = null;
	private JXTreeTable treeTable;
	private InfoBean infoBean;

	public CustomerTreeTableModel(final JXTreeTable treeTable) {
		super();
		this.treeTable = treeTable;
	}

	/**
	 * 列的类型
	 */
	@Override
	public Class<?> getColumnClass(int col) {
		return _types[col];
	}

	/**
	 * 列的数量
	 */
	@Override
	public int getColumnCount() {
		return _names.length;
	}

	/**
	 * 表头显示的内容
	 */
	@Override
	public String getColumnName(int column) {

		return _names[column];
	}

	@Override
	public void setValueAt(Object value, Object node, int column) {
		super.setValueAt(value, node, column);
		if (node instanceof DefaultMutableTreeTableNode) {
			Object userObject = ((DefaultMutableTreeTableNode) node).getUserObject();
			if (userObject instanceof InfoBean) {
				switch (column) {
				case 1:
					InfoBean currentBean = (InfoBean) userObject;
					if (!currentBean.getValue().equals(value.toString()))
					{
						String result = currentBean.checkCurrentValueType(value.toString());
						if (currentBean.getValue().equals(result))
						{
							LoggerUtil.error(TCClient.clientUI,
											LanguageMgr.getTranslation("TClient.TreeTable.ChangeValue.Error"));
							return;
						}

						TCClient.clientUI.getTreeTableMenu().addHistory(
								getTreeTableRowPath((DefaultMutableTreeTableNode) node), currentBean.getValue());
						
						LoggerUtil.warn(TCClient.clientUI,
										String.format(LanguageMgr.getTranslation("TClient.TreeTable.ChangeValue.Log"),
													  bean.getTsBean().getId(), ((InfoBean) userObject).getMode(),
													  ((InfoBean) userObject).getValue(), value.toString()));
						((InfoBean) userObject).setValue(value.toString());
					}
					break;
				}
			}
		}
		if (infoBean != null)
		{
			bean.getTsBean().setContent(InfoBeanUtils.beans2JsonString(infoBean));

			// TODO: 2017/2/27 修改录制的数据
			// 通过改变协议树里表格的宽度来显示横向滚动条
			// treeTable.getColumnModel().getColumn(0).setWidth(200);
			// treeTable.getColumnModel().getColumn(0).setPreferredWidth(200);
			// treeTable.getColumnModel().getColumn(treeTable.getColumnCount() - 1).setWidth(400);
			// treeTable.getColumnModel().getColumn(treeTable.getColumnCount() - 1).setPreferredWidth(400);
		}
	}

	public static String getTreeTableRowPath(DefaultMutableTreeTableNode treeTableNode)
	{
		if (treeTableNode == null)
		{
			return null;
		}
		StringBuilder path = new StringBuilder();
		path.append(((InfoBean) treeTableNode.getUserObject()).getMode());
		while (treeTableNode.getParent() != null)
		{
			treeTableNode = (DefaultMutableTreeTableNode) treeTableNode.getParent();
			path.insert(0, ((InfoBean) treeTableNode.getUserObject()).getMode() + ".");
		}
		return path.toString();
	}

	/**
	 * 返回在单元格中显示的Object
	 */
	@Override
	public Object getValueAt(Object node, int column) {
		Object value = null;
		if (node instanceof DefaultMutableTreeTableNode) {
			DefaultMutableTreeTableNode mutableNode = (DefaultMutableTreeTableNode) node;
			Object o = mutableNode.getUserObject();
			if (o != null && o instanceof InfoBean) {
				InfoBean bean = (InfoBean) o;
				switch (column) {
				case 0:
					value = bean.getMode();
					break;
				case 1:
					if (bean.getType().equals(InfoBean.TypeBase) || bean.getType().equals(InfoBean.TypeArrayBase))
					{
						value = bean.getValue();
					}
					break;
				}
			}
		}
		return value;
	}

	/**
	 * 设置所有单元格都不能编辑
	 */
	@Override
	public boolean isCellEditable(Object node, int column) {
		if (node instanceof DefaultMutableTreeTableNode)
		{
			InfoBean bean = (InfoBean) ((DefaultMutableTreeTableNode) node).getUserObject();
			return bean.isEditable() && column != 0 && (bean.getType().contains(InfoBean.TypeBase));
		}
		return column != 0 ;
	}

	public void setBean(Object object) {
		if (object instanceof WrapTsBean) {
			this.bean = (WrapTsBean) object;
		} else {
			setRoot(null);
			return;
		}
		if (bean.getTsBean() == null || bean.getTsBean().getContent()==null) {
			return;
		}

		infoBean = InfoBeanUtils.json2InfoBeans(bean.getTsBean());
		 
		DefaultMutableTreeTableNode root = getNode(infoBean);
		setRoot(root);
		treeTable.expandAll();
		treeTable.setHorizontalScrollEnabled(true);
		// treeTable.addTreeExpansionListener(new TreeExpansionListener() {
		// 	@Override public void treeExpanded(TreeExpansionEvent event)
		// 	{
		//
		// 	}
        //
		// 	@Override public void treeCollapsed(TreeExpansionEvent event)
		// 	{
        //
		// 	}
		// });
		// treeTable.getPathForRow(0);
	}

	private DefaultMutableTreeTableNode getNode(InfoBean bean) {
		DefaultMutableTreeTableNode node = new DefaultMutableTreeTableNode(bean);
		if (bean.getChild() != null && bean.getChild().size() > 0) {
			for (InfoBean bean1 : bean.getChild()) {
				node.add(getNode(bean1));
			}
		}
		return node;
	}
}
