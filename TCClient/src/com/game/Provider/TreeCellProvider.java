package com.game.Provider;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;

import org.jdesktop.swingx.renderer.CellContext;
import org.jdesktop.swingx.renderer.ComponentProvider;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

import com.game.Model.TreeTableSelectionModel;
import com.game.ts.net.bean.InfoBean;

/**
 * @author jianpeng.zhang
 * @since 2017/1/9.
 */
public class TreeCellProvider extends ComponentProvider<JPanel> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TreeTableSelectionModel selectionModel;
	private JLabel _label = null;

	public TreeCellProvider(TreeTableSelectionModel selectionModel) {
		this.selectionModel = selectionModel;
		_label = new JLabel(); // 创建一个JLabel实例
	}

	@Override
	protected void format(CellContext arg0) {
		// 从CellContext获取tree中的文字和图标
		JTree tree = (JTree) arg0.getComponent();
		DefaultMutableTreeTableNode node = (DefaultMutableTreeTableNode) arg0.getValue();
		InfoBean bean = (InfoBean) node.getUserObject();
		_label.setText(bean.getMode());
		_label.setIcon(null);

		// TreePath path = tree.getPathForRow(arg0.getRow());
		// if (path != null)
		// {
		// if (selectionModel.isPathSelected(path, true))
		// {
		// }
		// }

		// 使用BorderLayout布局，依次放置TristateCheckBox和JLabel
		rendererComponent.setLayout(new BorderLayout());
		rendererComponent.add(_label, BorderLayout.LINE_END);
	}

	@Override
	protected void configureState(CellContext arg0) {
	}

	/**
	 * 初始化一个JPanel来放置TristateCheckBox和JLabel
	 */
	@Override
	protected JPanel createRendererComponent() {
		return new JPanel();
	}
}
