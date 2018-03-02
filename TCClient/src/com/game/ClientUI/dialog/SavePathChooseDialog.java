package com.game.ClientUI.dialog;

import com.game.Data.*;
import com.game.TCClient;
import com.game.config.TCConfig;
import com.game.util.LoggerUtil;
import com.game.utils.StringUtil;
import com.game.utils.language.LanguageMgr;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static com.game.utils.ComponentUtil.createComboBox;

/**
 * @author jianpeng.zhang
 * @since 2017/2/22.
 */
public class SavePathChooseDialog extends JDialog implements ActionListener
{
    private JButton chooseBtn;
    private JComboBox pathField;
    private JTextField fileName;
    private JTextField describe;


    private JCheckBox upBox;
    private JCheckBox downBox;
    private JCheckBox markBox;

    private JButton okButton;
    private JButton cancelBtn;

    public SavePathChooseDialog(Frame owner)
    {
        super(owner, "Save");
        setSize(680, 250);

        JLabel path = new JLabel("Save");
        JLabel module = new JLabel("Case");
        JLabel desc = new JLabel("Message");
        JLabel type = new JLabel("Package Type");

        path.setFont(new Font("", 0, 15));
        module.setFont(new Font("", 0, 15));
        desc.setFont(new Font("", 0, 15));
        type.setFont(new Font("", 0, 15));

        upBox = new JCheckBox("up", null, true);
        upBox.setFont(new Font("", 0, 15));
        downBox = new JCheckBox("down", null, true);
        downBox.setFont(new Font("", 0, 15));
        markBox = new JCheckBox("only marked", null, false);
        markBox.setFont(new Font("", 0, 15));
        upBox.addActionListener(this);
        downBox.addActionListener(this);
        markBox.addActionListener(this);

        fileName = new JTextField();
        describe = new JTextField();

        fileName.setFont(new Font("", 0, 15));
        describe.setFont(new Font("", 0, 15));

        okButton = new JButton("OK");
        cancelBtn = new JButton("Cancel");
        chooseBtn = new JButton("Open");
        chooseBtn.addActionListener(this);
        okButton.addActionListener(this);
        cancelBtn.addActionListener(this);

        setLayout(null);
        add(path);
        add(module);
        add(desc);
        add(type);
        add(fileName);
        add(describe);
        add(chooseBtn);
        add(upBox);
        add(downBox);
        add(markBox);
        add(okButton);
        add(cancelBtn);

        path.setBounds(15, 17, 100, 30);
        module.setBounds(15, 60, 100, 30);
        desc.setBounds(15, 98, 100, 30);
        type.setBounds(15, 135, 100, 30);
        upBox.setBounds(130, 137, 45, 30);
        downBox.setBounds(180, 137, 60, 30);
        markBox.setBounds(250, 137, 110, 30);
        okButton.setBounds(440, 170, 80, 30);
        cancelBtn.setBounds(540, 170, 80, 30);
        fileName.setBounds(90, 60, 560, 30);
        describe.setBounds(90, 100, 560, 30);
        chooseBtn.setBounds(580, 20, 70, 30);

        describe.setBorder(new LineBorder(new Color(0x00A5FF), 2));

        pathField = createComboBox(DataProvider.getInstance().getModuleData(SaveModule.RecordPathData, String[].class),
                                   true, false, new Dimension(480, 30));
        pathField.setFont(new Font(null, 0, 15));
        add(pathField);
        pathField.setBounds(90, 20, 480, 30);

        ((JTextField) pathField.getEditor().getEditorComponent()).setText("");

        this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2,
                         (Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2);
    }

    @Override public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == chooseBtn)
        {
            JFileChooser dialog = new JFileChooser();
            dialog.setDialogTitle("保存文件");
            dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            if (StringUtil.isEmpty(TCConfig.getRecordPath()))
            {
                dialog.setCurrentDirectory(new File(System.getProperty("user.dir"))); // 设置默认保存路径为桌面路径
            }
            else
            {
                dialog.setCurrentDirectory(new File(TCConfig.getRecordPath()));
            }

            dialog.setFileFilter(new FileFilter() {
                @Override public boolean accept(File f)
                {
                    return f.isDirectory();
                }

                @Override public String getDescription()
                {
                    return null;
                }
            });
            int result = dialog.showSaveDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION)
            {
                ((JTextField) pathField.getEditor().getEditorComponent())
                        .setText(dialog.getSelectedFile().getAbsolutePath());
            }
        }
        else if (e.getSource() == okButton)
        {
            if (StringUtil.isEmpty(((JTextField) pathField.getEditor().getEditorComponent()).getText()))
            {
                JOptionPane.showMessageDialog(null, LanguageMgr.getTranslation("TClient.Warn.Path.NoNull"));
                return;
            }
            else if (StringUtil.isEmpty(fileName.getText()))
            {
                JOptionPane.showMessageDialog(null, LanguageMgr.getTranslation("TClient.Warn.FileName.NoNull"));
                return;
            }

            RecordData recordData = DataProvider.getInstance().getModule(SaveModule.RecordData, RecordData.class);
            DataProvider.getInstance().getModule(SaveModule.RecordPathData, RecordPathHistory.class)
                    .addData(((JTextField) pathField.getEditor().getEditorComponent()).getText());
            recordData.setFilePath(((JTextField) pathField.getEditor().getEditorComponent()).getText());
            recordData.setFileName(fileName.getText());
            recordData.setMessage(describe.getText());

            RecordInfo.setIncludeUp(upBox.isSelected());
            RecordInfo.setIncludeDown(downBox.isSelected());
            RecordInfo.setOnlyMark(markBox.isSelected());
            DataProvider.getInstance().getModule(SaveModule.RecordData, RecordData.class).clear();
            dispose();
            RecordInfo.state = RecordInfo.RecordState.recording;
            LoggerUtil.info(TCClient.clientUI, LanguageMgr.getTranslation("TClient.Info.Record"));
        }
        else if (e.getSource() == cancelBtn)
        {
            dispose();
        }
        else if (e.getSource() == upBox)
        {
            if (!upBox.isSelected() && !downBox.isSelected())
            {
                upBox.setSelected(true);
                JOptionPane.showMessageDialog(null, LanguageMgr.getTranslation("TClient.Warn.CheckBox.NotEmpty"));
            }
        }
        else if (e.getSource() == downBox)
        {
            if (!upBox.isSelected() && !downBox.isSelected())
            {
                downBox.setSelected(true);
                JOptionPane.showMessageDialog(null, LanguageMgr.getTranslation("TClient.Warn.CheckBox.NotEmpty"));
            }
        }
        else if (e.getSource() == markBox && markBox.isSelected())
        {
            downBox.setSelected(true);
            upBox.setSelected(true);
        }
    }

    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        SavePathChooseDialog dialog = new SavePathChooseDialog(frame);
        dialog.setVisible(true);
    }
}

