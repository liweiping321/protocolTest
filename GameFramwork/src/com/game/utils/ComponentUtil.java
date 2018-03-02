package com.game.utils;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;

/**
 * @author jianpeng.zhang
 * @since 2017/1/9.
 */
public class ComponentUtil {
	public static void addSeparator(Container container, int orientation, Color color, Dimension maximumSize) {
		container.add(createSeparator(orientation, color, maximumSize));
	}

	public static JSeparator createSeparator(int orientation, Color color, Dimension maximumSize) {
		JSeparator jSeparator = new JSeparator(orientation);
		if (color != null) {
			jSeparator.setForeground(color);
		}
		if (maximumSize != null) {
			jSeparator.setMaximumSize(maximumSize);
		}
		return jSeparator;
	}

	public static void addEmptyLabel(Container container, int width, int height) {
		JLabel label = new JLabel();
		label.setBackground(new Color(0xF9F9F9));
		label.setMaximumSize(new Dimension(width, height));
		container.add(label);
	}

	public static <E> JComboBox<E> createComboBox(Dimension dimension) {
		return createComboBox(null, true, true, dimension);
	}

	public static <E> JComboBox<E> createComboBox(E[] textLabels, boolean editable, boolean focusable,
			Dimension dimension) {
		JComboBox<E> comboBox;
		if (textLabels != null) {
			comboBox = new JComboBox<E>(textLabels);
		} else {
			comboBox = new JComboBox<>();
		}
		comboBox.setEditable(editable);
		comboBox.setFocusable(focusable);
		if (dimension != null) {
			comboBox.setPreferredSize(dimension);
			comboBox.setMaximumSize(dimension);
		}
		return comboBox;
	}

	public static void insert(JTextPane textPane, String label, String content, AttributeSet attrset,
			AttributeSet contentAttr) {

		Document docs = textPane.getDocument();// 利用getDocument()方法取得JTextPane的Document
												// instance.
		content = content + "\n";
		try {
			docs.insertString(docs.getLength(), label, attrset);
			docs.insertString(docs.getLength(), content, contentAttr);
		} catch (BadLocationException ble) {
			System.out.println("BadLocationException:" + ble);
		}
	}

	public static void createFilterStringPanel() {

	}

	public static void setJTableWidth(JTable table, int index, int width) {
		TableColumn column = table.getTableHeader().getColumnModel().getColumn(index);
		column.setPreferredWidth(width);
		column.setMaxWidth(width);
		column.setMinWidth(width);
		column.setWidth(width);

		column = table.getColumnModel().getColumn(index);
		column.setPreferredWidth(width);
		column.setMaxWidth(width);
		column.setMinWidth(width);
		column.setWidth(width);
	}

	public static JButton createButton(String name, String tip)
	{
		JButton jButton = new JButton(name);
		jButton.setToolTipText(tip);
		return jButton;
	}

	public static JButton createButton(String name, String tip, int width, int height)
	{
		JButton jButton = new JButton(name);
		jButton.setToolTipText(tip);
		if (width > 0 && height > 0)
		{
			jButton.setPreferredSize(new Dimension(width, height));
			jButton.setMinimumSize(new Dimension(width, height));
			jButton.setMaximumSize(new Dimension(width, height));
		}
		return jButton;
	}

	public static JTextArea createTextArea(Dimension maximumSize) {
		JTextArea jTextArea = new JTextArea();
		if (maximumSize != null)
		{
			jTextArea.setPreferredSize(maximumSize);
			jTextArea.setMinimumSize(maximumSize);
			jTextArea.setMaximumSize(maximumSize);
		}
		return jTextArea;
	}
}
