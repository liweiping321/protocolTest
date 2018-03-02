package com.game.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author jianpeng.zhang
 * @since 2017/1/10.
 */
public class FilterStringPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JLabel label = new JLabel();

	public FilterStringPanel() {
		this("");
	}

	public FilterStringPanel(String filterText) {
		setLayout(new BorderLayout());
		add(label, "Center");
		label.setFont(new Font("", 0, 13));
		label.setText(filterText);
		setBackground(new Color(57689));
	}

	public void setText(String filterText) {
		if (filterText != null) {
			label.setText(filterText);
		}
	}

	public String getText() {
		return label.getText();
	}
}
