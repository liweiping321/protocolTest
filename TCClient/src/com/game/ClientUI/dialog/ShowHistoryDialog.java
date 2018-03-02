package com.game.ClientUI.dialog;

import com.game.Data.DataProvider;
import com.game.Data.SaveModule;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author jianpeng.zhang
 * @since 2017/2/17.
 */
public class ShowHistoryDialog extends JDialog implements ActionListener
{
    private JButton okButton;

    private JButton cancelButton;

    private JList jList;

    private JTextArea jTextArea;

    public ShowHistoryDialog(Frame owner, String title, final JTextArea jTextArea)
    {
        super(owner, title);
        setSize(700, 350);
        this.jTextArea = jTextArea;
        jList = new JList();

        ListModel<String> jList1Model = new DefaultComboBoxModel<>(
                DataProvider.getInstance().getModuleData(SaveModule.FindConditionText, String[].class));

        jList.setModel(jList1Model);
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setViewportView(jList);
        jScrollPane.setBorder(new LineBorder(new Color(0x00A5FF), 2));

        setLayout(null);
        add(jScrollPane);
        add(okButton);
        add(cancelButton);

        jScrollPane.setBounds(25, 20, 640, 240);
        okButton.setBounds(460, 272, 85, 30);
        cancelButton.setBounds(560, 272, 85, 30);

        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        jList.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() == 2)
                {
                    if (jTextArea != null)
                    {
                        int index = jList.getSelectedIndex();    //已选项的下标
                        jTextArea.setText(jList.getModel().getElementAt(index).toString());
                        dispose();
                    }
                }
            }
        });

        this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2,
                         (Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2);
    }

    @Override public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == okButton)
        {
            if (jTextArea != null)
            {
                int index = jList.getSelectedIndex();
                jTextArea.setText(jList.getModel().getElementAt(index).toString());
                dispose();
            }
        }
        else if (e.getSource() == cancelButton)
        {
            dispose();
        }
    }
}
