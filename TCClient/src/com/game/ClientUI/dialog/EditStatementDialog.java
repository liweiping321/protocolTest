package com.game.ClientUI.dialog;

import com.game.utils.StringUtil;
import com.game.utils.language.LanguageMgr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * @author jianpeng.zhang
 * @since 2017/2/20.
 * 修改过滤表达式的dialog
 */
public class EditStatementDialog extends AbstractEditorDialog implements ActionListener
{
    private JList jList;
    private int selectIndex;

    public EditStatementDialog(JFrame owner, JList jList, int selectIndex)
    {
        super(owner, LanguageMgr.getTranslation("TClient.Label.Editor"));
        this.jList = jList;
        this.selectIndex = selectIndex;
        okButton.addActionListener(this);
        cancelBtn.addActionListener(this);
        JLabel label = new JLabel(LanguageMgr.getTranslation("TClient.Label.ModifyFilter.Tip"));
        label.setFont(new Font("", 0, 16));
        label.setBounds(25, 170, 200, 35);
        add(label);

        textArea.setText(jList.getSelectedValue().toString());

        this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2,
                         (Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2);

    }

    @Override public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == okButton)
        {
            if (StringUtil.isEmpty(textArea.getText()))
            {
                JOptionPane.showMessageDialog(null, LanguageMgr.getTranslation("TClient.Label.Warn"));
                return;
            }
            ((DefaultListModel) jList.getModel()).set(selectIndex, textArea.getText());
            dispose();
        }
        else if (e.getSource() == cancelBtn)
        {
            dispose();
        }
    }

    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        EditStatementDialog dialog = new EditStatementDialog(frame, null, 0);
        dialog.setVisible(true);
    }
}
