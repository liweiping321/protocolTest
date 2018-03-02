package com.game.Render;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * @author jianpeng.zhang
 * @since 2017/1/6.
 */
public class RowHeadRender extends JLabel implements TableCellRenderer
{
    private static final long serialVersionUID = -77608456654644254L;

    public RowHeadRender(JTable table)
    {
        JTableHeader header = table.getTableHeader();
        setOpaque(true);//是否是不透明的
        // setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        setBorder(table.getTableHeader().getBorder());
        setHorizontalAlignment(CENTER);
        setForeground(header.getForeground());
        setBackground(header.getBackground());
        setFont(header.getFont());
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column)
    {
        setText(value == null ? "" : value.toString());
        return this;
    }
}