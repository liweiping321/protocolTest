package com.game.ClientUI.dialog;

import com.game.Data.DataProvider;
import com.game.Data.SaveModule;
import com.game.Model.RowNumberTableModel;
import com.game.TCClient;
import com.game.express.eng.ExpressEngine;
import com.game.util.LoggerUtil;
import com.game.utils.ComponentUtil;
import com.game.utils.StringUtil;
import com.game.utils.language.LanguageMgr;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author jianpeng.zhang
 * @since 2017/2/4.
 */
public class FilterEditorDialog extends JDialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextArea textArea;
	private JButton findBtn;
	private JButton cancelBtn;
	private JButton historyBtn;
	private JButton exampleBtn;

	public FilterEditorDialog(JFrame f, String s) {
		super(f, s);
		setSize(700, 250);

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0x00A5FF), 2));
		panel.setLayout(new GridLayout());

		textArea = new JTextArea();
		textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		textArea.setMargin(new Insets(500, 5, 5, 5));
		textArea.setLineWrap(true);
		panel.add(textArea);

		findBtn = ComponentUtil.createButton(LanguageMgr.getTranslation("TClient.Button.Find"), "");
		cancelBtn = ComponentUtil.createButton(LanguageMgr.getTranslation("TClient.Button.Cancel"), "");
		historyBtn = ComponentUtil.createButton(LanguageMgr.getTranslation("TClient.Button.History"), "");
		exampleBtn = ComponentUtil.createButton(LanguageMgr.getTranslation("TClient.Button.Example"), "");

		setLayout(null);
		add(panel);
		add(findBtn);
		add(cancelBtn);
		add(historyBtn);
		add(exampleBtn);

		panel.setBounds(25, 20, 640, 140);
		findBtn.setBounds(260, 172, 85, 30);
		cancelBtn.setBounds(360, 172, 85, 30);
		historyBtn.setBounds(460, 172, 85, 30);
		exampleBtn.setBounds(560, 172, 85, 30);

		findBtn.addActionListener(this);
		cancelBtn.addActionListener(this);
		historyBtn.addActionListener(this);
		exampleBtn.addActionListener(this);

		this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2);
		// JOptionPane.showOptionDialog()
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == findBtn) {
			if (StringUtil.isEmpty(textArea.getText())) {
				TCClient.clientUI.getFilterComboBox().getModel()
						.setSelectedItem(LanguageMgr.getTranslation("TClient.JComboBox.AllData"));
				FilterEditorDialog.this.dispose();
				return;
			}

			try {
				ExpressEngine.checkSyntax(textArea.getText());
				((RowNumberTableModel) TCClient.clientUI.getTable().getModel()).setDataCondition(textArea.getText());
				String history = textArea.getText();
				FilterEditorDialog.this.dispose();
				if (history != null)
					DataProvider.getInstance().addModuleData(SaveModule.FindConditionText, history);
			} catch (Exception e1) {
				LoggerUtil.error(TCClient.clientUI, LanguageMgr.getTranslation("TClient.JComboBox.Condition.Error"));
				LoggerUtil.error(TCClient.clientUI, "Exception : " + e1.getMessage());
				JOptionPane.showMessageDialog(null, LanguageMgr.getTranslation("TClient.JComboBox.Condition.Error"));
			}
		} else if (e.getSource() == cancelBtn) {
			this.dispose();
		} else if (e.getSource() == historyBtn) {

			ShowHistoryDialog historyDialog = new ShowHistoryDialog(TCClient.clientUI.getFrame(), LanguageMgr
					.getTranslation("TClient.Button.FindDialog.Title"), textArea);
			historyDialog.setVisible(true);

		} else if (e.getSource() == exampleBtn) {
			// textArea.setText("(([id=2]||content[isChangeChannel=true])||
			// [length <2000] &&)");
			textArea.setText("(content[other=0])&& [length <2000]");
		}
	}
}
