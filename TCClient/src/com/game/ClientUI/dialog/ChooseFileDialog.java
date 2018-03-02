package com.game.ClientUI.dialog;

import com.game.Data.DataProvider;
import com.game.Data.RecordData;
import com.game.Data.SaveModule;
import com.game.TCClient;
import com.game.config.TCConfig;
import com.game.ts.net.bean.WrapTsBean;
import com.game.utils.StringUtil;
import com.game.utils.language.LanguageMgr;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static com.game.utils.ComponentUtil.createComboBox;

/**
 * @author jianpeng.zhang
 * @since 2017/2/23.
 */
public class ChooseFileDialog extends JDialog implements ActionListener {

	private JButton chooseBtn;
	private JButton okButton;
	private JButton cancelBtn;

	private JComboBox comboBox;

	public ChooseFileDialog(Frame owner) {
		super(owner, "选择文件");
		setSize(750, 150);
		JLabel path = new JLabel("Case Name");
		path.setFont(new Font("", 0, 15));
		chooseBtn = new JButton("Choose");
		chooseBtn.addActionListener(this);
		okButton = new JButton("OK");
		okButton.addActionListener(this);
		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(this);

		comboBox = createComboBox(DataProvider.getInstance().getModuleData(SaveModule.OpenHistory, String[].class),
				false, true, new Dimension(520, 30));

		setLayout(null);
		add(path);
		add(chooseBtn);
		add(comboBox);
		add(okButton);
		add(cancelBtn);

		path.setBounds(15, 17, 100, 30);
		comboBox.setBounds(110, 20, 520, 30);
		chooseBtn.setBounds(645, 20, 70, 30);
		okButton.setBounds(460, 65, 90, 30);
		cancelBtn.setBounds(580, 65, 90, 30);

		this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == chooseBtn) {
			JFileChooser dialog = new JFileChooser();
			dialog.setDialogTitle(LanguageMgr.getTranslation("TClient.Dialog.OpenFile.Title"));
			dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);

			if (StringUtil.isEmpty(TCConfig.getRecordPath())) {
				dialog.setCurrentDirectory(new File(System.getProperty("user.dir"))); // 设置默认保存路径为桌面路径
			} else {
				dialog.setCurrentDirectory(new File(TCConfig.getRecordPath()));
			}

			dialog.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					return f.isDirectory() || (f.isFile() && f.getName().endsWith(".tb"));
				}

				@Override
				public String getDescription() {
					return null;
				}
			});
			int result = dialog.showSaveDialog(dialog);
			if (result == JFileChooser.APPROVE_OPTION) {
				comboBox.getModel().setSelectedItem(dialog.getSelectedFile().getAbsolutePath());
			}
		} else if (e.getSource() == okButton) {
			if (StringUtil.isEmpty(comboBox.getModel().getSelectedItem().toString())) {
				dispose();
			} else if (!comboBox.getModel().getSelectedItem().toString().endsWith(".tb")) {
				JOptionPane.showMessageDialog(null, LanguageMgr.getTranslation("TClient.Warn.FileType.NoRight"));
			} else {
				File file = new File(comboBox.getModel().getSelectedItem().toString());
				if (!file.exists()) {
					JOptionPane.showMessageDialog(null, LanguageMgr.getTranslation("TClient.File.Error"));
					return;
				}

				DataProvider.getInstance().getModule(SaveModule.RecordData, RecordData.class)
						.setFilePath(file.getParent());
				DataProvider.getInstance().getModule(SaveModule.RecordData, RecordData.class)
						.setFileName(file.getName());
				DataProvider.getInstance().getModule(SaveModule.RecordData, RecordData.class).load();
				java.util.List<WrapTsBean> tsBeanList = DataProvider.getInstance().getModuleData(SaveModule.RecordData,
						java.util.List.class);
				TCClient.clientUI.setRecordData(tsBeanList);
				TCClient.clientUI.getReplayBtn().setEnabled(true);

				DataProvider.getInstance().addModuleData(SaveModule.OpenHistory, file.getAbsolutePath());
				dispose();
			}
		} else if (e.getSource() == cancelBtn) {
			dispose();
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.pack();
		frame.setVisible(true);
		ChooseFileDialog dialog = new ChooseFileDialog(frame);
		dialog.setVisible(true);
	}
}
