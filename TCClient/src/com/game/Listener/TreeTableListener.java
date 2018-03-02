package com.game.Listener;

import com.game.ClientUI.TreeTableMenu;
import com.game.Data.TableDateProvider;
import com.game.Model.CustomerTreeTableModel;
import com.game.Model.TreeTableSelectionModel;
import com.game.Provider.TreeCellProvider;
import com.game.ts.net.bean.InfoBean;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author jianpeng.zhang
 * @since 2017/1/9.
 */
public class TreeTableListener implements TreeSelectionListener, MouseListener
{
	private TreeTableSelectionModel selectionModel;
	private JXTreeTable treeTable;
	private JTree tree;
	private TreeTableMenu menu;

	public TreeTableListener(JXTreeTable treeTable, TreeTableMenu menu) {
		this.treeTable = treeTable;
		this.menu = menu;
		this.tree = (JTree) treeTable.getCellRenderer(0, 0);
		selectionModel = new TreeTableSelectionModel(tree.getModel());
		tree.setCellRenderer(new DefaultTreeRenderer(new TreeCellProvider(selectionModel)));
		treeTable.addTreeSelectionListener(this);
		treeTable.addMouseListener(this);
	}

	public TreeTableSelectionModel getSelectionModel() {
		return selectionModel;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TreePath path = e.getPath();
		if (path == null) {
			return;
		}
		boolean selected = selectionModel.isPathSelected(path, true);

		try {
			if (selected) {
				selectionModel.removeSelectionPath(path);
			} else {
				selectionModel.addSelectionPath(path);
			}
		} finally {
			treeTable.repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{

	}

	@Override
	public void mousePressed(MouseEvent e)
	{

	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (e.isPopupTrigger() && e.getSource() instanceof JXTreeTable)
		{
			if (((JXTreeTable) e.getSource()).columnAtPoint(e.getPoint()) <= 0
					|| ((JXTreeTable) e.getSource()).rowAtPoint(e.getPoint()) <= 0)
			{
				return;
			}
			int selectRow = ((JTable) e.getSource()).rowAtPoint(e.getPoint());
			if (selectRow > 0)
			{
				((JTable) e.getSource()).setRowSelectionInterval(selectRow, selectRow);
			}
			if (menu != null)
			{
				DefaultMutableTreeTableNode treeTableNode =
						(DefaultMutableTreeTableNode) ((JXTreeTable) e.getSource()).getPathForRow(selectRow)
								.getLastPathComponent();
				InfoBean bean = (InfoBean) treeTableNode.getUserObject();

				if (bean.getType().equals(InfoBean.TypeBase))
				{
					// bean.getMode();
					// bean.getValue();
					// bean.getValueType();
					if (TableDateProvider.canOperate()) {
						menu.show((JComponent) e.getSource(), e.getX(), e.getY(),
								  CustomerTreeTableModel.getTreeTableRowPath(treeTableNode));
					}
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{

	}

	@Override
	public void mouseExited(MouseEvent e)
	{

	}
}
