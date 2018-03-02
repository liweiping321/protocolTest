package com.game.ClientUI.dialog;

import com.game.utils.ComponentUtil;
import com.game.utils.language.LanguageMgr;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * @author jianpeng.zhang
 * @since 2017/2/21.
 */
public class AbstractEditorDialog extends JDialog
{
    protected JTextArea textArea;
    protected JButton okButton;
    protected JButton cancelBtn;

    public AbstractEditorDialog(Frame owner, String title)
    {
        super(owner, title);
        setSize(700, 250);
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textArea.setMargin(new Insets(500, 5, 5, 5));
        textArea.setFont(new Font("", 0, 14));
        okButton = ComponentUtil.createButton(LanguageMgr.getTranslation("TClient.Button.OK"), "");
        cancelBtn = ComponentUtil.createButton(LanguageMgr.getTranslation("TClient.Button.Cancel"), "");

        setLayout(null);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(new LineBorder(new Color(0x00A5FF), 2));
        scrollPane.setViewportView(textArea);

        add(scrollPane);
        add(okButton);
        add(cancelBtn);

        scrollPane.setBounds(23, 20, 640, 140);
        okButton.setBounds(460, 172, 85, 30);
        cancelBtn.setBounds(560, 172, 85, 30);

        this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2,
                         (Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2);
    }

    public static void main(String[] args)
    {
        AbstractEditorDialog dialog = new AbstractEditorDialog(new JFrame("d"), "dsfsdf");
        dialog.setVisible(true);
    }
}
