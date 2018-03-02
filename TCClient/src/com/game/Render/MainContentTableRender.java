package com.game.Render;

import com.game.Model.RowNumberTableModel;
import com.game.ts.net.bean.WrapTsBean;
import sun.swing.DefaultLookup;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Vector;

/**
 * @author jianpeng.zhang
 * @since 2017/1/10.
 */
public class MainContentTableRender extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private RowNumberTableModel tableModel;
	private final int SEND_MARK_ROW;
	private final int MARK_ROW;
	private final int DIRE_ROW;

	public MainContentTableRender(JTable table) {
		setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		tableModel = (RowNumberTableModel) table.getModel();
		SEND_MARK_ROW = tableModel.findColumn("action");
		MARK_ROW = tableModel.findColumn("id");
		DIRE_ROW = tableModel.findColumn("directType");

	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		// ((MainContentTableRender) component).setToolTipText(value.toString());

		if (!isSelected) {

			if (tableModel != null)
			{
				if (column == SEND_MARK_ROW && ((WrapTsBean) ((Vector) tableModel.getDataVector().get(row))
						.get(tableModel.findColumn("tsBean"))).isHasSend())
				{
					component.setBackground(Color.green);
				}
				else if (column == MARK_ROW && ((WrapTsBean) ((Vector) tableModel.getDataVector().get(row))
						.get(tableModel.findColumn("tsBean"))).isHasMark())
				{
					component.setBackground(new Color(0xD6EA5A));
				}
				else if (column == DIRE_ROW)
				{
					component.setBackground(((WrapTsBean) ((Vector) tableModel.getDataVector().get(row))
							.get(tableModel.findColumn("tsBean"))).getBackgroundColor());
				}
				else
				{
					Color background = table.getBackground();
					if (background == null || background instanceof javax.swing.plaf.UIResource)
					{
						Color alternateColor = DefaultLookup.getColor(this, ui, "Table.alternateRowColor");
						if (alternateColor != null && row % 2 != 0)
						{
							background = alternateColor;
						}
					}
					super.setBackground(background);
				}
			}

		} else {
			Color bg = DefaultLookup.getColor(this, ui, "Table.dropCellBackground");
			super.setBackground(bg == null ? table.getSelectionBackground() : bg);
		}
		return component;
	}

}
