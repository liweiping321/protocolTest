package com.game.Model;

import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author jianpeng.zhang
 * @since 2017/1/9.
 */
public class TreeTableSelectionModel extends DefaultTreeSelectionModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TreeModel model;

	public TreeTableSelectionModel(TreeModel model) {
		this.model = model;
		setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
	}

	public boolean isPathSelected(TreePath path, boolean dig) {
		// System.out.println("-----------------1");
		if (!dig) {
			return super.isPathSelected(path);
		}
		while (path != null && !super.isPathSelected(path)) {
			path = path.getParentPath();
		}
		return path != null;
	}

}
