package com.game.Render;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.game.component.FilterStringPanel;

/**
 * @author jianpeng.zhang
 * @since 2017/1/10.
 */
public class FilterBoxListRender implements ListCellRenderer<FilterStringPanel> {
	@Override
	public Component getListCellRendererComponent(JList<? extends FilterStringPanel> list, FilterStringPanel value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (isSelected) {
			value.setBackground(Color.LIGHT_GRAY);
		} else {
			value.setBackground(Color.WHITE);
		}
		return value;
	}
}
