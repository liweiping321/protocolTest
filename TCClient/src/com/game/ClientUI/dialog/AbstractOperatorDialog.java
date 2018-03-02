package com.game.ClientUI.dialog;

import javax.swing.*;
import java.awt.*;

/**
 * @author jianpeng.zhang
 * @since 2017/2/14.
 */
public class AbstractOperatorDialog extends JDialog
{
    protected JButton okButton;
    protected JButton cancelButton;
    protected JLabel label;

    public AbstractOperatorDialog(Frame owner, String title)
    {
        this(owner, title, 0, 0);
    }

    public AbstractOperatorDialog(Frame owner, String title, int width, int height)
    {
        super(owner, title);

        if (width <= 0 || height <= 0)
        {
            setSize(250, 150);
        }
        else
        {
            setSize(width, height);
        }
        setLayout(null);
        setResizable(false);

        add(getDescription());
        add(getInputField());
        add(getOKButton());
        add(getCancelButton());

        this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2,
                         (Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2);
    }

    protected Component getDescription()
    {
        label = new JLabel();
        label.setText("Delay Time(ms)");
        Font font = new Font("", 0, 15);
        label.setFont(font);
        label.setBounds(15, 5, 400, 35);
        return label;
    }

    protected Component getInputField()
    {
        JSpinner spinner = new JSpinner();
        spinner.setBounds(15, 40, 220, 30);
        return spinner;
    }

    protected Component getOKButton()
    {
        okButton = new JButton("OK");
        okButton.setBounds(60, 80, 75, 30);
        return okButton;
    }

    protected Component getCancelButton()
    {
        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(150, 80, 75, 30);
        return cancelButton;
    }

    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        frame.pack();
        frame.setVisible(true);
        AbstractOperatorDialog dialog = new AbstractOperatorDialog(frame, "dd");
        dialog.setVisible(true);
    }
}
