package com.game.ClientUI.dialog;

import com.game.component.FilterStringPanel;
import com.game.utils.language.LanguageMgr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author jianpeng.zhang
 * @since 2017/2/21.
 * 新建过滤表达的dialog
 */
public class CreateStatementDialog extends AbstractEditorDialog implements ActionListener
{
    private JComboBox comboBox;
    public CreateStatementDialog(Frame owner, JComboBox comboBox)
    {
        super(owner, LanguageMgr.getTranslation("TClient.Dialog.Filter.Title"));
        this.comboBox = comboBox;
        okButton.addActionListener(this);
        cancelBtn.addActionListener(this);
        textArea.setText(comboBox.getEditor().getItem().toString());
    }

    @Override public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == okButton)
        {
            comboBox.getEditor().setItem(new FilterStringPanel(textArea.getText()));
            dispose();
        }
        else if (e.getSource() == cancelBtn)
        {
            dispose();
        }
    }
}
