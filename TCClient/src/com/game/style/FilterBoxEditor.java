package com.game.style;

import com.game.component.FilterStringPanel;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @author jianpeng.zhang
 * @since 2017/1/10.
 */
public class FilterBoxEditor implements ComboBoxEditor {
	private JTextField editor = new JTextField("");
	private Color origin;
	private EventListenerList listenerList = new EventListenerList();
	private String realText;

	public FilterBoxEditor() {
		origin = editor.getBackground();
	}

	@Override
	public Component getEditorComponent() {
		return editor;
	}

	public void setRight() {
		editor.setBackground(new Color(0xB1E07A));
	}

	public void setError() {
		editor.setBackground(new Color(0xFF9599));
	}

	public void reset() {
		editor.setText("");
		editor.setBackground(origin);
		realText = "";
	}

	@Override
	public void setItem(Object anObject) {
		if (anObject instanceof FilterStringPanel) {
			realText = ((FilterStringPanel) anObject).getText();
			editor.setText(realText);	//会过滤掉换行符
			editor.setFont(new Font("", 0, 13));
		}
	}

	@Override
	public Object getItem() {
		return realText;
	}

	@Override
	public void selectAll() {

	}

	@Override
	public void addActionListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}

	@Override
	public void removeActionListener(ActionListener l) {
		listenerList.remove(ActionListener.class, l);
	}
}
