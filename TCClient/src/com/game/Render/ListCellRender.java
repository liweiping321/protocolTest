package com.game.Render;

import javax.swing.*;
import java.awt.*;

/**
 * @author jianpeng.zhang
 * @since 2017/2/20.
 */
public class ListCellRender extends DefaultListCellRenderer
{
    private Color textColor;

    public ListCellRender(Color color)
    {
        textColor = color;
    }

    public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus)
    {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Font font = new Font("", 0, 15);
        setFont(font);
        if (textColor != null)
        {
            setForeground(textColor);
        }
        return this;
    }

}
