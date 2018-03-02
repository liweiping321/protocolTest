package com.game.Render;

import com.game.utils.LogUtils;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

/**
 * 实现jList拖动
 */
public class ListTransferHandler extends TransferHandler {
	private int[] indices = null;
	private int addIndex = -1;
	private int addCount = 0;

	private String exportString(JComponent c) {
		JList list = (JList) c;
		indices = list.getSelectedIndices();
		List values = list.getSelectedValuesList();

		StringBuilder buff = new StringBuilder();

		for (int i = 0; i < values.size(); i++) {
			Object val = values.get(i);
			buff.append(val == null ? "" : val.toString());
			if (i != values.size() - 1) {
				buff.append("\n");
			}
		}

		return buff.toString();
	}

	private void importString(JComponent c, String str) {
		JList target = (JList) c;
		DefaultListModel listModel = (DefaultListModel) target.getModel();
		int index = target.getSelectedIndex();

		if (indices != null && index >= indices[0] - 1 && index <= indices[indices.length - 1]) {
			indices = null;
			return;
		}

		int max = listModel.getSize();
		if (index < 0) {
			index = max;
		} else {
			index++;
			if (index > max) {
				index = max;
			}
		}
		addIndex = index;
		String[] values = str.split("\n");
		addCount = values.length;
		for (String value : values) {
			listModel.add(index++, value);
		}
	}

	private void cleanup(JComponent c, boolean remove) {
		if (remove && indices != null) {
			JList source = (JList) c;
			DefaultListModel model = (DefaultListModel) source.getModel();
			if (addCount > 0) {
				for (int i = 0; i < indices.length; i++) {
					if (indices[i] > addIndex) {
						indices[i] += addCount;
					}
				}
			}
			for (int i = indices.length - 1; i >= 0; i--) {
				model.remove(indices[i]);
			}
		}
		indices = null;
		addCount = 0;
		addIndex = -1;
	}

	protected Transferable createTransferable(JComponent c) {
		return new StringSelection(exportString(c));
	}

	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}

	public boolean importData(JComponent c, Transferable t) {
		if (canImport(c, t.getTransferDataFlavors())) {
			try {
				String str = (String) t.getTransferData(DataFlavor.stringFlavor);
				importString(c, str);
				return true;
			} catch (UnsupportedFlavorException | IOException e) {
				LogUtils.error(e);
			}
		}
		return false;
	}

	protected void exportDone(JComponent c, Transferable data, int action) {
		cleanup(c, action == MOVE);
	}

	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		for (DataFlavor flavor : flavors) {
			if (DataFlavor.stringFlavor.equals(flavor)) {
				return true;
			}
		}
		return false;
	}
}
