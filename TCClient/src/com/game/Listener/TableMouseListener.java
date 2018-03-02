package com.game.Listener;

import com.game.ClientUI.Client;
import com.game.Model.CustomerTreeTableModel;
import com.game.Model.RowNumberTableModel;
import com.game.ts.net.bean.WrapTsBean;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

/**
 * @author jianpeng.zhang
 * @since 2017/1/17.
 */
public class TableMouseListener extends MouseAdapter {
	private Client client;

	public TableMouseListener(Client client) {
		this.client = client;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);

		if (client.getTable().getSelectedRows().length == 1) {
			Vector<?> vector = (Vector<?>) ((DefaultTableModel) ((JTable) e.getSource()).getModel()).getDataVector()
					.get(client.getTable().getSelectedRow());
			WrapTsBean tsBean =
					(WrapTsBean) vector.get(((RowNumberTableModel) client.getTable().getModel()).findColumn("tsBean"));
			((CustomerTreeTableModel) client.getTreetable().getTreeTableModel()).setBean(tsBean);

			if (tsBean.getTsBean() == null)
			{
				client.setXmlData(null);
			}
			else
			{
				client.setXmlData(tsBean.getTsBean());
			}
		}
	}
}
